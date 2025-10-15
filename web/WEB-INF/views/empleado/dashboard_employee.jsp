<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Empleado - Donaciones Perú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .sidebar {
            min-height: 100vh;
            background: linear-gradient(180deg, #ffc107 0%, #e0a800 100%);
        }
        .sidebar .nav-link {
            color: rgba(0,0,0,0.7);
            padding: 15px 20px;
            border-radius: 10px;
            margin: 5px 10px;
            transition: all 0.3s ease;
        }
        .sidebar .nav-link:hover, .sidebar .nav-link.active {
            background: rgba(0,0,0,0.1);
            color: #000;
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
            <jsp:include page="../empleado/sidebar_empleado.jsp" />

            <!-- Contenedor -->
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <!-- Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="fw-bold text-dark">Panel de Empleado</h2>
                            <p class="text-muted mb-0">
                                <i class="fas fa-calendar me-2"></i>
                                <%= new java.text.SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "PE")).format(new java.util.Date()) %>
                            </p>
                        </div>
                        <div class="text-end">
                            <span class="badge bg-warning fs-6 px-3 py-2 text-dark">
                                <i class="fas fa-user-tie me-2"></i>Empleado Activo
                            </span>
                        </div>
                    </div>

                    <!-- Estadisticas -->
                    <div class="row g-4 mb-5">
                        <div class="col-md-4">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-success text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-hand-holding-heart fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-success">
                                        <%= request.getAttribute("myDonors") != null ? request.getAttribute("myDonors") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Donadores Registrados</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-4">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-warning text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-hands-helping fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-warning">
                                        <%= request.getAttribute("myReceivers") != null ? request.getAttribute("myReceivers") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Receptores Registrados</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-4">
                            <div class="card stat-card h-100">
                                <div class="card-body text-center">
                                    <div class="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                        <i class="fas fa-gift fa-lg"></i>
                                    </div>
                                    <h3 class="fw-bold text-primary">
                                        <%= request.getAttribute("myDonations") != null ? request.getAttribute("myDonations") : "0" %>
                                    </h3>
                                    <p class="text-muted mb-0">Donaciones Gestionadas</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Menu -->
                    <div class="row g-4 mb-4">
                        <div class="col-12">
                            <div class="card border-0 shadow-sm">
                                <div class="card-header bg-white border-0 py-3">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-bolt me-2 text-warning"></i>
                                        Acciones Rápidas
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="row g-3">
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/users?action=newDonor" class="btn btn-success w-100 py-3">
                                                <i class="fas fa-user-plus fa-lg mb-2 d-block"></i>
                                                Registrar Donador
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/users?action=newReceiver" class="btn btn-warning w-100 py-3">
                                                <i class="fas fa-user-plus fa-lg mb-2 d-block"></i>
                                                Registrar Receptor
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-primary w-100 py-3">
                                                <i class="fas fa-gift fa-lg mb-2 d-block"></i>
                                                Nueva Donación
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/donations?action=list" class="btn btn-info w-100 py-3">
                                                <i class="fas fa-list fa-lg mb-2 d-block"></i>
                                                Ver Donaciones
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Tabla de opciones -->
                    <div class="row g-4">
                        <div class="col-md-8">
                            <div class="card border-0 shadow-sm">
                                <div class="card-header bg-white border-0 py-3">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-clock me-2 text-primary"></i>
                                        Mi Trabajo Reciente
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <% 
                                    List<String> recentWork = (List<String>) request.getAttribute("recentWork");
                                    if (recentWork != null && !recentWork.isEmpty()) {
                                        for (String work : recentWork) {
                                    %>
                                        <div class="d-flex align-items-center mb-3 p-3 bg-light rounded">
                                            <div class="bg-warning text-white rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 40px; height: 40px;">
                                                <i class="fas fa-check fa-sm"></i>
                                            </div>
                                            <div>
                                                <p class="mb-0"><%= work %></p>
                                                <small class="text-muted">Completado hoy</small>
                                            </div>
                                        </div>
                                    <% 
                                        }
                                    } else {
                                    %>
                                        <div class="text-center py-5">
                                            <i class="fas fa-clipboard-list fa-3x text-muted mb-3"></i>
                                            <p class="text-muted">No hay actividades recientes</p>
                                            <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-primary">
                                                Comenzar a trabajar
                                            </a>
                                        </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-4">
                            <div class="card border-0 shadow-sm">
                                <div class="card-header bg-white border-0 py-3">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-map-marker-alt me-2 text-danger"></i>
                                        Regiones Atendidas
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="mb-3">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <span class="text-muted">Lima</span>
                                            <span class="badge bg-primary">35</span>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <span class="text-muted">Arequipa</span>
                                            <span class="badge bg-success">12</span>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <span class="text-muted">Cusco</span>
                                            <span class="badge bg-warning">8</span>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <span class="text-muted">Trujillo</span>
                                            <span class="badge bg-info">5</span>
                                        </div>
                                    </div>
                                    
                                    <div class="text-center mt-3">
                                        <small class="text-muted">Total: 60 donaciones gestionadas</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Consejos -->
                    <div class="row g-4 mt-4">
                        <div class="col-12">
                            <div class="card border-0 shadow-sm bg-light">
                                <div class="card-body">
                                    <h6 class="fw-bold text-warning mb-3">
                                        <i class="fas fa-lightbulb me-2"></i>
                                        Consejos para Empleados
                                    </h6>
                                    <div class="row">
                                        <div class="col-md-4">
                                            <small class="text-muted">
                                                <i class="fas fa-check-circle text-success me-2"></i>
                                                Verifica siempre los datos de contacto
                                            </small>
                                        </div>
                                        <div class="col-md-4">
                                            <small class="text-muted">
                                                <i class="fas fa-check-circle text-success me-2"></i>
                                                Confirma la ubicación exacta
                                            </small>
                                        </div>
                                        <div class="col-md-4">
                                            <small class="text-muted">
                                                <i class="fas fa-check-circle text-success me-2"></i>
                                                Documenta el estado de las donaciones
                                            </small>
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