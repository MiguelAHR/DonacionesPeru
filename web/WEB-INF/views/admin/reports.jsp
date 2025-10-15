<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reportes - Administrador</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="../admin/sidebar.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <h2 class="fw-bold mb-4">Reportes del Sistema</h2>
                    
                    <!-- Estadísticas Principales -->
                    <div class="row g-4 mb-4">
                        <div class="col-md-3">
                            <div class="card text-white bg-primary">
                                <div class="card-body text-center">
                                    <i class="fas fa-gift fa-2x mb-2"></i>
                                    <h3>${totalDonations}</h3>
                                    <p class="mb-0">Total Donaciones</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-success">
                                <div class="card-body text-center">
                                    <i class="fas fa-hand-holding-heart fa-2x mb-2"></i>
                                    <h3>${totalDonors}</h3>
                                    <p class="mb-0">Donadores</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-warning">
                                <div class="card-body text-center">
                                    <i class="fas fa-hands-helping fa-2x mb-2"></i>
                                    <h3>${totalReceivers}</h3>
                                    <p class="mb-0">Receptores</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-info">
                                <div class="card-body text-center">
                                    <i class="fas fa-chart-line fa-2x mb-2"></i>
                                    <h3>${recentDonations}</h3>
                                    <p class="mb-0">Último Mes</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Reportes Detallados -->
                    <div class="row g-4">
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-primary text-white">
                                    <h5 class="mb-0"><i class="fas fa-chart-pie me-2"></i>Donaciones por Tipo</h5>
                                </div>
                                <div class="card-body">
                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <th>Tipo</th>
                                                <th>Cantidad</th>
                                                <th>Porcentaje</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                            Map<String, Long> donationsByType = (Map<String, Long>) request.getAttribute("donationsByType");
                                            int total = (Integer) request.getAttribute("totalDonations");
                                            if (donationsByType != null) {
                                                for (Map.Entry<String, Long> entry : donationsByType.entrySet()) {
                                                    double percentage = (entry.getValue() * 100.0) / total;
                                            %>
                                            <tr>
                                                <td><%= entry.getKey() %></td>
                                                <td><%= entry.getValue() %></td>
                                                <td><%= String.format("%.1f%%", percentage) %></td>
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
                        
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-success text-white">
                                    <h5 class="mb-0"><i class="fas fa-map-marker-alt me-2"></i>Donaciones por Ubicación</h5>
                                </div>
                                <div class="card-body">
                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <th>Ubicación</th>
                                                <th>Cantidad</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                            Map<String, Long> donationsByLocation = (Map<String, Long>) request.getAttribute("donationsByLocation");
                                            if (donationsByLocation != null) {
                                                for (Map.Entry<String, Long> entry : donationsByLocation.entrySet()) {
                                            %>
                                            <tr>
                                                <td><%= entry.getKey() %></td>
                                                <td><%= entry.getValue() %></td>
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
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>