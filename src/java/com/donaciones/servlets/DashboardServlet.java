package com.donaciones.servlets;

import com.donaciones.models.User;
import com.donaciones.models.Donation;
import com.donaciones.models.Request;
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

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userType = (String) session.getAttribute("userType");
        String username = (String) session.getAttribute("username");

        switch (userType.toLowerCase()) {
            case "admin":
                loadAdminDashboardData(request);
                request.getRequestDispatcher("/WEB-INF/views/admin/dashboard_admin.jsp").forward(request, response);
                break;
            case "empleado":
                loadEmployeeDashboardData(request, username);
                request.getRequestDispatcher("/WEB-INF/views/empleado/dashboard_employee.jsp").forward(request, response);
                break;
            case "usuario":
                loadUserProfileData(request, username);
                request.getRequestDispatcher("/WEB-INF/views/usuario/profile_user.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    private void loadAdminDashboardData(HttpServletRequest request) {
        DataManager dm = DataManager.getInstance();

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
    }

    private void loadEmployeeDashboardData(HttpServletRequest request, String employeeUsername) {
        DataManager dm = DataManager.getInstance();

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
    }

    private void loadUserProfileData(HttpServletRequest request, String username) {
        DataManager dm = DataManager.getInstance();

        // Cargar donaciones del usuario (últimas 3)
        List<Donation> userDonations = dm.getDonationsByUser(username); 
        request.setAttribute("recentDonations", userDonations.stream()
                .sorted((d1, d2) -> d2.getCreatedDate().compareTo(d1.getCreatedDate()))
                .limit(3)
                .collect(Collectors.toList()));

        // Cargar solicitudes del usuario (últimas 3)
        List<Request> userRequests = dm.getRequestsByUser(username); 
        request.setAttribute("recentRequests", userRequests.stream()
                .sorted((r1, r2) -> r2.getRequestDate().compareTo(r1.getRequestDate()))
                .limit(3)
                .collect(Collectors.toList()));

        // Estadísticas
        request.setAttribute("totalUserDonations", userDonations.size());
        request.setAttribute("totalUserRequests", userRequests.size());
        request.setAttribute("activeUserDonations", userDonations.stream()
                .filter(d -> "active".equals(d.getStatus()) || "pending".equals(d.getStatus()))
                .count());
    }
}