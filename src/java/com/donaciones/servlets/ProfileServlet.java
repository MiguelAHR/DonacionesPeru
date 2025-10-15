package com.donaciones.servlets;

import com.donaciones.models.User;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String username = (String) session.getAttribute("username");
        String userType = (String) session.getAttribute("userType");
        
        DataManager dm = DataManager.getInstance();
        User user = dm.getUser(username);
        
        if (user != null) {
            request.setAttribute("userProfile", user);
            
            // Cargar datos específicos según el tipo de usuario
            switch (userType.toLowerCase()) {
                case "admin":
                    loadAdminProfileData(request, username, dm);
                    request.getRequestDispatcher("/WEB-INF/views/admin/profile_admin.jsp").forward(request, response);
                    break;
                case "empleado":
                    loadEmployeeProfileData(request, username, dm);
                    request.getRequestDispatcher("/WEB-INF/views/empleado/profile_employee.jsp").forward(request, response);
                    break;
                case "usuario":
                    loadUserProfileData(request, username, dm);
                    request.getRequestDispatcher("/WEB-INF/views/usuario/profile_user.jsp").forward(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/dashboard");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
    
    private void loadAdminProfileData(HttpServletRequest request, String username, DataManager dm) {
        request.setAttribute("totalActions", dm.getAllDonations().size() + dm.getAllUsers().size());
        request.setAttribute("lastLogin", new java.util.Date());
        request.setAttribute("systemUptime", "100%"); // Ejemplo
    }
    
    private void loadEmployeeProfileData(HttpServletRequest request, String username, DataManager dm) {
        // CORREGIDO: usar getEmployeeUsername en lugar de getEmployeeAssigned
        long myDonations = dm.getAllDonations().stream()
                .filter(d -> username.equals(d.getEmployeeUsername()))
                .count();
        
        long activeDonations = dm.getAllDonations().stream()
                .filter(d -> username.equals(d.getEmployeeUsername()) && "active".equals(d.getStatus()))
                .count();
        
        request.setAttribute("myDonations", myDonations);
        request.setAttribute("activeDonations", activeDonations);
        request.setAttribute("lastLogin", new java.util.Date());
        request.setAttribute("employeeSince", "2024"); // Ejemplo
    }
    
    private void loadUserProfileData(HttpServletRequest request, String username, DataManager dm) {
        // Datos específicos para usuario normal
        java.util.List<com.donaciones.models.Donation> userDonations = dm.getDonationsByUser(username);
        request.setAttribute("userDonations", userDonations);
        request.setAttribute("totalUserDonations", userDonations.size());
        
        request.setAttribute("activeUserDonations", userDonations.stream()
                .filter(d -> "active".equals(d.getStatus()))
                .count());
        request.setAttribute("completedUserDonations", userDonations.stream()
                .filter(d -> "completed".equals(d.getStatus()))
                .count());
        
        request.setAttribute("memberSince", "2024"); // Ejemplo
        // CORREGIDO: usar getCreatedDate() en lugar de getDate()
        request.setAttribute("lastDonation", userDonations.isEmpty() ? "Nunca" : 
            new java.text.SimpleDateFormat("dd/MM/yyyy").format(userDonations.get(0).getCreatedDate()));
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Para actualizar perfil en el futuro
        doGet(request, response);
    }
}