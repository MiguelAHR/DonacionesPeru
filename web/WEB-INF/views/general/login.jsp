<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Iniciar Sesión - Donaciones Perú</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

        <style>
            .login-container {
                min-height: 100vh;
                background: linear-gradient(135deg, #dc3545 0%, #ffc107 100%);
            }
            .login-card {
                backdrop-filter: blur(10px);
                background: rgba(255, 255, 255, 0.95);
                border-radius: 20px;
                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            }
            .btn-peru {
                background: linear-gradient(45deg, #dc3545, #ffc107);
                border: none;
                color: white;
            }
            .btn-peru:hover {
                background: linear-gradient(45deg, #c82333, #e0a800);
                color: white;
            }
            .face-recognition-btn {
                background: linear-gradient(45deg, #28a745, #20c997);
                border: none;
                color: white;
            }
            .face-recognition-btn:hover {
                background: linear-gradient(45deg, #218838, #1ea080);
                color: white;
            }
        </style>
    </head>
    <body>
        <div class="login-container d-flex align-items-center justify-content-center">
            <div class="container">
                <div class="row justify-content-center">
                    <div class="col-md-6 col-lg-5">
                        <div class="login-card p-5">
                            <!-- Header -->
                            <div class="text-center mb-4">
                                <div class="mb-3">
                                    <i class="fas fa-heart fa-3x text-danger"></i>
                                </div>
                                <h2 class="fw-bold text-dark">Donaciones Perú</h2>
                                <p class="text-muted">Ingresa a tu cuenta</p>
                            </div>

                            <!-- Error Mensajes -->
                            <% if (request.getParameter("error") != null) { %>
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Usuario o contraseña incorrectos
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                            <% } %>

                            <!-- Login -->
                            <form action="${pageContext.request.contextPath}/login" method="post">
                                <div class="mb-3">
                                    <label for="username" class="form-label fw-bold">
                                        <i class="fas fa-user me-2"></i>Usuario
                                    </label>
                                    <input type="text" class="form-control form-control-lg" id="username" 
                                           name="username" required placeholder="Ingresa tu usuario">
                                </div>

                                <div class="mb-4">
                                    <label for="password" class="form-label fw-bold">
                                        <i class="fas fa-lock me-2"></i>Contraseña
                                    </label>
                                    <input type="password" class="form-control form-control-lg" id="password" 
                                           name="password" required placeholder="Ingresa tu contraseña">
                                </div>

                                <div class="d-grid gap-2 mb-3">
                                    <button type="submit" class="btn btn-peru btn-lg py-3">
                                        <i class="fas fa-sign-in-alt me-2"></i>Iniciar Sesión
                                    </button>
                                </div>
                            </form>

                            <!-- Reconocimiento Facial -->
                            <div class="d-grid gap-2 mb-4">
                                <button type="button" class="btn face-recognition-btn btn-lg py-3" onclick="showFaceRecognition()">
                                    <i class="fas fa-camera me-2"></i>Reconocimiento Facial
                                </button>
                            </div>

                            <!-- Demo de Usuarios -->
                            <div class="card bg-light border-0">
                                <div class="card-body">
                                    <h6 class="fw-bold text-center mb-3">
                                        <i class="fas fa-info-circle me-2"></i>Usuarios de Prueba
                                    </h6>
                                    <div class="row text-center">
                                        <div class="col-3">
                                            <small class="fw-bold text-danger">Admin</small><br>
                                            <small class="text-muted">admin/admin</small>
                                        </div>
                                        <div class="col-3">
                                            <small class="fw-bold text-warning">Empleado</small><br>
                                            <small class="text-muted">emp/emp</small>
                                        </div>
                                        <div class="col-3">
                                            <small class="fw-bold text-success">Receptor</small><br>
                                            <small class="text-muted">user/user</small>
                                        </div>
                                        <div class="col-3">
                                            <small class="fw-bold text-info">Donador</small><br>
                                            <small class="text-muted">Reyes/arduinouno</small>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Volver -->
                            <div class="text-center mt-4">
                                <a href="${pageContext.request.contextPath}/index.html" class="text-decoration-none">
                                    <i class="fas fa-arrow-left me-2"></i>Volver al inicio
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Reconocimiento Facial -->
        <div class="modal fade" id="faceRecognitionModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title">
                            <i class="fas fa-camera me-2"></i>Reconocimiento Facial
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body text-center">
                        <div class="mb-4">
                            <i class="fas fa-camera fa-5x text-success mb-3"></i>
                            <h5>Simulación de Reconocimiento Facial</h5>
                            <p class="text-muted">Esta funcionalidad estará disponible próximamente</p>
                        </div>
                        <div class="progress mb-3">
                            <div class="progress-bar bg-success progress-bar-striped progress-bar-animated" 
                                 role="progressbar" style="width: 0%" id="progressBar"></div>
                        </div>
                        <p class="text-muted" id="statusText">Iniciando reconocimiento...</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                    function showFaceRecognition() {
                                        const modal = new bootstrap.Modal(document.getElementById('faceRecognitionModal'));
                                        modal.show();

                                        let progress = 0;
                                        const progressBar = document.getElementById('progressBar');
                                        const statusText = document.getElementById('statusText');

                                        const interval = setInterval(() => {
                                            progress += 10;
                                            progressBar.style.width = progress + '%';

                                            if (progress === 30) {
                                                statusText.textContent = 'Detectando rostro...';
                                            } else if (progress === 60) {
                                                statusText.textContent = 'Analizando características...';
                                            } else if (progress === 90) {
                                                statusText.textContent = 'Verificando identidad...';
                                            } else if (progress >= 100) {
                                                statusText.textContent = 'Funcionalidad en desarrollo';
                                                progressBar.classList.remove('progress-bar-animated');
                                                clearInterval(interval);

                                                setTimeout(() => {
                                                    modal.hide();
                                                }, 2000);
                                            }
                                        }, 300);
                                    }
        </script>
    </body>
</html>