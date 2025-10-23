<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<%@page import="com.donaciones.dao.RequestDAO"%>
<%@page import="com.donaciones.dao.UserDAO"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Solicitudes - Empleado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .nav-tabs .nav-link {
            color: #495057;
            font-weight: 500;
        }
        .nav-tabs .nav-link.active {
            font-weight: 600;
            border-bottom: 3px solid #ffc107;
        }
        .tab-pane {
            padding-top: 1.5rem;
        }
        .stat-card {
            transition: transform 0.2s;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_empleado.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <h2 class="fw-bold mb-4">Gestión de Solicitudes</h2>

                    <!-- Mostrar mensajes de éxito o error -->
                    <% 
                        String success = request.getParameter("success");
                        String error = request.getParameter("error");
                        if (success != null) {
                    %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <%= getSuccessMessage(success) %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <%
                        }
                        if (error != null) {
                    %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <%= getErrorMessage(error) %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <%
                        }
                    %>

                    <!-- Pestañas para separar las solicitudes -->
                    <ul class="nav nav-tabs" id="requestsTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="assigned-tab" data-bs-toggle="tab" 
                                    data-bs-target="#assigned" type="button" role="tab" 
                                    aria-controls="assigned" aria-selected="true">
                                <i class="fas fa-list-check me-1"></i> Mis Solicitudes Asignadas
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="available-tab" data-bs-toggle="tab" 
                                    data-bs-target="#available" type="button" role="tab" 
                                    aria-controls="available" aria-selected="false">
                                <i class="fas fa-hand-paper me-1"></i> Solicitudes Disponibles
                            </button>
                        </li>
                    </ul>

                    <div class="tab-content" id="requestsTabsContent">
                        <!-- Pestaña 1: Mis Solicitudes Asignadas -->
                        <div class="tab-pane fade show active" id="assigned" role="tabpanel" aria-labelledby="assigned-tab">
                            <!-- Filtros para Mis Solicitudes -->
                            <div class="card mb-4">
                                <div class="card-header bg-warning text-dark">
                                    <h6 class="mb-0"><i class="fas fa-filter me-2"></i>Filtrar Mis Solicitudes</h6>
                                </div>
                                <div class="card-body">
                                    <form method="get" class="row g-3">
                                        <input type="hidden" name="action" value="list">
                                        <input type="hidden" name="tab" value="assigned">
                                        <div class="col-md-4">
                                            <label class="form-label fw-bold">Estado</label>
                                            <select name="status" class="form-select">
                                                <option value="">Todos los estados</option>
                                                <option value="pending" <%= "pending".equals(request.getParameter("status")) ? "selected" : "" %>>Pendiente</option>
                                                <option value="in_progress" <%= "in_progress".equals(request.getParameter("status")) ? "selected" : "" %>>En Progreso</option>
                                                <option value="completed" <%= "completed".equals(request.getParameter("status")) ? "selected" : "" %>>Completado</option>
                                                <option value="cancelled" <%= "cancelled".equals(request.getParameter("status")) ? "selected" : "" %>>Cancelado</option>
                                            </select>
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label fw-bold">Tipo</label>
                                            <select name="type" class="form-select">
                                                <option value="">Todos los tipos</option>
                                                <option value="ropa" <%= "ropa".equals(request.getParameter("type")) ? "selected" : "" %>>Ropa</option>
                                                <option value="cuadernos" <%= "cuadernos".equals(request.getParameter("type")) ? "selected" : "" %>>Cuadernos</option>
                                                <option value="utiles_escolares" <%= "utiles_escolares".equals(request.getParameter("type")) ? "selected" : "" %>>Útiles Escolares</option>
                                                <option value="material_reciclable" <%= "material_reciclable".equals(request.getParameter("type")) ? "selected" : "" %>>Material Reciclable</option>
                                                <option value="ropa_casi_nueva" <%= "ropa_casi_nueva".equals(request.getParameter("type")) ? "selected" : "" %>>Ropa Casi Nueva</option>
                                                <option value="otros" <%= "otros".equals(request.getParameter("type")) ? "selected" : "" %>>Otros</option>
                                            </select>
                                        </div>
                                        <div class="col-md-4 d-flex align-items-end">
                                            <button type="submit" class="btn btn-warning me-2">
                                                <i class="fas fa-search me-1"></i> Filtrar
                                            </button>
                                            <a href="requestManagement?action=list&tab=assigned" class="btn btn-outline-secondary">
                                                <i class="fas fa-times me-1"></i> Limpiar
                                            </a>
                                        </div>
                                    </form>
                                    
                                    <!-- Mostrar filtros activos -->
                                    <%
                                        String currentStatus = request.getParameter("status");
                                        String currentType = request.getParameter("type");
                                        
                                        boolean hasActiveFilters = (currentStatus != null && !currentStatus.isEmpty()) ||
                                                                 (currentType != null && !currentType.isEmpty());
                                        
                                        if (hasActiveFilters) {
                                    %>
                                    <div class="mt-3">
                                        <small class="text-muted">Filtros activos:</small>
                                        <div class="d-flex flex-wrap gap-2 mt-1">
                                            <%
                                                if (currentStatus != null && !currentStatus.isEmpty()) {
                                            %>
                                            <span class="badge bg-primary">
                                                Estado: <%= getStatusText(currentStatus) %>
                                                <a href="<%= removeFilter(request, "status") %>" class="text-white ms-1">
                                                    <i class="fas fa-times"></i>
                                                </a>
                                            </span>
                                            <%
                                                }
                                                if (currentType != null && !currentType.isEmpty()) {
                                            %>
                                            <span class="badge bg-success">
                                                Tipo: <%= getTypeText(currentType) %>
                                                <a href="<%= removeFilter(request, "type") %>" class="text-white ms-1">
                                                    <i class="fas fa-times"></i>
                                                </a>
                                            </span>
                                            <%
                                                }
                                            %>
                                        </div>
                                    </div>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>

                            <!-- Estadísticas de Mis Solicitudes -->
                            <div class="row mb-4">
                                <%
                                    RequestDAO requestDAO = new RequestDAO();
                                    String username = (String) session.getAttribute("username");
                                    List<Request> currentRequests = (List<Request>) request.getAttribute("requests");
                                    if (currentRequests == null) {
                                        currentRequests = requestDAO.getRequestsByEmployee(username);
                                    }
                                    int totalRequests = currentRequests.size();
                                    
                                    // Contar por estado para las estadísticas actuales
                                    int pendingCount = 0, inProgressCount = 0, completedCount = 0;
                                    for (Request req : currentRequests) {
                                        switch(req.getStatus()) {
                                            case "pending": pendingCount++; break;
                                            case "in_progress": inProgressCount++; break;
                                            case "completed": completedCount++; break;
                                        }
                                    }
                                %>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-warning text-dark">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= totalRequests %></h4>
                                            <small>Total Asignadas</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-primary text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= pendingCount %></h4>
                                            <small>Pendientes</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-info text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= inProgressCount %></h4>
                                            <small>En Progreso</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-success text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= completedCount %></h4>
                                            <small>Completadas</small>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Tabla de Solicitudes del Empleado -->
                            <div class="card">
                                <div class="card-header bg-warning text-dark d-flex justify-content-between align-items-center">
                                    <div>
                                        <h5 class="mb-0"><i class="fas fa-list me-2"></i>Mis Solicitudes Asignadas</h5>
                                        <small>
                                            <%
                                                if (hasActiveFilters) {
                                                    out.print("Mostrando resultados filtrados");
                                                } else {
                                                    out.print("Mostrando todas mis solicitudes");
                                                }
                                            %>
                                        </small>
                                    </div>
                                    <span class="badge bg-dark">
                                        <%= totalRequests %> solicitud<%= totalRequests != 1 ? "es" : "" %>
                                    </span>
                                </div>
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead class="table-warning">
                                                <tr>
                                                    <th>ID</th>
                                                    <th>Tipo</th>
                                                    <th>Descripción</th>
                                                    <th>Solicitante</th>
                                                    <th>Ubicación</th>
                                                    <th>Prioridad</th>
                                                    <th>Estado</th>
                                                    <th>Fecha Solicitud</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% 
                                                    List<Request> requests = (List<Request>) request.getAttribute("requests");
                                                    if (requests == null) {
                                                        requests = requestDAO.getRequestsByEmployee(username);
                                                    }
                                                    
                                                    if (requests != null && !requests.isEmpty()) {
                                                        for (Request req : requests) {
                                                %>
                                                <tr>
                                                    <td><strong>#<%= req.getId() %></strong></td>
                                                    <td>
                                                        <span class="badge bg-info">
                                                            <%= getTypeText(req.getType()) %>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <div class="text-truncate" style="max-width: 200px;" 
                                                             title="<%= req.getDescription() %>">
                                                            <%= req.getDescription() %>
                                                        </div>
                                                    </td>
                                                    <td><%= req.getRequestedBy() %></td>
                                                    <td><%= req.getLocation() %></td>
                                                    <td>
                                                        <span class="badge <%= req.getPriorityBadge() %>">
                                                            <%= req.getPriorityText() %>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-<%= req.getStatusBadgeColor() %>">
                                                            <%= req.getFormattedStatus() %>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <small class="text-muted">
                                                            <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(req.getRequestDate()) %>
                                                        </small>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group btn-group-sm">
                                                            <!-- Botón para cambiar estado -->
                                                            <button class="btn btn-outline-warning" 
                                                                    onclick="showStatusModal(<%= req.getId() %>, '<%= req.getStatus() %>')"
                                                                    title="Cambiar estado">
                                                                <i class="fas fa-edit"></i>
                                                            </button>
                                                            
                                                            <!-- Botón para ver detalles -->
                                                            <button class="btn btn-outline-info" 
                                                                    onclick="showDetails(<%= req.getId() %>)"
                                                                    title="Ver detalles">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                            
                                                            <!-- Botón para completar rápido -->
                                                            <button class="btn btn-outline-success" 
                                                                    onclick="completeRequest(<%= req.getId() %>)"
                                                                    title="Marcar como completada">
                                                                <i class="fas fa-check"></i>
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <% 
                                                        }
                                                    } else {
                                                %>
                                                <tr>
                                                    <td colspan="9" class="text-center py-4">
                                                        <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                                        <h5 class="text-muted">No hay solicitudes asignadas</h5>
                                                        <%
                                                            if (hasActiveFilters) {
                                                        %>
                                                        <p class="text-muted">Intenta con otros criterios de filtro</p>
                                                        <a href="requestManagement?action=list&tab=assigned" class="btn btn-warning mt-2">
                                                            <i class="fas fa-times me-1"></i> Limpiar Filtros
                                                        </a>
                                                        <%
                                                            }
                                                        %>
                                                    </td>
                                                </tr>
                                                <% } %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Pestaña 2: Solicitudes Disponibles -->
                        <div class="tab-pane fade" id="available" role="tabpanel" aria-labelledby="available-tab">
                            <!-- Estadísticas de Solicitudes Disponibles -->
                            <div class="row mb-4">
                                <%
                                    List<Request> availableRequests = requestDAO.getAvailableRequests();
                                    int availableCount = availableRequests != null ? availableRequests.size() : 0;
                                    
                                    // Contar por tipo para estadísticas
                                    int ropaCount = 0, cuadernosCount = 0, utilesCount = 0, reciclableCount = 0, ropaNuevaCount = 0, otrosCount = 0;
                                    if (availableRequests != null) {
                                        for (Request req : availableRequests) {
                                            switch(req.getType()) {
                                                case "ropa": ropaCount++; break;
                                                case "cuadernos": cuadernosCount++; break;
                                                case "utiles_escolares": utilesCount++; break;
                                                case "material_reciclable": reciclableCount++; break;
                                                case "ropa_casi_nueva": ropaNuevaCount++; break;
                                                case "otros": otrosCount++; break;
                                            }
                                        }
                                    }
                                %>
                                <div class="col-md-4">
                                    <div class="card stat-card bg-success text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= availableCount %></h4>
                                            <small>Total Disponibles</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="card stat-card bg-info text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= ropaCount + ropaNuevaCount %></h4>
                                            <small>Solicitudes de Ropa</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="card stat-card bg-primary text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= cuadernosCount + utilesCount %></h4>
                                            <small>Material Escolar</small>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Tabla de Solicitudes Disponibles -->
                            <div class="card">
                                <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                                    <div>
                                        <h5 class="mb-0"><i class="fas fa-hand-paper me-2"></i>Solicitudes Disponibles</h5>
                                        <small>Puedes tomar estas solicitudes para gestionarlas</small>
                                    </div>
                                    <span class="badge bg-dark">
                                        <%= availableCount %> solicitud<%= availableCount != 1 ? "es" : "" %> disponible<%= availableCount != 1 ? "s" : "" %>
                                    </span>
                                </div>
                                <div class="card-body">
                                    <%
                                        if (availableRequests != null && !availableRequests.isEmpty()) {
                                    %>
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead class="table-success">
                                                <tr>
                                                    <th>ID</th>
                                                    <th>Tipo</th>
                                                    <th>Descripción</th>
                                                    <th>Solicitante</th>
                                                    <th>Ubicación</th>
                                                    <th>Prioridad</th>
                                                    <th>Fecha Solicitud</th>
                                                    <th>Acción</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% for (Request req : availableRequests) { %>
                                                <tr>
                                                    <td><strong>#<%= req.getId() %></strong></td>
                                                    <td>
                                                        <span class="badge bg-info">
                                                            <%= getTypeText(req.getType()) %>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <div class="text-truncate" style="max-width: 200px;" 
                                                             title="<%= req.getDescription() %>">
                                                            <%= req.getDescription() %>
                                                        </div>
                                                    </td>
                                                    <td><%= req.getRequestedBy() %></td>
                                                    <td><%= req.getLocation() %></td>
                                                    <td>
                                                        <span class="badge <%= req.getPriorityBadge() %>">
                                                            <%= req.getPriorityText() %>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <small class="text-muted">
                                                            <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(req.getRequestDate()) %>
                                                        </small>
                                                    </td>
                                                    <td>
                                                        <button class="btn btn-success btn-sm" 
                                                                onclick="takeRequest(<%= req.getId() %>)"
                                                                title="Tomar esta solicitud">
                                                            <i class="fas fa-hand-paper me-1"></i> Tomar
                                                        </button>
                                                    </td>
                                                </tr>
                                                <% } %>
                                            </tbody>
                                        </table>
                                    </div>
                                    <% } else { %>
                                    <div class="text-center py-5">
                                        <i class="fas fa-check-circle fa-4x text-success mb-3"></i>
                                        <h5 class="text-success">¡Excelente trabajo!</h5>
                                        <p class="text-muted">No hay solicitudes disponibles en este momento.</p>
                                        <p class="text-muted">Todas las solicitudes han sido asignadas.</p>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal para cambiar estado (para empleados) -->
    <div class="modal fade" id="statusModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="requestManagement" method="post">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="requestId" id="statusRequestId">

                    <div class="modal-header">
                        <h5 class="modal-title">Cambiar Estado de Solicitud</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Nuevo Estado</label>
                            <select name="status" class="form-select" id="statusSelect" required>
                                <option value="pending">Pendiente</option>
                                <option value="in_progress">En Progreso</option>
                                <option value="completed">Completado</option>
                                <option value="cancelled">Cancelado</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-warning">Actualizar Estado</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Activar la pestaña correcta según el parámetro de URL
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const tabParam = urlParams.get('tab');
            
            if (tabParam === 'available') {
                const availableTab = new bootstrap.Tab(document.getElementById('available-tab'));
                availableTab.show();
            } else {
                const assignedTab = new bootstrap.Tab(document.getElementById('assigned-tab'));
                assignedTab.show();
            }
        });
        
        function showStatusModal(requestId, currentStatus) {
            console.log("DEBUG - Estado modal para solicitud:", requestId, "Estado actual:", currentStatus);
            document.getElementById('statusRequestId').value = requestId;
            document.getElementById('statusSelect').value = currentStatus;
            new bootstrap.Modal(document.getElementById('statusModal')).show();
        }
        
        function showDetails(requestId) {
            console.log("DEBUG - Ver detalles de solicitud:", requestId);
            window.location.href = 'requestManagement?action=view&id=' + requestId;
        }

        function completeRequest(requestId) {
            if (confirm('¿Estás seguro de que deseas marcar esta solicitud como completada?')) {
                // Crear un formulario dinámico para enviar la solicitud de completado
                const form = document.createElement('form');
                form.method = 'post';
                form.action = 'requestManagement';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'updateStatus';
                form.appendChild(actionInput);
                
                const requestIdInput = document.createElement('input');
                requestIdInput.type = 'hidden';
                requestIdInput.name = 'requestId';
                requestIdInput.value = requestId;
                form.appendChild(requestIdInput);
                
                const statusInput = document.createElement('input');
                statusInput.type = 'hidden';
                statusInput.name = 'status';
                statusInput.value = 'completed';
                form.appendChild(statusInput);
                
                document.body.appendChild(form);
                form.submit();
            }
        }

        function takeRequest(requestId) {
            if (confirm('¿Estás seguro de que deseas tomar esta solicitud?')) {
                // Crear un formulario dinámico para tomar la solicitud
                const form = document.createElement('form');
                form.method = 'post';
                form.action = 'requestManagement';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'takeRequest';
                form.appendChild(actionInput);
                
                const requestIdInput = document.createElement('input');
                requestIdInput.type = 'hidden';
                requestIdInput.name = 'requestId';
                requestIdInput.value = requestId;
                form.appendChild(requestIdInput);
                
                // Agregar parámetro para mantener la pestaña activa
                const tabInput = document.createElement('input');
                tabInput.type = 'hidden';
                tabInput.name = 'tab';
                tabInput.value = 'available';
                form.appendChild(tabInput);
                
                document.body.appendChild(form);
                form.submit();
            }
        }
    </script>
</body>
</html>

<%!
    // Método para remover un filtro específico
    private String removeFilter(HttpServletRequest request, String filterToRemove) {
        StringBuilder url = new StringBuilder("requestManagement?action=list&tab=assigned");
        
        String status = request.getParameter("status");
        String type = request.getParameter("type");
        
        if (status != null && !status.isEmpty() && !filterToRemove.equals("status")) {
            url.append("&status=").append(status);
        }
        if (type != null && !type.isEmpty() && !filterToRemove.equals("type")) {
            url.append("&type=").append(type);
        }
        
        return url.toString();
    }

    // Método para obtener el color del badge según el estado
    private String getStatusBadgeColor(String status) {
        if (status == null) return "secondary";
        
        switch (status) {
            case "pending": return "warning";
            case "in_progress": return "info";
            case "completed": return "success";
            case "cancelled": return "danger";
            default: return "secondary";
        }
    }
    
    // Método para obtener el texto en español del estado
    private String getStatusText(String status) {
        if (status == null) return "Desconocido";
        
        switch (status) {
            case "pending": return "Pendiente";
            case "in_progress": return "En Progreso";
            case "completed": return "Completado";
            case "cancelled": return "Cancelado";
            default: return status;
        }
    }

    // Método para obtener el texto en español del tipo
    private String getTypeText(String type) {
        if (type == null) return "Desconocido";
        
        switch (type) {
            case "ropa": return "Ropa";
            case "cuadernos": return "Cuadernos";
            case "utiles_escolares": return "Útiles Escolares";
            case "material_reciclable": return "Material Reciclable";
            case "ropa_casi_nueva": return "Ropa Casi Nueva";
            case "otros": return "Otros";
            default: return type;
        }
    }

    // Método para obtener mensajes de éxito
    private String getSuccessMessage(String successType) {
        switch (successType) {
            case "status_updated":
                return "¡Estado actualizado exitosamente!";
            case "request_taken":
                return "¡Solicitud tomada exitosamente!";
            case "employee_assigned":
                return "¡Empleado asignado exitosamente!";
            case "request_updated":
                return "¡Solicitud actualizada exitosamente!";
            case "request_deleted":
                return "¡Solicitud eliminada exitosamente!";
            default:
                return "Operación completada exitosamente";
        }
    }

    // Método para obtener mensajes de error
    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case "invalid_id":
                return "ID de solicitud inválido";
            case "not_found":
                return "Solicitud no encontrada";
            case "no_permission":
                return "No tienes permisos para realizar esta acción";
            case "invalid_status":
                return "Estado inválido";
            case "update_failed":
                return "Error al actualizar la solicitud";
            case "assignment_failed":
                return "Error al asignar el empleado";
            case "take_failed":
                return "Error al tomar la solicitud";
            case "already_assigned":
                return "La solicitud ya está asignada a otro empleado";
            case "delete_failed":
                return "Error al eliminar la solicitud";
            case "invalid_data":
                return "Datos de solicitud inválidos";
            case "server_error":
                return "Error interno del servidor";
            case "invalid_employee":
                return "Nombre de empleado inválido";
            default:
                return "Ha ocurrido un error. Intenta nuevamente";
        }
    }
%>