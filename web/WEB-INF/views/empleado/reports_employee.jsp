<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Reportes - Empleado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .stat-card {
            border-radius: 10px;
            transition: transform 0.3s ease;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="../empleado/sidebar_empleado.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <h2 class="fw-bold mb-4">Mis Reportes de Actividad</h2>
                    <p class="text-muted">Reportes específicos de tus actividades como empleado</p>

                    <!-- Estadísticas del Empleado -->
                    <div class="row g-4 mb-4">
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-primary text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-gift fa-2x mb-2"></i>
                                    <h3>${myDonations}</h3>
                                    <p class="mb-0">Mis Donaciones</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-success text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-check-circle fa-2x mb-2"></i>
                                    <h3>${activeDonations}</h3>
                                    <p class="mb-0">Activas</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-warning text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-clipboard-check fa-2x mb-2"></i>
                                    <h3>${completedDonations}</h3>
                                    <p class="mb-0">Completadas</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-info text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-chart-line fa-2x mb-2"></i>
                                    <h3>${totalDonations}</h3>
                                    <p class="mb-0">Total Sistema</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Reportes Detallados -->
                    <div class="row g-4">
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-warning text-white">
                                    <h5 class="mb-0">
                                        <i class="fas fa-chart-pie me-2"></i>
                                        Mis Donaciones por Tipo
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <th>Tipo de Donación</th>
                                                <th>Cantidad</th>
                                                <th>Porcentaje</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                            Map<String, Long> myDonationsByType = (Map<String, Long>) request.getAttribute("myDonationsByType");
                                            Long totalMyDonations = (Long) request.getAttribute("myDonations");
                                            if (myDonationsByType != null && totalMyDonations != null && totalMyDonations > 0) {
                                                for (Map.Entry<String, Long> entry : myDonationsByType.entrySet()) {
                                                    double percentage = (entry.getValue() * 100.0) / totalMyDonations;
                                            %>
                                            <tr>
                                                <td><%= entry.getKey() %></td>
                                                <td><%= entry.getValue() %></td>
                                                <td><%= String.format("%.1f%%", percentage) %></td>
                                            </tr>
                                            <%
                                                }
                                            } else {
                                            %>
                                            <tr>
                                                <td colspan="3" class="text-center text-muted">No hay datos disponibles</td>
                                            </tr>
                                            <%
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
                                    <h5 class="mb-0">
                                        <i class="fas fa-map-marker-alt me-2"></i>
                                        Mis Donaciones por Ubicación
                                    </h5>
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
                                            Map<String, Long> myDonationsByLocation = (Map<String, Long>) request.getAttribute("myDonationsByLocation");
                                            if (myDonationsByLocation != null && !myDonationsByLocation.isEmpty()) {
                                                for (Map.Entry<String, Long> entry : myDonationsByLocation.entrySet()) {
                                            %>
                                            <tr>
                                                <td><%= entry.getKey() %></td>
                                                <td><%= entry.getValue() %></td>
                                            </tr>
                                            <%
                                                }
                                            } else {
                                            %>
                                            <tr>
                                                <td colspan="2" class="text-center text-muted">No hay datos disponibles</td>
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

                    <!-- Información General -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header bg-light">
                                    <h5 class="mb-0">
                                        <i class="fas fa-info-circle me-2"></i>
                                        Información General del Sistema
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-4 text-center">
                                            <h4 class="text-primary">${totalDonations}</h4>
                                            <p class="text-muted">Total Donaciones en Sistema</p>
                                        </div>
                                        <div class="col-md-4 text-center">
                                            <h4 class="text-success">${totalDonors}</h4>
                                            <p class="text-muted">Total Donadores Registrados</p>
                                        </div>
                                        <div class="col-md-4 text-center">
                                            <h4 class="text-warning">${employeeUsername}</h4>
                                            <p class="text-muted">Tu usuario</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Exportar Reportes -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body text-center">
                                    <h5>Exportar Mis Reportes</h5>
                                    <p class="text-muted">Descarga un resumen de tu actividad</p>
                                    <button class="btn btn-outline-primary me-2">
                                        <i class="fas fa-file-pdf me-2"></i>Exportar PDF
                                    </button>
                                    <button class="btn btn-outline-success">
                                        <i class="fas fa-file-excel me-2"></i>Exportar Excel
                                    </button>
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