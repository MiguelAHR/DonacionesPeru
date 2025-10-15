<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Administrador - Donaciones Perú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .sidebar {
            min-height: 100vh;
            background: linear-gradient(180deg, #dc3545 0%, #c82333 100%);
        }
        .sidebar .nav-link {
            color: rgba(255,255,255,0.8);
            padding: 15px 20px;
            border-radius: 10px;
            margin: 5px 10px;
            transition: all 0.3s ease;
        }
        .sidebar .nav-link:hover, .sidebar .nav-link.active {
            background: rgba(255,255,255,0.2);
            color: white;
            transform: translateX(5px);
        }
        .stat-card {
            background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            transition: transform 0.3s ease;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
        .main-content {
            background-color: #f8f9fa;
            min-height: 100vh;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <jsp:include page="../admin/sidebar.jsp" />

            <!-- Contenedor -->
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <!-- Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="fw-bold text-dark">Dashboard Administrativo</h2>
                            <p class="text-muted mb-0">
                                <i class="fas fa-calendar me-2"></i>
                                <%= new java.text.SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "PE")).format(new java.util.Date()) %>
                            </p>
                        </div>
                        <div class="text-end">
                            <span class="badge bg-success fs-6 px-3 py-2">
                                <i class="fas fa-circle me-2"></i>Sistema Activo
                            </span>
                        </div>
                    </div>

                    <!-- Estadisticas -->
                    <div class="row g-4 mb-5">
                        <div class="col-md-2">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-gift fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-primary">
                                        <%= request.getAttribute("totalDonations") != null ? request.getAttribute("totalDonations") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Total Donaciones</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-2">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-success text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-check-circle fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-success">
                                        <%= request.getAttribute("activeDonations") != null ? request.getAttribute("activeDonations") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Activas</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-2">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-warning text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-clock fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-warning">
                                        <%= request.getAttribute("pendingDonations") != null ? request.getAttribute("pendingDonations") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Pendientes</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-2">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-info text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-hand-holding-heart fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-info">
                                        <%= request.getAttribute("totalDonors") != null ? request.getAttribute("totalDonors") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Donadores</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-2">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-danger text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-hands-helping fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-danger">
                                        <%= request.getAttribute("totalReceivers") != null ? request.getAttribute("totalReceivers") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Receptores</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-2">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-secondary text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-users fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-secondary">
                                        <%= request.getAttribute("totalUsers") != null ? request.getAttribute("totalUsers") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Total Usuarios</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Donaciones Recientes -->
                    <div class="row g-4">
                        <div class="col-md-6">
                            <div class="card border-0 shadow-sm">
                                <div class="card-header bg-white border-0 py-3">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-clock me-2 text-primary"></i>
                                        Donaciones Recientes
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <% 
                                    List<Donation> recentDonations = (List<Donation>) request.getAttribute("recentDonations");
                                    if (recentDonations != null && !recentDonations.isEmpty()) {
                                        for (Donation donation : recentDonations) {
                                    %>
                                        <div class="d-flex align-items-center mb-3 p-3 bg-light rounded">
                                            <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 40px; height: 40px;">
                                                <i class="fas fa-gift fa-sm"></i>
                                            </div>
                                            <div class="flex-grow-1">
                                                <p class="mb-0 fw-bold"><%= donation.getType() %></p>
                                                <small class="text-muted"><%= donation.getDescription() %></small>
                                                <br>
                                                <small class="text-muted">
                                                    <i class="fas fa-map-marker-alt me-1"></i><%= donation.getLocation() %>
                                                    • <i class="fas fa-calendar me-1"></i>
                                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(donation.getCreatedDate()) %>
                                                </small>
                                            </div>
                                            <span class="badge bg-<%= "active".equals(donation.getStatus()) ? "success" : "warning" %>">
                                                <%= donation.getStatus() %>
                                            </span>
                                        </div>
                                    <% 
                                        }
                                    } else {
                                    %>
                                        <div class="text-center py-5">
                                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                            <p class="text-muted">No hay donaciones recientes</p>
                                        </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="card border-0 shadow-sm">
                                <div class="card-header bg-white border-0 py-3">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-bell me-2 text-success"></i>
                                        Actividad Reciente
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <% 
                                    List<String> recentActivity = (List<String>) request.getAttribute("recentActivity");
                                    if (recentActivity != null && !recentActivity.isEmpty()) {
                                        for (String activity : recentActivity) {
                                    %>
                                        <div class="d-flex align-items-center mb-3 p-3 bg-light rounded">
                                            <div class="bg-success text-white rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 40px; height: 40px;">
                                                <i class="fas fa-bell fa-sm"></i>
                                            </div>
                                            <div>
                                                <p class="mb-0"><%= activity %></p>
                                                <small class="text-muted">Hace unos minutos</small>
                                            </div>
                                        </div>
                                    <% 
                                        }
                                    } else {
                                    %>
                                        <div class="text-center py-5">
                                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                            <p class="text-muted">No hay actividad reciente</p>
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>