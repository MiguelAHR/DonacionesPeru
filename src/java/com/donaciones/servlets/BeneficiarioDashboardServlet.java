package com.donaciones.servlets;

import com.donaciones.models.Request;
import com.donaciones.models.User;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/beneficiario/dashboard")
public class BeneficiarioDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String username = (String) session.getAttribute("username");
        DataManager dataManager = DataManager.getInstance();
        
        // Obtener el usuario completo
        User user = dataManager.getUser(username);
        if (user == null || !"receptor".equals(user.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Configurar usuario en sesión y request
        session.setAttribute("user", user);
        session.setAttribute("userType", user.getUserType());
        
        // Obtener las solicitudes del beneficiario
        List<Request> solicitudes = dataManager.getRequestsByUser(username);
        
        // Estadísticas
        int totalSolicitudes = solicitudes.size();
        int solicitudesPendientes = 0;
        int solicitudesAprobadas = 0;
        
        for (Request r : solicitudes) {
            if ("pending".equals(r.getStatus())) {
                solicitudesPendientes++;
            } else if ("completed".equals(r.getStatus()) || "approved".equals(r.getStatus())) {
                solicitudesAprobadas++;
            }
        }
        
        // Establecer atributos en el request
        request.setAttribute("solicitudes", solicitudes);
        request.setAttribute("totalSolicitudes", totalSolicitudes);
        request.setAttribute("solicitudesPendientes", solicitudesPendientes);
        request.setAttribute("solicitudesAprobadas", solicitudesAprobadas);
        
        // Información del perfil
        request.setAttribute("userFullName", user.getFullName());
        request.setAttribute("userProfile", user);
        request.setAttribute("profileImage", user.getProfileImage());
        
        // Formatear fecha de registro
        if (user.getRegistrationDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            request.setAttribute("memberSince", sdf.format(user.getRegistrationDate()));
        }
        
        request.getRequestDispatcher("/WEB-INF/views/beneficiario/dashboard.jsp").forward(request, response);
    }
}