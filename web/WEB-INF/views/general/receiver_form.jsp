<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Receptor - Donaciones Perú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .form-header {
            background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
            color: white;
            padding: 60px 0;
        }
        .form-card {
            margin-top: -50px;
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
        }
        .btn-peru {
            background: linear-gradient(45deg, #ffc107, #e0a800);
            border: none;
            color: white;
            padding: 12px 30px;
            border-radius: 50px;
        }
        .btn-peru:hover {
            background: linear-gradient(45deg, #e0a800, #d39e00);
            color: white;
        }
        .form-floating > label {
            color: #6c757d;
        }
        .form-floating > .form-control:focus ~ label {
            color: #ffc107;
        }
        .need-card {
            border: 2px solid #e9ecef;
            border-radius: 15px;
            padding: 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .need-card:hover {
            border-color: #ffc107;
            transform: translateY(-5px);
        }
        .need-card.selected {
            border-color: #ffc107;
            background: #fff8e1;
        }
    </style>
</head>
<body class="bg-light">
    <!--  Header -->
    <div class="form-header">
        <div class="container text-center">
            <h1 class="fw-bold mb-3">
                <i class="fas fa-hands-helping me-3"></i>
                Registro de Receptor
            </h1>
            <p class="lead mb-0">Regístrate para recibir ayuda de nuestra comunidad solidaria</p>
        </div>
    </div>

    <div class="container">
        <div class="card form-card border-0">
            <div class="card-body p-5">
                <% if (request.getParameter("success") != null) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>
                        ¡Receptor registrado exitosamente!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>
                
                <% if (request.getParameter("error") != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Error al registrar el receptor. Intenta nuevamente.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/users" method="post" id="receiverForm">
                    <input type="hidden" name="action" value="createReceiver">
                    
                    <div class="row g-4">
                        <!-- Informacion Personal -->
                        <div class="col-12">
                            <h4 class="fw-bold text-warning mb-4">
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

                        <!-- Informacion Familiar -->
                        <div class="col-12 mt-5">
                            <h4 class="fw-bold text-warning mb-4">
                                <i class="fas fa-home me-2"></i>Información Familiar
                            </h4>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <select class="form-select form-select-lg" id="familySize" name="familySize" required>
                                    <option value="">Seleccionar...</option>
                                    <option value="1">1 persona</option>
                                    <option value="2">2 personas</option>
                                    <option value="3">3 personas</option>
                                    <option value="4">4 personas</option>
                                    <option value="5">5 personas</option>
                                    <option value="6+">6 o más personas</option>
                                </select>
                                <label for="familySize">
                                    <i class="fas fa-users me-2"></i>Tamaño de Familia *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="number" class="form-control form-control-lg" id="children" name="children" min="0" placeholder="Número de niños">
                                <label for="children">
                                    <i class="fas fa-child me-2"></i>Número de Niños
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-12">
                            <div class="form-floating">
                                <select class="form-select form-select-lg" id="economicSituation" name="economicSituation" required>
                                    <option value="">Seleccionar situación...</option>
                                    <option value="muy_baja">Muy baja - Sin ingresos fijos</option>
                                    <option value="baja">Baja - Ingresos menores a S/500</option>
                                    <option value="media_baja">Media baja - Ingresos S/500-S/1000</option>
                                    <option value="media">Media - Ingresos S/1000-S/1500</option>
                                </select>
                                <label for="economicSituation">
                                    <i class="fas fa-coins me-2"></i>Situación Económica *
                                </label>
                            </div>
                        </div>

                        <!-- Informacion de Localizacion -->
                        <div class="col-12 mt-5">
                            <h4 class="fw-bold text-warning mb-4">
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

                        <!-- Informacion Necesaria -->
                        <div class="col-12 mt-5">
                            <h4 class="fw-bold text-warning mb-4">
                                <i class="fas fa-heart me-2"></i>¿Qué tipo de ayuda necesitas?
                            </h4>
                        </div>
                        
                        <div class="col-12">
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <div class="need-card" data-need="ropa">
                                        <i class="fas fa-tshirt fa-3x text-primary mb-3"></i>
                                        <h5 class="fw-bold">Ropa</h5>
                                        <p class="text-muted">Prendas de vestir para la familia</p>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="need-card" data-need="cuadernos">
                                        <i class="fas fa-book fa-3x text-warning mb-3"></i>
                                        <h5 class="fw-bold">Cuadernos</h5>
                                        <p class="text-muted">Material escolar para estudios</p>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="need-card" data-need="utiles_escolares">
                                        <i class="fas fa-pencil-alt fa-3x text-info mb-3"></i>
                                        <h5 class="fw-bold">Útiles Escolares</h5>
                                        <p class="text-muted">Lápices, colores, reglas, etc.</p>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="need-card" data-need="material_reciclable">
                                        <i class="fas fa-recycle fa-3x text-success mb-3"></i>
                                        <h5 class="fw-bold">Material Reciclable</h5>
                                        <p class="text-muted">Para proyectos de reciclaje</p>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="need-card" data-need="ropa_casi_nueva">
                                        <i class="fas fa-star fa-3x text-danger mb-3"></i>
                                        <h5 class="fw-bold">Ropa Casi Nueva</h5>
                                        <p class="text-muted">Prendas en excelente estado</p>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="need-card" data-need="otros">
                                        <i class="fas fa-plus fa-3x text-secondary mb-3"></i>
                                        <h5 class="fw-bold">Otros</h5>
                                        <p class="text-muted">Otras necesidades específicas</p>
                                    </div>
                                </div>
                            </div>
                            <input type="hidden" name="needs" id="selectedNeeds">
                        </div>
                        
                        <div class="col-12">
                            <div class="form-floating">
                                <textarea class="form-control" id="needsDescription" name="needsDescription" style="height: 120px" required placeholder="Describe tu situación y necesidades específicas"></textarea>
                                <label for="needsDescription">
                                    <i class="fas fa-comment me-2"></i>Describe tu Situación y Necesidades Específicas *
                                </label>
                            </div>
                        </div>

                        <!-- Verificacion -->
                        <div class="col-12 mt-5">
                            <h4 class="fw-bold text-warning mb-4">
                                <i class="fas fa-shield-alt me-2"></i>Verificación
                            </h4>
                        </div>
                        
                        <div class="col-12">
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i>
                                <strong>Nota importante:</strong> Para garantizar que la ayuda llegue a quienes más la necesitan, 
                                nuestro equipo verificará la información proporcionada. Un empleado se pondrá en contacto contigo 
                                para confirmar los datos y coordinar la entrega de las donaciones.
                            </div>
                        </div>

                        <!-- Terminos y Condiciones -->
                        <div class="col-12 mt-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="terms" name="terms" required>
                                <label class="form-check-label" for="terms">
                                    Acepto los <a href="#" class="text-warning">términos y condiciones</a> del sistema de donaciones *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-12">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="dataConsent" name="dataConsent" required>
                                <label class="form-check-label" for="dataConsent">
                                    Autorizo el uso de mis datos para verificación y contacto relacionado con las donaciones *
                                </label>
                            </div>
                        </div>
                        
                        <div class="col-12">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="notifications" name="notifications">
                                <label class="form-check-label" for="notifications">
                                    Deseo recibir notificaciones sobre donaciones disponibles
                                </label>
                            </div>
                        </div>

                        <!-- Submit Button -->
                        <div class="col-12 text-center mt-5">
                            <button type="submit" class="btn btn-peru btn-lg px-5 py-3">
                                <i class="fas fa-user-plus me-2"></i>Registrar como Receptor
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
        let selectedNeeds = [];

        // Handle need selection
        document.querySelectorAll('.need-card').forEach(card => {
            card.addEventListener('click', function() {
                const need = this.dataset.need;
                
                if (this.classList.contains('selected')) {
                    // Deselect
                    this.classList.remove('selected');
                    selectedNeeds = selectedNeeds.filter(n => n !== need);
                } else {
                    // Select
                    this.classList.add('selected');
                    selectedNeeds.push(need);
                }
                
                // Update hidden input
                document.getElementById('selectedNeeds').value = selectedNeeds.join(',');
            });
        });

        // Form validation
        document.getElementById('receiverForm').addEventListener('submit', function(e) {
            if (selectedNeeds.length === 0) {
                e.preventDefault();
                alert('Por favor, selecciona al menos un tipo de ayuda que necesites.');
                return false;
            }
            
            // Validate DNI format
            const dni = document.getElementById('dni').value;
            if (!/^\d{8}$/.test(dni)) {
                e.preventDefault();
                alert('El DNI debe tener exactamente 8 dígitos.');
                return false;
            }
        });

        // Phone number formatting
        document.getElementById('phone').addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 9) {
                value = value.substring(0, 9);
            }
            e.target.value = value;
        });

        // DNI formatting
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