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

public class DonationManagementServlet extends HttpServlet {

    private DonationDAO donationDAO;

    @Override
    public void init() throws ServletException {
        donationDAO = new DonationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isUserAuthorized(session, "admin")) {
            redirectToLogin(response, request.getContextPath());
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "edit":
                    handleEditDonation(request, response);
                    break;
                case "view":
                    handleViewDonation(request, response);
                    break;
                case "list":
                default:
                    handleListDonations(request, response);
                    break;
            }
        } catch (Exception e) {
            System.out.println("ERROR DonationManagementServlet - Error en GET: " + e.getMessage());
            e.printStackTrace();
            handleError(response, request.getContextPath() + "/donationManagement?error=server_error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isUserAuthorized(session, "admin")) {
            redirectToLogin(response, request.getContextPath());
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/donationManagement");
            return;
        }

        try {
            switch (action) {
                case "delete":
                    deleteDonation(request, response);
                    break;
                case "updateStatus":
                    updateDonationStatus(request, response);
                    break;
                case "assignEmployee":
                    assignEmployeeToDonation(request, response);
                    break;
                case "updateDonation":
                    updateDonation(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/donationManagement");
            }
        } catch (Exception e) {
            System.out.println("ERROR DonationManagementServlet - Error en POST: " + e.getMessage());
            e.printStackTrace();
            handleError(response, request.getContextPath() + "/donationManagement?error=server_error");
        }
    }

    // ========== MÉTODOS DE VERIFICACIÓN DE PERMISOS ==========
    private boolean isUserAuthorized(HttpSession session, String... allowedRoles) {
        if (session == null || session.getAttribute("username") == null) {
            return false;
        }

        String userType = (String) session.getAttribute("userType");
        if (userType == null) {
            return false;
        }

        for (String role : allowedRoles) {
            if (role.equals(userType)) {
                return true;
            }
        }
        return false;
    }

    // ========== MANEJADORES DE ACCIONES ==========
    private void handleListDonations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener parámetros de filtro
        String statusFilter = request.getParameter("status");
        String typeFilter = request.getParameter("type");
        String locationFilter = request.getParameter("location");

        System.out.println("DEBUG DonationManagementServlet - Filtros aplicados:");
        System.out.println("DEBUG - Status: " + statusFilter);
        System.out.println("DEBUG - Type: " + typeFilter);
        System.out.println("DEBUG - Location: " + locationFilter);

        List<Donation> donations;

        // Verificar si hay filtros activos
        boolean hasFilters = (statusFilter != null && !statusFilter.isEmpty()) ||
                           (typeFilter != null && !typeFilter.isEmpty()) ||
                           (locationFilter != null && !locationFilter.isEmpty());

        if (hasFilters) {
            // Usar método con filtros
            donations = donationDAO.getDonationsByFilters(statusFilter, typeFilter, locationFilter);
            System.out.println("DEBUG DonationManagementServlet - Usando filtros, donaciones encontradas: " + donations.size());
        } else {
            // Obtener todas las donaciones
            donations = donationDAO.getAllDonations();
            System.out.println("DEBUG DonationManagementServlet - Sin filtros, donaciones totales: " + donations.size());
        }

        // Pasar los filtros actuales a la vista para mantenerlos en el formulario
        request.setAttribute("currentStatus", statusFilter);
        request.setAttribute("currentType", typeFilter);
        request.setAttribute("currentLocation", locationFilter);
        request.setAttribute("donations", donations);
        
        request.getRequestDispatcher("/WEB-INF/views/admin/donation_management.jsp").forward(request, response);
    }

    private void handleEditDonation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Donation donation = donationDAO.getDonation(id);

            if (donation == null) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_found");
                return;
            }

            request.setAttribute("donation", donation);
            request.setAttribute("statuses", new String[]{"pending", "in_progress", "approved", "completed", "cancelled"});
            request.getRequestDispatcher("/WEB-INF/views/admin/edit_donation.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
        }
    }

    private void handleViewDonation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Donation donation = donationDAO.getDonation(id);

            if (donation == null) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_found");
                return;
            }

            request.setAttribute("donation", donation);
            request.getRequestDispatcher("/WEB-INF/views/admin/donation_details.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
        }
    }

    // ========== MÉTODOS POST CORREGIDOS ==========
    private void updateDonationStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            String newStatus = request.getParameter("status");

            if (!isValidStatus(newStatus)) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_status");
                return;
            }

            boolean success = donationDAO.updateDonationStatus(donationId, newStatus);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?success=status_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=update_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
        }
    }

    private void assignEmployeeToDonation(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            String employeeUsername = request.getParameter("employeeUsername");

            if (employeeUsername == null || employeeUsername.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_employee");
                return;
            }

            boolean success = donationDAO.assignDonationToEmployee(donationId, employeeUsername);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?success=employee_assigned");
            } else {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=assignment_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
        }
    }

    private void updateDonation(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            Donation existingDonation = donationDAO.getDonation(donationId);

            if (existingDonation == null) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_found");
                return;
            }

            existingDonation.setType(request.getParameter("type"));
            existingDonation.setDescription(request.getParameter("description"));
            existingDonation.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            existingDonation.setCondition(request.getParameter("condition"));
            existingDonation.setLocation(request.getParameter("location"));
            existingDonation.setAddress(request.getParameter("address"));

            String newStatus = request.getParameter("status");
            if (isValidStatus(newStatus)) {
                existingDonation.setStatus(newStatus);
            }

            boolean success = donationDAO.updateDonation(existingDonation);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?success=donation_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=update_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_data");
        }
    }

    // MÉTODO DELETE CORREGIDO CON MÁS LOGS
    private void deleteDonation(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        System.out.println("DEBUG DonationManagementServlet - Iniciando proceso de eliminación");
        
        try {
            String donationIdParam = request.getParameter("donationId");
            System.out.println("DEBUG DonationManagementServlet - Parámetro donationId recibido: " + donationIdParam);
            
            if (donationIdParam == null || donationIdParam.trim().isEmpty()) {
                System.out.println("ERROR DonationManagementServlet - Parámetro donationId está vacío");
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
                return;
            }
            
            int donationId = Integer.parseInt(donationIdParam);
            System.out.println("DEBUG DonationManagementServlet - ID a eliminar: " + donationId);
            
            // Verificar que la donación existe antes de intentar eliminar
            Donation donation = donationDAO.getDonation(donationId);
            if (donation == null) {
                System.out.println("ERROR DonationManagementServlet - Donación no encontrada: " + donationId);
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_found");
                return;
            }
            
            System.out.println("DEBUG DonationManagementServlet - Donación encontrada: " + donation.getDescription());
            
            boolean success = donationDAO.deleteDonation(donationId);
            
            if (success) {
                System.out.println("DEBUG DonationManagementServlet - Eliminación exitosa para ID: " + donationId);
                response.sendRedirect(request.getContextPath() + "/donationManagement?success=donation_deleted");
            } else {
                System.out.println("ERROR DonationManagementServlet - Eliminación fallida para ID: " + donationId);
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("ERROR DonationManagementServlet - Error parseando donationId: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
        } catch (Exception e) {
            System.out.println("ERROR DonationManagementServlet - Error inesperado: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=server_error");
        }
    }

    // ========== MÉTODOS DE UTILIDAD ==========
    private boolean isValidStatus(String status) {
        return status != null
                && (status.equals("pending") || status.equals("in_progress")
                || status.equals("approved") || status.equals("completed")
                || status.equals("cancelled"));
    }

    private void redirectToLogin(HttpServletResponse response, String contextPath) throws IOException {
        response.sendRedirect(contextPath + "/login");
    }

    private void handleError(HttpServletResponse response, String redirectUrl) throws IOException {
        response.sendRedirect(redirectUrl);
    }
}