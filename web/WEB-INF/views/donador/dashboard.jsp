<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Donador - Donaciones Perú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .profile-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 60px 0;
        }
        .profile-card {
            margin-top: -50px;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }
        .nav-pills .nav-link {
            border-radius: 50px;
            padding: 12px 25px;
            margin: 0 5px;
            transition: all 0.3s ease;
        }
        .nav-pills .nav-link.active {
            background: linear-gradient(45deg, #28a745, #20c997);
            color: white;
        }
        .donation-card {
            border-radius: 15px;
            transition: transform 0.3s ease;
        }
        .donation-card:hover {
            transform: translateY(-5px);
        }
        .status-badge {
            font-size: 0.8rem;
            padding: 8px 15px;
            border-radius: 20px;
        }
    </style>
</head>
<body class="bg-light">
    <!-- Mostrar mensajes -->
    <div class="container mt-3">
        <% 
            String success = request.getParameter("success");
            String error = request.getParameter("error");
            String tabParam = request.getParameter("tab");
            
            if (success != null) {
        %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                <% 
                    if ("donation_created".equals(success)) {
                        out.print("¡Donación creada exitosamente!");
                    } else {
                        out.print("Operación completada exitosamente");
                    }
                %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>
        
        <% if (error != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>
                <% 
                    if ("missing_fields".equals(error)) {
                        out.print("Por favor, completa todos los campos requeridos");
                    } else if ("invalid_quantity".equals(error)) {
                        out.print("La cantidad debe ser un número válido mayor a 0");
                    } else {
                        out.print("Ha ocurrido un error. Intenta nuevamente");
                    }
                %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>
    </div>

    <!-- Header -->
    <div class="profile-header">
        <div class="container text-center">
            <div class="mb-3">
                <div class="bg-white text-success rounded-circle d-inline-flex align-items-center justify-content-center" style="width: 100px; height: 100px;">
                    <i class="fas fa-hands-helping fa-3x"></i>
                </div>
            </div>
            <h2 class="fw-bold">Bienvenido, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "Donador" %></h2>
            <p class="mb-0">Donador - Sistema de Donaciones Perú</p>
        </div>
    </div>

    <div class="container">
        <div class="card profile-card border-0">
            <div class="card-body p-4">
                <ul class="nav nav-pills justify-content-center mb-4" id="profileTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link <%= (tabParam == null || "dashboard".equals(tabParam)) ? "active" : "" %>" 
                                id="dashboard-tab" data-bs-toggle="pill" data-bs-target="#dashboard" 
                                type="button" role="tab" aria-controls="dashboard" 
                                aria-selected="<%= (tabParam == null || "dashboard".equals(tabParam)) ? "true" : "false" %>">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link <%= "donations".equals(tabParam) ? "active" : "" %>" 
                                id="donations-tab" data-bs-toggle="pill" data-bs-target="#donations" 
                                type="button" role="tab" aria-controls="donations" 
                                aria-selected="<%= "donations".equals(tabParam) ? "true" : "false" %>">
                            <i class="fas fa-gift me-2"></i>Mis Donaciones
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link <%= "profile".equals(tabParam) ? "active" : "" %>" 
                                id="profile-tab" data-bs-toggle="pill" data-bs-target="#profile" 
                                type="button" role="tab" aria-controls="profile" 
                                aria-selected="<%= "profile".equals(tabParam) ? "true" : "false" %>">
                            <i class="fas fa-user me-2"></i>Mi Perfil
                        </button>
                    </li>
                </ul>

                <!-- Contenido de las pestañas -->
                <div class="tab-content" id="profileTabsContent">
                    <!-- Dashboard -->
                    <div class="tab-pane fade <%= (tabParam == null || "dashboard".equals(tabParam)) ? "show active" : "" %>" 
                         id="dashboard" role="tabpanel" aria-labelledby="dashboard-tab">
                        
                        <!-- Estadísticas -->
                        <div class="row mb-4">
                            <div class="col-md-4">
                                <div class="card text-white bg-success">
                                    <div class="card-body text-center">
                                        <i class="fas fa-gift fa-2x mb-2"></i>
                                        <h3><%= request.getAttribute("totalDonations") != null ? request.getAttribute("totalDonations") : "0" %></h3>
                                        <p class="mb-0">Total Donaciones</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-warning">
                                    <div class="card-body text-center">
                                        <i class="fas fa-clock fa-2x mb-2"></i>
                                        <h3><%= request.getAttribute("donacionesPendientes") != null ? request.getAttribute("donacionesPendientes") : "0" %></h3>
                                        <p class="mb-0">Pendientes</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-primary">
                                    <div class="card-body text-center">
                                        <i class="fas fa-check-circle fa-2x mb-2"></i>
                                        <h3><%= request.getAttribute("donacionesCompletadas") != null ? request.getAttribute("donacionesCompletadas") : "0" %></h3>
                                        <p class="mb-0">Completadas</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Acciones Rápidas -->
                        <div class="card border-0 bg-light mb-4">
                            <div class="card-body text-center p-4">
                                <h5 class="fw-bold mb-3">
                                    <i class="fas fa-rocket me-2 text-success"></i>
                                    Acciones Rápidas
                                </h5>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <!-- CORREGIDO: Enlace a nueva donación -->
                                        <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-success btn-lg w-100 py-3">
                                            <i class="fas fa-plus me-2"></i>Nueva Donación
                                        </a>
                                    </div>
                                    <div class="col-md-6">
                                        <!-- CORREGIDO: Enlace a mis donaciones -->
                                        <a href="${pageContext.request.contextPath}/donations?action=myDonations" class="btn btn-outline-success btn-lg w-100 py-3">
                                            <i class="fas fa-history me-2"></i>Ver Historial
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Mis Donaciones -->
                    <div class="tab-pane fade <%= "donations".equals(tabParam) ? "show active" : "" %>" 
                         id="donations" role="tabpanel" aria-labelledby="donations-tab">
                        <div class="row">
                            <div class="col-12">
                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-gift me-2 text-success"></i>
                                        Mis Donaciones Realizadas
                                    </h5>
                                    <!-- CORREGIDO: Enlace a nueva donación -->
                                    <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-success">
                                        <i class="fas fa-plus me-2"></i>Nueva Donación
                                    </a>
                                </div>

                                <% 
                                List<Donation> donaciones = (List<Donation>) request.getAttribute("donaciones");
                                if (donaciones != null && !donaciones.isEmpty()) {
                                    for (Donation donation : donaciones) {
                                %>
                                    <div class="card donation-card border-0 shadow-sm mb-3">
                                        <div class="card-body">
                                            <div class="row align-items-center">
                                                <div class="col-md-2 text-center">
                                                    <div class="bg-success text-white rounded-circle d-inline-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                                                        <i class="fas fa-gift"></i>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <h6 class="fw-bold mb-1"><%= donation.getType() != null ? donation.getType() : "Sin tipo" %></h6>
                                                    <p class="text-muted mb-1"><%= donation.getDescription() != null ? donation.getDescription() : "Sin descripción" %></p>
                                                    <small class="text-muted">
                                                        <i class="fas fa-map-marker-alt me-1"></i>
                                                        <%= donation.getLocation() != null ? donation.getLocation() : "Sin ubicación" %>
                                                    </small>
                                                </div>
                                                <div class="col-md-2 text-center">
                                                    <span class="fw-bold">Cantidad: <%= donation.getQuantity() %></span>
                                                </div>
                                                <div class="col-md-2 text-center">
                                                    <span class="status-badge <%= 
                                                        "pending".equals(donation.getStatus()) ? "bg-warning" : 
                                                        "completed".equals(donation.getStatus()) ? "bg-success" : 
                                                        "in_progress".equals(donation.getStatus()) ? "bg-info" : "bg-secondary" %>">
                                                        <%= donation.getStatus() != null ? donation.getStatus() : "Desconocido" %>
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                <% 
                                    }
                                } else {
                                %>
                                    <div class="text-center py-5">
                                        <i class="fas fa-gift fa-4x text-muted mb-3"></i>
                                        <h5 class="text-muted">No has realizado donaciones aún</h5>
                                        <p class="text-muted">¡Comienza a ayudar a tu comunidad!</p>
                                        <!-- CORREGIDO: Enlace a nueva donación -->
                                        <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-success btn-lg">
                                            <i class="fas fa-plus me-2"></i>Hacer Mi Primera Donación
                                        </a>
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <!-- Perfil -->
                    <div class="tab-pane fade <%= "profile".equals(tabParam) ? "show active" : "" %>" 
                         id="profile" role="tabpanel" aria-labelledby="profile-tab">
                        <div class="row">
                            <div class="col-md-8 mx-auto">
                                <div class="card border-0 bg-light">
                                    <div class="card-body p-4">
                                        <h5 class="fw-bold mb-4">
                                            <i class="fas fa-id-card me-2 text-success"></i>
                                            Información del Donador
                                        </h5>
                                        
                                        <div class="row g-3">
                                            <div class="col-md-6">
                                                <label class="form-label fw-bold">Usuario</label>
                                                <div class="form-control bg-white">
                                                    <%= session.getAttribute("username") != null ? session.getAttribute("username") : "N/A" %>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-bold">Tipo de Usuario</label>
                                                <div class="form-control bg-white">
                                                    <span class="badge bg-success">Donador</span>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-bold">Fecha de Registro</label>
                                                <div class="form-control bg-white">
                                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()) %>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-bold">Estado</label>
                                                <div class="form-control bg-white">
                                                    <span class="badge bg-success">Activo</span>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="text-center mt-4">
                                            <!-- CORREGIDO: Enlace a nueva donación -->
                                            <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-success btn-lg px-4">
                                                <i class="fas fa-plus me-2"></i>Hacer Nueva Donación
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Logout -->
        <div class="text-center my-4">
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger">
                <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Función para cambiar de pestaña
        function switchToTab(tabName) {
            const tabElement = document.getElementById(tabName + '-tab');
            if (tabElement) {
                const tab = new bootstrap.Tab(tabElement);
                tab.show();
            }
        }

        // Activar pestañas según parámetro URL
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const tabParam = urlParams.get('tab');
            
            if (tabParam) {
                switchToTab(tabParam);
            }
            
            // Auto-ocultar alertas después de 5 segundos
            setTimeout(() => {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(alert => {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);
        });
    </script>
</body>
</html>