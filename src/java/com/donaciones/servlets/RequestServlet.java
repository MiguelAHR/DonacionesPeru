package com.donaciones.servlets;

import com.donaciones.models.Request;
import com.donaciones.models.User;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/requests")
public class RequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            createRequest(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/beneficiario/dashboard");
        }
    }
    
    private void createRequest(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        try {
            String type = request.getParameter("type");
            String description = request.getParameter("description");
            String location = request.getParameter("location");
            String priorityStr = request.getParameter("priority");
            
            // Validar campos requeridos
            if (type == null || description == null || location == null || 
                type.trim().isEmpty() || description.trim().isEmpty() || location.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/beneficiario/dashboard?error=missing_fields");
                return;
            }
            
            int priority = 3; // Prioridad media por defecto
            if (priorityStr != null && !priorityStr.trim().isEmpty()) {
                try {
                    priority = Integer.parseInt(priorityStr);
                    // Asegurar que la prioridad esté entre 1 y 5
                    if (priority < 1) priority = 1;
                    if (priority > 5) priority = 5;
                } catch (NumberFormatException e) {
                    priority = 3;
                }
            }
            
            // Crear la solicitud
            Request newRequest = new Request(type, description, location, user.getUsername());
            newRequest.setPriority(priority);
            
            DataManager dataManager = DataManager.getInstance();
            boolean success = dataManager.addRequest(newRequest);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/beneficiario/dashboard?success=request_created");
            } else {
                response.sendRedirect(request.getContextPath() + "/beneficiario/dashboard?error=creation_failed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/beneficiario/dashboard?error=server_error");
        }
    }
}