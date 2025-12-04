package com.donaciones.servlets;

import com.donaciones.models.Donation;
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

@WebServlet("/donador/dashboard")
public class DonadorDashboardServlet extends HttpServlet {

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
        if (user == null || !"donador".equals(user.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Configurar usuario en sesión y request
        session.setAttribute("user", user);
        session.setAttribute("userType", user.getUserType());
        
        // Obtener las donaciones del donador
        List<Donation> donaciones = dataManager.getDonationsByUser(username);
        
        // Estadísticas
        int totalDonaciones = donaciones.size();
        int donacionesPendientes = 0;
        int donacionesCompletadas = 0;
        
        for (Donation d : donaciones) {
            if ("pending".equals(d.getStatus())) {
                donacionesPendientes++;
            } else if ("completed".equals(d.getStatus())) {
                donacionesCompletadas++;
            }
        }
        
        // Establecer atributos en el request
        request.setAttribute("donaciones", donaciones);
        request.setAttribute("totalDonations", totalDonaciones);
        request.setAttribute("pendingDonations", donacionesPendientes);
        request.setAttribute("completedDonations", donacionesCompletadas);
        
        // Información del perfil
        request.setAttribute("userFullName", user.getFullName());
        request.setAttribute("userProfile", user);
        request.setAttribute("profileImage", user.getProfileImage());
        
        // Formatear fecha de registro
        if (user.getRegistrationDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            request.setAttribute("memberSince", sdf.format(user.getRegistrationDate()));
        }
        
        request.getRequestDispatcher("/WEB-INF/views/donador/dashboard.jsp").forward(request, response);
    }
}