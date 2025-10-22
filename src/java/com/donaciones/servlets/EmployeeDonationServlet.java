package com.donaciones.servlets;

import com.donaciones.dao.DonationDAO;
import com.donaciones.models.Donation;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EmployeeDonationServlet extends HttpServlet {

    private DonationDAO donationDAO;

    @Override
    public void init() throws ServletException {
        donationDAO = new DonationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isUserAuthorized(session, "empleado")) {
            redirectToLogin(response, request.getContextPath());
            return;
        }

        String username = (String) session.getAttribute("username");
        String action = request.getParameter("action");
        if (action == null) action = "myDonations";

        try {
            switch (action) {
                case "myDonations":
                    showMyDonations(request, response, username);
                    break;
                case "availableDonations":
                    showAvailableDonations(request, response, username);
                    break;
                case "view":
                    viewDonation(request, response, username);
                    break;
                default:
                    showMyDonations(request, response, username);
                    break;
            }
        } catch (Exception e) {
            System.out.println("ERROR EmployeeDonationServlet - Error en GET: " + e.getMessage());
            e.printStackTrace();
            handleError(response, request.getContextPath() + "/employeeDonations?error=server_error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isUserAuthorized(session, "empleado")) {
            redirectToLogin(response, request.getContextPath());
            return;
        }

        String username = (String) session.getAttribute("username");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/employeeDonations");
            return;
        }

        try {
            switch (action) {
                case "assignToMe":
                    assignDonationToMe(request, response, username);
                    break;
                case "updateStatus":
                    updateDonationStatus(request, response, username);
                    break;
                case "completeDelivery":
                    completeDelivery(request, response, username);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/employeeDonations");
            }
        } catch (Exception e) {
            System.out.println("ERROR EmployeeDonationServlet - Error en POST: " + e.getMessage());
            e.printStackTrace();
            handleError(response, request.getContextPath() + "/employeeDonations?error=server_error");
        }
    }

    // ========== MÉTODOS PRINCIPALES ==========

    private void showMyDonations(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        List<Donation> myDonations = donationDAO.getDonationsByEmployee(username);
        System.out.println("DEBUG EmployeeDonationServlet - Mostrando " + myDonations.size() + " donaciones para empleado: " + username);
        
        request.setAttribute("donations", myDonations);
        request.setAttribute("viewType", "myDonations");
        
        request.getRequestDispatcher("/WEB-INF/views/empleado/donation_management.jsp").forward(request, response);
    }

    private void showAvailableDonations(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        // Obtener donaciones disponibles (pendientes y sin asignar)
        List<Donation> availableDonations = donationDAO.getAvailableDonations();
        System.out.println("DEBUG EmployeeDonationServlet - Mostrando " + availableDonations.size() + " donaciones disponibles");
        
        request.setAttribute("donations", availableDonations);
        request.setAttribute("viewType", "availableDonations");
        
        request.getRequestDispatcher("/WEB-INF/views/empleado/donation_management.jsp").forward(request, response);
    }

    private void viewDonation(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/employeeDonations?error=invalid_id");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Donation donation = donationDAO.getDonation(id);
            
            if (donation == null) {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=not_found");
                return;
            }

            request.setAttribute("donation", donation);
            request.getRequestDispatcher("/WEB-INF/views/empleado/donation_details.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/employeeDonations?error=invalid_id");
        }
    }

    // ========== MÉTODOS POST CORREGIDOS ==========

    private void assignDonationToMe(HttpServletRequest request, HttpServletResponse response, String username)
            throws IOException {
        
        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            System.out.println("DEBUG EmployeeDonationServlet - Asignando donación " + donationId + " a empleado: " + username);
            
            boolean success = donationDAO.assignDonationToEmployee(donationId, username);
            
            if (success) {
                System.out.println("DEBUG EmployeeDonationServlet - Donación asignada exitosamente");
                response.sendRedirect(request.getContextPath() + "/employeeDonations?success=assigned");
            } else {
                System.out.println("DEBUG EmployeeDonationServlet - Error al asignar donación");
                response.sendRedirect(request.getContextPath() + "/employeeDonations?action=availableDonations&error=assignment_failed");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("ERROR EmployeeDonationServlet - ID inválido: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/employeeDonations?error=invalid_id");
        } catch (Exception e) {
            System.out.println("ERROR EmployeeDonationServlet - Error inesperado: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/employeeDonations?action=availableDonations&error=assignment_failed");
        }
    }

    private void updateDonationStatus(HttpServletRequest request, HttpServletResponse response, String username)
            throws IOException {
        
        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            String newStatus = request.getParameter("status");
            
            System.out.println("DEBUG EmployeeDonationServlet - Actualizando estado de donación " + donationId + " a: " + newStatus);

            // Validar estados permitidos para empleado
            if (!isValidEmployeeStatus(newStatus)) {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=invalid_status");
                return;
            }

            // Verificar que la donación pertenece a este empleado
            Donation donation = donationDAO.getDonation(donationId);
            if (donation == null || !username.equals(donation.getEmployeeUsername())) {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=no_permission");
                return;
            }

            boolean success = donationDAO.updateDonationStatusForEmployee(donationId, newStatus, username);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?success=status_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/employeeDonations?error=invalid_id");
        }
    }

    private void completeDelivery(HttpServletRequest request, HttpServletResponse response, String username)
            throws IOException {
        
        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            
            System.out.println("DEBUG EmployeeDonationServlet - Completando entrega de donación " + donationId);

            // Verificar que la donación pertenece a este empleado
            Donation donation = donationDAO.getDonation(donationId);
            if (donation == null || !username.equals(donation.getEmployeeUsername())) {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=no_permission");
                return;
            }

            boolean success = donationDAO.updateDonationStatusForEmployee(donationId, "completed", username);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?success=delivery_completed");
            } else {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=completion_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/employeeDonations?error=invalid_id");
        }
    }

    // ========== MÉTODOS DE UTILIDAD ==========

    private boolean isUserAuthorized(HttpSession session, String... allowedRoles) {
        if (session == null || session.getAttribute("username") == null) {
            return false;
        }
        
        String userType = (String) session.getAttribute("userType");
        if (userType == null) return false;
        
        for (String role : allowedRoles) {
            if (role.equals(userType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidEmployeeStatus(String status) {
        return status != null && 
               (status.equals("in_progress") || status.equals("completed") || 
                status.equals("cancelled"));
    }

    private void redirectToLogin(HttpServletResponse response, String contextPath) throws IOException {
        response.sendRedirect(contextPath + "/login");
    }

    private void handleError(HttpServletResponse response, String redirectUrl) throws IOException {
        response.sendRedirect(redirectUrl);
    }
}