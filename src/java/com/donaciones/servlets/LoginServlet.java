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
        // Si ya está logueado, redirigir al dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            String userType = (String) session.getAttribute("userType");
            if (userType != null) {
                switch (userType.toLowerCase()) {
                    case "admin":
                    case "empleado":
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                        return;
                    case "usuario":
                        response.sendRedirect(request.getContextPath() + "/profile");
                        return;
                }
            }
        }
        
        // Si no está logueado, mostrar login
        request.getRequestDispatcher("/WEB-INF/views/general/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("DEBUG LoginServlet - Intentando login para: " + username);

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

            System.out.println("DEBUG LoginServlet - Login exitoso. Usuario: " + username + ", Tipo: " + user.getUserType());

            // REDIRECT en lugar de forward - ESTO ES CLAVE
            String redirectPath;
            switch (user.getUserType().toLowerCase()) {
                case "admin":
                case "empleado":
                    redirectPath = "/dashboard";
                    break;
                case "usuario":
                    redirectPath = "/profile";
                    break;
                default:
                    redirectPath = "/dashboard";
            }
            
            System.out.println("DEBUG LoginServlet - Redirigiendo a: " + redirectPath);
            response.sendRedirect(request.getContextPath() + redirectPath);
            
        } else {
            // Authentication failed
            System.out.println("DEBUG LoginServlet - Login fallido para: " + username);
            request.setAttribute("error", "invalid");
            request.getRequestDispatcher("/WEB-INF/views/general/login.jsp").forward(request, response);
        }
    }

    // Elimina los métodos loadAdminDashboardData, loadEmployeeDashboardData y loadUserProfileData
    // porque ahora eso lo manejan DashboardServlet y ProfileServlet
}