<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Donador - Donaciones Perú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .form-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 60px 0;
        }
        .form-card {
            margin-top: -50px;
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
        }
        .btn-peru {
            background: linear-gradient(45deg, #28a745, #20c997);
            border: none;
            color: white;
            padding: 12px 30px;
            border-radius: 50px;
        }
        .btn-peru:hover {
            background: linear-gradient(45deg, #218838, #1ea080);
            color: white;
        }
        .form-floating > label {
            color: #6c757d;
        }
        .form-floating > .form-control:focus ~ label {
            color: #28a745;
        }
    </style>
</head>
<body class="bg-light">
    <!-- Form Header -->
    <div class="form-header">
        <div class="container text-center">
            <h1 class="fw-bold mb-3">
                <i class="fas fa-hand-holding-heart me-3"></i>
                Registro de Donador
            </h1>
            <p class="lead mb-0">Únete a nuestra comunidad de personas solidarias</p>
        </div>
    </div>

    <div class="container">
        <div class="card form-card border-0">
            <div class="card-body p-5">
                <!-- Success/Error Messages -->
                <% if (request.getParameter("success") != null) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>
                        ¡Donador registrado exitosamente!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>
                
                <% if (request.getParameter("error") != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Error al registrar el donador. Intenta nuevamente.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/users" method="post" id="donorForm">
                    <input type="hidden" name="action" value="createDonor">
                    
                    <div class="row g-4">
                        <!-- Personal Information -->
                        <div class="col-12">
                            <h4 class="fw-bold text-success mb-4">
                                <i class="fas fa-user me-2"></i>Información Personal
                            </h4>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control form-control-lg" id="firstName" name="firstName" required placeholder="Nombres">
                                <label for="firstName">
                                    <i class="fas fa-user me-2"></i>Nombres *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control form-control-lg" id="lastName" name="lastName" required placeholder="Apellidos">
                                <label for="lastName">
                                    <i class="fas fa-user me-2"></i>Apellidos *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="email" class="form-control form-control-lg" id="email" name="email" required placeholder="Correo electrónico">
                                <label for="email">
                                    <i class="fas fa-envelope me-2"></i>Correo Electrónico *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="tel" class="form-control form-control-lg" id="phone" name="phone" required placeholder="Teléfono">
                                <label for="phone">
                                    <i class="fas fa-phone me-2"></i>Teléfono *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control form-control-lg" id="dni" name="dni" required placeholder="DNI" maxlength="8" pattern="[0-9]{8}">
                                <label for="dni">
                                    <i class="fas fa-id-card me-2"></i>DNI *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="date" class="form-control form-control-lg" id="birthDate" name="birthDate" required>
                                <label for="birthDate">
                                    <i class="fas fa-calendar me-2"></i>Fecha de Nacimiento *
                                </label>
                            </div>
                        </div>

                        <!-- Localizacion -->
                        <div class="col-12 mt-5">
                            <h4 class="fw-bold text-success mb-4">
                                <i class="fas fa-map-marker-alt me-2"></i>Información de Ubicación
                            </h4>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <select class="form-select form-select-lg" id="region" name="region" required>
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
                                    <option value="Cajamarca">Cajamarca</option>
                                    <option value="Puno">Puno</option>
                                    <option value="Ica">Ica</option>
                                    <option value="Huánuco">Huánuco</option>
                                    <option value="Tarapoto">Tarapoto</option>
                                </select>
                                <label for="region">
                                    <i class="fas fa-map me-2"></i>Región *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control form-control-lg" id="district" name="district" required placeholder="Distrito">
                                <label for="district">
                                    <i class="fas fa-building me-2"></i>Distrito *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-12">
                            <div class="form-floating">
                                <input type="text" class="form-control form-control-lg" id="address" name="address" required placeholder="Dirección completa">
                                <label for="address">
                                    <i class="fas fa-home me-2"></i>Dirección Completa *
                                </label>
                            </div>
                        </div>

                        <!-- Preferencias de Donación -->
                        <div class="col-12 mt-5">
                            <h4 class="fw-bold text-success mb-4">
                                <i class="fas fa-heart me-2"></i>Preferencias de Donación
                            </h4>
                        </div>
                        
                        <div class="col-12">
                            <label class="form-label fw-bold">¿Qué tipos de donaciones estás dispuesto(a) a realizar?</label>
                            <div class="row g-3 mt-2">
                                <div class="col-md-4">
                                    <div class="form-check form-check-lg">
                                        <input class="form-check-input" type="checkbox" id="ropa" name="donationTypes" value="ropa">
                                        <label class="form-check-label" for="ropa">
                                            <i class="fas fa-tshirt me-2 text-primary"></i>Ropa
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-check form-check-lg">
                                        <input class="form-check-input" type="checkbox" id="cuadernos" name="donationTypes" value="cuadernos">
                                        <label class="form-check-label" for="cuadernos">
                                            <i class="fas fa-book me-2 text-warning"></i>Cuadernos
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-check form-check-lg">
                                        <input class="form-check-input" type="checkbox" id="utiles" name="donationTypes" value="utiles_escolares">
                                        <label class="form-check-label" for="utiles">
                                            <i class="fas fa-pencil-alt me-2 text-info"></i>Útiles Escolares
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-check form-check-lg">
                                        <input class="form-check-input" type="checkbox" id="reciclable" name="donationTypes" value="material_reciclable">
                                        <label class="form-check-label" for="reciclable">
                                            <i class="fas fa-recycle me-2 text-success"></i>Material Reciclable
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-check form-check-lg">
                                        <input class="form-check-input" type="checkbox" id="ropaNueva" name="donationTypes" value="ropa_casi_nueva">
                                        <label class="form-check-label" for="ropaNueva">
                                            <i class="fas fa-star me-2 text-danger"></i>Ropa Casi Nueva
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-check form-check-lg">
                                        <input class="form-check-input" type="checkbox" id="otros" name="donationTypes" value="otros">
                                        <label class="form-check-label" for="otros">
                                            <i class="fas fa-plus me-2 text-secondary"></i>Otros
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-12">
                            <div class="form-floating">
                                <textarea class="form-control" id="comments" name="comments" style="height: 100px" placeholder="Comentarios adicionales"></textarea>
                                <label for="comments">
                                    <i class="fas fa-comment me-2"></i>Comentarios Adicionales (Opcional)
                                </label>
                            </div>
                        </div>

                        <!-- Terms and Conditions -->
                        <div class="col-12 mt-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="terms" name="terms" required>
                                <label class="form-check-label" for="terms">
                                    Acepto los <a href="#" class="text-success">términos y condiciones</a> del sistema de donaciones *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-12">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="notifications" name="notifications">
                                <label class="form-check-label" for="notifications">
                                    Deseo recibir notificaciones sobre oportunidades de donación
                                </label>
                            </div>
                        </div>

                        <!-- Submit Button -->
                        <div class="col-12 text-center mt-5">
                            <button type="submit" class="btn btn-peru btn-lg px-5 py-3">
                                <i class="fas fa-user-plus me-2"></i>Registrar como Donador
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Volver -->
        <div class="text-center my-4">
            <a href="${pageContext.request.contextPath}/dashboard" class="text-decoration-none">
                <i class="fas fa-arrow-left me-2"></i>Volver al Dashboard
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById('donorForm').addEventListener('submit', function(e) {
            const checkboxes = document.querySelectorAll('input[name="donationTypes"]:checked');
            if (checkboxes.length === 0) {
                e.preventDefault();
                alert('Por favor, selecciona al menos un tipo de donación que estés dispuesto(a) a realizar.');
                return false;
            }
            
            const dni = document.getElementById('dni').value;
            if (!/^\d{8}$/.test(dni)) {
                e.preventDefault();
                alert('El DNI debe tener exactamente 8 dígitos.');
                return false;
            }
            
            const birthDate = new Date(document.getElementById('birthDate').value);
            const today = new Date();
            const age = today.getFullYear() - birthDate.getFullYear();
            const monthDiff = today.getMonth() - birthDate.getMonth();
            
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            
            if (age < 18) {
                e.preventDefault();
                alert('Debes ser mayor de 18 años para registrarte como donador.');
                return false;
            }
        });

        document.getElementById('phone').addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 9) {
                value = value.substring(0, 9);
            }
            e.target.value = value;
        });

        document.getElementById('dni').addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 8) {
                value = value.substring(0, 8);
            }
            e.target.value = value;
        });
    </script>
</body>
</html>