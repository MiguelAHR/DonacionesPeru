package com.donaciones.servlets;

import com.donaciones.models.User;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to login page
        request.getRequestDispatcher("/WEB-INF/views/general/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate input
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "empty");
            request.getRequestDispatcher("/WEB-INF/views/general/login.jsp").forward(request, response);
            return;
        }

        // Authenticate user
        User user = DataManager.getInstance().authenticateUser(username, password);

        if (user != null) {
            // Create session
            HttpSession session = request.getSession();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userType", user.getUserType());
            session.setAttribute("user", user);

            // Redirect based on user type
            switch (user.getUserType().toLowerCase()) {
                case "admin":
                    // Load admin dashboard data
                    loadAdminDashboardData(request);
                    request.getRequestDispatcher("/WEB-INF/views/admin/dashboard_admin.jsp").forward(request, response);
                    break;
                case "empleado":
                    // Load employee dashboard data
                    loadEmployeeDashboardData(request, user.getUsername());
                    request.getRequestDispatcher("/WEB-INF/views/empleado/dashboard_employee.jsp").forward(request, response);
                    break;
                case "usuario":
                    // Load user profile data
                    loadUserProfileData(request, user.getUsername());
                    request.getRequestDispatcher("/WEB-INF/views/usuario/profile_user.jsp").forward(request, response);
                    break;
                default:
                    request.setAttribute("error", "invalid");
                    request.getRequestDispatcher("/WEB-INF/views/general/login.jsp").forward(request, response);
            }
        } else {
            // Authentication failed
            request.setAttribute("error", "invalid");
            request.getRequestDispatcher("/WEB-INF/views/general/login.jsp").forward(request, response);

        }
    }

    private void loadAdminDashboardData(HttpServletRequest request) {
        DataManager dm = DataManager.getInstance();

        request.setAttribute("totalDonations", dm.getAllDonations().size());
        request.setAttribute("totalDonors", dm.getAllDonors().size());
        request.setAttribute("totalReceivers", dm.getAllReceivers().size());
        request.setAttribute("totalUsers", dm.getAllUsers().size());

        // Recent activity (sample data)
        java.util.List<String> recentActivity = new java.util.ArrayList<>();
        recentActivity.add("Nuevo donador registrado: María García");
        recentActivity.add("Donación de ropa completada en Lima");
        recentActivity.add("Receptor verificado: Carlos Mendoza");
        recentActivity.add("Nueva donación de útiles escolares registrada");
        request.setAttribute("recentActivity", recentActivity);
    }

    private void loadEmployeeDashboardData(HttpServletRequest request, String employeeUsername) {
        DataManager dm = DataManager.getInstance();

        // For demo purposes, showing sample data
        request.setAttribute("myDonors", 15);
        request.setAttribute("myReceivers", 8);
        request.setAttribute("myDonations", 23);

        // Recent work (sample data)
        java.util.List<String> recentWork = new java.util.ArrayList<>();
        recentWork.add("Registré a la familia Rodríguez como receptores");
        recentWork.add("Coordiné donación de ropa en San Juan de Lurigancho");
        recentWork.add("Verifiqué datos de donador en Miraflores");
        request.setAttribute("recentWork", recentWork);
    }

    private void loadUserProfileData(HttpServletRequest request, String username) {
        DataManager dm = DataManager.getInstance();

        // Load user's donations
        request.setAttribute("userDonations", dm.getDonationsByUser(username));

        // Sample user requests
        java.util.List<String> userRequests = new java.util.ArrayList<>();
        userRequests.add("Solicitud de ropa para niños - Pendiente");
        userRequests.add("Solicitud de útiles escolares - En proceso");
        request.setAttribute("userRequests", userRequests);
    }
}
