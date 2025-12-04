package com.donaciones.servlets;

import com.donaciones.models.User;
import com.donaciones.models.Donation;
import com.donaciones.models.Request;
import com.donaciones.utils.DataManager;
import com.donaciones.utils.FileUploadUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
            
            // **CORRECCIÓN CRÍTICA: Cargar imagen de perfil usando método unificado**
            String profileImage = FileUploadUtil.getProfileImageForSession(user, userType, request.getContextPath());
            
            // Actualizar sesión con ruta completa
            session.setAttribute("profileImage", profileImage);
            request.setAttribute("profileImage", profileImage);
            
            System.out.println("DEBUG ProfileServlet - Imagen cargada: " + profileImage);
            
            // Para compatibilidad con el dashboard existente
            if (user.getFirstName() != null && user.getLastName() != null) {
                request.setAttribute("userFullName", user.getFirstName() + " " + user.getLastName());
                session.setAttribute("fullName", user.getFirstName() + " " + user.getLastName());
            }

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
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                    }
                    break;
                    
                case "donador":
                    System.out.println("DEBUG ProfileServlet - Cargando datos para dashboard de donador");
                    try {
                        loadDonorDashboardData(request, username, dm);
                        System.out.println("DEBUG ProfileServlet - Redirigiendo a dashboard_donador.jsp");
                        request.getRequestDispatcher("/WEB-INF/views/donador/dashboard.jsp").forward(request, response);
                    } catch (Exception e) {
                        System.out.println("ERROR ProfileServlet - Excepción al cargar datos de donador: " + e.getMessage());
                        e.printStackTrace();
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                    }
                    break;
                    
                case "receptor":
                    System.out.println("DEBUG ProfileServlet - Cargando datos para dashboard de receptor");
                    try {
                        loadReceiverDashboardData(request, username, dm);
                        System.out.println("DEBUG ProfileServlet - Redirigiendo a dashboard_receptor.jsp");
                        request.getRequestDispatcher("/WEB-INF/views/beneficiario/dashboard.jsp").forward(request, response);
                    } catch (Exception e) {
                        System.out.println("ERROR ProfileServlet - Excepción al cargar datos de receptor: " + e.getMessage());
                        e.printStackTrace();
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
    
    // Los métodos load...Data permanecen IGUALES
    private void loadAdminProfileData(HttpServletRequest request, String username, DataManager dm) {
        try {
            // Estadísticas para admin
            request.setAttribute("totalActions", dm.getAllDonations().size() + dm.getAllUsers().size());
            request.setAttribute("lastLogin", new java.util.Date());
            request.setAttribute("systemUptime", "100%");
            
            // Datos adicionales para dashboard
            request.setAttribute("totalUsers", dm.getTotalUsers());
            request.setAttribute("totalDonors", dm.getTotalDonors());
            request.setAttribute("totalReceivers", dm.getTotalReceivers());
            request.setAttribute("totalAdmins", dm.getTotalAdmins());
            request.setAttribute("totalEmployees", dm.getTotalEmployees());
            request.setAttribute("totalDonations", dm.getTotalDonations());
            request.setAttribute("pendingDonations", dm.getTotalPendingDonations());
            request.setAttribute("totalRequests", dm.getTotalRequestsCount());
            request.setAttribute("pendingRequests", dm.getPendingRequestsCount());
            request.setAttribute("totalCatalogItems", dm.getTotalCatalogItemsCount());
            request.setAttribute("availableCatalogItems", dm.getAvailableCatalogItemsCount());
            
            // Fecha de registro
            User user = dm.getUser(username);
            if (user != null && user.getRegistrationDate() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                request.setAttribute("registrationDateFormatted", sdf.format(user.getRegistrationDate()));
            }
            
        } catch (Exception e) {
            System.out.println("ERROR ProfileServlet - Error en loadAdminProfileData: " + e.getMessage());
            request.setAttribute("totalActions", 0);
            request.setAttribute("systemUptime", "N/A");
        }
    }

    private void loadEmployeeProfileData(HttpServletRequest request, String username, DataManager dm) {
        try {
            // Estadísticas para empleado
            long myDonations = dm.getAllDonations().stream()
                    .filter(d -> username.equals(d.getEmployeeUsername()))
                    .count();

            long activeDonations = dm.getAllDonations().stream()
                    .filter(d -> username.equals(d.getEmployeeUsername()) && "active".equals(d.getStatus()))
                    .count();
                    
            long completedDonations = dm.getAllDonations().stream()
                    .filter(d -> username.equals(d.getEmployeeUsername()) && "completed".equals(d.getStatus()))
                    .count();

            request.setAttribute("myDonations", myDonations);
            request.setAttribute("activeDonations", activeDonations);
            request.setAttribute("completedDonations", completedDonations);
            request.setAttribute("lastLogin", new java.util.Date());
            
            // Obtener nombre de empleado
            User user = dm.getUser(username);
            if (user != null) {
                request.setAttribute("employeeName", user.getFirstName() + " " + user.getLastName());
                if (user.getRegistrationDate() != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM yyyy");
                    request.setAttribute("employeeSince", sdf.format(user.getRegistrationDate()));
                }
            }
            
        } catch (Exception e) {
            System.out.println("ERROR ProfileServlet - Error en loadEmployeeProfileData: " + e.getMessage());
            request.setAttribute("myDonations", 0);
            request.setAttribute("activeDonations", 0);
            request.setAttribute("employeeSince", "N/A");
        }
    }

    private void loadUserProfileData(HttpServletRequest request, String username, DataManager dm) {
        try {
            System.out.println("DEBUG ProfileServlet - Cargando datos para usuario: " + username);

            // Cargar donaciones del usuario
            List<Donation> userDonations = dm.getDonationsByUser(username);
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
            List<Request> userRequests = dm.getRequestsByUser(username);
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

            // Fecha de registro
            User user = dm.getUser(username);
            if (user != null && user.getRegistrationDate() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                request.setAttribute("memberSince", sdf.format(user.getRegistrationDate()));
            }

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
            request.setAttribute("memberSince", "N/A");
        }
    }

    // NUEVO MÉTODO: Cargar datos para el dashboard de donador
    private void loadDonorDashboardData(HttpServletRequest request, String username, DataManager dm) {
        try {
            System.out.println("DEBUG ProfileServlet - Cargando dashboard para donador: " + username);

            // Cargar donaciones del donador
            List<Donation> donorDonations = dm.getDonationsByUser(username);
            if (donorDonations == null) {
                donorDonations = new java.util.ArrayList<>();
                System.out.println("DEBUG ProfileServlet - donorDonations era null, se inicializó lista vacía");
            }

            // Calcular estadísticas
            long totalDonations = donorDonations.size();
            long pendingDonations = donorDonations.stream()
                    .filter(d -> d != null && "pending".equals(d.getStatus()))
                    .count();
            long completedDonations = donorDonations.stream()
                    .filter(d -> d != null && "completed".equals(d.getStatus()))
                    .count();

            // Pasar datos al dashboard
            request.setAttribute("donaciones", donorDonations);
            request.setAttribute("totalSolicitudes", totalDonations);
            request.setAttribute("solicitudesPendientes", pendingDonations);
            request.setAttribute("solicitudesAprobadas", completedDonations);
            
            // También pasar los nombres correctos para el dashboard existente
            request.setAttribute("totalDonations", totalDonations);
            request.setAttribute("totalDonaciones", totalDonations);
            request.setAttribute("pendingDonations", pendingDonations);
            request.setAttribute("completedDonations", completedDonations);

            // Información del usuario
            User user = dm.getUser(username);
            if (user != null) {
                request.setAttribute("userFullName", user.getFirstName() + " " + user.getLastName());
                if (user.getRegistrationDate() != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    request.setAttribute("memberSince", sdf.format(user.getRegistrationDate()));
                    request.setAttribute("registrationDate", sdf.format(user.getRegistrationDate()));
                }
            }

            System.out.println("DEBUG ProfileServlet - Datos cargados para donador: "
                    + donorDonations.size() + " donaciones");

        } catch (Exception e) {
            System.out.println("ERROR ProfileServlet - Error en loadDonorDashboardData: " + e.getMessage());
            e.printStackTrace();

            // Inicializar con valores por defecto en caso de error
            request.setAttribute("donaciones", new java.util.ArrayList<>());
            request.setAttribute("totalSolicitudes", 0);
            request.setAttribute("solicitudesPendientes", 0);
            request.setAttribute("solicitudesAprobadas", 0);
            request.setAttribute("totalDonations", 0);
            request.setAttribute("totalDonaciones", 0);
            request.setAttribute("memberSince", "N/A");
        }
    }

    // NUEVO MÉTODO: Cargar datos para el dashboard de receptor
    private void loadReceiverDashboardData(HttpServletRequest request, String username, DataManager dm) {
        try {
            System.out.println("DEBUG ProfileServlet - Cargando dashboard para receptor: " + username);

            // Cargar solicitudes del receptor
            List<Request> receiverRequests = dm.getRequestsByUser(username);
            if (receiverRequests == null) {
                receiverRequests = new java.util.ArrayList<>();
                System.out.println("DEBUG ProfileServlet - receiverRequests era null, se inicializó lista vacía");
            }

            // Calcular estadísticas
            long totalRequests = receiverRequests.size();
            long pendingRequests = receiverRequests.stream()
                    .filter(r -> r != null && "pending".equals(r.getStatus()))
                    .count();
            long completedRequests = receiverRequests.stream()
                    .filter(r -> r != null && "completed".equals(r.getStatus()))
                    .count();

            // Pasar datos al dashboard
            request.setAttribute("solicitudes", receiverRequests);
            request.setAttribute("totalSolicitudes", totalRequests);
            request.setAttribute("solicitudesPendientes", pendingRequests);
            request.setAttribute("solicitudesAprobadas", completedRequests);
            
            // También pasar los nombres correctos para el dashboard existente
            request.setAttribute("totalRequests", totalRequests);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("completedRequests", completedRequests);

            // Información del usuario
            User user = dm.getUser(username);
            if (user != null) {
                request.setAttribute("userFullName", user.getFirstName() + " " + user.getLastName());
                if (user.getRegistrationDate() != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    request.setAttribute("memberSince", sdf.format(user.getRegistrationDate()));
                    request.setAttribute("registrationDate", sdf.format(user.getRegistrationDate()));
                }
            }

            System.out.println("DEBUG ProfileServlet - Datos cargados para receptor: "
                    + receiverRequests.size() + " solicitudes");

        } catch (Exception e) {
            System.out.println("ERROR ProfileServlet - Error en loadReceiverDashboardData: " + e.getMessage());
            e.printStackTrace();

            // Inicializar con valores por defecto en caso de error
            request.setAttribute("solicitudes", new java.util.ArrayList<>());
            request.setAttribute("totalSolicitudes", 0);
            request.setAttribute("solicitudesPendientes", 0);
            request.setAttribute("solicitudesAprobadas", 0);
            request.setAttribute("totalRequests", 0);
            request.setAttribute("pendingRequests", 0);
            request.setAttribute("memberSince", "N/A");
        }
    }

    // Los métodos doPost, updateUserProfile y updateUserPassword permanecen IGUALES
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Para actualizar perfil o contraseña
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        if ("update_profile".equals(action)) {
            updateUserProfile(request, response);
        } else if ("update_password".equals(action)) {
            updateUserPassword(request, response);
        } else {
            // Si no hay acción específica, mostrar el perfil normal
            doGet(request, response);
        }
    }
    
    private void updateUserProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        DataManager dm = DataManager.getInstance();
        
        try {
            User user = dm.getUser(username);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/profile?error=user_not_found");
                return;
            }
            
            // Actualizar campos del perfil
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String region = request.getParameter("region");
            String district = request.getParameter("district");
            String address = request.getParameter("address");
            
            if (firstName != null && !firstName.trim().isEmpty()) {
                user.setFirstName(firstName.trim());
            }
            if (lastName != null && !lastName.trim().isEmpty()) {
                user.setLastName(lastName.trim());
            }
            if (email != null && !email.trim().isEmpty()) {
                user.setEmail(email.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone.trim());
            }
            if (region != null && !region.trim().isEmpty()) {
                user.setRegion(region.trim());
            }
            if (district != null && !district.trim().isEmpty()) {
                user.setDistrict(district.trim());
            }
            if (address != null && !address.trim().isEmpty()) {
                user.setAddress(address.trim());
            }
            
            // Guardar cambios
            boolean success = dm.updateUser(user);
            if (success) {
                // Actualizar nombre en sesión si se cambió
                session.setAttribute("firstName", user.getFirstName());
                session.setAttribute("lastName", user.getLastName());
                session.setAttribute("fullName", user.getFullName());
                
                response.sendRedirect(request.getContextPath() + "/profile?success=profile_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/profile?error=save_failed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/profile?error=general");
        }
    }
    
    private void updateUserPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        DataManager dm = DataManager.getInstance();
        
        try {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            // Validaciones
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/profile?error=missing_fields&tab=security");
                return;
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/profile?error=missing_fields&tab=security");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                response.sendRedirect(request.getContextPath() + "/profile?error=new_password_mismatch&tab=security");
                return;
            }
            
            if (newPassword.length() < 6) {
                response.sendRedirect(request.getContextPath() + "/profile?error=new_password_weak&tab=security");
                return;
            }
            
            // Verificar contraseña actual
            User user = dm.getUser(username);
            if (user == null || !user.getPassword().equals(currentPassword)) {
                response.sendRedirect(request.getContextPath() + "/profile?error=current_password_incorrect&tab=security");
                return;
            }
            
            // Actualizar contraseña
            user.setPassword(newPassword);
            boolean success = dm.updateUser(user);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/profile?success=password_updated&tab=security");
            } else {
                response.sendRedirect(request.getContextPath() + "/profile?error=save_failed&tab=security");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/profile?error=general&tab=security");
        }
    }
}