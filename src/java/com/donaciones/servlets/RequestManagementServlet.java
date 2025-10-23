package com.donaciones.servlets;

import com.donaciones.dao.RequestDAO;
import com.donaciones.models.Request;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestManagementServlet extends HttpServlet {

    private RequestDAO requestDAO;

    @Override
    public void init() throws ServletException {
        requestDAO = new RequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isUserAuthorized(session, "admin", "empleado")) {
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
                    handleEditRequest(request, response);
                    break;
                case "view":
                    handleViewRequest(request, response);
                    break;
                case "list":
                default:
                    handleListRequests(request, response);
                    break;
            }
        } catch (Exception e) {
            System.out.println("ERROR RequestManagementServlet - Error en GET: " + e.getMessage());
            e.printStackTrace();
            handleError(response, request.getContextPath() + "/requestManagement?error=server_error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isUserAuthorized(session, "admin", "empleado")) {
            redirectToLogin(response, request.getContextPath());
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/requestManagement");
            return;
        }

        try {
            switch (action) {
                case "delete":
                    deleteRequest(request, response);
                    break;
                case "updateStatus":
                    updateRequestStatus(request, response);
                    break;
                case "assignEmployee":
                    assignEmployeeToRequest(request, response);
                    break;
                case "updateRequest":
                    updateRequest(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/requestManagement");
            }
        } catch (Exception e) {
            System.out.println("ERROR RequestManagementServlet - Error en POST: " + e.getMessage());
            e.printStackTrace();
            handleError(response, request.getContextPath() + "/requestManagement?error=server_error");
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
    private void handleListRequests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener parámetros de filtro
        String statusFilter = request.getParameter("status");
        String typeFilter = request.getParameter("type");
        String locationFilter = request.getParameter("location");

        System.out.println("DEBUG RequestManagementServlet - Filtros aplicados:");
        System.out.println("DEBUG - Status: " + statusFilter);
        System.out.println("DEBUG - Type: " + typeFilter);
        System.out.println("DEBUG - Location: " + locationFilter);

        List<Request> requests;

        // Verificar si hay filtros activos
        boolean hasFilters = (statusFilter != null && !statusFilter.isEmpty()) ||
                           (typeFilter != null && !typeFilter.isEmpty()) ||
                           (locationFilter != null && !locationFilter.isEmpty());

        if (hasFilters) {
            // Usar método con filtros
            requests = requestDAO.getRequestsByFilters(statusFilter, typeFilter, locationFilter);
            System.out.println("DEBUG RequestManagementServlet - Usando filtros, solicitudes encontradas: " + requests.size());
        } else {
            // Obtener todas las solicitudes
            requests = requestDAO.getAllRequests();
            System.out.println("DEBUG RequestManagementServlet - Sin filtros, solicitudes totales: " + requests.size());
        }

        // Pasar los filtros actuales a la vista para mantenerlos en el formulario
        request.setAttribute("currentStatus", statusFilter);
        request.setAttribute("currentType", typeFilter);
        request.setAttribute("currentLocation", locationFilter);
        request.setAttribute("requests", requests);

        // Calcular estadísticas para la vista actual (filtrada o total)
        long pendingRequests = requests.stream().filter(req -> "pending".equals(req.getStatus())).count();
        long inProgressRequests = requests.stream().filter(req -> "in_progress".equals(req.getStatus())).count();

        request.setAttribute("pendingRequests", pendingRequests);
        request.setAttribute("inProgressRequests", inProgressRequests);

        HttpSession session = request.getSession(false);
        String userType = (String) session.getAttribute("userType");

        if ("admin".equals(userType)) {
            request.getRequestDispatcher("/WEB-INF/views/admin/request_management.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/empleado/request_management.jsp").forward(request, response);
        }
    }

    private void handleEditRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_id");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Request req = requestDAO.getRequest(id);

            if (req == null) {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=not_found");
                return;
            }

            request.setAttribute("request", req);
            request.setAttribute("statuses", new String[]{"pending", "in_progress", "completed", "cancelled"});
            request.getRequestDispatcher("/WEB-INF/views/admin/edit_request.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_id");
        }
    }

    private void handleViewRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_id");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Request req = requestDAO.getRequest(id);

            if (req == null) {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=not_found");
                return;
            }

            request.setAttribute("request", req);
            request.getRequestDispatcher("/WEB-INF/views/admin/request_details.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_id");
        }
    }

    // ========== MÉTODOS POST CORREGIDOS ==========
    private void updateRequestStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String newStatus = request.getParameter("status");

            if (!isValidStatus(newStatus)) {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_status");
                return;
            }

            boolean success = requestDAO.updateRequestStatus(requestId, newStatus);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/requestManagement?success=status_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=update_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_id");
        }
    }

    private void assignEmployeeToRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String employeeUsername = request.getParameter("employeeUsername");

            if (employeeUsername == null || employeeUsername.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_employee");
                return;
            }

            boolean success = requestDAO.assignRequestToEmployee(requestId, employeeUsername);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/requestManagement?success=employee_assigned");
            } else {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=assignment_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_id");
        }
    }

    private void updateRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            Request existingRequest = requestDAO.getRequest(requestId);

            if (existingRequest == null) {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=not_found");
                return;
            }

            // Actualizar campos
            existingRequest.setType(request.getParameter("type"));
            existingRequest.setDescription(request.getParameter("description"));
            existingRequest.setLocation(request.getParameter("location"));
            existingRequest.setPriority(Integer.parseInt(request.getParameter("priority")));

            String newStatus = request.getParameter("status");
            if (isValidStatus(newStatus)) {
                existingRequest.setStatus(newStatus);
            }

            boolean success = requestDAO.updateRequest(existingRequest);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/requestManagement?success=request_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=update_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_data");
        }
    }

    // MÉTODO DELETE CORREGIDO CON MÁS LOGS
    private void deleteRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        System.out.println("DEBUG RequestManagementServlet - Iniciando proceso de eliminación");
        
        try {
            String requestIdParam = request.getParameter("requestId");
            System.out.println("DEBUG RequestManagementServlet - Parámetro requestId recibido: " + requestIdParam);
            
            if (requestIdParam == null || requestIdParam.trim().isEmpty()) {
                System.out.println("ERROR RequestManagementServlet - Parámetro requestId está vacío");
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_id");
                return;
            }
            
            int requestId = Integer.parseInt(requestIdParam);
            System.out.println("DEBUG RequestManagementServlet - ID a eliminar: " + requestId);
            
            // Verificar que la solicitud existe antes de intentar eliminar
            Request req = requestDAO.getRequest(requestId);
            if (req == null) {
                System.out.println("ERROR RequestManagementServlet - Solicitud no encontrada: " + requestId);
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=not_found");
                return;
            }
            
            System.out.println("DEBUG RequestManagementServlet - Solicitud encontrada: " + req.getDescription());
            
            boolean success = requestDAO.deleteRequest(requestId);
            
            if (success) {
                System.out.println("DEBUG RequestManagementServlet - Eliminación exitosa para ID: " + requestId);
                response.sendRedirect(request.getContextPath() + "/requestManagement?success=request_deleted");
            } else {
                System.out.println("ERROR RequestManagementServlet - Eliminación fallida para ID: " + requestId);
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("ERROR RequestManagementServlet - Error parseando requestId: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid_id");
        } catch (Exception e) {
            System.out.println("ERROR RequestManagementServlet - Error inesperado: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=server_error");
        }
    }

    // ========== MÉTODOS DE UTILIDAD ==========
    private boolean isValidStatus(String status) {
        return status != null
                && (status.equals("pending") || status.equals("in_progress")
                || status.equals("completed") || status.equals("cancelled"));
    }

    private void redirectToLogin(HttpServletResponse response, String contextPath) throws IOException {
        response.sendRedirect(contextPath + "/login");
    }

    private void handleError(HttpServletResponse response, String redirectUrl) throws IOException {
        response.sendRedirect(redirectUrl);
    }
}