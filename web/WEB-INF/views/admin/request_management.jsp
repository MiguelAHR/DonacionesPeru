<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Solicitudes - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <h2 class="fw-bold mb-4">Gestión de Solicitudes</h2>

                    <!-- Estadísticas Rápidas -->
                    <div class="row g-4 mb-4">
                        <div class="col-md-3">
                            <div class="card text-white bg-warning">
                                <div class="card-body text-center">
                                    <i class="fas fa-clock fa-2x mb-2"></i>
                                    <h3>${pendingRequests}</h3>
                                    <p class="mb-0">Pendientes</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-info">
                                <div class="card-body text-center">
                                    <i class="fas fa-spinner fa-2x mb-2"></i>
                                    <h3>${inProgressRequests}</h3>
                                    <p class="mb-0">En Progreso</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-success">
                                <div class="card-body text-center">
                                    <i class="fas fa-check-circle fa-2x mb-2"></i>
                                    <h3>${requests.size() - pendingRequests - inProgressRequests}</h3>
                                    <p class="mb-0">Completadas</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-secondary">
                                <div class="card-body text-center">
                                    <i class="fas fa-list-alt fa-2x mb-2"></i>
                                    <h3>${requests.size()}</h3>
                                    <p class="mb-0">Total</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Tabla de Solicitudes -->
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0"><i class="fas fa-list me-2"></i>Lista de Solicitudes</h5>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Tipo</th>
                                            <th>Descripción</th>
                                            <th>Solicitante</th>
                                            <th>Ubicación</th>
                                            <th>Prioridad</th>
                                            <th>Estado</th>
                                            <th>Asignado a</th>
                                            <th>Acciones</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% 
                                        List<Request> requests = (List<Request>) request.getAttribute("requests");
                                        if (requests != null && !requests.isEmpty()) {
                                            for (Request req : requests) {
                                        %>
                                        <tr>
                                            <td>#<%= req.getId() %></td>
                                            <td><%= req.getType() %></td>
                                            <td title="<%= req.getDescription() %>">
                                                <%= req.getDescription().length() > 50 ? 
                                                    req.getDescription().substring(0, 50) + "..." : 
                                                    req.getDescription() %>
                                            </td>
                                            <td><%= req.getRequestedBy() %></td>
                                            <td><%= req.getLocation() %></td>
                                            <td>
                                                <span class="badge <%= req.getPriorityBadge() %>">
                                                    <%= req.getPriorityText() %>
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge bg-<%= 
                                                    "pending".equals(req.getStatus()) ? "warning" :
                                                    "in_progress".equals(req.getStatus()) ? "info" :
                                                    "completed".equals(req.getStatus()) ? "success" : "secondary" %>">
                                                    <%= req.getFormattedStatus() %>
                                                </span>
                                            </td>
                                            <td><%= req.getAssignedTo() != null ? req.getAssignedTo() : "Sin asignar" %></td>
                                            <td>
                                                <div class="btn-group btn-group-sm">
                                                    <a href="${pageContext.request.contextPath}/requestManagement?action=edit&id=<%= req.getId() %>" 
                                                       class="btn btn-outline-primary">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <button class="btn btn-outline-info" 
                                                            onclick="viewRequest(<%= req.getId() %>)">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                        <% 
                                            }
                                        } else {
                                        %>
                                        <tr>
                                            <td colspan="9" class="text-center text-muted py-4">
                                                <i class="fas fa-inbox fa-3x mb-3"></i>
                                                <p>No hay solicitudes registradas</p>
                                            </td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        function viewRequest(requestId) {
            // Implementar vista detallada
            alert('Vista detallada de solicitud #' + requestId);
        }
    </script>
</body>
</html>