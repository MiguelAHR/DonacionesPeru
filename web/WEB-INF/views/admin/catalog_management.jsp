<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<%@page import="com.donaciones.dao.CatalogoDAO"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Catálogo - Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <style>
            .table-actions {
                white-space: nowrap;
            }
            .badge-priority-1 {
                background-color: #dc3545 !important;
            }
            .badge-priority-2 {
                background-color: #ffc107 !important;
                color: #000 !important;
            }
            .badge-priority-3 {
                background-color: #198754 !important;
            }
        </style>
    </head>
    <body>
        <%
            // Debug information
            System.out.println("DEBUG JSP - catalogItems attribute: " + request.getAttribute("catalogItems"));
            if (request.getAttribute("catalogItems") == null) {
                System.out.println("DEBUG JSP - catalogItems es NULL");
            } else {
                java.util.List<Catalogo> items = (java.util.List<Catalogo>) request.getAttribute("catalogItems");
                System.out.println("DEBUG JSP - Número de items: " + items.size());
            }
        %>

        <div class="container-fluid">
            <div class="row">
                <jsp:include page="../admin/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 main-content">
                    <div class="p-4">
                        <h2 class="fw-bold mb-4">Gestión de Catálogo</h2>

                        <!-- Mensajes de éxito o error -->
                        <%
                            String success = request.getParameter("success");
                            String error = request.getParameter("error");
                            if (success != null) {
                        %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <%= getSuccessMessage(success)%>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <%
                            }
                            if (error != null) {
                        %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <%= getErrorMessage(error)%>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <%
                            }
                        %>

                        <!-- Filtros -->
                        <div class="card mb-4">
                            <div class="card-header bg-secondary text-white">
                                <h6 class="mb-0"><i class="fas fa-filter me-2"></i>Filtrar Catálogo</h6>
                            </div>
                            <div class="card-body">
                                <form method="get" action="adminCatalog" class="row g-3">
                                    <input type="hidden" name="action" value="view">
                                    <div class="col-md-4">
                                        <label class="form-label fw-bold">Estado</label>
                                        <select name="status" class="form-select">
                                            <option value="">Todos los estados</option>
                                            <option value="disponible" <%= "disponible".equals(request.getParameter("status")) ? "selected" : ""%>>Disponible</option>
                                            <option value="reservado" <%= "reservado".equals(request.getParameter("status")) ? "selected" : ""%>>Reservado</option>
                                            <option value="entregado" <%= "entregado".equals(request.getParameter("status")) ? "selected" : ""%>>Entregado</option>
                                            <option value="inactivo" <%= "inactivo".equals(request.getParameter("status")) ? "selected" : ""%>>Inactivo</option>
                                        </select>
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label fw-bold">Tipo</label>
                                        <select name="type" class="form-select">
                                            <option value="">Todos los tipos</option>
                                            <option value="ropa" <%= "ropa".equals(request.getParameter("type")) ? "selected" : ""%>>Ropa</option>
                                            <option value="cuadernos" <%= "cuadernos".equals(request.getParameter("type")) ? "selected" : ""%>>Cuadernos</option>
                                            <option value="utiles_escolares" <%= "utiles_escolares".equals(request.getParameter("type")) ? "selected" : ""%>>Útiles Escolares</option>
                                            <option value="material_reciclable" <%= "material_reciclable".equals(request.getParameter("type")) ? "selected" : ""%>>Material Reciclable</option>
                                            <option value="ropa_casi_nueva" <%= "ropa_casi_nueva".equals(request.getParameter("type")) ? "selected" : ""%>>Ropa Casi Nueva</option>
                                            <option value="otros" <%= "otros".equals(request.getParameter("type")) ? "selected" : ""%>>Otros</option>
                                        </select>
                                    </div>
                                    <div class="col-md-4 d-flex align-items-end">
                                        <button type="submit" class="btn btn-primary me-2">
                                            <i class="fas fa-search me-1"></i> Aplicar Filtros
                                        </button>
                                        <a href="adminCatalog?action=view" class="btn btn-outline-secondary">
                                            <i class="fas fa-times me-1"></i> Limpiar
                                        </a>
                                    </div>
                                </form>

                                <!-- Mostrar filtros activos -->
                                <%
                                    String currentStatus = request.getParameter("status");
                                    String currentType = request.getParameter("type");

                                    boolean hasActiveFilters = (currentStatus != null && !currentStatus.isEmpty())
                                            || (currentType != null && !currentType.isEmpty());

                                    if (hasActiveFilters) {
                                %>
                                <div class="mt-3">
                                    <small class="text-muted">Filtros activos:</small>
                                    <div class="d-flex flex-wrap gap-2 mt-1">
                                        <%
                                            if (currentStatus != null && !currentStatus.isEmpty()) {
                                        %>
                                        <span class="badge bg-primary">
                                            Estado: <%= getStatusText(currentStatus)%>
                                            <a href="<%= removeFilter(request, "status")%>" class="text-white ms-1">
                                                <i class="fas fa-times"></i>
                                            </a>
                                        </span>
                                        <%
                                            }
                                            if (currentType != null && !currentType.isEmpty()) {
                                        %>
                                        <span class="badge bg-success">
                                            Tipo: <%= getTypeText(currentType)%>
                                            <a href="<%= removeFilter(request, "type")%>" class="text-white ms-1">
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

                        <!-- Estadísticas -->
                        <div class="row mb-4">
                            <%
                                CatalogoDAO catalogoDAO = new CatalogoDAO();
                                List<Catalogo> currentItems = (List<Catalogo>) request.getAttribute("catalogItems");
                                if (currentItems == null) {
                                    currentItems = catalogoDAO.getAllCatalogItems();
                                }
                                int totalItems = currentItems.size();

                                // Contar por estado para las estadísticas actuales
                                int disponibleCount = 0, reservadoCount = 0, entregadoCount = 0, inactivoCount = 0;
                                for (Catalogo item : currentItems) {
                                    if (item.getEstado() != null) {
                                        switch (item.getEstado()) {
                                            case "disponible":
                                                disponibleCount++;
                                                break;
                                            case "reservado":
                                                reservadoCount++;
                                                break;
                                            case "entregado":
                                                entregadoCount++;
                                                break;
                                            case "inactivo":
                                                inactivoCount++;
                                                break;
                                        }
                                    }
                                }
                            %>
                            <div class="col-md-3">
                                <div class="card bg-primary text-white">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= totalItems%></h4>
                                        <small>Total</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card bg-success text-white">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= disponibleCount%></h4>
                                        <small>Disponibles</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card bg-warning text-dark">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= reservadoCount%></h4>
                                        <small>Reservados</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card bg-info text-white">
                                    <div class="card-body text-center">
                                        <h4 class="mb-0"><%= entregadoCount%></h4>
                                        <small>Entregados</small>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Tabla del catálogo -->
                        <div class="card">
                            <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                <div>
                                    <h5 class="mb-0">Lista del Catálogo</h5>
                                    <small>
                                        <%
                                            if (hasActiveFilters) {
                                                out.print("Mostrando resultados filtrados");
                                            } else {
                                                out.print("Mostrando todos los ítems del catálogo");
                                            }
                                        %>
                                    </small>
                                </div>
                                <span class="badge bg-light text-dark">
                                    <%= totalItems%> ítem<%= totalItems != 1 ? "es" : ""%>
                                </span>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>ID</th>
                                                <th>Título</th>
                                                <th>Tipo</th>
                                                <th>Descripción</th>
                                                <th>Donante</th>
                                                <th>Cantidad</th>
                                                <th>Ubicación</th>
                                                <th>Estado</th>
                                                <th>Prioridad</th>
                                                <th>Fecha Disponible</th>
                                                <th>Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                List<Catalogo> catalogItems = (List<Catalogo>) request.getAttribute("catalogItems");
                                                if (catalogItems == null) {
                                                    catalogItems = catalogoDAO.getAllCatalogItems();
                                                }

                                                if (catalogItems != null && !catalogItems.isEmpty()) {
                                                    for (Catalogo item : catalogItems) {
                                            %>
                                            <tr>
                                                <td><strong>#<%= item.getId()%></strong></td>
                                                <td>
                                                    <div class="text-truncate" style="max-width: 200px;" 
                                                         title="<%= item.getTitulo() != null ? item.getTitulo() : ""%>">
                                                        <%= item.getTitulo() != null ? item.getTitulo() : ""%>
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="badge bg-info">
                                                        <%= getTypeText(item.getTipo())%>
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="text-truncate" style="max-width: 200px;" 
                                                         title="<%= item.getDescripcion() != null ? item.getDescripcion() : ""%>">
                                                        <%= item.getDescripcion() != null ? item.getDescripcion() : ""%>
                                                    </div>
                                                </td>
                                                <td><%= item.getDonante() != null ? item.getDonante() : ""%></td>
                                                <td>
                                                    <span class="badge bg-secondary">
                                                        <%= item.getCantidad()%>
                                                    </span>
                                                </td>
                                                <td><%= item.getUbicacion() != null ? item.getUbicacion() : ""%></td>
                                                <td>
                                                    <span class="badge bg-<%= getStatusBadgeColor(item.getEstado())%>">
                                                        <%= getStatusText(item.getEstado())%>
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="badge badge-priority-<%= item.getPrioridad()%>">
                                                        <%= getPriorityText(item.getPrioridad())%>
                                                    </span>
                                                </td>
                                                <td>
                                                    <small class="text-muted">
                                                        <%= item.getFechaDisponible() != null
                                                                ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(item.getFechaDisponible())
                                                                : "No disponible"%>
                                                    </small>
                                                </td>
                                                <td class="table-actions">
                                                    <div class="btn-group btn-group-sm">
                                                        <button class="btn btn-outline-info" 
                                                                onclick="viewDetails(<%= item.getId()%>)"
                                                                title="Ver detalles">
                                                            <i class="fas fa-eye"></i>
                                                        </button>

                                                        <button class="btn btn-outline-warning" 
                                                                onclick="openEditModal(<%= item.getId()%>)"
                                                                title="Editar ítem">
                                                            <i class="fas fa-edit"></i>
                                                        </button>

                                                        <button class="btn btn-outline-primary" 
                                                                onclick="openStatusModal(<%= item.getId()%>)"
                                                                title="Gestionar estado">
                                                            <i class="fas fa-sync"></i>
                                                        </button>

                                                        <button class="btn btn-outline-danger" 
                                                                onclick="openDeleteModal(<%= item.getId()%>)"
                                                                title="Eliminar ítem">
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
                                                <td colspan="11" class="text-center py-4">
                                                    <i class="fas fa-book fa-3x text-muted mb-3"></i>
                                                    <h5 class="text-muted">No se encontraron ítems en el catálogo</h5>
                                                    <%
                                                        if (hasActiveFilters) {
                                                    %>
                                                    <p class="text-muted">Intenta con otros criterios de filtro</p>
                                                    <a href="adminCatalog?action=view" class="btn btn-primary mt-2">
                                                        <i class="fas fa-times me-1"></i> Limpiar Filtros
                                                    </a>
                                                    <%
                                                    } else {
                                                    %>
                                                    <p class="text-muted">Los ítems aparecerán aquí cuando las donaciones se completen</p>
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

        <!-- Modal para Editar Ítem -->
        <div class="modal fade" id="editModal">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <form id="editForm" action="adminCatalog" method="post">
                        <input type="hidden" name="action" value="updateItem">
                        <input type="hidden" name="itemId" id="editItemId">

                        <div class="modal-header">
                            <h5 class="modal-title text-warning">
                                <i class="fas fa-edit me-2"></i>
                                Editar Ítem del Catálogo
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editTitulo" class="form-label">Título *</label>
                                        <input type="text" class="form-control" id="editTitulo" name="titulo" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editTipo" class="form-label">Tipo *</label>
                                        <select class="form-select" id="editTipo" name="tipo" required>
                                            <option value="ropa">Ropa</option>
                                            <option value="cuadernos">Cuadernos</option>
                                            <option value="utiles_escolares">Útiles Escolares</option>
                                            <option value="material_reciclable">Material Reciclable</option>
                                            <option value="ropa_casi_nueva">Ropa Casi Nueva</option>
                                            <option value="otros">Otros</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="editDescripcion" class="form-label">Descripción *</label>
                                <textarea class="form-control" id="editDescripcion" name="descripcion" rows="3" required></textarea>
                            </div>

                            <div class="row">
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="editCantidad" class="form-label">Cantidad *</label>
                                        <input type="number" class="form-control" id="editCantidad" name="cantidad" min="1" required>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="editCondicion" class="form-label">Condición *</label>
                                        <input type="text" class="form-control" id="editCondicion" name="condicion" required>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="editPrioridad" class="form-label">Prioridad *</label>
                                        <select class="form-select" id="editPrioridad" name="prioridad" required>
                                            <option value="1">Alta (1)</option>
                                            <option value="2">Media (2)</option>
                                            <option value="3">Baja (3)</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editUbicacion" class="form-label">Ubicación *</label>
                                        <input type="text" class="form-control" id="editUbicacion" name="ubicacion" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editEstado" class="form-label">Estado *</label>
                                        <select class="form-select" id="editEstado" name="estado" required>
                                            <option value="disponible">Disponible</option>
                                            <option value="reservado">Reservado</option>
                                            <option value="entregado">Entregado</option>
                                            <option value="inactivo">Inactivo</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fas fa-times me-2"></i>Cancelar
                            </button>
                            <button type="submit" class="btn btn-warning">
                                <i class="fas fa-save me-2"></i>Guardar Cambios
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal para Gestionar Estado -->
        <div class="modal fade" id="statusModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form id="statusForm" action="adminCatalog" method="post">
                        <input type="hidden" name="action" value="updateStatus">
                        <input type="hidden" name="itemId" id="statusItemId">

                        <div class="modal-header">
                            <h5 class="modal-title text-primary">
                                <i class="fas fa-sync me-2"></i>
                                Gestionar Estado del Ítem
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label for="statusEstado" class="form-label">Nuevo Estado *</label>
                                <select class="form-select" id="statusEstado" name="estado" required>
                                    <option value="disponible">Disponible</option>
                                    <option value="reservado">Reservado</option>
                                    <option value="entregado">Entregado</option>
                                    <option value="inactivo">Inactivo</option>
                                </select>
                            </div>
                            <div class="alert alert-info">
                                <small>
                                    <i class="fas fa-info-circle me-2"></i>
                                    <strong>Disponible:</strong> El ítem está disponible para donación<br>
                                    <strong>Reservado:</strong> El ítem ha sido reservado<br>
                                    <strong>Entregado:</strong> El ítem ha sido entregado<br>
                                    <strong>Inactivo:</strong> El ítem no está disponible
                                </small>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fas fa-times me-2"></i>Cancelar
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-2"></i>Actualizar Estado
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal para confirmar eliminación -->
        <div class="modal fade" id="deleteModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form id="deleteForm" action="adminCatalog" method="post">
                        <input type="hidden" name="action" value="deleteItem">
                        <input type="hidden" name="itemId" id="deleteItemId">

                        <div class="modal-header">
                            <h5 class="modal-title text-danger">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Confirmar Eliminación Permanente
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="alert alert-danger">
                                <h6 class="alert-heading">¿Estás seguro de que deseas eliminar permanentemente este ítem del catálogo?</h6>
                                <p class="mb-0">
                                    <strong>¡ADVERTENCIA: Esta acción no se puede deshacer!</strong><br>
                                    El ítem será eliminado permanentemente de la base de datos y no podrá ser recuperado.
                                </p>
                            </div>
                            <div class="mt-3">
                                <strong>Información del ítem a eliminar:</strong>
                                <ul class="mt-2">
                                    <li><strong>ID:</strong> <span id="deleteItemInfo"></span></li>
                                    <li><strong>Título:</strong> <span id="deleteItemTitle"></span></li>
                                    <li><strong>Tipo:</strong> <span id="deleteItemType"></span></li>
                                </ul>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fas fa-times me-2"></i>Cancelar
                            </button>
                            <button type="submit" class="btn btn-danger">
                                <i class="fas fa-trash me-2"></i>Eliminar Permanentemente
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                                                    // Función para ver detalles (redirección)
                                                                    function viewDetails(itemId) {
                                                                        window.location.href = 'adminCatalog?action=viewItem&id=' + itemId;
                                                                    }

                                                                    // Función para abrir modal de edición
                                                                    function openEditModal(itemId) {
                                                                        console.log('Abriendo modal de edición para item:', itemId);

                                                                        // Mostrar loading
                                                                        $('#editModal .modal-body').html(`
                    <div class="text-center py-4">
                        <div class="spinner-border text-warning" role="status">
                            <span class="visually-hidden">Cargando...</span>
                        </div>
                        <p class="mt-2">Cargando datos del ítem...</p>
                    </div>
                `);

                                                                        var editModal = new bootstrap.Modal(document.getElementById('editModal'));
                                                                        editModal.show();

                                                                        // Cargar datos del ítem via AJAX
                                                                        $.ajax({
                                                                            url: 'adminCatalog?action=getItemData&id=' + itemId,
                                                                            type: 'GET',
                                                                            success: function (data) {
                                                                                console.log('Datos recibidos:', data);

                                                                                // Llenar el formulario con los datos
                                                                                $('#editItemId').val(data.id);
                                                                                $('#editTitulo').val(data.titulo);
                                                                                $('#editDescripcion').val(data.descripcion);
                                                                                $('#editTipo').val(data.tipo);
                                                                                $('#editCantidad').val(data.cantidad);
                                                                                $('#editCondicion').val(data.condicion);
                                                                                $('#editUbicacion').val(data.ubicacion);
                                                                                $('#editEstado').val(data.estado);
                                                                                $('#editPrioridad').val(data.prioridad);

                                                                                // Restaurar el contenido original del modal
                                                                                $('#editModal .modal-body').html(`
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editTitulo" class="form-label">Título *</label>
                                        <input type="text" class="form-control" id="editTitulo" name="titulo" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editTipo" class="form-label">Tipo *</label>
                                        <select class="form-select" id="editTipo" name="tipo" required>
                                            <option value="ropa">Ropa</option>
                                            <option value="cuadernos">Cuadernos</option>
                                            <option value="utiles_escolares">Útiles Escolares</option>
                                            <option value="material_reciclable">Material Reciclable</option>
                                            <option value="ropa_casi_nueva">Ropa Casi Nueva</option>
                                            <option value="otros">Otros</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editDescripcion" class="form-label">Descripción *</label>
                                <textarea class="form-control" id="editDescripcion" name="descripcion" rows="3" required></textarea>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="editCantidad" class="form-label">Cantidad *</label>
                                        <input type="number" class="form-control" id="editCantidad" name="cantidad" min="1" required>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="editCondicion" class="form-label">Condición *</label>
                                        <input type="text" class="form-control" id="editCondicion" name="condicion" required>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="editPrioridad" class="form-label">Prioridad *</label>
                                        <select class="form-select" id="editPrioridad" name="prioridad" required>
                                            <option value="1">Alta (1)</option>
                                            <option value="2">Media (2)</option>
                                            <option value="3">Baja (3)</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editUbicacion" class="form-label">Ubicación *</label>
                                        <input type="text" class="form-control" id="editUbicacion" name="ubicacion" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editEstado" class="form-label">Estado *</label>
                                        <select class="form-select" id="editEstado" name="estado" required>
                                            <option value="disponible">Disponible</option>
                                            <option value="reservado">Reservado</option>
                                            <option value="entregado">Entregado</option>
                                            <option value="inactivo">Inactivo</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        `);

                                                                                // Volver a llenar los campos
                                                                                $('#editItemId').val(data.id);
                                                                                $('#editTitulo').val(data.titulo);
                                                                                $('#editDescripcion').val(data.descripcion);
                                                                                $('#editTipo').val(data.tipo);
                                                                                $('#editCantidad').val(data.cantidad);
                                                                                $('#editCondicion').val(data.condicion);
                                                                                $('#editUbicacion').val(data.ubicacion);
                                                                                $('#editEstado').val(data.estado);
                                                                                $('#editPrioridad').val(data.prioridad);
                                                                            },
                                                                            error: function (xhr, status, error) {
                                                                                console.error('Error cargando datos:', error);
                                                                                $('#editModal .modal-body').html(`
                            <div class="alert alert-danger">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Error al cargar los datos del ítem: ${xhr.responseText || error}
                            </div>
                        `);
                                                                            }
                                                                        });
                                                                    }

                                                                    // Función para abrir modal de estado
                                                                    function openStatusModal(itemId) {
                                                                        console.log('Abriendo modal de estado para item:', itemId);

                                                                        // Mostrar loading
                                                                        $('#statusModal .modal-body').html(`
                    <div class="text-center py-4">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Cargando...</span>
                        </div>
                        <p class="mt-2">Cargando datos del ítem...</p>
                    </div>
                `);

                                                                        var statusModal = new bootstrap.Modal(document.getElementById('statusModal'));
                                                                        statusModal.show();

                                                                        // Cargar datos del ítem via AJAX
                                                                        $.ajax({
                                                                            url: 'adminCatalog?action=getItemData&id=' + itemId,
                                                                            type: 'GET',
                                                                            success: function (data) {
                                                                                console.log('Datos recibidos:', data);

                                                                                // Llenar el formulario con los datos
                                                                                $('#statusItemId').val(data.id);
                                                                                $('#statusEstado').val(data.estado);

                                                                                // Restaurar el contenido original del modal
                                                                                $('#statusModal .modal-body').html(`
                            <div class="mb-3">
                                <label for="statusEstado" class="form-label">Nuevo Estado *</label>
                                <select class="form-select" id="statusEstado" name="estado" required>
                                    <option value="disponible">Disponible</option>
                                    <option value="reservado">Reservado</option>
                                    <option value="entregado">Entregado</option>
                                    <option value="inactivo">Inactivo</option>
                                </select>
                            </div>
                            <div class="alert alert-info">
                                <small>
                                    <i class="fas fa-info-circle me-2"></i>
                                    <strong>Disponible:</strong> El ítem está disponible para donación<br>
                                    <strong>Reservado:</strong> El ítem ha sido reservado<br>
                                    <strong>Entregado:</strong> El ítem ha sido entregado<br>
                                    <strong>Inactivo:</strong> El ítem no está disponible
                                </small>
                            </div>
                        `);

                                                                                // Volver a llenar los campos
                                                                                $('#statusItemId').val(data.id);
                                                                                $('#statusEstado').val(data.estado);
                                                                            },
                                                                            error: function (xhr, status, error) {
                                                                                console.error('Error cargando datos:', error);
                                                                                $('#statusModal .modal-body').html(`
                            <div class="alert alert-danger">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Error al cargar los datos del ítem: ${xhr.responseText || error}
                            </div>
                        `);
                                                                            }
                                                                        });
                                                                    }

                                                                    // Función para abrir modal de eliminación
                                                                    function openDeleteModal(itemId) {
                                                                        // Buscar información del ítem para mostrar en el modal
                                                                        let foundRow = null;
                                                                        const rows = document.querySelectorAll('tbody tr');

                                                                        for (let row of rows) {
                                                                            const firstCell = row.cells[0];
                                                                            if (firstCell && firstCell.textContent.includes('#' + itemId)) {
                                                                                foundRow = row;
                                                                                break;
                                                                            }
                                                                        }

                                                                        if (foundRow) {
                                                                            const title = foundRow.cells[1].textContent.trim();
                                                                            const type = foundRow.cells[2].querySelector('.badge').textContent;

                                                                            document.getElementById('deleteItemId').value = itemId;
                                                                            document.getElementById('deleteItemInfo').textContent = '#' + itemId;
                                                                            document.getElementById('deleteItemTitle').textContent = title;
                                                                            document.getElementById('deleteItemType').textContent = type;

                                                                            new bootstrap.Modal(document.getElementById('deleteModal')).show();
                                                                        }
                                                                    }

                                                                    // Inicialización cuando el documento está listo
                                                                    $(document).ready(function () {
                                                                        console.log('Admin Catalog Management inicializado');
                                                                    });
        </script>
    </body>
</html>

<%!
    // Método para remover un filtro específico
    private String removeFilter(HttpServletRequest request, String filterToRemove) {
        StringBuilder url = new StringBuilder("adminCatalog?action=view");

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
        if (status == null) {
            return "secondary";
        }

        switch (status) {
            case "disponible":
                return "success";
            case "reservado":
                return "warning";
            case "entregado":
                return "info";
            case "inactivo":
                return "danger";
            default:
                return "secondary";
        }
    }

    // Método para obtener el texto de prioridad
    private String getPriorityText(int priority) {
        switch (priority) {
            case 1:
                return "Alta";
            case 2:
                return "Media";
            case 3:
                return "Baja";
            default:
                return "N/A";
        }
    }

    // Método para obtener el texto en español del estado
    private String getStatusText(String status) {
        if (status == null) {
            return "Desconocido";
        }

        switch (status) {
            case "disponible":
                return "Disponible";
            case "reservado":
                return "Reservado";
            case "entregado":
                return "Entregado";
            case "inactivo":
                return "Inactivo";
            default:
                return status;
        }
    }

    // Método para obtener el texto en español del tipo
    private String getTypeText(String type) {
        if (type == null) {
            return "Desconocido";
        }

        switch (type) {
            case "ropa":
                return "Ropa";
            case "cuadernos":
                return "Cuadernos";
            case "utiles_escolares":
                return "Útiles Escolares";
            case "material_reciclable":
                return "Material Reciclable";
            case "ropa_casi_nueva":
                return "Ropa Casi Nueva";
            case "otros":
                return "Otros";
            default:
                return type;
        }
    }

    // Método para obtener mensajes de éxito
    private String getSuccessMessage(String successType) {
        switch (successType) {
            case "item_updated":
                return "¡Ítem del catálogo actualizado exitosamente!";
            case "status_updated":
                return "¡Estado del ítem actualizado exitosamente!";
            case "item_deleted":
                return "¡Ítem eliminado permanentemente del catálogo!";
            default:
                return "Operación completada exitosamente";
        }
    }

    // Método para obtener mensajes de error
    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case "invalid_id":
                return "ID de ítem inválido";
            case "not_found":
                return "Ítem no encontrado";
            case "invalid_status":
                return "Estado inválido";
            case "update_failed":
                return "Error al actualizar el ítem";
            case "status_update_failed":
                return "Error al actualizar el estado";
            case "delete_failed":
                return "Error al eliminar el ítem";
            case "invalid_data":
                return "Datos de ítem inválidos";
            case "server_error":
                return "Error interno del servidor";
            default:
                return "Ha ocurrido un error. Intenta nuevamente";
        }
    }
%>