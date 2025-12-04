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
        .profile-img-container {
            position: relative;
            display: inline-block;
        }
        .profile-img-container img {
            border: 4px solid white;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
            object-fit: cover;
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
            background: white;
            border: 2px solid #28a745;
            cursor: pointer;
        }
        .camera-btn:hover {
            background: #f8f9fa;
            transform: scale(1.1);
        }
    </style>
</head>
<body class="bg-light">
    <!-- Mostrar mensajes -->
    <div class="container mt-3">
        <% 
            String success = request.getParameter("success");
            String error = request.getParameter("error");
            
            if (success != null) {
        %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                <% 
                    if ("image_uploaded".equals(success)) {
                        out.print("¡Foto de perfil actualizada exitosamente!");
                    } else if ("image_deleted".equals(success)) {
                        out.print("Foto de perfil restaurada a la predeterminada.");
                    } else if ("profile_updated".equals(success)) {
                        out.print("¡Perfil actualizado exitosamente!");
                    } else if ("password_updated".equals(success)) {
                        out.print("¡Contraseña actualizada exitosamente!");
                    } else if ("donation_created".equals(success)) {
                        out.print("¡Donación creada exitosamente!");
                    } else {
                        out.print("Operación completada exitosamente");
                    }
                %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% 
            } 
            
            if (error != null) {
        %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <% 
                    if ("no_file".equals(error)) {
                        out.print("No se seleccionó ningún archivo.");
                    } else if ("upload_failed".equals(error)) {
                        out.print("Error al subir la imagen.");
                    } else if ("db_update_failed".equals(error)) {
                        out.print("Error al guardar en la base de datos.");
                    } else if ("file_too_large".equals(error)) {
                        out.print("La imagen es demasiado grande. Máximo 2MB.");
                    } else if ("invalid_file_type".equals(error)) {
                        out.print("Formato de imagen no permitido. Use JPG, PNG, GIF o WebP.");
                    } else if ("current_password_incorrect".equals(error)) {
                        out.print("La contraseña actual es incorrecta.");
                    } else if ("new_password_mismatch".equals(error)) {
                        out.print("Las nuevas contraseñas no coinciden.");
                    } else if ("new_password_weak".equals(error)) {
                        out.print("La nueva contraseña debe tener al menos 6 caracteres.");
                    } else if ("save_failed".equals(error)) {
                        out.print("Error al guardar la información.");
                    } else if ("missing_fields".equals(error)) {
                        out.print("Por favor, completa todos los campos requeridos.");
                    } else if ("user_not_found".equals(error)) {
                        out.print("Usuario no encontrado.");
                    } else {
                        out.print("Error: " + error.replace("_", " "));
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
                <!-- Foto de perfil dinámica -->
                <div class="profile-img-container">
                    <%
                        // Obtener imagen de perfil CORRECTAMENTE
                        String profileImage = (String) request.getAttribute("profileImage");
                        String username = (String) session.getAttribute("username");
                        String userType = (String) session.getAttribute("userType");
                        
                        // Debug
                        System.out.println("JSP - Usuario: " + username + ", Tipo: " + userType);
                        System.out.println("JSP - ProfileImage desde request: " + profileImage);
                        
                        // Si no hay en request, intentar desde sesión
                        if (profileImage == null || profileImage.isEmpty()) {
                            profileImage = (String) session.getAttribute("profileImage");
                            System.out.println("JSP - ProfileImage desde sesión: " + profileImage);
                        }
                        
                        // Si todavía no hay, usar por defecto según tipo de usuario
                        if (profileImage == null || profileImage.isEmpty()) {
                            if ("donador".equalsIgnoreCase(userType)) {
                                profileImage = request.getContextPath() + "/images/donor-profile.png";
                            } else if ("receptor".equalsIgnoreCase(userType)) {
                                profileImage = request.getContextPath() + "/images/receiver-profile.png";
                            } else if ("admin".equalsIgnoreCase(userType)) {
                                profileImage = request.getContextPath() + "/images/admin-profile.png";
                            } else if ("empleado".equalsIgnoreCase(userType)) {
                                profileImage = request.getContextPath() + "/images/employee-profile.png";
                            } else if ("usuario".equalsIgnoreCase(userType)) {
                                profileImage = request.getContextPath() + "/images/user-profile.png";
                            } else {
                                profileImage = request.getContextPath() + "/images/default-profile.png";
                            }
                            System.out.println("JSP - Usando imagen por defecto: " + profileImage);
                        }
                        
                        // Asegurar que la imagen tenga el contexto si es ruta relativa
                        if (!profileImage.startsWith("http") && !profileImage.startsWith("/")) {
                            profileImage = request.getContextPath() + "/" + profileImage;
                        } else if (profileImage.startsWith("/") && !profileImage.startsWith(request.getContextPath())) {
                            profileImage = request.getContextPath() + profileImage;
                        }
                        
                        System.out.println("JSP - Imagen final para mostrar: " + profileImage);
                    %>
                    <img src="<%= profileImage %>" 
                         alt="Foto de perfil" 
                         class="rounded-circle"
                         style="width: 100px; height: 100px;"
                         id="profileImageDisplay"
                         onerror="this.onerror=null; this.src='<%= request.getContextPath() %>/images/default-profile.png';">
                    
                    <!-- Botón para cambiar foto -->
                    <button class="btn camera-btn" 
                            onclick="document.getElementById('profileImageInput').click()"
                            title="Cambiar foto de perfil">
                        <i class="fas fa-camera text-success"></i>
                    </button>
                    
                    <!-- Formulario oculto para subir imagen -->
                    <form id="imageUploadForm" action="<%= request.getContextPath() %>/uploadProfileImage" 
                          method="post" enctype="multipart/form-data" style="display: none;">
                        <input type="hidden" name="action" value="upload">
                        <input type="file" id="profileImageInput" name="profileImage" 
                               accept="image/jpeg,image/png,image/gif,image/webp" 
                               onchange="uploadProfileImage()">
                    </form>
                </div>
            </div>
            <h2 class="fw-bold">
                Bienvenido, 
                <%
                    String fullName = (String) request.getAttribute("userFullName");
                    if (fullName != null && !fullName.trim().isEmpty()) {
                        out.print(fullName);
                    } else if (username != null) {
                        out.print(username);
                    } else {
                        out.print("Donador");
                    }
                %>
            </h2>
            <p class="mb-0">Donador - Sistema de Donaciones Perú</p>
            <%
                String memberSince = (String) request.getAttribute("memberSince");
                String registrationDate = (String) request.getAttribute("registrationDate");
                if (memberSince != null || registrationDate != null) {
            %>
                <small>Miembro desde: <%= memberSince != null ? memberSince : registrationDate %></small>
            <% } %>
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
                        <button class="nav-link" id="donations-tab" data-bs-toggle="pill" data-bs-target="#donations" type="button" role="tab" aria-controls="donations" aria-selected="false" tabindex="-1">
                            <i class="fas fa-gift me-2"></i>Mis Donaciones
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
                                <div class="card text-white bg-success">
                                    <div class="card-body text-center">
                                        <i class="fas fa-gift fa-2x mb-2"></i>
                                        <h3><%= request.getAttribute("totalDonations") != null ? request.getAttribute("totalDonations") : 
                                               request.getAttribute("totalSolicitudes") != null ? request.getAttribute("totalSolicitudes") : 0 %></h3>
                                        <p class="mb-0">Total Donaciones</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-warning">
                                    <div class="card-body text-center">
                                        <i class="fas fa-clock fa-2x mb-2"></i>
                                        <h3><%= request.getAttribute("pendingDonations") != null ? request.getAttribute("pendingDonations") : 
                                               request.getAttribute("solicitudesPendientes") != null ? request.getAttribute("solicitudesPendientes") : 0 %></h3>
                                        <p class="mb-0">Pendientes</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-primary">
                                    <div class="card-body text-center">
                                        <i class="fas fa-check-circle fa-2x mb-2"></i>
                                        <h3><%= request.getAttribute("completedDonations") != null ? request.getAttribute("completedDonations") : 
                                               request.getAttribute("solicitudesAprobadas") != null ? request.getAttribute("solicitudesAprobadas") : 0 %></h3>
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
                                        <a href="<%= request.getContextPath() %>/donations?action=new" class="btn btn-success btn-lg w-100 py-3">
                                            <i class="fas fa-plus me-2"></i>Nueva Donación
                                        </a>
                                    </div>
                                    <div class="col-md-6">
                                        <button type="button" class="btn btn-outline-success btn-lg w-100 py-3" onclick="switchToTab('donations')">
                                            <i class="fas fa-history me-2"></i>Ver Historial
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Mis Donaciones -->
                    <div class="tab-pane fade" id="donations" role="tabpanel" aria-labelledby="donations-tab">
                        <div class="row">
                            <div class="col-12">
                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <h5 class="fw-bold mb-0">
                                        <i class="fas fa-gift me-2 text-success"></i>
                                        Mis Donaciones Realizadas
                                    </h5>
                                    <a href="<%= request.getContextPath() %>/donations?action=new" class="btn btn-success">
                                        <i class="fas fa-plus me-2"></i>Nueva Donación
                                    </a>
                                </div>

                                <% 
                                List<Donation> donaciones = (List<Donation>) request.getAttribute("donaciones");
                                if (donaciones != null && !donaciones.isEmpty()) {
                                    for (Donation donation : donaciones) {
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
                                                    <span class="status-badge <%= 
                                                        "pending".equals(donation.getStatus()) ? "bg-warning" : 
                                                        "completed".equals(donation.getStatus()) ? "bg-success" : 
                                                        "active".equals(donation.getStatus()) ? "bg-info" : "bg-secondary" %>">
                                                        <%= donation.getStatus() != null ? donation.getStatus() : "Pendiente" %>
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
                                        <p class="text-muted">Puedes empezar haciendo tu primera donación</p>
                                        <a href="<%= request.getContextPath() %>/donations?action=new" class="btn btn-success btn-lg">
                                            <i class="fas fa-plus me-2"></i>Hacer Primera Donación
                                        </a>
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <!-- Perfil -->
                    <div class="tab-pane fade" id="profile" role="tabpanel" aria-labelledby("profile-tab">
                        <div class="row">
                            <div class="col-md-8 mx-auto">
                                <div class="card border-0 bg-light">
                                    <div class="card-body p-4">
                                        <h5 class="fw-bold mb-4">
                                            <i class="fas fa-id-card me-2 text-success"></i>
                                            Mi Información Personal
                                        </h5>
                                        
                                        <!-- Formulario de edición de perfil -->
                                        <form action="<%= request.getContextPath() %>/profile" method="post">
                                            <input type="hidden" name="action" value="update_profile">
                                            
                                            <%
                                                User userProfile = (User) request.getAttribute("userProfile");
                                                if (userProfile == null) {
                                                    userProfile = new User();
                                                    userProfile.setFirstName("");
                                                    userProfile.setLastName("");
                                                    userProfile.setEmail("");
                                                    userProfile.setPhone("");
                                                    userProfile.setRegion("");
                                                    userProfile.setDistrict("");
                                                    userProfile.setAddress("");
                                                }
                                            %>
                                            
                                            <div class="row g-3 mb-4">
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Nombres</label>
                                                    <input type="text" class="form-control" name="firstName" 
                                                           value="<%= userProfile.getFirstName() != null ? userProfile.getFirstName() : "" %>" 
                                                           placeholder="Ingresa tus nombres">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Apellidos</label>
                                                    <input type="text" class="form-control" name="lastName" 
                                                           value="<%= userProfile.getLastName() != null ? userProfile.getLastName() : "" %>" 
                                                           placeholder="Ingresa tus apellidos">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Correo Electrónico</label>
                                                    <input type="email" class="form-control" name="email" 
                                                           value="<%= userProfile.getEmail() != null ? userProfile.getEmail() : "" %>" 
                                                           placeholder="correo@ejemplo.com">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Teléfono</label>
                                                    <input type="tel" class="form-control" name="phone" 
                                                           value="<%= userProfile.getPhone() != null ? userProfile.getPhone() : "" %>" 
                                                           placeholder="Número de teléfono">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Región</label>
                                                    <input type="text" class="form-control" name="region" 
                                                           value="<%= userProfile.getRegion() != null ? userProfile.getRegion() : "" %>" 
                                                           placeholder="Región">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label fw-bold">Distrito</label>
                                                    <input type="text" class="form-control" name="district" 
                                                           value="<%= userProfile.getDistrict() != null ? userProfile.getDistrict() : "" %>" 
                                                           placeholder="Distrito">
                                                </div>
                                                <div class="col-12">
                                                    <label class="form-label fw-bold">Dirección</label>
                                                    <input type="text" class="form-control" name="address" 
                                                           value="<%= userProfile.getAddress() != null ? userProfile.getAddress() : "" %>" 
                                                           placeholder="Dirección completa">
                                                </div>
                                            </div>
                                            
                                            <div class="text-center">
                                                <button type="submit" class="btn btn-success">
                                                    <i class="fas fa-save me-2"></i>Guardar Cambios
                                                </button>
                                            </div>
                                        </form>
                                        
                                        <hr class="my-4">
                                        
                                        <!-- Cambio de contraseña -->
                                        <h6 class="fw-bold mb-3">
                                            <i class="fas fa-lock me-2 text-success"></i>
                                            Seguridad y Contraseña
                                        </h6>
                                        
                                        <form action="<%= request.getContextPath() %>/profile" method="post">
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
                                                <button type="submit" class="btn btn-outline-success">
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
            <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-danger">
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
                    const img = document.getElementById('profileImageDisplay');
                    if (img) {
                        img.src = e.target.result;
                    }
                }
                reader.readAsDataURL(file);
                
                // Enviar formulario automáticamente
                setTimeout(() => {
                    uploadProfileImage();
                }, 500);
            }
        });
        
        // Función para subir la imagen
        function uploadProfileImage() {
            const form = document.getElementById('imageUploadForm');
            const fileInput = document.getElementById('profileImageInput');
            
            if (fileInput.files.length > 0) {
                // Mostrar indicador de carga
                const originalButton = document.querySelector('.camera-btn i');
                if (originalButton) {
                    originalButton.className = 'fas fa-spinner fa-spin text-success';
                }
                
                // Enviar formulario
                form.submit();
            }
        }
    </script>
</body>
</html>