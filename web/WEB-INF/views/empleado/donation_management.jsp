<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Donaciones - Empleado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .card {
            border-radius: 10px;
            border: none;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .badge-status {
            font-size: 0.75rem;
            padding: 6px 12px;
            border-radius: 15px;
        }
        .table th {
            border-top: none;
            font-weight: 600;
            color: #495057;
        }
        .action-buttons .btn {
            margin: 2px;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_empleado.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="fw-bold mb-1">Gestión de Donaciones</h2>
                            <p class="text-muted mb-0">
                                <% 
                                    String viewType = (String) request.getAttribute("viewType");
                                    if ("myDonations".equals(viewType)) {
                                        out.print("Donaciones asignadas a ti para entrega");
                                    } else {
                                        out.print("Donaciones disponibles para asignarte");
                                    }
                                %>
                            </p>
                        </div>
                        <div class="btn-group">
                            <a href="employeeDonations?action=myDonations" 
                               class="btn <%= "myDonations".equals(viewType) ? "btn-primary" : "btn-outline-primary" %>">
                                <i class="fas fa-list me-2"></i>Mis Donaciones
                            </a>
                            <a href="employeeDonations?action=availableDonations" 
                               class="btn <%= "availableDonations".equals(viewType) ? "btn-primary" : "btn-outline-primary" %>">
                                <i class="fas fa-plus me-2"></i>Disponibles
                            </a>
                        </div>
                    </div>

                    <!-- Mostrar mensajes -->
                    <% 
                        String success = request.getParameter("success");
                        String error = request.getParameter("error");
                        if (success != null) {
                    %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            <%= getSuccessMessage(success) %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <%
                        }
                        if (error != null) {
                    %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <%= getErrorMessage(error) %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <%
                        }
                    %>

                    <!-- Estadísticas rápidas -->
                    <div class="row mb-4">
                        <div class="col-md-3">
                            <div class="card bg-primary text-white">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between">
                                        <div>
                                            <h4 class="mb-0">
                                                <% 
                                                    List<Donation> donations = (List<Donation>) request.getAttribute("donations");
                                                    out.print(donations != null ? donations.size() : 0);
                                                %>
                                            </h4>
                                            <small>Total</small>
                                        </div>
                                        <div class="align-self-center">
                                            <i class="fas fa-boxes fs-2"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card bg-warning text-dark">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between">
                                        <div>
                                            <h4 class="mb-0">
                                                <%
                                                    if (donations != null) {
                                                        long inProgress = donations.stream()
                                                            .filter(d -> "in_progress".equals(d.getStatus()))
                                                            .count();
                                                        out.print(inProgress);
                                                    } else {
                                                        out.print("0");
                                                    }
                                                %>
                                            </h4>
                                            <small>En Progreso</small>
                                        </div>
                                        <div class="align-self-center">
                                            <i class="fas fa-truck-loading fs-2"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card bg-success text-white">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between">
                                        <div>
                                            <h4 class="mb-0">
                                                <%
                                                    if (donations != null) {
                                                        long completed = donations.stream()
                                                            .filter(d -> "completed".equals(d.getStatus()))
                                                            .count();
                                                        out.print(completed);
                                                    } else {
                                                        out.print("0");
                                                    }
                                                %>
                                            </h4>
                                            <small>Completadas</small>
                                        </div>
                                        <div class="align-self-center">
                                            <i class="fas fa-check-circle fs-2"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Tabla de donaciones -->
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0">
                                <i class="fas fa-list me-2"></i>
                                <% 
                                    if ("myDonations".equals(viewType)) {
                                        out.print("Mis Donaciones Asignadas");
                                    } else {
                                        out.print("Donaciones Disponibles");
                                    }
                                %>
                            </h5>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Tipo</th>
                                            <th>Descripción</th>
                                            <th>Donador</th>
                                            <th>Ubicación</th>
                                            <th>Cantidad</th>
                                            <th>Estado</th>
                                            <th>Fecha</th>
                                            <th>Acciones</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% 
                                        if (donations != null && !donations.isEmpty()) {
                                            for (Donation donation : donations) {
                                        %>
                                        <tr>
                                            <td>#<%= donation.getId() %></td>
                                            <td><span class="badge bg-info"><%= getTypeText(donation.getType()) %></span></td>
                                            <td title="<%= donation.getDescription() %>">
                                                <%= donation.getDescription().length() > 50 ? 
                                                    donation.getDescription().substring(0, 50) + "..." : 
                                                    donation.getDescription() %>
                                            </td>
                                            <td><%= donation.getDonorUsername() %></td>
                                            <td><%= donation.getLocation() %></td>
                                            <td><span class="badge bg-secondary"><%= donation.getQuantity() %></span></td>
                                            <td>
                                                <span class="badge badge-status bg-<%= getStatusBadgeColor(donation.getStatus()) %>">
                                                    <%= getStatusText(donation.getStatus()) %>
                                                </span>
                                            </td>
                                            <td>
                                                <small class="text-muted">
                                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(donation.getCreatedDate()) %>
                                                </small>
                                            </td>
                                            <td>
                                                <div class="action-buttons">
                                                    <% if ("myDonations".equals(viewType)) { %>
                                                        <!-- Acciones para donaciones asignadas -->
                                                        <button class="btn btn-success btn-sm" 
                                                                onclick="completeDelivery(<%= donation.getId() %>)"
                                                                <%= "completed".equals(donation.getStatus()) ? "disabled" : "" %>
                                                                title="Marcar como completado">
                                                            <i class="fas fa-check"></i>
                                                        </button>
                                                        <button class="btn btn-warning btn-sm" 
                                                                onclick="showUpdateModal(<%= donation.getId() %>, '<%= donation.getStatus() %>')"
                                                                title="Cambiar estado">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                    <% } else { %>
                                                        <!-- Acciones para donaciones disponibles -->
                                                        <button class="btn btn-primary btn-sm" 
                                                                onclick="assignToMe(<%= donation.getId() %>)"
                                                                title="Asignarme esta donación">
                                                            <i class="fas fa-user-plus"></i> Asignar
                                                        </button>
                                                    <% } %>
                                                    <button class="btn btn-info btn-sm" 
                                                            onclick="viewDetails(<%= donation.getId() %>)"
                                                            title="Ver detalles">
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
                                                <p>
                                                    <% 
                                                        if ("myDonations".equals(viewType)) {
                                                            out.print("No tienes donaciones asignadas");
                                                        } else {
                                                            out.print("No hay donaciones disponibles para asignar");
                                                        }
                                                    %>
                                                </p>
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

    <!-- Modal para actualizar estado -->
    <div class="modal fade" id="updateModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="employeeDonations" method="post" id="updateForm">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="donationId" id="updateDonationId">

                    <div class="modal-header">
                        <h5 class="modal-title">Actualizar Estado de Entrega</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Nuevo Estado</label>
                            <select name="status" class="form-select" id="updateStatus" required>
                                <option value="in_progress">En Progreso</option>
                                <option value="completed">Completado</option>
                                <option value="cancelled">Cancelado</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Actualizar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Formulario oculto para asignaciones -->
    <form id="assignForm" method="post" action="employeeDonations" style="display: none;">
        <input type="hidden" name="action" value="assignToMe">
        <input type="hidden" name="donationId" id="assignDonationId">
    </form>

    <!-- Formulario oculto para completar entrega -->
    <form id="completeForm" method="post" action="employeeDonations" style="display: none;">
        <input type="hidden" name="action" value="completeDelivery">
        <input type="hidden" name="donationId" id="completeDonationId">
    </form>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function showUpdateModal(donationId, currentStatus) {
            document.getElementById('updateDonationId').value = donationId;
            document.getElementById('updateStatus').value = currentStatus;
            new bootstrap.Modal(document.getElementById('updateModal')).show();
        }

        function assignToMe(donationId) {
            if (confirm('¿Estás seguro de que quieres asignarte esta donación?\n\nSerás responsable de la entrega de esta donación.')) {
                document.getElementById('assignDonationId').value = donationId;
                document.getElementById('assignForm').submit();
            }
        }

        function completeDelivery(donationId) {
            if (confirm('¿Marcar esta donación como completada?\n\nEsta acción no se puede deshacer.')) {
                document.getElementById('completeDonationId').value = donationId;
                document.getElementById('completeForm').submit();
            }
        }

        function viewDetails(donationId) {
            window.location.href = 'employeeDonations?action=view&id=' + donationId;
        }
    </script>
</body>
</html>

<%!
    // Método para obtener el color del badge según el estado
    private String getStatusBadgeColor(String status) {
        if (status == null) return "secondary";
        
        switch (status) {
            case "pending": return "warning";
            case "in_progress": return "primary";
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
            case "assigned":
                return "¡Donación asignada exitosamente! Ahora eres responsable de esta entrega.";
            case "status_updated":
                return "¡Estado actualizado exitosamente!";
            case "delivery_completed":
                return "¡Entrega completada exitosamente!";
            default:
                return "Operación completada exitosamente";
        }
    }

    // Método para obtener mensajes de error
    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case "invalid_id":
                return "ID de donación inválido";
            case "not_found":
                return "Donación no encontrada";
            case "no_permission":
                return "No tienes permisos para realizar esta acción";
            case "invalid_status":
                return "Estado inválido";
            case "assignment_failed":
                return "Error al asignar la donación. Puede que ya haya sido asignada a otro empleado.";
            case "update_failed":
                return "Error al actualizar la donación";
            case "completion_failed":
                return "Error al completar la entrega";
            case "server_error":
                return "Error interno del servidor";
            default:
                return "Ha ocurrido un error. Intenta nuevamente";
        }
    }
%>