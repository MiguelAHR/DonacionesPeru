<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<%@page import="com.donaciones.dao.DonationDAO"%>
<%@page import="com.donaciones.dao.UserDAO"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Donaciones - Empleado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .nav-tabs .nav-link {
            color: #495057;
            font-weight: 500;
        }
        .nav-tabs .nav-link.active {
            font-weight: 600;
            border-bottom: 3px solid #0d6efd;
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
        .card {
            border-radius: 10px;
            border: none;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .text-truncate-custom {
            max-width: 200px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_empleado.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <h2 class="fw-bold mb-4">Gestión de Donaciones</h2>

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

                    <!-- Pestañas para separar las donaciones -->
                    <ul class="nav nav-tabs" id="donationsTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="my-donations-tab" data-bs-toggle="tab" 
                                    data-bs-target="#my-donations" type="button" role="tab" 
                                    aria-controls="my-donations" aria-selected="true">
                                <i class="fas fa-list-check me-1"></i> Mis Donaciones Asignadas
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="available-donations-tab" data-bs-toggle="tab" 
                                    data-bs-target="#available-donations" type="button" role="tab" 
                                    aria-controls="available-donations" aria-selected="false">
                                <i class="fas fa-hand-paper me-1"></i> Donaciones Disponibles
                            </button>
                        </li>
                    </ul>

                    <div class="tab-content" id="donationsTabsContent">
                        <!-- Pestaña 1: Mis Donaciones Asignadas -->
                        <div class="tab-pane fade show active" id="my-donations" role="tabpanel" aria-labelledby="my-donations-tab">
                            <!-- Filtros para Mis Donaciones -->
                            <div class="card mb-4">
                                <div class="card-header bg-primary text-white">
                                    <h6 class="mb-0"><i class="fas fa-filter me-2"></i>Filtrar Mis Donaciones</h6>
                                </div>
                                <div class="card-body">
                                    <form method="get" class="row g-3">
                                        <input type="hidden" name="action" value="myDonations">
                                        <input type="hidden" name="tab" value="my-donations">
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
                                            <button type="submit" class="btn btn-primary me-2">
                                                <i class="fas fa-search me-1"></i> Filtrar
                                            </button>
                                            <a href="employeeDonations?action=myDonations&tab=my-donations" class="btn btn-outline-secondary">
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

                            <!-- Estadísticas de Mis Donaciones -->
                            <div class="row mb-4">
                                <%
                                    DonationDAO donationDAO = new DonationDAO();
                                    String username = (String) session.getAttribute("username");
                                    List<Donation> myDonations = (List<Donation>) request.getAttribute("myDonations");
                                    if (myDonations == null) {
                                        myDonations = donationDAO.getDonationsByEmployee(username);
                                    }

                                    // Aplicar filtros
                                    String statusFilter = request.getParameter("status");
                                    String typeFilter = request.getParameter("type");
                                    List<Donation> filteredMyDonations = new ArrayList<>();
                                    for (Donation donation : myDonations) {
                                        if ((statusFilter == null || statusFilter.isEmpty() || donation.getStatus().equals(statusFilter)) &&
                                            (typeFilter == null || typeFilter.isEmpty() || donation.getType().equals(typeFilter))) {
                                            filteredMyDonations.add(donation);
                                        }
                                    }

                                    int totalMyDonations = filteredMyDonations.size();
                                    
                                    // Contar por estado para estadísticas
                                    int myPendingCount = 0, myInProgressCount = 0, myCompletedCount = 0, myCancelledCount = 0;
                                    for (Donation donation : filteredMyDonations) {
                                        switch(donation.getStatus()) {
                                            case "pending": myPendingCount++; break;
                                            case "in_progress": myInProgressCount++; break;
                                            case "completed": myCompletedCount++; break;
                                            case "cancelled": myCancelledCount++; break;
                                        }
                                    }
                                %>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-primary text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= totalMyDonations %></h4>
                                            <small>Total Asignadas</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-warning text-dark">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= myPendingCount %></h4>
                                            <small>Pendientes</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-info text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= myInProgressCount %></h4>
                                            <small>En Progreso</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-success text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= myCompletedCount %></h4>
                                            <small>Completadas</small>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Tabla de Mis Donaciones Asignadas -->
                            <div class="card">
                                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                    <div>
                                        <h5 class="mb-0"><i class="fas fa-list me-2"></i>Mis Donaciones Asignadas</h5>
                                        <small>
                                            <%
                                                if (hasActiveFilters) {
                                                    out.print("Mostrando resultados filtrados");
                                                } else {
                                                    out.print("Donaciones que tienes asignadas para entrega");
                                                }
                                            %>
                                        </small>
                                    </div>
                                    <span class="badge bg-dark">
                                        <%= totalMyDonations %> donación<%= totalMyDonations != 1 ? "es" : "" %>
                                    </span>
                                </div>
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead class="table-primary">
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
                                                if (filteredMyDonations != null && !filteredMyDonations.isEmpty()) {
                                                    for (Donation donation : filteredMyDonations) {
                                                %>
                                                <tr>
                                                    <td><strong>#<%= donation.getId() %></strong></td>
                                                    <td>
                                                        <span class="badge bg-info">
                                                            <%= getTypeText(donation.getType()) %>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <div class="text-truncate-custom" title="<%= donation.getDescription() %>">
                                                            <%= donation.getDescription() %>
                                                        </div>
                                                    </td>
                                                    <td><%= donation.getDonorUsername() %></td>
                                                    <td><%= donation.getLocation() %></td>
                                                    <td>
                                                        <span class="badge bg-secondary">
                                                            <%= donation.getQuantity() %>
                                                        </span>
                                                    </td>
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
                                                        <div class="btn-group btn-group-sm">
                                                            <!-- Botón para completar entrega -->
                                                            <button class="btn btn-outline-success" 
                                                                    onclick="completeDelivery(<%= donation.getId() %>)"
                                                                    <%= "completed".equals(donation.getStatus()) ? "disabled" : "" %>
                                                                    title="Marcar como completado">
                                                                <i class="fas fa-check"></i>
                                                            </button>
                                                            
                                                            <!-- Botón para cambiar estado -->
                                                            <button class="btn btn-outline-warning" 
                                                                    onclick="showUpdateModal(<%= donation.getId() %>, '<%= donation.getStatus() %>')"
                                                                    title="Cambiar estado">
                                                                <i class="fas fa-edit"></i>
                                                            </button>
                                                            
                                                            <!-- Botón para ver detalles -->
                                                            <button class="btn btn-outline-info" 
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
                                                    <td colspan="9" class="text-center py-4">
                                                        <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                                        <h5 class="text-muted">No tienes donaciones asignadas</h5>
                                                        <%
                                                            if (hasActiveFilters) {
                                                        %>
                                                        <p class="text-muted">Intenta con otros criterios de filtro</p>
                                                        <a href="employeeDonations?action=myDonations&tab=my-donations" class="btn btn-primary mt-2">
                                                            <i class="fas fa-times me-1"></i> Limpiar Filtros
                                                        </a>
                                                        <%
                                                            } else {
                                                        %>
                                                        <p class="text-muted">Las donaciones que te asignen aparecerán aquí</p>
                                                        <a href="#available-donations" class="btn btn-success mt-2" onclick="switchToAvailableTab()">
                                                            <i class="fas fa-hand-paper me-1"></i> Ver Donaciones Disponibles
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

                        <!-- Pestaña 2: Donaciones Disponibles -->
                        <div class="tab-pane fade" id="available-donations" role="tabpanel" aria-labelledby="available-donations-tab">
                            <!-- Estadísticas de Donaciones Disponibles -->
                            <div class="row mb-4">
                                <%
                                    List<Donation> availableDonations = donationDAO.getAvailableDonations();
                                    int totalAvailableDonations = availableDonations != null ? availableDonations.size() : 0;
                                    
                                    // Contar por tipo para estadísticas
                                    int availableRopaCount = 0, availableCuadernosCount = 0, availableUtilesCount = 0;
                                    int availableReciclableCount = 0, availableRopaNuevaCount = 0, availableOtrosCount = 0;
                                    
                                    if (availableDonations != null) {
                                        for (Donation donation : availableDonations) {
                                            switch(donation.getType()) {
                                                case "ropa": availableRopaCount++; break;
                                                case "cuadernos": availableCuadernosCount++; break;
                                                case "utiles_escolares": availableUtilesCount++; break;
                                                case "material_reciclable": availableReciclableCount++; break;
                                                case "ropa_casi_nueva": availableRopaNuevaCount++; break;
                                                case "otros": availableOtrosCount++; break;
                                            }
                                        }
                                    }
                                %>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-success text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= totalAvailableDonations %></h4>
                                            <small>Total Disponibles</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-info text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= availableRopaCount + availableRopaNuevaCount %></h4>
                                            <small>Donaciones de Ropa</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-primary text-white">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= availableCuadernosCount + availableUtilesCount %></h4>
                                            <small>Material Escolar</small>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card stat-card bg-warning text-dark">
                                        <div class="card-body text-center">
                                            <h4 class="mb-0"><%= availableReciclableCount + availableOtrosCount %></h4>
                                            <small>Otros Materiales</small>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Tabla de Donaciones Disponibles -->
                            <div class="card">
                                <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                                    <div>
                                        <h5 class="mb-0"><i class="fas fa-hand-paper me-2"></i>Donaciones Disponibles</h5>
                                        <small>Puedes asignarte estas donaciones para gestionar su entrega</small>
                                    </div>
                                    <span class="badge bg-dark">
                                        <%= totalAvailableDonations %> donación<%= totalAvailableDonations != 1 ? "es" : "" %> disponible<%= totalAvailableDonations != 1 ? "s" : "" %>
                                    </span>
                                </div>
                                <div class="card-body">
                                    <%
                                        if (availableDonations != null && !availableDonations.isEmpty()) {
                                    %>
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead class="table-success">
                                                <tr>
                                                    <th>ID</th>
                                                    <th>Tipo</th>
                                                    <th>Descripción</th>
                                                    <th>Donador</th>
                                                    <th>Ubicación</th>
                                                    <th>Cantidad</th>
                                                    <th>Estado</th>
                                                    <th>Fecha</th>
                                                    <th>Acción</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% for (Donation donation : availableDonations) { %>
                                                <tr>
                                                    <td><strong>#<%= donation.getId() %></strong></td>
                                                    <td>
                                                        <span class="badge bg-info">
                                                            <%= getTypeText(donation.getType()) %>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <div class="text-truncate-custom" title="<%= donation.getDescription() %>">
                                                            <%= donation.getDescription() %>
                                                        </div>
                                                    </td>
                                                    <td><%= donation.getDonorUsername() %></td>
                                                    <td><%= donation.getLocation() %></td>
                                                    <td>
                                                        <span class="badge bg-secondary">
                                                            <%= donation.getQuantity() %>
                                                        </span>
                                                    </td>
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
                                                        <button class="btn btn-success btn-sm" 
                                                                onclick="assignToMe(<%= donation.getId() %>)"
                                                                title="Asignarme esta donación">
                                                            <i class="fas fa-user-plus me-1"></i> Asignar
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
                                        <p class="text-muted">No hay donaciones disponibles en este momento.</p>
                                        <p class="text-muted">Todas las donaciones han sido asignadas.</p>
                                        <a href="#my-donations" class="btn btn-primary mt-2" onclick="switchToMyDonationsTab()">
                                            <i class="fas fa-list-check me-1"></i> Ver Mis Donaciones
                                        </a>
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

    <!-- Modal para actualizar estado -->
    <div class="modal fade" id="updateModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="employeeDonations" method="post" id="updateForm">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="donationId" id="updateDonationId">
                    <input type="hidden" name="tab" value="my-donations">

                    <div class="modal-header">
                        <h5 class="modal-title">Actualizar Estado de Entrega</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Nuevo Estado</label>
                            <select name="status" class="form-select" id="updateStatus" required>
                                <option value="pending">Pendiente</option>
                                <option value="in_progress">En Progreso</option>
                                <option value="completed">Completado</option>
                                <option value="cancelled">Cancelado</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Notas (Opcional)</label>
                            <textarea name="notes" class="form-control" rows="3" placeholder="Agregar notas sobre el estado de la entrega..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Actualizar Estado</button>
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
            
            if (tabParam === 'available-donations') {
                const availableTab = new bootstrap.Tab(document.getElementById('available-donations-tab'));
                availableTab.show();
            } else {
                const myDonationsTab = new bootstrap.Tab(document.getElementById('my-donations-tab'));
                myDonationsTab.show();
            }
        });

        function switchToAvailableTab() {
            const availableTab = new bootstrap.Tab(document.getElementById('available-donations-tab'));
            availableTab.show();
        }

        function switchToMyDonationsTab() {
            const myDonationsTab = new bootstrap.Tab(document.getElementById('my-donations-tab'));
            myDonationsTab.show();
        }

        function showUpdateModal(donationId, currentStatus) {
            console.log("DEBUG - Mostrar modal para donación:", donationId, "Estado actual:", currentStatus);
            document.getElementById('updateDonationId').value = donationId;
            document.getElementById('updateStatus').value = currentStatus;
            new bootstrap.Modal(document.getElementById('updateModal')).show();
        }

        function assignToMe(donationId) {
            if (confirm('¿Estás seguro de que quieres asignarte esta donación?\n\nSerás responsable de la entrega de esta donación.')) {
                // Crear un formulario dinámico para asignar la donación
                const form = document.createElement('form');
                form.method = 'post';
                form.action = 'employeeDonations';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'assignToMe';
                form.appendChild(actionInput);
                
                const donationIdInput = document.createElement('input');
                donationIdInput.type = 'hidden';
                donationIdInput.name = 'donationId';
                donationIdInput.value = donationId;
                form.appendChild(donationIdInput);
                
                const tabInput = document.createElement('input');
                tabInput.type = 'hidden';
                tabInput.name = 'tab';
                tabInput.value = 'available-donations';
                form.appendChild(tabInput);
                
                document.body.appendChild(form);
                form.submit();
            }
        }

        function completeDelivery(donationId) {
            if (confirm('¿Marcar esta donación como completada?\n\nEsta acción no se puede deshacer.')) {
                // Crear un formulario dinámico para completar la entrega
                const form = document.createElement('form');
                form.method = 'post';
                form.action = 'employeeDonations';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'completeDelivery';
                form.appendChild(actionInput);
                
                const donationIdInput = document.createElement('input');
                donationIdInput.type = 'hidden';
                donationIdInput.name = 'donationId';
                donationIdInput.value = donationId;
                form.appendChild(donationIdInput);
                
                const tabInput = document.createElement('input');
                tabInput.type = 'hidden';
                tabInput.name = 'tab';
                tabInput.value = 'my-donations';
                form.appendChild(tabInput);
                
                document.body.appendChild(form);
                form.submit();
            }
        }

        function viewDetails(donationId) {
            window.location.href = 'employeeDonations?action=view&id=' + donationId;
        }
    </script>
</body>
</html>

<%!
    // Método para remover un filtro específico
    private String removeFilter(HttpServletRequest request, String filterToRemove) {
        StringBuilder url = new StringBuilder("employeeDonations?action=myDonations&tab=my-donations");
        
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