<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.donaciones.models.User"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil - Empleado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .profile-header {
            background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
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
            <jsp:include page="sidebar_empleado.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <!-- Header -->
                <div class="profile-header">
                    <div class="container-fluid">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h1 class="fw-bold">Mi Perfil - Empleado</h1>
                                <p class="mb-0">Gestión de donaciones y ayuda comunitaria</p>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="bg-white text-warning rounded-circle d-inline-flex align-items-center justify-content-center" 
                                     style="width: 80px; height: 80px;">
                                    <i class="fas fa-user-tie fa-3x"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="p-4">
                    <!-- Estadísticas -->
                    <div class="row g-4 mb-4">
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-warning text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-gift fa-2x mb-2"></i>
                                    <h3>${myDonations}</h3>
                                    <p class="mb-0">Donaciones Asignadas</p>
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
                            <div class="card stat-card border-0 bg-info text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-calendar fa-2x mb-2"></i>
                                    <h5>${employeeSince}</h5>
                                    <p class="mb-0">Empleado desde</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-secondary text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-sign-in-alt fa-2x mb-2"></i>
                                    <h5><%= new java.text.SimpleDateFormat("dd/MM/yy").format(new java.util.Date()) %></h5>
                                    <p class="mb-0">Último acceso</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Información Personal -->
                    <div class="row">
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-warning text-white">
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
                                    </table>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-info text-white">
                                    <h5 class="mb-0"><i class="fas fa-chart-bar me-2"></i>Actividad Reciente</h5>
                                </div>
                                <div class="card-body">
                                    <div class="mb-3">
                                        <small class="text-muted">Donación procesada</small>
                                        <div class="progress mb-2" style="height: 8px;">
                                            <div class="progress-bar bg-success" style="width: 75%"></div>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <small class="text-muted">Receptores registrados</small>
                                        <div class="progress mb-2" style="height: 8px;">
                                            <div class="progress-bar bg-warning" style="width: 60%"></div>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <small class="text-muted">Donadores contactados</small>
                                        <div class="progress mb-2" style="height: 8px;">
                                            <div class="progress-bar bg-primary" style="width: 90%"></div>
                                        </div>
                                    </div>
                                    <div class="text-center">
                                        <a href="${pageContext.request.contextPath}/reports" class="btn btn-outline-info btn-sm">
                                            Ver Reportes Completos
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Acciones Rápidas -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header bg-light">
                                    <h5 class="mb-0"><i class="fas fa-bolt me-2"></i>Acciones Rápidas</h5>
                                </div>
                                <div class="card-body">
                                    <div class="row g-3">
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-success w-100">
                                                <i class="fas fa-plus me-2"></i>Nueva Donación
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/users?action=newDonor" class="btn btn-primary w-100">
                                                <i class="fas fa-user-plus me-2"></i>Nuevo Donador
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/users?action=newReceiver" class="btn btn-warning w-100">
                                                <i class="fas fa-user-plus me-2"></i>Nuevo Receptor
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/donations?action=list" class="btn btn-info w-100">
                                                <i class="fas fa-list me-2"></i>Ver Donaciones
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
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>