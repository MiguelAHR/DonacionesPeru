package com.donaciones.servlets;

import com.donaciones.models.Request;
import com.donaciones.models.User;
import com.donaciones.utils.DataManager;
import java.io.IOException;
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
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (!"receptor".equals(user.getUserType())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        DataManager dataManager = DataManager.getInstance();
        
        // Obtener las solicitudes del beneficiario
        List<Request> solicitudes = dataManager.getRequestsByUser(user.getUsername());
        request.setAttribute("solicitudes", solicitudes);
        
        // Estadísticas del beneficiario
        int totalSolicitudes = solicitudes.size();
        int solicitudesPendientes = (int) solicitudes.stream()
                .filter(r -> "pending".equals(r.getStatus()))
                .count();
        int solicitudesAprobadas = (int) solicitudes.stream()
                .filter(r -> "approved".equals(r.getStatus()) || "completed".equals(r.getStatus()))
                .count();
        
        request.setAttribute("totalSolicitudes", totalSolicitudes);
        request.setAttribute("solicitudesPendientes", solicitudesPendientes);
        request.setAttribute("solicitudesAprobadas", solicitudesAprobadas);
        
        request.getRequestDispatcher("/WEB-INF/views/beneficiario/dashboard.jsp").forward(request, response);
    }
}