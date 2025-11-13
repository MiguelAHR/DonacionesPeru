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
        <title>Gestión de Donaciones - Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    </head>
    <body>
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="../admin/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 main-content">
                    <div class="p-4">
                        <h2 class="fw-bold mb-4">Gestión de Donaciones</h2>

                        <!-- Mensajes de éxito o error -->
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

                        <!-- Filtros MEJORADOS -->
                        <div class="card mb-4">
                            <div class="card-header bg-secondary text-white">
                                <h6 class="mb-0"><i class="fas fa-filter me-2"></i>Filtrar Donaciones</h6>
                            </div>
                            <div class="card-body">
                                <form method="get" class="row g-3">
                                    <input type="hidden" name="action" value="list">
                                    <div class="col-md-3">
                                        <label class="form-label fw-bold">Estado</label>
                                        <select name="status" class="form-select">
                                            <option value="">Todos los estados</option>
                                            <option value="pending" <%= "pending".equals(request.getParameter("status")) ? "selected" : "" %>>Pendiente</option>
                                            <option value="in_progress" <%= "in_progress".equals(request.getParameter("status")) ? "selected" : "" %>>En Progreso</option>
                                            <option value="approved" <%= "approved".equals(request.getParameter("status")) ? "selected" : "" %>>Aprobado</option>
                                            <option value="completed" <%= "completed".equals(request.getParameter("status")) ? "selected" : "" %>>Completado</option>
                                            <option value="cancelled" <%= "cancelled".equals(request.getParameter("status")) ? "selected" : "" %>>Cancelado</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
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
                                    <div class="col-md-3">
                                        <label class="form-label fw-bold">Ubicación</label>
                                        <select name="location" class="form-select">
                                            <option value="">Todas las ubicaciones</option>
                                            <option value="Lima" <%= "Lima".equals(request.getParameter("location")) ? "selected" : "" %>>Lima</option>
                                            <option value="Arequipa" <%= "Arequipa".equals(request.getParameter("location")) ? "selected" : "" %>>Arequipa</option>
                                            <option value="Cusco" <%= "Cusco".equals(request.getParameter("location")) ? "selected" : "" %>>Cusco</option>
                                            <option value="Chiclayo" <%= "Chiclayo".equals(request.getParameter("location")) ? "selected" : "" %>>Chiclayo</option>
                                            <option value="Tacna" <%= "Tacna".equals(request.getParameter("location")) ? "selected" : "" %>>Tacna</option>
                                            <option value="Ayacucho" <%= "Ayacucho".equals(request.getParameter("location")) ? "selected" : "" %>>Ayacucho</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3 d-flex align-items-end">
                                        <button type="submit" class="btn btn-primary me-2">
                                            <i class="fas fa-search me-1"></i> Aplicar Filtros
                                        </button>
                                        <a href="donationManagement?action=list" class="btn btn-outline-secondary">
                                            <i class="fas fa-times me-1"></i> Limpiar
                                        </a>
                                    </div>
                                </form>
                                
                                <!-- Mostrar filtros activos -->
                                <%
                                    String currentStatus = request.getParameter("status");
                                    String currentType = request.getParameter("type");
                                    String currentLocation = request.getParameter("location");
                                    
                                    boolean hasActiveFilters = (currentStatus != null && !currentStatus.isEmpty()) ||
                                                             (currentType != null && !currentType.isEmpty()) ||
                                                             (currentLocation != null && !currentLocation.isEmpty());
                                    
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
                                            if (currentLocation != null && !currentLocation.isEmpty()) {
                                        %>
                                        <span class="badge bg-info">
                                            Ubicación: <%= currentLocation %>
                                            <a href="<%= removeFilter(request, "location") %>" class="text-white ms-1">
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

                        <!-- Estadísticas ACTUALIZADAS CON FILTROS -->
                        <div class="row mb-4">
                            <%
                                DonationDAO donationDAO = new DonationDAO();
                                List<Donation> currentDonations = (List<Donation>) request.getAttribute("donations");
                                if (currentDonations == null) {
                                    currentDonations = donationDAO.getAllDonations();
                                }
                                int totalDonations = currentDonations.size();
                                
                                // Contar por estado para las estadísticas actuales
                                int pendingCount = 0, inProgressCount = 0, completedCount = 0, cancelledCount = 0;
                                for (Donation d : currentDonations) {
                                    switch(d.getStatus()) {
                                        case "pending": pendingCount++; break;
                                        case "in_progress": inProgressCount++; break;
                                        case "completed": completedCount++; break;
                                        case "cancelled": cancelledCount++; break;
                                    }
                                }
                            %>
                            <div class="col-md-2">
                                <div class="card bg-primary text-white">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= totalDonations %></h4>
                                        <small>Total</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="card bg-warning text-dark">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= pendingCount %></h4>
                                        <small>Pendientes</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="card bg-info text-white">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= inProgressCount %></h4>
                                        <small>En Progreso</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="card bg-success text-white">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= completedCount %></h4>
                                        <small>Completadas</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="card bg-danger text-white">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= cancelledCount %></h4>
                                        <small>Canceladas</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="card bg-secondary text-white">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= hasActiveFilters ? "Filtrado" : "Todos" %></h4>
                                        <small>Estado</small>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Tabla de donaciones -->
                        <div class="card">
                            <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                <div>
                                    <h5 class="mb-0">Lista de Donaciones</h5>
                                    <small>
                                        <%
                                            if (hasActiveFilters) {
                                                out.print("Mostrando resultados filtrados");
                                            } else {
                                                out.print("Mostrando todas las donaciones");
                                            }
                                        %>
                                    </small>
                                </div>
                                <span class="badge bg-light text-dark">
                                    <%= totalDonations %> donación<%= totalDonations != 1 ? "es" : "" %>
                                </span>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>ID</th>
                                                <th>Tipo</th>
                                                <th>Descripción</th>
                                                <th>Donador</th>
                                                <th>Ubicación</th>
                                                <th>Cantidad</th>
                                                <th>Estado</th>
                                                <th>Empleado Asignado</th>
                                                <th>Fecha</th>
                                                <th>Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                List<Donation> donations = (List<Donation>) request.getAttribute("donations");
                                                if (donations == null) {
                                                    donations = donationDAO.getAllDonations();
                                                }
                                                
                                                if (donations != null && !donations.isEmpty()) {
                                                    for (Donation donation : donations) {
                                            %>
                                            <tr>
                                                <td><strong>#<%= donation.getId() %></strong></td>
                                                <td>
                                                    <span class="badge bg-info">
                                                        <%= getTypeText(donation.getType()) %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="text-truncate" style="max-width: 200px;" 
                                                         title="<%= donation.getDescription() %>">
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
                                                    <span class="badge bg-<%= getStatusBadgeColor(donation.getStatus()) %>">
                                                        <%= getStatusText(donation.getStatus()) %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <% 
                                                        String employee = donation.getEmployeeUsername();
                                                        if (employee != null && !employee.trim().isEmpty()) {
                                                    %>
                                                        <span class="badge bg-success"><%= employee %></span>
                                                    <% } else { %>
                                                        <span class="badge bg-warning">Sin asignar</span>
                                                    <% } %>
                                                </td>
                                                <td>
                                                    <small class="text-muted">
                                                        <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(donation.getCreatedDate()) %>
                                                    </small>
                                                </td>
                                                <td>
                                                    <div class="btn-group btn-group-sm">
                                                        <button class="btn btn-outline-primary" 
                                                                onclick="showAssignModal(<%= donation.getId() %>, '<%= donation.getEmployeeUsername() != null ? donation.getEmployeeUsername() : "" %>')"
                                                                title="Asignar empleado">
                                                            <i class="fas fa-user-plus"></i>
                                                        </button>
                                                        
                                                        <button class="btn btn-outline-warning" 
                                                                onclick="showStatusModal(<%= donation.getId() %>, '<%= donation.getStatus() %>')"
                                                                title="Cambiar estado">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        
                                                        <!-- NUEVO BOTÓN: Agregar al Catálogo -->
                                                        <% if ("completed".equals(donation.getStatus())) { %>
                                                        <button class="btn btn-outline-success" 
                                                                onclick="addToCatalog(<%= donation.getId() %>)"
                                                                title="Agregar al Catálogo">
                                                            <i class="fas fa-cart-plus"></i>
                                                        </button>
                                                        <% } %>
                                                        
                                                        <button class="btn btn-outline-info" 
                                                                onclick="showDetails(<%= donation.getId() %>)"
                                                                title="Ver detalles">
                                                            <i class="fas fa-eye"></i>
                                                        </button>
                                                        
                                                        <button class="btn btn-outline-danger" 
                                                                onclick="showDeleteModal(<%= donation.getId() %>)"
                                                                title="Eliminar donación">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                            <%
                                                    }
                                                } else {
                                            %>
                                            <tr>
                                                <td colspan="10" class="text-center py-4">
                                                    <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                                    <h5 class="text-muted">No se encontraron donaciones</h5>
                                                    <%
                                                        if (hasActiveFilters) {
                                                    %>
                                                    <p class="text-muted">Intenta con otros criterios de filtro</p>
                                                    <a href="donationManagement?action=list" class="btn btn-primary mt-2">
                                                        <i class="fas fa-times me-1"></i> Limpiar Filtros
                                                    </a>
                                                    <%
                                                        }
                                                    %>
                                                </td>
                                            </tr>
                                            <%
                                                }
                                            %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Los modales se mantienen igual -->
        <!-- Modal para asignar empleado -->
        <div class="modal fade" id="assignModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="donationManagement" method="post">
                        <input type="hidden" name="action" value="assignEmployee">
                        <input type="hidden" name="donationId" id="assignDonationId">

                        <div class="modal-header">
                            <h5 class="modal-title">Asignar Empleado a Donación</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label">Seleccionar Empleado</label>
                                <select name="employeeUsername" class="form-select" id="assignEmployee" required>
                                    <option value="">-- Seleccionar empleado --</option>
                                    <%
                                        UserDAO userDAO = new UserDAO();
                                        List<String[]> empleados = userDAO.getEmployeesWithNames();
                                        
                                        if (empleados != null && !empleados.isEmpty()) {
                                            for (String[] empleado : empleados) {
                                                String username = empleado[0];
                                                String nombre = empleado[1] != null ? empleado[1] : "";
                                                String apellido = empleado[2] != null ? empleado[2] : "";
                                                String nombreCompleto = nombre + " " + apellido;
                                    %>
                                        <option value="<%= username %>">
                                            <%= nombreCompleto.trim().isEmpty() ? username : nombreCompleto + " (" + username + ")" %>
                                        </option>
                                    <%
                                            }
                                        } else {
                                    %>
                                        <option value="" disabled>No hay empleados disponibles</option>
                                    <%
                                        }
                                    %>
                                </select>
                                <div class="form-text">
                                    Selecciona el empleado que se encargará de esta donación.
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-primary">Asignar Empleado</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal para cambiar estado -->
        <div class="modal fade" id="statusModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="donationManagement" method="post">
                        <input type="hidden" name="action" value="updateStatus">
                        <input type="hidden" name="donationId" id="statusDonationId">

                        <div class="modal-header">
                            <h5 class="modal-title">Cambiar Estado de Donación</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label">Nuevo Estado</label>
                                <select name="status" class="form-select" id="statusSelect" required>
                                    <option value="pending">Pendiente</option>
                                    <option value="in_progress">En Progreso</option>
                                    <option value="approved">Aprobado</option>
                                    <option value="completed">Completado</option>
                                    <option value="cancelled">Cancelado</option>
                                </select>
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

        <!-- Modal para confirmar eliminación -->
        <div class="modal fade" id="deleteModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="donationManagement" method="post">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="donationId" id="deleteDonationId">

                        <div class="modal-header">
                            <h5 class="modal-title text-danger">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Confirmar Eliminación
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="alert alert-warning">
                                <h6 class="alert-heading">¿Estás seguro de que deseas eliminar esta donación?</h6>
                                <p class="mb-0">
                                    Esta acción <strong>no se puede deshacer</strong>. Se eliminará permanentemente 
                                    toda la información de la donación del sistema.
                                </p>
                            </div>
                            <div class="mt-3">
                                <strong>Información de la donación a eliminar:</strong>
                                <ul class="mt-2">
                                    <li><strong>ID:</strong> <span id="deleteDonationInfo"></span></li>
                                    <li><strong>Tipo:</strong> <span id="deleteDonationType"></span></li>
                                    <li><strong>Donador:</strong> <span id="deleteDonationDonor"></span></li>
                                </ul>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fas fa-times me-2"></i>Cancelar
                            </button>
                            <button type="submit" class="btn btn-danger">
                                <i class="fas fa-trash me-2"></i>Eliminar Donación
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Funciones JavaScript existentes...
            function showAssignModal(donationId, currentEmployee) {
                console.log("DEBUG - Asignar modal para donación:", donationId, "Empleado actual:", currentEmployee);
                document.getElementById('assignDonationId').value = donationId;
                const selectElement = document.getElementById('assignEmployee');
                
                selectElement.value = '';
                
                if (currentEmployee && currentEmployee.trim() !== '') {
                    for (let i = 0; i < selectElement.options.length; i++) {
                        if (selectElement.options[i].value === currentEmployee) {
                            selectElement.selectedIndex = i;
                            break;
                        }
                    }
                }
                
                new bootstrap.Modal(document.getElementById('assignModal')).show();
            }

            function showStatusModal(donationId, currentStatus) {
                console.log("DEBUG - Estado modal para donación:", donationId, "Estado actual:", currentStatus);
                document.getElementById('statusDonationId').value = donationId;
                document.getElementById('statusSelect').value = currentStatus;
                new bootstrap.Modal(document.getElementById('statusModal')).show();
            }
            
            function showDetails(donationId) {
                console.log("DEBUG - Ver detalles de donación:", donationId);
                window.location.href = 'donationManagement?action=view&id=' + donationId;
            }

            function showDeleteModal(donationId) {
                console.log("DEBUG - Mostrando modal para eliminar donación ID:", donationId);
                
                document.getElementById('deleteDonationId').value = donationId;
                
                let foundRow = null;
                const rows = document.querySelectorAll('tbody tr');
                
                for (let row of rows) {
                    const firstCell = row.cells[0];
                    if (firstCell && firstCell.textContent.includes('#' + donationId)) {
                        foundRow = row;
                        break;
                    }
                }
                
                if (foundRow) {
                    const type = foundRow.cells[1].querySelector('.badge').textContent;
                    const donor = foundRow.cells[3].textContent;
                    
                    document.getElementById('deleteDonationInfo').textContent = '#' + donationId;
                    document.getElementById('deleteDonationType').textContent = type;
                    document.getElementById('deleteDonationDonor').textContent = donor;
                    
                    console.log("DEBUG - Información cargada en modal:", {
                        id: donationId,
                        type: type,
                        donor: donor
                    });
                } else {
                    console.error("DEBUG - No se pudo encontrar la fila para donación ID:", donationId);
                    document.getElementById('deleteDonationInfo').textContent = '#' + donationId;
                    document.getElementById('deleteDonationType').textContent = 'Desconocido';
                    document.getElementById('deleteDonationDonor').textContent = 'Desconocido';
                }
                
                const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
                deleteModal.show();
            }

            // NUEVA FUNCIÓN: Agregar al Catálogo
            function addToCatalog(donationId) {
                if (confirm('¿Estás seguro de que deseas agregar esta donación al catálogo?')) {
                    window.location.href = 'donationManagement?action=addToCatalog&donationId=' + donationId;
                }
            }
        </script>
    </body>
</html>

<%!
    // Método para remover un filtro específico
    private String removeFilter(HttpServletRequest request, String filterToRemove) {
        StringBuilder url = new StringBuilder("donationManagement?action=list");
        
        String status = request.getParameter("status");
        String type = request.getParameter("type");
        String location = request.getParameter("location");
        
        if (status != null && !status.isEmpty() && !filterToRemove.equals("status")) {
            url.append("&status=").append(status);
        }
        if (type != null && !type.isEmpty() && !filterToRemove.equals("type")) {
            url.append("&type=").append(type);
        }
        if (location != null && !location.isEmpty() && !filterToRemove.equals("location")) {
            url.append("&location=").append(location);
        }
        
        return url.toString();
    }

    // Método para obtener el color del badge según el estado
    private String getStatusBadgeColor(String status) {
        if (status == null) return "secondary";
        
        switch (status) {
            case "pending": return "warning";
            case "in_progress": return "primary";
            case "approved": return "success";
            case "completed": return "info";
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
            case "approved": return "Aprobado";
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

    // Método para obtener mensajes de éxito (ACTUALIZADO)
    private String getSuccessMessage(String successType) {
        switch (successType) {
            case "status_updated":
                return "¡Estado actualizado exitosamente!";
            case "employee_assigned":
                return "¡Empleado asignado exitosamente!";
            case "donation_updated":
                return "¡Donación actualizada exitosamente!";
            case "donation_deleted":
                return "¡Donación eliminada exitosamente!";
            case "added_to_catalog":
                return "¡Donación agregada al catálogo exitosamente!";
            default:
                return "Operación completada exitosamente";
        }
    }

    // Método para obtener mensajes de error (ACTUALIZADO)
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
            case "update_failed":
                return "Error al actualizar la donación";
            case "assignment_failed":
                return "Error al asignar el empleado";
            case "delete_failed":
                return "Error al eliminar la donación";
            case "invalid_data":
                return "Datos de donación inválidos";
            case "server_error":
                return "Error interno del servidor";
            case "invalid_employee":
                return "Nombre de empleado inválido";
            case "not_completed":
                return "Solo las donaciones completadas pueden agregarse al catálogo";
            case "already_in_catalog":
                return "Esta donación ya está en el catálogo";
            case "catalog_failed":
                return "Error al agregar la donación al catálogo";
            default:
                return "Ha ocurrido un error. Intenta nuevamente";
        }
    }
%>