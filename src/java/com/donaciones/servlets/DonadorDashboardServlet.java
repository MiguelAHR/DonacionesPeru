package com.donaciones.servlets;

import com.donaciones.models.Donation;
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

@WebServlet("/donador/dashboard")
public class DonadorDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (!"donador".equals(user.getUserType())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        DataManager dataManager = DataManager.getInstance();
        
        // Obtener las donaciones del donador
        List<Donation> donaciones = dataManager.getDonationsByUser(user.getUsername());
        request.setAttribute("donaciones", donaciones);
        
        // Estadísticas del donador
        int totalDonaciones = donaciones.size();
        int donacionesPendientes = (int) donaciones.stream()
                .filter(d -> "pending".equals(d.getStatus()))
                .count();
        int donacionesCompletadas = (int) donaciones.stream()
                .filter(d -> "completed".equals(d.getStatus()))
                .count();
        
        request.setAttribute("totalDonaciones", totalDonaciones);
        request.setAttribute("donacionesPendientes", donacionesPendientes);
        request.setAttribute("donacionesCompletadas", donacionesCompletadas);
        
        request.getRequestDispatcher("/WEB-INF/views/donador/dashboard.jsp").forward(request, response);
    }
}