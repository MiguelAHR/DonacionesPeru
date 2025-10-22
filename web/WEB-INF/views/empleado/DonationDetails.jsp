<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.donaciones.models.Donation"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalles de Donación - Empleado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_empleado.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2 class="fw-bold mb-0">Detalles de Donación</h2>
                        <a href="employeeDonations" class="btn btn-outline-primary">
                            <i class="fas fa-arrow-left me-2"></i>Volver
                        </a>
                    </div>

                    <%
                        Donation donation = (Donation) request.getAttribute("donation");
                        if (donation != null) {
                    %>
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0">Donación #<%= donation.getId() %></h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6>Información General</h6>
                                    <table class="table table-bordered">
                                        <tr>
                                            <th width="40%">Tipo:</th>
                                            <td><%= donation.getType() %></td>
                                        </tr>
                                        <tr>
                                            <th>Descripción:</th>
                                            <td><%= donation.getDescription() %></td>
                                        </tr>
                                        <tr>
                                            <th>Cantidad:</th>
                                            <td><%= donation.getQuantity() %></td>
                                        </tr>
                                        <tr>
                                            <th>Condición:</th>
                                            <td><%= donation.getCondition() %></td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="col-md-6">
                                    <h6>Información de Entrega</h6>
                                    <table class="table table-bordered">
                                        <tr>
                                            <th width="40%">Ubicación:</th>
                                            <td><%= donation.getLocation() %></td>
                                        </tr>
                                        <tr>
                                            <th>Donador:</th>
                                            <td><%= donation.getDonorUsername() %></td>
                                        </tr>
                                        <tr>
                                            <th>Estado:</th>
                                            <td>
                                                <span class="badge bg-<%= 
                                                    "pending".equals(donation.getStatus()) ? "warning" :
                                                    "in_progress".equals(donation.getStatus()) ? "primary" :
                                                    "completed".equals(donation.getStatus()) ? "success" : "danger" %>">
                                                    <%= donation.getStatus() %>
                                                </span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Fecha Creación:</th>
                                            <td><%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(donation.getCreatedDate()) %></td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                            
                            <% if (donation.getAddress() != null && !donation.getAddress().isEmpty()) { %>
                            <div class="row mt-3">
                                <div class="col-12">
                                    <h6>Dirección de Entrega</h6>
                                    <div class="alert alert-info">
                                        <i class="fas fa-map-marker-alt me-2"></i>
                                        <%= donation.getAddress() %>
                                    </div>
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                    <% } else { %>
                    <div class="alert alert-danger">
                        No se encontró la donación solicitada.
                    </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</body>
</html>