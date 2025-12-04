<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Beneficiario - Donaciones Perú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .profile-header {
            background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
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
            background: linear-gradient(45deg, #ff6b35, #f7931e);
            color: white;
        }
        .request-card {
            border-radius: 15px;
            transition: transform 0.3s ease;
        }
        .request-card:hover {
            transform: translateY(-5px);
        }
        .status-badge {
            font-size: 0.8rem;
            padding: 8px 15px;
            border-radius: 20px;
        }
        .profile-img-container {
            position: relative;
            display: inline-block;
        }
        .profile-img-container img {
            border: 4px solid white;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        .camera-btn {
            position: absolute;
            bottom: 0;
            right: 0;
            width: 32px;
            height: 32px;
            border-radius: 50%;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    </style>
</head>
<body class="bg-light">
    <!-- Mostrar mensajes de éxito/error para foto de perfil -->
    <div class="container mt-3">
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
        <% } else if ("password_updated".equals(success)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i> ¡Contraseña actualizada exitosamente!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("request_created".equals(success)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i> ¡Solicitud creada exitosamente!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if (error != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <% 
                    switch(error) {
                        case "missing_fields": out.print("Por favor, completa todos los campos requeridos"); break;
                        case "no_file": out.print("No se seleccionó ningún archivo."); break;
                        case "upload_failed": out.print("Error al subir la imagen."); break;
                        case "db_update_failed": out.print("Error al guardar en la base de datos."); break;
                        case "file_too_large": out.print("La imagen es demasiado grande. Máximo 2MB."); break;
                        case "invalid_file_type": out.print("Formato de imagen no permitido. Use JPG, PNG, GIF o WebP."); break;
                        case "current_password_incorrect": out.print("La contraseña actual es incorrecta."); break;
                        case "new_password_mismatch": out.print("Las nuevas contraseñas no coinciden."); break;
                        case "new_password_weak": out.print("La nueva contraseña debe tener al menos 6 caracteres."); break;
                        case "save_failed": out.print("Error al guardar la información."); break;
                        default: out.print("Error: " + error.replace("_", " "));
                    }
                %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>
    </div>

    <!-- Header con foto de perfil -->
    <div class="profile-header">
        <div class="container text-center">
            <div class="mb-3">
                <!-- Reemplazar el ícono por la foto de perfil -->
                <div class="profile-img-container">
                    <img src="${profileImage != null ? profileImage : '/images/receiver-profile.png'}" 
                         alt="Foto de perfil" 
                         class="rounded-circle"
                         style="width: 100px; height: 100px; object-fit: cover;">
                    
                    <!-- Botón para cambiar foto -->
                    <button class="btn btn-light camera-btn" 
                            onclick="document.getElementById('profileImageInput').click()"
                            title="Cambiar foto de perfil">
                        <i class="fas fa-camera text-warning"></i>
                    </button>
                    
                    <!-- Formulario oculto para subir imagen -->
                    <form id="imageUploadForm" action="${pageContext.request.contextPath}/uploadProfileImage" 
                          method="post" enctype="multipart/form-data" style="display: none;">
                        <input type="hidden" name="action" value="upload">
                        <input type="file" id="profileImageInput" name="profileImage" 
                               accept="image/*" onchange="uploadProfileImage()">
                    </form>
                </div>
            </div>
            <h2 class="fw-bold">Bienvenido, ${userFullName != null ? userFullName : sessionScope.username}</h2>
            <p class="mb-0">Beneficiario - Sistema de Donaciones Perú</p>
            <small>Miembro desde: ${memberSince != null ? memberSince : registrationDate != null ? registrationDate : "N/A"}</small>
        </div>
    </div>

    <div class="container">
        <div class="card profile-card border-0">
            <div class="card-body p-4">
                <ul class="nav nav-pills justify-content-center mb-4" id="profileTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="dashboard-tab" data-bs-toggle="pill" data-bs-target="#dashboard" type="button" role="tab" aria-controls="dashboard" aria-selected="true">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="requests-tab" data-bs-toggle="pill" data-bs-target="#requests" type="button" role="tab" aria-controls="requests" aria-selected="false" tabindex="-1">
                            <i class="fas fa-hand-holding-heart me-2"></i>Mis Solicitudes
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="profile-tab" data-bs-toggle="pill" data-bs-target="#profile" type="button" role="tab" aria-controls="profile" aria-selected="false" tabindex="-1">
                            <i class="fas fa-user me-2"></i>Mi Perfil
                        </button>
                    </li>
                </ul>

                <!-- Contenido de las pestañas -->
                <div class="tab-content" id="profileTabsContent">
                    <!-- Dashboard -->
                    <div class="tab-pane fade active show" id="dashboard" role="tabpanel" aria-labelledby="dashboard-tab">
                        <!-- Estadísticas -->
                        <div class="row mb-4">
                            <div class="col-md-4">
                                <div class="card text-white bg-warning">
                                    <div class="card-body text-center">
                                        <i class="fas fa-hand-holding-heart fa-2x mb-2"></i>
                                        <h3>${totalRequests != null ? totalRequests : totalSolicitudes != null ? totalSolicitudes : 0}</h3>
                                        <p class="mb-0">Total Solicitudes</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-info">
                                    <div class="card-body text-center">
                                        <i class="fas fa-clock fa-2x mb-2"></i>
                                        <h3>${pendingRequests != null ? pendingRequests : solicitudesPendientes != null ? solicitudesPendientes : 0}</h3>
                                        <p class="mb-0">Pendientes</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-success">
                                    <div class="card-body text-center">
                                        <i class="fas fa-check-circle fa-2x mb-2"></i>
                                        <h3>${completedRequests != null ? completedRequests : solicitudesAprobadas != null ? solicitudesAprobadas : 0}</h3>
                                        <p class="mb-0">Aprobadas</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Acciones Rápidas -->
                        <div class="card border-0 bg-light mb-4">
                            <div class="card-body text-center p-4">
                                <h5 class="fw-bold mb-3">
                                    <i class="fas fa-rocket me-2 text-warning"></i>
                                    Acciones Rápidas
                                </h5>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <button class="btn btn-warning btn-lg w-100 py-3" data-bs-toggle="modal" data-bs-target="#nuevaSolicitudModal">
                                            <i class="fas fa-plus me-2"></i>Nueva Solicitud
                                        </button>
                                    </div>
                                    <div class="col-md-6">
                                        <!-- Botón corregido para activar la pestaña "Mis Solicitudes" -->
                                        <button type="button" class="btn btn-outline-warning btn-lg w-100 py-3" onclick="switchToTab('requests')">
                                            <i class="fas fa-history me-2"></i>Ver Historial
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Mis Solicitudes -->
                    <div class="tab-pane fade" id="requests" role="tabpanel" aria-labelledby="requests-tab">
                        <div class="row">
                            <div class="col-12">
                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-hand-holding-heart me-2 text-warning"></i>
                                        Mis Solicitudes de Ayuda
                                    </h5>
                                    <button class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#nuevaSolicitudModal">
                                        <i class="fas fa-plus me-2"></i>Nueva Solicitud
                                    </button>
                                </div>

                                <% 
                                List<Request> solicitudes = (List<Request>) request.getAttribute("solicitudes");
                                if (solicitudes != null && !solicitudes.isEmpty()) {
                                    for (Request solicitud : solicitudes) {
                                        if (solicitud != null) {
                                %>
                                    <div class="card request-card border-0 shadow-sm mb-3">
                                        <div class="card-body">
                                            <div class="row align-items-center">
                                                <div class="col-md-2 text-center">
                                                    <div class="bg-warning text-white rounded-circle d-inline-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                                                        <i class="fas fa-hand-holding-heart"></i>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <h6 class="fw-bold mb-1"><%= solicitud.getType() != null ? solicitud.getType() : "Sin tipo" %></h6>
                                                    <p class="text-muted mb-1"><%= solicitud.getDescription() != null ? solicitud.getDescription() : "Sin descripción" %></p>
                                                    <small class="text-muted">
                                                        <i class="fas fa-map-marker-alt me-1"></i>
                                                        <%= solicitud.getLocation() != null ? solicitud.getLocation() : "Sin ubicación" %>
                                                        <% if (solicitud.getAssignedTo() != null && !solicitud.getAssignedTo().isEmpty()) { %>
                                                            • <i class="fas fa-user me-1"></i>Asignado a: <%= solicitud.getAssignedTo() %>
                                                        <% } %>
                                                    </small>
                                                </div>
                                                <div class="col-md-2 text-center">
                                                    <span class="fw-bold">Prioridad: 
                                                        <span class="badge <%= 
                                                            solicitud.getPriority() >= 4 ? "bg-danger" : 
                                                            solicitud.getPriority() >= 3 ? "bg-warning" : "bg-info" %>">
                                                            <%= solicitud.getPriority() %>
                                                        </span>
                                                    </span>
                                                </div>
                                                <div class="col-md-2 text-center">
                                                    <span class="status-badge <%= 
                                                        "pending".equals(solicitud.getStatus()) ? "bg-warning" : 
                                                        "completed".equals(solicitud.getStatus()) ? "bg-success" : 
                                                        "in_progress".equals(solicitud.getStatus()) ? "bg-info" : "bg-secondary" %>">
                                                        <%= solicitud.getStatus() != null ? solicitud.getStatus() : "Pendiente" %>
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
                                        <button class="btn btn-warning btn-lg" data-bs-toggle="modal" data-bs-target="#nuevaSolicitudModal">
                                            <i class="fas fa-plus me-2"></i>Crear Primera Solicitud
                                        </button>
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <!-- Perfil -->
                    <div class="tab-pane fade" id="profile" role="tabpanel" aria-labelledby="profile-tab">
                        <div class="row">
                            <div class="col-md-8 mx-auto">
                                <div class="card border-0 bg-light">
                                    <div class="card-body p-4">
                                        <h5 class="fw-bold mb-4">
                                            <i class="fas fa-id-card me-2 text-warning"></i>
                                            Mi Información Personal
                                        </h5>
                                        
                                        <!-- Formulario de edición de perfil -->
                                        <form action="${pageContext.request.contextPath}/profile" method="post">
                                            <input type="hidden" name="action" value="update_profile">
                                            
                                            <div class="row g-3 mb-4">
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Nombres</label>
                                                    <input type="text" class="form-control" name="firstName" 
                                                           value="${userProfile.firstName != null ? userProfile.firstName : ''}" 
                                                           placeholder="Ingresa tus nombres">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Apellidos</label>
                                                    <input type="text" class="form-control" name="lastName" 
                                                           value="${userProfile.lastName != null ? userProfile.lastName : ''}" 
                                                           placeholder="Ingresa tus apellidos">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Correo Electrónico</label>
                                                    <input type="email" class="form-control" name="email" 
                                                           value="${userProfile.email != null ? userProfile.email : ''}" 
                                                           placeholder="correo@ejemplo.com">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Teléfono</label>
                                                    <input type="tel" class="form-control" name="phone" 
                                                           value="${userProfile.phone != null ? userProfile.phone : ''}" 
                                                           placeholder="Número de teléfono">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Región</label>
                                                    <input type="text" class="form-control" name="region" 
                                                           value="${userProfile.region != null ? userProfile.region : ''}" 
                                                           placeholder="Región">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Distrito</label>
                                                    <input type="text" class="form-control" name="district" 
                                                           value="${userProfile.district != null ? userProfile.district : ''}" 
                                                           placeholder="Distrito">
                                                </div>
                                                <div class="col-12">
                                                    <label class="form-label fw-bold">Dirección</label>
                                                    <input type="text" class="form-control" name="address" 
                                                           value="${userProfile.address != null ? userProfile.address : ''}" 
                                                           placeholder="Dirección completa">
                                                </div>
                                            </div>
                                            
                                            <div class="text-center">
                                                <button type="submit" class="btn btn-warning">
                                                    <i class="fas fa-save me-2"></i>Guardar Cambios
                                                </button>
                                            </div>
                                        </form>
                                        
                                        <hr class="my-4">
                                        
                                        <!-- Cambio de contraseña -->
                                        <h6 class="fw-bold mb-3">
                                            <i class="fas fa-lock me-2 text-warning"></i>
                                            Seguridad y Contraseña
                                        </h6>
                                        
                                        <form action="${pageContext.request.contextPath}/profile" method="post">
                                            <input type="hidden" name="action" value="update_password">
                                            
                                            <div class="row g-3">
                                                <div class="col-12">
                                                    <label class="form-label fw-bold">Contraseña Actual</label>
                                                    <input type="password" class="form-control" name="currentPassword" 
                                                           placeholder="Ingresa tu contraseña actual" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Nueva Contraseña</label>
                                                    <input type="password" class="form-control" name="newPassword" 
                                                           placeholder="Nueva contraseña" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Confirmar Nueva Contraseña</label>
                                                    <input type="password" class="form-control" name="confirmPassword" 
                                                           placeholder="Confirmar nueva contraseña" required>
                                                </div>
                                            </div>
                                            
                                            <div class="text-center mt-3">
                                                <button type="submit" class="btn btn-outline-warning">
                                                    <i class="fas fa-key me-2"></i>Cambiar Contraseña
                                                </button>
                                            </div>
                                        </form>
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

    <!-- Modal para nueva solicitud -->
    <div class="modal fade" id="nuevaSolicitudModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header bg-warning text-white">
                    <h5 class="modal-title">
                        <i class="fas fa-plus me-2"></i>Nueva Solicitud de Ayuda
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/requests" method="post">
                    <input type="hidden" name="action" value="create">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Tipo de Ayuda Necesaria</label>
                                    <select class="form-select" name="type" required>
                                        <option value="">Seleccionar tipo...</option>
                                        <option value="ropa">Ropa</option>
                                        <option value="cuadernos">Cuadernos</option>
                                        <option value="utiles_escolares">Útiles Escolares</option>
                                        <option value="material_reciclable">Material Reciclable</option>
                                        <option value="ropa_casi_nueva">Ropa Casi Nueva</option>
                                        <option value="otros">Otros</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Nivel de Urgencia</label>
                                    <select class="form-select" name="priority">
                                        <option value="3">Media</option>
                                        <option value="4">Alta</option>
                                        <option value="5">Urgente</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold">Descripción Detallada</label>
                            <textarea class="form-control" name="description" rows="4" 
                                    placeholder="Describe tu situación y necesidades específicas..." required></textarea>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold">Ubicación donde necesita la ayuda</label>
                            <select class="form-select" name="location" required>
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

        // Mostrar vista previa y subir imagen
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
                    const img = document.querySelector('.profile-header img');
                    if (img) img.src = e.target.result;
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
    </script>
</body>
</html>