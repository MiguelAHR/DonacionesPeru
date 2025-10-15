<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Donaciones - Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="../admin/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 main-content">
                    <div class="p-4">
                        <h2 class="fw-bold mb-4">Gestión de Donaciones</h2>

                        <!-- Filtros -->
                        <div class="card mb-4">
                            <div class="card-body">
                                <form method="get" class="row g-3">
                                    <input type="hidden" name="action" value="list">
                                    <div class="col-md-3">
                                        <label class="form-label">Estado</label>
                                        <select name="status" class="form-select">
                                            <option value="">Todos</option>
                                            <option value="active">Activa</option>
                                            <option value="completed">Completada</option>
                                            <option value="cancelled">Cancelada</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Tipo</label>
                                        <select name="type" class="form-select">
                                            <option value="">Todos</option>
                                            <option value="ropa">Ropa</option>
                                            <option value="cuadernos">Cuadernos</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Ubicación</label>
                                        <select name="location" class="form-select">
                                            <option value="">Todas</option>
                                            <option value="Lima">Lima</option>
                                            <option value="Arequipa">Arequipa</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <button type="submit" class="btn btn-primary mt-4">Filtrar</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Tabla de donaciones -->
                        <div class="card">
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
                                                <th>Estado</th>
                                                <th>Empleado Asignado</th>
                                                <th>Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                List<Donation> donations = (List<Donation>) request.getAttribute("donations");
                                                if (donations != null) {
                                                    for (Donation donation : donations) {
                                            %>
                                            <tr>
                                                <td><%= donation.getId()%></td>
                                                <td><%= donation.getType()%></td>
                                                <td><%= donation.getDescription()%></td>
                                                <td><%= donation.getDonorUsername()%></td>
                                                <td><%= donation.getLocation()%></td>
                                                <td>
                                                    <span class="badge bg-<%= getStatusBadgeColor(donation.getStatus())%>">
                                                        <%= donation.getStatus()%>
                                                    </span>
                                                </td>
                                                <td><%= donation.getEmployeeAssigned() != null ? donation.getEmployeeAssigned() : "Sin asignar"%></td>
                                                <td>
                                                    <div class="btn-group">
                                                        <button class="btn btn-sm btn-outline-primary" 
                                                                onclick="showEditModal(<%= donation.getId()%>, '<%= donation.getStatus()%>')">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-outline-info" 
                                                                onclick="showDetails(<%= donation.getId()%>)">
                                                            <i class="fas fa-eye"></i>
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                            <%
                                                    }
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

        <!-- Modal para editar estado -->
        <div class="modal fade" id="editModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="donationManagement" method="post">
                        <input type="hidden" name="action" value="updateStatus">
                        <input type="hidden" name="donationId" id="editDonationId">

                        <div class="modal-header">
                            <h5 class="modal-title">Editar Estado de Donación</h5>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label">Nuevo Estado</label>
                                <select name="status" class="form-select" id="editStatus">
                                    <option value="active">Activa</option>
                                    <option value="completed">Completada</option>
                                    <option value="cancelled">Cancelada</option>
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

        <script>
            function showEditModal(donationId, currentStatus) {
                document.getElementById('editDonationId').value = donationId;
                document.getElementById('editStatus').value = currentStatus;
                new bootstrap.Modal(document.getElementById('editModal')).show();
            }
        </script>
    </body>
</html>

<%!
    private String getStatusBadgeColor(String status) {
        switch (status) {
            case "active":
                return "success";
            case "completed":
                return "primary";
            case "cancelled":
                return "danger";
            default:
                return "secondary";
        }
    }
%>