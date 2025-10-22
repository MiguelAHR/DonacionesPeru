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

        System.out.println("DEBUG ProfileServlet - Iniciando doGet");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            System.out.println("DEBUG ProfileServlet - No hay sesión, redirigiendo a login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        String userType = (String) session.getAttribute("userType");

        System.out.println("DEBUG ProfileServlet - Usuario: " + username + ", Tipo: " + userType);

        DataManager dm = DataManager.getInstance();
        User user = dm.getUser(username);

        if (user != null) {
            request.setAttribute("userProfile", user);

            // Cargar mensajes de éxito/error
            String success = request.getParameter("success");
            String error = request.getParameter("error");
            String tab = request.getParameter("tab");

            System.out.println("DEBUG ProfileServlet - Parámetros: success=" + success + ", error=" + error + ", tab=" + tab);

            // Cargar datos según tipo de usuario
            switch (userType.toLowerCase()) {
                case "admin":
                    System.out.println("DEBUG ProfileServlet - Cargando perfil de admin");
                    loadAdminProfileData(request, username, dm);
                    request.getRequestDispatcher("/WEB-INF/views/admin/profile_admin.jsp").forward(request, response);
                    break;
                case "empleado":
                    System.out.println("DEBUG ProfileServlet - Cargando perfil de empleado");
                    loadEmployeeProfileData(request, username, dm);
                    request.getRequestDispatcher("/WEB-INF/views/empleado/profile_employee.jsp").forward(request, response);
                    break;
                case "usuario":
                    System.out.println("DEBUG ProfileServlet - Cargando perfil de usuario");
                    try {
                        loadUserProfileData(request, username, dm);
                        System.out.println("DEBUG ProfileServlet - Redirigiendo a profile_user.jsp");
                        request.getRequestDispatcher("/WEB-INF/views/usuario/profile_user.jsp").forward(request, response);
                    } catch (Exception e) {
                        System.out.println("ERROR ProfileServlet - Excepción al cargar perfil de usuario: " + e.getMessage());
                        e.printStackTrace();
                        // En caso de error, redirigir al dashboard
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                    }
                    break;
                default:
                    System.out.println("DEBUG ProfileServlet - Tipo de usuario desconocido, redirigiendo a dashboard");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
            }
        } else {
            System.out.println("DEBUG ProfileServlet - Usuario no encontrado, redirigiendo a dashboard");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }

        System.out.println("DEBUG ProfileServlet - doGet completado");
    }

// Agregar estos métodos auxiliares al ProfileServlet
    private String getSuccessMessage(String successType) {
        switch (successType) {
            case "donation_created":
                return "¡Donación creada exitosamente!";
            case "request_created":
                return "¡Solicitud creada exitosamente!";
            default:
                return "Operación completada exitosamente";
        }
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case "missing_fields":
                return "Por favor, completa todos los campos requeridos";
            case "invalid_quantity":
                return "La cantidad debe ser un número válido mayor a 0";
            case "save_failed":
                return "Error al guardar la información en la base de datos";
            case "general":
                return "Ha ocurrido un error inesperado. Intenta nuevamente";
            default:
                return "Ha ocurrido un error. Intenta nuevamente";
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
        try {
            System.out.println("DEBUG ProfileServlet - Cargando datos para usuario: " + username);

            // Cargar donaciones del usuario - con manejo de null
            java.util.List<com.donaciones.models.Donation> userDonations = dm.getDonationsByUser(username);
            if (userDonations == null) {
                userDonations = new java.util.ArrayList<>();
                System.out.println("DEBUG ProfileServlet - userDonations era null, se inicializó lista vacía");
            }

            request.setAttribute("userDonations", userDonations);
            request.setAttribute("totalUserDonations", userDonations.size());

            request.setAttribute("activeUserDonations", userDonations.stream()
                    .filter(d -> d != null && "active".equals(d.getStatus()))
                    .count());
            request.setAttribute("completedUserDonations", userDonations.stream()
                    .filter(d -> d != null && "completed".equals(d.getStatus()))
                    .count());

            // Cargar solicitudes del usuario también
            java.util.List<com.donaciones.models.Request> userRequests = dm.getRequestsByUser(username);
            if (userRequests == null) {
                userRequests = new java.util.ArrayList<>();
                System.out.println("DEBUG ProfileServlet - userRequests era null, se inicializó lista vacía");
            }

            request.setAttribute("userRequests", userRequests);
            request.setAttribute("totalUserRequests", userRequests.size());
            request.setAttribute("pendingUserRequests", userRequests.stream()
                    .filter(r -> r != null && "pending".equals(r.getStatus()))
                    .count());
            request.setAttribute("inProgressUserRequests", userRequests.stream()
                    .filter(r -> r != null && "in_progress".equals(r.getStatus()))
                    .count());
            request.setAttribute("completedUserRequests", userRequests.stream()
                    .filter(r -> r != null && "completed".equals(r.getStatus()))
                    .count());

            request.setAttribute("memberSince", "2024");

            // Manejar lastDonation de forma segura
            if (!userDonations.isEmpty() && userDonations.get(0) != null) {
                try {
                    String lastDonation = new java.text.SimpleDateFormat("dd/MM/yyyy")
                            .format(userDonations.get(0).getCreatedDate());
                    request.setAttribute("lastDonation", lastDonation);
                } catch (Exception e) {
                    request.setAttribute("lastDonation", "Nunca");
                }
            } else {
                request.setAttribute("lastDonation", "Nunca");
            }

            System.out.println("DEBUG ProfileServlet - Datos cargados: "
                    + userDonations.size() + " donaciones, "
                    + userRequests.size() + " solicitudes");

        } catch (Exception e) {
            System.out.println("ERROR ProfileServlet - Error en loadUserProfileData: " + e.getMessage());
            e.printStackTrace();

            // Inicializar con valores por defecto en caso de error
            request.setAttribute("userDonations", new java.util.ArrayList<>());
            request.setAttribute("userRequests", new java.util.ArrayList<>());
            request.setAttribute("totalUserDonations", 0);
            request.setAttribute("totalUserRequests", 0);
            request.setAttribute("lastDonation", "Nunca");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Para actualizar perfil en el futuro
        doGet(request, response);
    }
}
