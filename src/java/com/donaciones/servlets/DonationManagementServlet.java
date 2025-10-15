package com.donaciones.servlets;

import com.donaciones.models.Donation;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DonationManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userType = (String) session.getAttribute("userType");
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        DataManager dm = DataManager.getInstance();
        
        switch (action) {
            case "edit":
                String idStr = request.getParameter("id");
                if (idStr != null) {
                    try {
                        int id = Integer.parseInt(idStr);
                        Donation donation = dm.getDonation(id);
                        if (donation != null) {
                            request.setAttribute("donation", donation);
                            request.getRequestDispatcher("/WEB-INF/views/admin/edit_donation.jsp").forward(request, response);
                        } else {
                            response.sendRedirect(request.getContextPath() + "/donationManagement?error=notfound");
                        }
                    } catch (NumberFormatException e) {
                        response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid");
                    }
                }
                break;
                
            case "list":
            default:
                request.setAttribute("donations", dm.getAllDonations());
                request.getRequestDispatcher("/WEB-INF/views/admin/donation_management.jsp").forward(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userType = (String) session.getAttribute("userType");
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "update";
        
        DataManager dm = DataManager.getInstance();
        
        switch (action) {
            case "updateStatus":
                updateDonationStatus(request, response, dm);
                break;
            case "assignEmployee":
                assignEmployeeToDonation(request, response, dm);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/donationManagement");
        }
    }
    
    private void updateDonationStatus(HttpServletRequest request, HttpServletResponse response, DataManager dm)
            throws IOException {
        
        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            String newStatus = request.getParameter("status");
            String employeeUsername = (String) request.getSession().getAttribute("username");
            
            Donation donation = dm.getDonation(donationId);
            if (donation != null) {
                donation.setStatus(newStatus);
                donation.setEmployeeUsername(employeeUsername); // CORREGIDO: era setEmployeeAssigned
                dm.updateDonation(donation); // CORREGIDO: llamar al método update
                
                response.sendRedirect(request.getContextPath() + "/donationManagement?success=status_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=notfound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid");
        }
    }
    
    private void assignEmployeeToDonation(HttpServletRequest request, HttpServletResponse response, DataManager dm)
            throws IOException {
        
        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            String employeeUsername = request.getParameter("employeeUsername");
            
            Donation donation = dm.getDonation(donationId);
            if (donation != null) {
                donation.setEmployeeUsername(employeeUsername); // CORREGIDO: era setEmployeeAssigned
                dm.updateDonation(donation); // CORREGIDO: llamar al método update
                
                response.sendRedirect(request.getContextPath() + "/donationManagement?success=employee_assigned");
            } else {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=notfound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid");
        }
    }
}