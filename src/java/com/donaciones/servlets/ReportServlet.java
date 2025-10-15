package com.donaciones.servlets;

import com.donaciones.utils.DataManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ReportServlet extends HttpServlet {

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
        
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        DataManager dm = DataManager.getInstance();
        
        // Generar reportes según el tipo de usuario
        if ("admin".equals(userType)) {
            generateAdminReports(request, dm);
            request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);
        } else {
            generateEmployeeReports(request, dm, username);
            request.getRequestDispatcher("/WEB-INF/views/empleado/reports_employee.jsp").forward(request, response);
        }
    }
    
    private void generateAdminReports(HttpServletRequest request, DataManager dm) {
        // Estadísticas generales para admin
        request.setAttribute("totalDonations", dm.getTotalDonations());
        request.setAttribute("totalDonors", dm.getTotalDonors());
        request.setAttribute("totalReceivers", dm.getTotalReceivers());
        
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
        
        request.setAttribute("myDonations", myDonations);
        request.setAttribute("activeDonations", activeDonations);
        request.setAttribute("completedDonations", completedDonations);
        request.setAttribute("myDonationsByType", myDonationsByType);
        request.setAttribute("myDonationsByLocation", myDonationsByLocation);
        request.setAttribute("employeeUsername", employeeUsername);
        
        // Estadísticas generales (solo lectura para contexto)
        request.setAttribute("totalDonations", dm.getTotalDonations());
        request.setAttribute("totalDonors", dm.getTotalDonors());
    }
}