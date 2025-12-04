<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.donaciones.models.User"%>
<%@page import="com.donaciones.utils.DataManager"%>
<%@page import="java.util.Map"%>
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
        .profile-picture {
            width: 150px;
            height: 150px;
            object-fit: cover;
            border: 5px solid white;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            transition: transform 0.3s ease;
        }
        .profile-picture:hover {
            transform: scale(1.05);
        }
        .upload-btn {
            position: relative;
            overflow: hidden;
            display: inline-block;
        }
        .upload-btn input[type=file] {
            position: absolute;
            left: 0;
            top: 0;
            opacity: 0;
            cursor: pointer;
            width: 100%;
            height: 100%;
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
                                <!-- Foto de perfil -->
                                <div class="position-relative">
                                    <img src="${profileImage != null ? profileImage : userProfile.profileImage}" 
                                         alt="Foto de perfil" 
                                         class="profile-picture rounded-circle"
                                         id="profileImagePreview">
                                    
                                    <!-- Botón para cambiar foto -->
                                    <button class="btn btn-light btn-sm position-absolute bottom-0 end-0 rounded-circle p-2" 
                                            style="width: 40px; height: 40px;"
                                            onclick="document.getElementById('profileImageInput').click()"
                                            title="Cambiar foto">
                                        <i class="fas fa-camera"></i>
                                    </button>
                                    
                                    <!-- Input oculto para subir archivo -->
                                    <form id="imageUploadForm" action="${pageContext.request.contextPath}/uploadProfileImage" 
                                          method="post" enctype="multipart/form-data" style="display: none;">
                                        <input type="hidden" name="action" value="upload">
                                        <input type="file" id="profileImageInput" name="profileImage" 
                                               accept="image/*" onchange="uploadProfileImage()">
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="p-4">
                    <!-- Mensajes de éxito/error -->
                    <% 
                        String success = request.getParameter("success");
                        String error = request.getParameter("error");
                        
                        if ("image_uploaded".equals(success)) { 
                    %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i> ¡Foto de perfil actualizada exitosamente!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <% } else if ("image_deleted".equals(success)) { %>
                        <div class="alert alert-info alert-dismissible fade show" role="alert">
                            <i class="fas fa-info-circle me-2"></i> Foto de perfil restaurada a la predeterminada.
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <% } else if ("profile_updated".equals(success)) { %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i> ¡Perfil actualizado exitosamente!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <% } else if (error != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <% 
                                switch(error) {
                                    case "no_file": out.print("No se seleccionó ningún archivo."); break;
                                    case "upload_failed": out.print("Error al subir la imagen."); break;
                                    case "db_update_failed": out.print("Error al guardar en la base de datos."); break;
                                    case "no_custom_image": out.print("No hay foto personalizada para eliminar."); break;
                                    default: out.print("Error: " + error.replace("_", " "));
                                }
                            %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <% } %>

                    <!-- Estadísticas del Empleado -->
                    <div class="row g-4 mb-4">
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-warning text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-gift fa-2x mb-2"></i>
                                    <h3>${myDonations != null ? myDonations : 0}</h3>
                                    <p class="mb-0">Donaciones Asignadas</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-success text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-check-circle fa-2x mb-2"></i>
                                    <h3>${activeDonations != null ? activeDonations : 0}</h3>
                                    <p class="mb-0">Donaciones Activas</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-primary text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-check-double fa-2x mb-2"></i>
                                    <h3>${completedDonations != null ? completedDonations : 0}</h3>
                                    <p class="mb-0">Completadas</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stat-card border-0 bg-info text-white">
                                <div class="card-body text-center">
                                    <i class="fas fa-calendar fa-2x mb-2"></i>
                                    <h5>${employeeSince != null ? employeeSince : "N/A"}</h5>
                                    <p class="mb-0">Empleado desde</p>
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
                                            <td>${userProfile.phone != null ? userProfile.phone : "No registrado"}</td>
                                        </tr>
                                        <tr>
                                            <td><strong><i class="fas fa-id-card me-2"></i>DNI:</strong></td>
                                            <td>${userProfile.dni != null ? userProfile.dni : "No registrado"}</td>
                                        </tr>
                                        <tr>
                                            <td><strong><i class="fas fa-map-marker-alt me-2"></i>Región:</strong></td>
                                            <td>${userProfile.region != null ? userProfile.region : "No registrada"}</td>
                                        </tr>
                                        <tr>
                                            <td><strong><i class="fas fa-calendar me-2"></i>Fecha Registro:</strong></td>
                                            <td>
                                                <%
                                                    User user = (User) request.getAttribute("userProfile");
                                                    if (user.getRegistrationDate() != null) {
                                                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                                        out.print(sdf.format(user.getRegistrationDate()));
                                                    } else {
                                                        out.print("N/A");
                                                    }
                                                %>
                                            </td>
                                        </tr>
                                    </table>
                                    
                                    <!-- Botón para eliminar foto personalizada -->
                                    <% if (user.hasCustomProfileImage()) { %>
                                    <div class="mt-3">
                                        <form action="${pageContext.request.contextPath}/uploadProfileImage" method="post">
                                            <input type="hidden" name="action" value="delete">
                                            <button type="submit" class="btn btn-outline-danger btn-sm" 
                                                    onclick="return confirm('¿Restaurar foto de perfil a la predeterminada?')">
                                                <i class="fas fa-trash me-1"></i> Restaurar Foto Predeterminada
                                            </button>
                                        </form>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-info text-white">
                                    <h5 class="mb-0"><i class="fas fa-chart-bar me-2"></i>Estadísticas del Sistema</h5>
                                </div>
                                <div class="card-body">
                                    <%
                                        DataManager dm = DataManager.getInstance();
                                        Map<String, Integer> dashboardStats = (Map<String, Integer>) request.getAttribute("dashboardStats");
                                        
                                        if (dashboardStats != null) {
                                    %>
                                    <div class="mb-3">
                                        <small class="text-muted">Donaciones totales en sistema</small>
                                        <div class="d-flex justify-content-between mb-1">
                                            <span><%= dashboardStats.getOrDefault("total_donations", 0) %> donaciones</span>
                                            <%
                                                int totalDonations = dashboardStats.getOrDefault("total_donations", 0);
                                                int pendingDonations = dashboardStats.getOrDefault("pending_donations", 0);
                                                int percentage = totalDonations > 0 ? (pendingDonations * 100) / totalDonations : 0;
                                            %>
                                            <span><%= pendingDonations %> pendientes</span>
                                        </div>
                                        <div class="progress mb-3" style="height: 8px;">
                                            <div class="progress-bar bg-warning" style="width: <%= percentage %>%"></div>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <small class="text-muted">Solicitudes pendientes</small>
                                        <div class="d-flex justify-content-between mb-1">
                                            <span><%= dashboardStats.getOrDefault("total_requests", 0) %> solicitudes</span>
                                            <span><%= dashboardStats.getOrDefault("pending_requests", 0) %> pendientes</span>
                                        </div>
                                        <div class="progress mb-3" style="height: 8px;">
                                            <%
                                                int totalRequests = dashboardStats.getOrDefault("total_requests", 0);
                                                int pendingRequests = dashboardStats.getOrDefault("pending_requests", 0);
                                                int requestsPercentage = totalRequests > 0 ? (pendingRequests * 100) / totalRequests : 0;
                                            %>
                                            <div class="progress-bar bg-primary" style="width: <%= requestsPercentage %>%"></div>
                                        </div>
                                    </div>
                                    
                                    <div class="row text-center mt-3">
                                        <div class="col-6">
                                            <div class="text-success">
                                                <i class="fas fa-hand-holding-heart fa-lg"></i>
                                                <div class="mt-1"><small><%= dashboardStats.getOrDefault("total_donors", 0) %> Donadores</small></div>
                                            </div>
                                        </div>
                                        <div class="col-6">
                                            <div class="text-primary">
                                                <i class="fas fa-hands-helping fa-lg"></i>
                                                <div class="mt-1"><small><%= dashboardStats.getOrDefault("total_receivers", 0) %> Receptores</small></div>
                                            </div>
                                        </div>
                                    </div>
                                    <% } else { %>
                                    <div class="text-center text-muted py-3">
                                        <i class="fas fa-chart-line fa-2x mb-2"></i>
                                        <p class="mb-0">No hay estadísticas disponibles</p>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Acciones Rápidas para Empleado -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header bg-light">
                                    <h5 class="mb-0"><i class="fas fa-bolt me-2"></i>Acciones Rápidas</h5>
                                </div>
                                <div class="card-body">
                                    <div class="row g-3">
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/employeeDonations" class="btn btn-warning w-100">
                                                <i class="fas fa-gift me-2"></i>Mis Donaciones
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/donationManagement" class="btn btn-success w-100">
                                                <i class="fas fa-plus me-2"></i>Nueva Donación
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/userManagement?action=list" class="btn btn-primary w-100">
                                                <i class="fas fa-users me-2"></i>Ver Usuarios
                                            </a>
                                        </div>
                                        <div class="col-md-3">
                                            <a href="${pageContext.request.contextPath}/requestManagement" class="btn btn-info w-100">
                                                <i class="fas fa-list me-2"></i>Solicitudes
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Información del Perfil -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header bg-secondary text-white">
                                    <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Información del Perfil</h5>
                                </div>
                                <div class="card-body">
                                    <div class="row text-center">
                                        <div class="col-md-3">
                                            <i class="fas fa-user-tie fa-2x text-warning mb-2"></i>
                                            <h6>Rol del Sistema</h6>
                                            <small class="text-muted">Empleado</small>
                                        </div>
                                        <div class="col-md-3">
                                            <i class="fas fa-shield-alt fa-2x text-success mb-2"></i>
                                            <h6>Privilegios</h6>
                                            <small class="text-muted">Gestión de donaciones</small>
                                        </div>
                                        <div class="col-md-3">
                                            <i class="fas fa-clock fa-2x text-info mb-2"></i>
                                            <h6>Sesión Activa</h6>
                                            <small class="text-muted">Desde <%= 
                                                new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()) 
                                            %></small>
                                        </div>
                                        <div class="col-md-3">
                                            <i class="fas fa-heart fa-2x text-danger mb-2"></i>
                                            <h6>Estado</h6>
                                            <small class="text-muted">
                                                <%= user.isActive() ? "Activo" : "Inactivo" %>
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

    <!-- Modal para información de la foto -->
    <div class="modal fade" id="imageInfoModal" tabindex="-1">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h6 class="modal-title">Información de la Foto</h6>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p class="small">
                        <i class="fas fa-info-circle text-info me-1"></i>
                        Formatos permitidos: JPG, PNG, GIF, WebP
                    </p>
                    <p class="small">
                        <i class="fas fa-info-circle text-info me-1"></i>
                        Tamaño máximo: 2MB
                    </p>
                    <p class="small">
                        <i class="fas fa-info-circle text-info me-1"></i>
                        La imagen se recortará a proporción 1:1
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Mostrar vista previa de la imagen seleccionada
        document.getElementById('profileImageInput').addEventListener('change', function(event) {
            const file = event.target.files[0];
            if (file) {
                // Validar tamaño (2MB máximo)
                if (file.size > 2 * 1024 * 1024) {
                    alert('La imagen es demasiado grande. El tamaño máximo es 2MB.');
                    this.value = '';
                    return;
                }
                
                // Validar tipo de archivo
                const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
                if (!allowedTypes.includes(file.type)) {
                    alert('Formato de imagen no permitido. Use JPG, PNG, GIF o WebP.');
                    this.value = '';
                    return;
                }
                
                // Mostrar vista previa
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('profileImagePreview').src = e.target.result;
                }
                reader.readAsDataURL(file);
                
                // Enviar formulario automáticamente
                uploadProfileImage();
            }
        });
        
        // Función para subir la imagen
        function uploadProfileImage() {
            const form = document.getElementById('imageUploadForm');
            if (document.getElementById('profileImageInput').value) {
                form.submit();
            }
        }
        
        // Mostrar info de la foto al hacer clic
        document.getElementById('profileImagePreview').addEventListener('click', function() {
            const modal = new bootstrap.Modal(document.getElementById('imageInfoModal'));
            modal.show();
        });
        
        // Auto-eliminar mensajes después de 5 segundos
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
</body>
</html>