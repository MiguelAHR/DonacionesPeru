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

public class DonationManagementServlet extends HttpServlet {

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
                case "addToCatalog":
                    handleAddToCatalog(request, response);
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
                case "addToCatalog":
                    addToCatalogManual(request, response);
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
            // Usar método con filtros del DataManager
            donations = dataManager.getDonationsByFilters(statusFilter, typeFilter, locationFilter);
            System.out.println("DEBUG DonationManagementServlet - Usando filtros, donaciones encontradas: " + donations.size());
        } else {
            // Obtener todas las donaciones
            donations = dataManager.getAllDonations();
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
            Donation donation = dataManager.getDonation(id);

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
            Donation donation = dataManager.getDonation(id);

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

    private void handleAddToCatalog(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Donation donation = dataManager.getDonation(id);

            if (donation == null) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_found");
                return;
            }

            if (!"completed".equals(donation.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_completed");
                return;
            }

            // Verificar si ya existe en el catálogo
            boolean alreadyInCatalog = checkIfInCatalog(donation.getId());
            request.setAttribute("donation", donation);
            request.setAttribute("alreadyInCatalog", alreadyInCatalog);
            
            request.getRequestDispatcher("/WEB-INF/views/admin/add_to_catalog.jsp").forward(request, response);

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

            // Obtener el estado anterior para verificar si estamos cambiando a "completed"
            Donation donation = dataManager.getDonation(donationId);
            String oldStatus = donation != null ? donation.getStatus() : "";

            boolean success = dataManager.updateDonationStatus(donationId, newStatus);

            if (success) {
                // ========== NUEVA FUNCIONALIDAD: AGREGAR AL CATÁLOGO AUTOMÁTICAMENTE ==========
                if ("completed".equals(newStatus) && !"completed".equals(oldStatus)) {
                    addToCatalogAutomatically(donationId);
                }
                
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

            boolean success = dataManager.assignDonationToEmployee(donationId, employeeUsername);

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
            Donation existingDonation = dataManager.getDonation(donationId);

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
            String oldStatus = existingDonation.getStatus();
            
            if (isValidStatus(newStatus)) {
                existingDonation.setStatus(newStatus);
            }

            boolean success = dataManager.updateDonation(existingDonation);

            if (success) {
                // ========== NUEVA FUNCIONALIDAD: AGREGAR AL CATÁLOGO AUTOMÁTICAMENTE ==========
                if ("completed".equals(newStatus) && !"completed".equals(oldStatus)) {
                    addToCatalogAutomatically(donationId);
                }
                
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
            Donation donation = dataManager.getDonation(donationId);
            if (donation == null) {
                System.out.println("ERROR DonationManagementServlet - Donación no encontrada: " + donationId);
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_found");
                return;
            }
            
            System.out.println("DEBUG DonationManagementServlet - Donación encontrada: " + donation.getDescription());
            
            boolean success = dataManager.deleteDonation(donationId);
            
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

    // ========== NUEVOS MÉTODOS PARA EL CATÁLOGO ==========
    
    private void addToCatalogManual(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int donationId = Integer.parseInt(request.getParameter("donationId"));
            Donation donation = dataManager.getDonation(donationId);
            
            if (donation == null) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_found");
                return;
            }
            
            if (!"completed".equals(donation.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=not_completed");
                return;
            }
            
            // Verificar si ya está en el catálogo
            if (checkIfInCatalog(donationId)) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=already_in_catalog");
                return;
            }
            
            boolean success = addToCatalogAutomatically(donationId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/donationManagement?success=added_to_catalog");
            } else {
                response.sendRedirect(request.getContextPath() + "/donationManagement?error=catalog_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/donationManagement?error=catalog_failed");
        }
    }
    
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