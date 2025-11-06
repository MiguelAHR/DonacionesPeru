package com.donaciones.servlets;

import com.donaciones.utils.DataManager;
import com.donaciones.models.Reporte;
import com.donaciones.models.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/reportes")
public class ReportServlet extends HttpServlet {

    private DataManager dataManager;

    @Override
    public void init() throws ServletException {
        dataManager = DataManager.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String userType = user.getUserType();
        String username = user.getUsername();
        
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "view";
        }
        
        try {
            switch (action) {
                case "view":
                    if ("admin".equals(userType)) {
                        generateAdminReports(request, dataManager);
                        request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);
                    } else {
                        generateEmployeeReports(request, dataManager, username);
                        request.getRequestDispatcher("/WEB-INF/views/empleado/reports_employee.jsp").forward(request, response);
                    }
                    break;
                case "generate":
                    generateReport(request, response, user);
                    break;
                case "list":
                    listReports(request, response, user);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar reportes");
        }
    }
    
    private void generateAdminReports(HttpServletRequest request, DataManager dm) {
        // Estadísticas generales para admin
        Map<String, Integer> stats = dm.getDashboardStats();
        request.setAttribute("totalDonations", stats.get("total_donations"));
        request.setAttribute("totalDonors", stats.get("total_donors"));
        request.setAttribute("totalReceivers", stats.get("total_receivers"));
        request.setAttribute("pendingDonations", stats.get("pending_donations"));
        request.setAttribute("pendingRequests", stats.get("pending_requests"));
        
        // Donaciones por tipo
        Map<String, Long> donationsByType = dm.getDonationsByType();
        request.setAttribute("donationsByType", donationsByType);
        
        // Donaciones por ubicación
        Map<String, Long> donationsByLocation = dm.getDonationsByLocation();
        request.setAttribute("donationsByLocation", donationsByLocation);
        
        // Donaciones por estado
        Map<String, Long> donationsByStatus = dm.getDonationsByStatusForReports();
        request.setAttribute("donationsByStatus", donationsByStatus);
        
        // Donaciones del último mes
        long recentDonations = dm.getRecentDonations();
        request.setAttribute("recentDonations", recentDonations);
        
        // Estadísticas del catálogo - ACTUALIZADO con nuevos nombres de métodos
        request.setAttribute("totalCatalogItems", dm.getTotalCatalogItemsCount());
        request.setAttribute("availableCatalogItems", dm.getAvailableCatalogItemsCount());
        request.setAttribute("assignedCatalogItems", dm.getAssignedCatalogItemsCount());
        request.setAttribute("deliveredCatalogItems", dm.getDeliveredCatalogItemsCount());
        
        // Estadísticas de solicitudes - ACTUALIZADO con nuevos nombres de métodos
        request.setAttribute("totalRequests", dm.getTotalRequestsCount());
        request.setAttribute("pendingRequestsCount", dm.getPendingRequestsCount());
        request.setAttribute("inProgressRequests", dm.getInProgressRequestsCount());
        request.setAttribute("completedRequests", dm.getCompletedRequestsCount());
        
        // Estadísticas de usuarios
        request.setAttribute("totalUsers", dm.getTotalUsers());
        request.setAttribute("totalAdmins", dm.getTotalAdmins());
        request.setAttribute("totalEmployees", dm.getTotalEmployees());
        request.setAttribute("totalRegularUsers", dm.getTotalRegularUsers());
    }
    
    private void generateEmployeeReports(HttpServletRequest request, DataManager dm, String employeeUsername) {
        // Reportes específicos para el empleado
        long myDonations = dm.getEmployeeDonations(employeeUsername);
        long activeDonations = dm.getEmployeeActiveDonations(employeeUsername);
        long completedDonations = dm.getEmployeeCompletedDonations(employeeUsername);
        
        // Donaciones del empleado por tipo
        Map<String, Long> myDonationsByType = dm.getEmployeeDonationsByType(employeeUsername);
        
        // Donaciones del empleado por ubicación
        Map<String, Long> myDonationsByLocation = dm.getEmployeeDonationsByLocation(employeeUsername);
        
        // Solicitudes del empleado
        int myRequests = dm.getRequestsByEmployee(employeeUsername).size();
        int myCompletedRequests = (int) dm.getRequestsByEmployee(employeeUsername).stream()
                .filter(req -> "completed".equals(req.getStatus()))
                .count();
        
        request.setAttribute("myDonations", myDonations);
        request.setAttribute("activeDonations", activeDonations);
        request.setAttribute("completedDonations", completedDonations);
        request.setAttribute("myDonationsByType", myDonationsByType);
        request.setAttribute("myDonationsByLocation", myDonationsByLocation);
        request.setAttribute("employeeUsername", employeeUsername);
        request.setAttribute("myRequests", myRequests);
        request.setAttribute("myCompletedRequests", myCompletedRequests);
        
        // Estadísticas generales (solo lectura para contexto)
        request.setAttribute("totalDonations", dm.getTotalDonations());
        request.setAttribute("totalDonors", dm.getTotalDonors());
        request.setAttribute("totalCatalogItems", dm.getTotalCatalogItemsCount());
        request.setAttribute("availableCatalogItems", dm.getAvailableCatalogItemsCount());
    }
    
    private void generateReport(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        // Lógica para generar un reporte específico y guardarlo en la base de datos
        String reportType = request.getParameter("type");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        
        Reporte reporte = new Reporte();
        reporte.setTitulo(title != null ? title : "Reporte de " + reportType);
        reporte.setDescripcion(description != null ? description : "Reporte generado automáticamente");
        reporte.setTipoReporte(reportType);
        reporte.setGeneradoPor(user.getUsername());
        reporte.setFormato("html"); // o pdf, excel, etc.
        
        // Parámetros del reporte
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("fechaInicio", request.getParameter("fechaInicio"));
        parametros.put("fechaFin", request.getParameter("fechaFin"));
        // Agregar más parámetros según sea necesario
        reporte.setParametros(parametros);
        
        // Guardar el reporte en la base de datos
        if (dataManager.addReport(reporte)) {
            request.setAttribute("mensaje", "Reporte generado exitosamente");
        } else {
            request.setAttribute("error", "Error al generar el reporte");
        }
        
        // Redirigir a la lista de reportes o a la vista de reportes
        listReports(request, response, user);
    }
    
    private void listReports(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        // Obtener la lista de reportes generados por el usuario
        java.util.List<Reporte> reports = dataManager.getReportsByUser(user.getUsername());
        request.setAttribute("reports", reports);
        
        // Redirigir a la página de lista de reportes
        request.getRequestDispatcher("/WEB-INF/views/admin/listaReportes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}