package com.donaciones.servlets;

import com.donaciones.models.User;
import com.donaciones.models.Donation;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DEBUG DashboardServlet - Iniciando doGet");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            System.out.println("DEBUG DashboardServlet - No hay sesión, redirigiendo a login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        String userType = (String) session.getAttribute("userType");

        System.out.println("DEBUG DashboardServlet - Usuario: " + username + ", Tipo: " + userType);

        DataManager dm = DataManager.getInstance();

        // Verificar que el usuario existe en la base de datos
        User user = dm.getUser(username);
        if (user == null) {
            System.out.println("DEBUG DashboardServlet - Usuario no encontrado en BD, cerrando sesión");
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Cargar datos según el tipo de usuario
        try {
            switch (userType.toLowerCase()) {
                case "admin":
                    System.out.println("DEBUG DashboardServlet - Cargando dashboard de admin");
                    loadAdminDashboardData(request, dm);
                    request.getRequestDispatcher("/WEB-INF/views/admin/dashboard_admin.jsp").forward(request, response);
                    break;
                case "empleado":
                    System.out.println("DEBUG DashboardServlet - Cargando dashboard de empleado");
                    loadEmployeeDashboardData(request, username, dm);
                    request.getRequestDispatcher("/WEB-INF/views/empleado/dashboard_employee.jsp").forward(request, response);
                    break;
                case "usuario":
                    System.out.println("DEBUG DashboardServlet - Redirigiendo usuario a perfil");
                    // Para usuario, redirigir al perfil (que es su dashboard)
                    response.sendRedirect(request.getContextPath() + "/profile");
                    break;
                default:
                    System.out.println("DEBUG DashboardServlet - Tipo de usuario desconocido: " + userType);
                    response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        } catch (Exception e) {
            System.out.println("ERROR DashboardServlet - Excepción: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }

        System.out.println("DEBUG DashboardServlet - doGet completado");
    }

    private void loadAdminDashboardData(HttpServletRequest request, DataManager dm) {
        try {
            request.setAttribute("totalDonations", dm.getTotalDonations());
            request.setAttribute("totalDonors", dm.getTotalDonors());
            request.setAttribute("totalReceivers", dm.getTotalReceivers());
            request.setAttribute("totalUsers", dm.getTotalUsers());
            request.setAttribute("activeDonations", dm.getTotalActiveDonations());
            request.setAttribute("pendingDonations", dm.getTotalPendingDonations());

            // Donaciones recientes (últimas 5)
            List<Donation> recentDonations = dm.getAllDonations().stream()
                    .sorted((d1, d2) -> d2.getCreatedDate().compareTo(d1.getCreatedDate()))
                    .limit(5)
                    .collect(Collectors.toList());
            request.setAttribute("recentDonations", recentDonations);

            // Recent activity
            java.util.List<String> recentActivity = new java.util.ArrayList<>();
            recentActivity.add("Nuevo donador registrado: Ana Pérez");
            recentActivity.add("Donación de cuadernos completada en Arequipa");
            recentActivity.add("Receptor verificado: Luis Torres");
            recentActivity.add("Nueva donación de material reciclable en Cusco");
            recentActivity.add("Empleado procesó 5 donaciones hoy");
            request.setAttribute("recentActivity", recentActivity);
        } catch (Exception e) {
            System.out.println("ERROR DashboardServlet - Error cargando datos de admin: " + e.getMessage());
        }
    }

    private void loadEmployeeDashboardData(HttpServletRequest request, String employeeUsername, DataManager dm) {
        try {
            // Usar datos reales de la base de datos
            request.setAttribute("myDonations", dm.getEmployeeDonations(employeeUsername));
            request.setAttribute("myActiveDonations", dm.getEmployeeActiveDonations(employeeUsername));
            request.setAttribute("myCompletedDonations", dm.getEmployeeCompletedDonations(employeeUsername));

            // Datos de muestra para donadores y receptores (puedes implementar métodos similares)
            request.setAttribute("myDonors", 18);
            request.setAttribute("myReceivers", 12);

            java.util.List<String> recentWork = new java.util.ArrayList<>();
            recentWork.add("Registré a la familia Vásquez como receptores");
            recentWork.add("Coordiné donación de útiles escolares en Villa El Salvador");
            recentWork.add("Verifiqué datos de donador en San Borja");
            recentWork.add("Procesé solicitud de ropa casi nueva");
            request.setAttribute("recentWork", recentWork);
        } catch (Exception e) {
            System.out.println("ERROR DashboardServlet - Error cargando datos de empleado: " + e.getMessage());
        }
    }
}