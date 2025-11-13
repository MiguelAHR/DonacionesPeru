package com.donaciones.servlets;

import com.donaciones.utils.DataManager;
import com.donaciones.models.Donation;
import com.donaciones.models.Catalogo;
import com.donaciones.dao.CatalogoDAO;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EmployeeDonationServlet extends HttpServlet {

    private DataManager dataManager;
    private CatalogoDAO catalogoDAO;

    @Override
    public void init() throws ServletException {
        dataManager = DataManager.getInstance();
        catalogoDAO = new CatalogoDAO();
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
        
        List<Donation> myDonations = dataManager.getDonationsByEmployee(username);
        System.out.println("DEBUG EmployeeDonationServlet - Mostrando " + myDonations.size() + " donaciones para empleado: " + username);
        
        request.setAttribute("donations", myDonations);
        request.setAttribute("viewType", "myDonations");
        
        request.getRequestDispatcher("/WEB-INF/views/empleado/donation_management.jsp").forward(request, response);
    }

    private void showAvailableDonations(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        // Obtener donaciones disponibles (pendientes y sin asignar)
        List<Donation> availableDonations = dataManager.getAvailableDonations();
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
            Donation donation = dataManager.getDonation(id);
            
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
            
            boolean success = dataManager.assignDonationToEmployee(donationId, username);
            
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
            Donation donation = dataManager.getDonation(donationId);
            if (donation == null || !username.equals(donation.getEmployeeUsername())) {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=no_permission");
                return;
            }

            // Obtener el estado anterior
            String oldStatus = donation.getStatus();

            // CORREGIDO: Usar el método del DataManager
            boolean success = dataManager.updateDonationStatusForEmployee(donationId, newStatus, username);
            
            if (success) {
                // ========== NUEVA FUNCIONALIDAD: AGREGAR AL CATÁLOGO AUTOMÁTICAMENTE ==========
                if ("completed".equals(newStatus) && !"completed".equals(oldStatus)) {
                    addToCatalogAutomatically(donationId);
                }
                
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
            Donation donation = dataManager.getDonation(donationId);
            if (donation == null || !username.equals(donation.getEmployeeUsername())) {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=no_permission");
                return;
            }

            // Obtener el estado anterior
            String oldStatus = donation.getStatus();

            // CORREGIDO: Usar el método del DataManager
            boolean success = dataManager.updateDonationStatusForEmployee(donationId, "completed", username);
            
            if (success) {
                // ========== NUEVA FUNCIONALIDAD: AGREGAR AL CATÁLOGO AUTOMÁTICAMENTE ==========
                if (!"completed".equals(oldStatus)) {
                    addToCatalogAutomatically(donationId);
                }
                
                response.sendRedirect(request.getContextPath() + "/employeeDonations?success=delivery_completed");
            } else {
                response.sendRedirect(request.getContextPath() + "/employeeDonations?error=completion_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/employeeDonations?error=invalid_id");
        }
    }

    // ========== NUEVOS MÉTODOS PARA EL CATÁLOGO ==========
    
    private boolean addToCatalogAutomatically(int donationId) {
        try {
            Donation donation = dataManager.getDonation(donationId);
            if (donation == null) {
                System.out.println("ERROR: Donación no encontrada para agregar al catálogo - ID: " + donationId);
                return false;
            }
            
            // Verificar si ya existe en el catálogo
            if (checkIfInCatalog(donationId)) {
                System.out.println("INFO: Donación ya existe en el catálogo - ID: " + donationId);
                return true;
            }
            
            Catalogo catalogoItem = new Catalogo();
            
            // Mapear datos de la donación al catálogo
            catalogoItem.setDonacionId(donation.getId());
            catalogoItem.setTitulo(generateCatalogTitle(donation.getType(), donation.getDescription()));
            catalogoItem.setDescripcion(donation.getDescription());
            catalogoItem.setTipo(donation.getType());
            catalogoItem.setCantidad(donation.getQuantity());
            catalogoItem.setCondicion(donation.getCondition());
            catalogoItem.setUbicacion(donation.getLocation());
            catalogoItem.setDonante(donation.getDonorUsername());
            
            // Fechas
            catalogoItem.setFechaIngreso(new Date());
            catalogoItem.setFechaDisponible(new Date()); // Disponible inmediatamente
            
            // Estado y prioridad
            catalogoItem.setEstado("disponible");
            catalogoItem.setPrioridad(calculatePriority(donation.getType(), donation.getCondition()));
            
            // Imagen por defecto según el tipo
            catalogoItem.setImagen(getDefaultImage(donation.getType()));
            
            // Tags basados en el tipo y condición
            catalogoItem.setTagsFromString(generateTags(donation.getType(), donation.getCondition()));
            
            // Agregar al catálogo
            boolean success = catalogoDAO.addCatalogoItem(catalogoItem);
            
            if (success) {
                System.out.println("✅ Donación ID " + donation.getId() + " agregada al catálogo exitosamente");
                return true;
            } else {
                System.out.println("❌ Error al agregar donación ID " + donation.getId() + " al catálogo");
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("💥 Error crítico al agregar al catálogo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean checkIfInCatalog(int donationId) {
        try {
            // Buscar si ya existe un ítem en el catálogo con este donationId
            List<Catalogo> catalogItems = catalogoDAO.getAllCatalogItems();
            for (Catalogo item : catalogItems) {
                if (item.getDonacionId() == donationId) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("ERROR al verificar catálogo: " + e.getMessage());
            return false;
        }
    }
    
    // ========== MÉTODOS AUXILIARES PARA EL CATÁLOGO ==========
    
    private String generateCatalogTitle(String type, String description) {
        String baseTitle;
        switch (type) {
            case "ropa": baseTitle = "Ropa en buen estado"; break;
            case "cuadernos": baseTitle = "Cuadernos escolares"; break;
            case "utiles_escolares": baseTitle = "Útiles escolares"; break;
            case "material_reciclable": baseTitle = "Material reciclable"; break;
            case "ropa_casi_nueva": baseTitle = "Ropa casi nueva"; break;
            case "otros": baseTitle = "Artículo donado"; break;
            default: baseTitle = "Donación disponible"; break;
        }
        
        // Acortar la descripción para el título si es muy larga
        if (description != null && description.length() > 30) {
            return baseTitle + " - " + description.substring(0, 30) + "...";
        } else if (description != null && !description.trim().isEmpty()) {
            return baseTitle + " - " + description;
        } else {
            return baseTitle;
        }
    }
    
    private int calculatePriority(String type, String condition) {
        int priority = 3; // Prioridad media por defecto
        
        // Prioridad alta para útiles escolares y ropa en buen estado
        if ("utiles_escolares".equals(type) || "ropa_casi_nueva".equals(type)) {
            priority = 1;
        } 
        // Prioridad media para ropa regular y cuadernos
        else if ("ropa".equals(type) || "cuadernos".equals(type)) {
            priority = 2;
        }
        
        // Ajustar prioridad según condición
        if ("excelente".equals(condition) || "nuevo".equals(condition)) {
            priority = Math.max(1, priority - 1);
        } else if ("regular".equals(condition)) {
            priority = Math.min(3, priority + 1);
        }
        
        return priority;
    }
    
    private String getDefaultImage(String type) {
        switch (type) {
            case "ropa": return "/images/ropa-default.jpg";
            case "cuadernos": return "/images/cuadernos-default.jpg";
            case "utiles_escolares": return "/images/utiles-default.jpg";
            case "material_reciclable": return "/images/reciclable-default.jpg";
            case "ropa_casi_nueva": return "/images/ropa-nueva-default.jpg";
            default: return "/images/donacion-default.jpg";
        }
    }
    
    private String generateTags(String type, String condition) {
        List<String> tags = new ArrayList<>();
        
        // Tags por tipo
        switch (type) {
            case "ropa":
                tags.add("ropa");
                tags.add("vestimenta");
                tags.add("textil");
                break;
            case "cuadernos":
                tags.add("cuadernos");
                tags.add("escolar");
                tags.add("educacion");
                break;
            case "utiles_escolares":
                tags.add("utiles");
                tags.add("escolar");
                tags.add("educacion");
                tags.add("material");
                break;
            case "material_reciclable":
                tags.add("reciclable");
                tags.add("medio ambiente");
                tags.add("sostenible");
                break;
            case "ropa_casi_nueva":
                tags.add("ropa");
                tags.add("nueva");
                tags.add("calidad");
                break;
        }
        
        // Tags por condición
        if ("excelente".equals(condition) || "nuevo".equals(condition)) {
            tags.add("excelente estado");
            tags.add("calidad");
        } else if ("bueno".equals(condition)) {
            tags.add("buen estado");
        } else if ("regular".equals(condition)) {
            tags.add("estado regular");
        }
        
        tags.add("donacion");
        tags.add("solidaridad");
        
        return String.join(",", tags);
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