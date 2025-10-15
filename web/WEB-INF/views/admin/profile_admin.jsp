<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.donaciones.models.User"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil - Administrador</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .profile-header {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            color: white;
            padding: 40px 0;
        }
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
            <jsp:include page="sidebar.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <!-- Header -->
                <div class="profile-header">
                    <div class="container-fluid">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h1 class="fw-bold">Mi Perfil - Administrador</h1>
                                <p class="mb-0">Panel de control del sistema de donaciones</p>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="bg-white text-danger rounded-circle d-inline-flex align-items-center justify-content-center" 
                                     style="width: 80px; height: 80px;">
                                    <i class="fas fa-user-shield fa-3x"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="p-4">
                    <!-- Estadísticas del Admin -->
                    <div class="row g-4 mb-4">
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-primary text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-cogs fa-2x mb-2"></i>
                                    <h3>${totalActions}</h3>
                                    <p class="mb-0">Acciones Realizadas</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-success text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-shield-alt fa-2x mb-2"></i>
                                    <h5>${systemUptime}</h5>
                                    <p class="mb-0">Disponibilidad</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-warning text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-calendar fa-2x mb-2"></i>
                                    <h5><%= new java.text.SimpleDateFormat("dd/MM/yy").format(new java.util.Date()) %></h5>
                                    <p class="mb-0">Último acceso</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-info text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-user-tie fa-2x mb-2"></i>
                                    <h5>Administrador</h5>
                                    <p class="mb-0">Rol del Sistema</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Información Personal -->
                    <div class="row">
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-danger text-white">
                                    <h5 class="mb-0"><i class="fas fa-id-card me-2"></i>Información Personal</h5>
                                </div>
                                <div class="card-body">
                                    <table class="table table-borderless">
                                        <tr>
                                            <td><strong><i class="fas fa-user me-2"></i>Nombre:</strong></td>
                                            <td>${userProfile.firstName} ${userProfile.lastName}</td>
                                        </tr>
                                        <tr>
                                            <td><strong><i class="fas fa-at me-2"></i>Usuario:</strong></td>
                                            <td>${userProfile.username}</td>
                                        </tr>
                                        <tr>
                                            <td><strong><i class="fas fa-envelope me-2"></i>Email:</strong></td>
                                            <td>${userProfile.email}</td>
                                        </tr>
                                        <tr>
                                            <td><strong><i class="fas fa-phone me-2"></i>Teléfono:</strong></td>
                                            <td>${userProfile.phone}</td>
                                        </tr>
                                        <tr>
                                            <td><strong><i class="fas fa-map-marker-alt me-2"></i>Región:</strong></td>
                                            <td>${userProfile.region}</td>
                                        </tr>
                                        <tr>
                                            <td><strong><i class="fas fa-calendar me-2"></i>Último acceso:</strong></td>
                                            <td>${lastLogin}</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-info text-white">
                                    <h5 class="mb-0"><i class="fas fa-chart-bar me-2"></i>Estadísticas del Sistema</h5>
                                </div>
                                <div class="card-body">
                                    <div class="mb-3">
                                        <small class="text-muted">Usuarios registrados</small>
                                        <div class="progress mb-2" style="height: 8px;">
                                            <div class="progress-bar bg-primary" style="width: 85%"></div>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <small class="text-muted">Donaciones procesadas</small>
                                        <div class="progress mb-2" style="height: 8px;">
                                            <div class="progress-bar bg-success" style="width: 92%"></div>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <small class="text-muted">Solicitudes atendidas</small>
                                        <div class="progress mb-2" style="height: 8px;">
                                            <div class="progress-bar bg-warning" style="width: 78%"></div>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <small class="text-muted">Sistema activo</small>
                                        <div class="progress mb-2" style="height: 8px;">
                                            <div class="progress-bar bg-danger" style="width: 100%"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Acciones de Administración -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header bg-light">
                                    <h5 class="mb-0"><i class="fas fa-cogs me-2"></i>Acciones de Administración</h5>
                                </div>
                                <div class="card-body">
                                    <div class="row g-3">
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/userManagement?action=list" class="btn btn-primary w-100">
                                                <i class="fas fa-users-cog me-2"></i>Gestión de Usuarios
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/donationManagement" class="btn btn-success w-100">
                                                <i class="fas fa-gift me-2"></i>Gestión de Donaciones
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/requestManagement" class="btn btn-warning w-100">
                                                <i class="fas fa-list-alt me-2"></i>Solicitudes Pendientes
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/reports" class="btn btn-info w-100">
                                                <i class="fas fa-chart-bar me-2"></i>Reportes del Sistema
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Información del Sistema -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header bg-secondary text-white">
                                    <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Información del Sistema</h5>
                                </div>
                                <div class="card-body">
                                    <div class="row text-center">
                                        <div class="col-md-3">
                                            <i class="fas fa-database fa-2x text-primary mb-2"></i>
                                            <h6>Base de Datos</h6>
                                            <small class="text-muted">DataManager Activo</small>
                                        </div>
                                        <div class="col-md-3">
                                            <i class="fas fa-shield-alt fa-2x text-success mb-2"></i>
                                            <h6>Seguridad</h6>
                                            <small class="text-muted">Sistema Protegido</small>
                                        </div>
                                        <div class="col-md-3">
                                            <i class="fas fa-clock fa-2x text-warning mb-2"></i>
                                            <h6>Última Actualización</h6>
                                            <small class="text-muted"><%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) %></small>
                                        </div>
                                        <div class="col-md-3">
                                            <i class="fas fa-heart fa-2x text-danger mb-2"></i>
                                            <h6>Estado</h6>
                                            <small class="text-muted">Sistema Operativo</small>
                                        </div>
                                    </div>
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