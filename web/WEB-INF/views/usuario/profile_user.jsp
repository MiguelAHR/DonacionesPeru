<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil - Donaciones Perú</title>
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
        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                <% 
                    String successType = request.getParameter("success");
                    if ("donation_created".equals(successType)) {
                        out.print("¡Donación creada exitosamente!");
                    } else if ("request_created".equals(successType)) {
                        out.print("¡Solicitud creada exitosamente!");
                    } else {
                        out.print("Operación completada exitosamente");
                    }
                %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>
        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>
                <% 
                    String errorType = request.getParameter("error");
                    if ("missing_fields".equals(errorType)) {
                        out.print("Por favor, completa todos los campos requeridos");
                    } else if ("invalid_quantity".equals(errorType)) {
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
                    <i class="fas fa-user fa-3x"></i>
                </div>
            </div>
            <h2 class="fw-bold">Bienvenido, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "Usuario" %></h2>
            <p class="mb-0">Usuario del Sistema de Donaciones Perú</p>
        </div>
    </div>

    <div class="container">
        <div class="card profile-card border-0">
            <div class="card-body p-4">
                <ul class="nav nav-pills justify-content-center mb-4" id="profileTabs">
                    <li class="nav-item">
                        <a class="nav-link active" data-bs-toggle="pill" href="#profile">
                            <i class="fas fa-user me-2"></i>Mi Perfil
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" data-bs-toggle="pill" href="#donations">
                            <i class="fas fa-gift me-2"></i>Mis Donaciones
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" data-bs-toggle="pill" href="#requests">
                            <i class="fas fa-hand-holding-heart me-2"></i>Mis Solicitudes
                        </a>
                    </li>
                </ul>

                <!-- Contenedor -->
                <div class="tab-content">
                    <div class="tab-pane fade show active" id="profile">
                        <div class="row">
                            <div class="col-md-8 mx-auto">
                                <div class="card border-0 bg-light">
                                    <div class="card-body p-4">
                                        <h5 class="fw-bold mb-4">
                                            <i class="fas fa-id-card me-2 text-success"></i>
                                            Información Personal
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
                                                    <span class="badge bg-success">
                                                        <%= session.getAttribute("userType") != null ? session.getAttribute("userType") : "Usuario" %>
                                                    </span>
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
                                            <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-success btn-lg px-4">
                                                <i class="fas fa-plus me-2"></i>Hacer Nueva Donación
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="tab-pane fade" id="donations">
                        <div class="row">
                            <div class="col-12">
                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-gift me-2 text-success"></i>
                                        Mis Donaciones Realizadas
                                    </h5>
                                    <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-success">
                                        <i class="fas fa-plus me-2"></i>Nueva Donación
                                    </a>
                                </div>

                                <% 
                                List<Donation> userDonations = (List<Donation>) request.getAttribute("userDonations");
                                if (userDonations != null && !userDonations.isEmpty()) {
                                    for (Donation donation : userDonations) {
                                        if (donation != null) {
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
                                                    <span class="status-badge bg-success text-white">
                                                        <%= donation.getStatus() != null ? donation.getStatus() : "Desconocido" %>
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                <% 
                                        }
                                    }
                                } else {
                                %>
                                    <div class="text-center py-5">
                                        <i class="fas fa-gift fa-4x text-muted mb-3"></i>
                                        <h5 class="text-muted">No has realizado donaciones aún</h5>
                                        <p class="text-muted">¡Comienza a ayudar a tu comunidad!</p>
                                        <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-success btn-lg">
                                            <i class="fas fa-plus me-2"></i>Hacer Mi Primera Donación
                                        </a>
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <div class="tab-pane fade" id="requests">
                        <div class="row">
                            <div class="col-12">
                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-hand-holding-heart me-2 text-warning"></i>
                                        Mis Solicitudes de Ayuda
                                    </h5>
                                    <button class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#newRequestModal">
                                        <i class="fas fa-plus me-2"></i>Nueva Solicitud
                                    </button>
                                </div>

                                <% 
                                List<Request> userRequests = (List<Request>) request.getAttribute("userRequests");
                                if (userRequests != null && !userRequests.isEmpty()) {
                                    for (Request requestItem : userRequests) {
                                        if (requestItem != null) {
                                %>
                                    <div class="card donation-card border-0 shadow-sm mb-3">
                                        <div class="card-body">
                                            <div class="row align-items-center">
                                                <div class="col-md-2 text-center">
                                                    <div class="bg-warning text-white rounded-circle d-inline-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                                                        <i class="fas fa-hand-holding-heart"></i>
                                                    </div>
                                                </div>
                                                <div class="col-md-8">
                                                    <h6 class="fw-bold mb-1"><%= requestItem.getType() != null ? requestItem.getType() : "Sin tipo" %></h6>
                                                    <p class="text-muted mb-1"><%= requestItem.getDescription() != null ? requestItem.getDescription() : "Sin descripción" %></p>
                                                    <small class="text-muted">
                                                        <i class="fas fa-map-marker-alt me-1"></i>
                                                        <%= requestItem.getLocation() != null ? requestItem.getLocation() : "Sin ubicación" %>
                                                    </small>
                                                </div>
                                                <div class="col-md-2 text-center">
                                                    <span class="status-badge bg-warning text-white">
                                                        <%= requestItem.getStatus() != null ? requestItem.getStatus() : "Pendiente" %>
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                <% 
                                        }
                                    }
                                } else {
                                %>
                                    <div class="text-center py-5">
                                        <i class="fas fa-hand-holding-heart fa-4x text-muted mb-3"></i>
                                        <h5 class="text-muted">No tienes solicitudes activas</h5>
                                        <p class="text-muted">Si necesitas ayuda, puedes crear una solicitud</p>
                                        <button class="btn btn-warning btn-lg" data-bs-toggle="modal" data-bs-target="#newRequestModal">
                                            <i class="fas fa-plus me-2"></i>Crear Solicitud
                                        </button>
                                    </div>
                                <% } %>
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

    <!-- Modal para nueva solicitud -->
    <div class="modal fade" id="newRequestModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-warning text-white">
                    <h5 class="modal-title">
                        <i class="fas fa-plus me-2"></i>Nueva Solicitud de Ayuda
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/donations" method="post">
                    <input type="hidden" name="action" value="newRequest">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="requestType" class="form-label fw-bold">Tipo de Ayuda Necesaria</label>
                            <select class="form-select" id="requestType" name="requestType" required>
                                <option value="">Seleccionar...</option>
                                <option value="ropa">Ropa</option>
                                <option value="cuadernos">Cuadernos</option>
                                <option value="utiles_escolares">Útiles Escolares</option>
                                <option value="material_reciclable">Material Reciclable</option>
                                <option value="ropa_casi_nueva">Ropa Casi Nueva</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="requestDescription" class="form-label fw-bold">Descripción de la Necesidad</label>
                            <textarea class="form-control" id="requestDescription" name="requestDescription" rows="3" required placeholder="Describe qué necesitas y por qué..."></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="requestLocation" class="form-label fw-bold">Ubicación</label>
                            <select class="form-select" id="requestLocation" name="requestLocation" required>
                                <option value="">Seleccionar región...</option>
                                <option value="Lima">Lima</option>
                                <option value="Arequipa">Arequipa</option>
                                <option value="Cusco">Cusco</option>
                                <option value="Trujillo">Trujillo</option>
                                <option value="Chiclayo">Chiclayo</option>
                                <option value="Piura">Piura</option>
                                <option value="Iquitos">Iquitos</option>
                                <option value="Huancayo">Huancayo</option>
                                <option value="Tacna">Tacna</option>
                                <option value="Ayacucho">Ayacucho</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-warning">
                            <i class="fas fa-paper-plane me-2"></i>Enviar Solicitud
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Activar pestañas
        document.addEventListener('DOMContentLoaded', function() {
            console.log("DEBUG - Página de perfil cargada");
            
            const urlParams = new URLSearchParams(window.location.search);
            const tabParam = urlParams.get('tab');
            
            console.log("DEBUG - tabParam from URL:", tabParam);
            
            if (tabParam) {
                const tabLink = document.querySelector(`a[href="#${tabParam}"]`);
                if (tabLink) {
                    console.log("DEBUG - Found tab link, activating:", tabParam);
                    const tab = new bootstrap.Tab(tabLink);
                    tab.show();
                }
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