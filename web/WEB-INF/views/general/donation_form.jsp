<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva Donación - Donaciones Perú</title>
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
        .form-step {
            display: none;
        }
        .form-step.active {
            display: block;
        }
        .step-indicator {
            background: #e9ecef;
            height: 4px;
            border-radius: 2px;
            overflow: hidden;
        }
        .step-progress {
            background: linear-gradient(45deg, #28a745, #20c997);
            height: 100%;
            transition: width 0.3s ease;
        }
        .donation-type-card {
            border: 2px solid #e9ecef;
            border-radius: 15px;
            padding: 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .donation-type-card:hover {
            border-color: #28a745;
            transform: translateY(-5px);
        }
        .donation-type-card.selected {
            border-color: #28a745;
            background: #f8fff9;
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
    </style>
</head>
<body class="bg-light">
    <!-- Mostrar mensajes de error en el formulario -->
    <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="container mt-3">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>
                <%= request.getAttribute("errorMessage") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </div>
    <% } %>
    <!-- Form Header -->
    <div class="form-header">
        <div class="container text-center">
            <h1 class="fw-bold mb-3">
                <i class="fas fa-gift me-3"></i>
                Nueva Donación
            </h1>
            <p class="lead mb-0">Ayuda a tu comunidad compartiendo lo que ya no necesitas</p>
        </div>
    </div>

    <div class="container">
        <div class="card form-card border-0">
            <div class="card-body p-5">
                <!-- Step Indicator -->
                <div class="step-indicator mb-4">
                    <div class="step-progress" id="stepProgress" style="width: 33%"></div>
                </div>
                
                <div class="text-center mb-4">
                    <span class="badge bg-success fs-6 px-3 py-2" id="stepLabel">
                        Paso 1 de 3: Tipo de Donación
                    </span>
                </div>

                <form action="donations" method="post" id="donationForm">
                    <input type="hidden" name="action" value="create">
                    
                    <!-- Paso 1: Donacion -->
                    <div class="form-step active" id="step1">
                        <h4 class="fw-bold text-center mb-4">¿Qué deseas donar?</h4>
                        
                        <div class="row g-4">
                            <div class="col-md-4">
                                <div class="donation-type-card" data-type="ropa">
                                    <i class="fas fa-tshirt fa-3x text-primary mb-3"></i>
                                    <h5 class="fw-bold">Ropa</h5>
                                    <p class="text-muted">Prendas de vestir en buen estado</p>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="donation-type-card" data-type="cuadernos">
                                    <i class="fas fa-book fa-3x text-warning mb-3"></i>
                                    <h5 class="fw-bold">Cuadernos</h5>
                                    <p class="text-muted">Cuadernos nuevos o poco usados</p>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="donation-type-card" data-type="utiles_escolares">
                                    <i class="fas fa-pencil-alt fa-3x text-info mb-3"></i>
                                    <h5 class="fw-bold">Útiles Escolares</h5>
                                    <p class="text-muted">Lápices, colores, reglas, etc.</p>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="donation-type-card" data-type="material_reciclable">
                                    <i class="fas fa-recycle fa-3x text-success mb-3"></i>
                                    <h5 class="fw-bold">Material Reciclable</h5>
                                    <p class="text-muted">Papel, cartón, plástico</p>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="donation-type-card" data-type="ropa_casi_nueva">
                                    <i class="fas fa-star fa-3x text-danger mb-3"></i>
                                    <h5 class="fw-bold">Ropa Casi Nueva</h5>
                                    <p class="text-muted">Prendas en excelente estado</p>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="donation-type-card" data-type="otros">
                                    <i class="fas fa-plus fa-3x text-secondary mb-3"></i>
                                    <h5 class="fw-bold">Otros</h5>
                                    <p class="text-muted">Otros artículos útiles</p>
                                </div>
                            </div>
                        </div>
                        
                        <input type="hidden" name="donationType" id="donationType" required>
                        
                        <div class="text-center mt-4">
                            <button type="button" class="btn btn-peru btn-lg" onclick="nextStep()" id="step1Next" disabled>
                                Continuar <i class="fas fa-arrow-right ms-2"></i>
                            </button>
                        </div>
                    </div>

                    <!-- Paso 2: Detalles -->
                    <div class="form-step" id="step2">
                        <h4 class="fw-bold text-center mb-4">Detalles de la Donación</h4>
                        
                        <div class="row g-4">
                            <div class="col-md-6">
                                <label for="description" class="form-label fw-bold">
                                    <i class="fas fa-align-left me-2"></i>Descripción
                                </label>
                                <textarea class="form-control form-control-lg" id="description" name="description" rows="4" required placeholder="Describe tu donación en detalle..."></textarea>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="quantity" class="form-label fw-bold">
                                        <i class="fas fa-sort-numeric-up me-2"></i>Cantidad
                                    </label>
                                    <input type="number" class="form-control form-control-lg" id="quantity" name="quantity" required min="1" placeholder="Ej: 5">
                                </div>
                                <div class="mb-3">
                                    <label for="condition" class="form-label fw-bold">
                                        <i class="fas fa-check-circle me-2"></i>Condición
                                    </label>
                                    <select class="form-select form-select-lg" id="condition" name="condition" required>
                                        <option value="">Seleccionar condición...</option>
                                        <option value="nuevo">Nuevo</option>
                                        <option value="como_nuevo">Como Nuevo</option>
                                        <option value="buen_estado">Buen Estado</option>
                                        <option value="usado">Usado</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        
                        <div class="text-center mt-4">
                            <button type="button" class="btn btn-outline-secondary me-3" onclick="prevStep()">
                                <i class="fas fa-arrow-left me-2"></i>Anterior
                            </button>
                            <button type="button" class="btn btn-peru btn-lg" onclick="nextStep()">
                                Continuar <i class="fas fa-arrow-right ms-2"></i>
                            </button>
                        </div>
                    </div>

                    <!-- Paso 3: Localizacion -->
                    <div class="form-step" id="step3">
                        <h4 class="fw-bold text-center mb-4">Ubicación de la Donación</h4>
                        
                        <div class="row justify-content-center">
                            <div class="col-md-8">
                                <div class="mb-4">
                                    <label for="location" class="form-label fw-bold">
                                        <i class="fas fa-map-marker-alt me-2"></i>Región del Perú
                                    </label>
                                    <select class="form-select form-select-lg" id="location" name="location" required>
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
                                </div>
                                
                                <div class="mb-4">
                                    <label for="address" class="form-label fw-bold">
                                        <i class="fas fa-home me-2"></i>Dirección Específica (Opcional)
                                    </label>
                                    <input type="text" class="form-control form-control-lg" id="address" name="address" placeholder="Ej: Av. Principal 123, San Isidro">
                                </div>
                                
                                <div class="alert alert-info">
                                    <i class="fas fa-info-circle me-2"></i>
                                    <strong>Nota:</strong> Un empleado se pondrá en contacto contigo para coordinar la recolección de tu donación.
                                </div>
                            </div>
                        </div>
                        
                        <div class="text-center mt-4">
                            <button type="button" class="btn btn-outline-secondary me-3" onclick="prevStep()">
                                <i class="fas fa-arrow-left me-2"></i>Anterior
                            </button>
                            <button type="submit" class="btn btn-peru btn-lg">
                                <i class="fas fa-heart me-2"></i>Confirmar Donación
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <div class="text-center my-4">
            <a href="dashboard" class="text-decoration-none">
                <i class="fas fa-arrow-left me-2"></i>Volver al Dashboard
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let currentStep = 1;
        const totalSteps = 3;

        document.querySelectorAll('.donation-type-card').forEach(card => {
            card.addEventListener('click', function() {
                document.querySelectorAll('.donation-type-card').forEach(c => c.classList.remove('selected'));
                
                this.classList.add('selected');
                
                document.getElementById('donationType').value = this.dataset.type;
                
                document.getElementById('step1Next').disabled = false;
            });
        });

        function nextStep() {
            if (currentStep < totalSteps) {
                if (currentStep === 1) {
                    if (!document.getElementById('donationType').value) {
                        alert('Por favor, selecciona un tipo de donación.');
                        return;
                    }
                }
                
                // Oculta el paso
                document.getElementById('step' + currentStep).classList.remove('active');
                
                // Muestra el siguiente paso
                currentStep++;
                document.getElementById('step' + currentStep).classList.add('active');
                
                // Actualiza la barra de progreso
                const progress = (currentStep / totalSteps) * 100;
                document.getElementById('stepProgress').style.width = progress + '%';
                
                // Actualiza los campos de texto
                document.getElementById('stepLabel').textContent = 'Paso ' + currentStep + ' de ' + totalSteps + ': ' + getStepTitle(currentStep);
            }
        }

        function prevStep() {
            if (currentStep > 1) {
                // Oculta el paso
                document.getElementById('step' + currentStep).classList.remove('active');
                
                // Muestra el anterior paso
                currentStep--;
                document.getElementById('step' + currentStep).classList.add('active');
                
                // Actualiza la barra de progreso
                const progress = (currentStep / totalSteps) * 100;
                document.getElementById('stepProgress').style.width = progress + '%';
                
                // Actualiza los campos de texto
                document.getElementById('stepLabel').textContent = 'Paso ' + currentStep + ' de ' + totalSteps + ': ' + getStepTitle(currentStep);
            }
        }

        function getStepTitle(step) {
            const titles = {
                1: 'Tipo de Donación',
                2: 'Detalles',
                3: 'Ubicación'
            };
            return titles[step];
        }

        // Form validation
        document.getElementById('donationForm').addEventListener('submit', function(e) {
            const requiredFields = ['donationType', 'description', 'quantity', 'condition', 'location'];
            let isValid = true;
            let firstErrorField = null;
            
            requiredFields.forEach(field => {
                const element = document.getElementsByName(field)[0];
                if (!element.value.trim()) {
                    isValid = false;
                    element.classList.add('is-invalid');
                    if (!firstErrorField) firstErrorField = element;
                } else {
                    element.classList.remove('is-invalid');
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                alert('Por favor, completa todos los campos requeridos.');
                if (firstErrorField) {
                    firstErrorField.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
                
                if (!document.getElementById('donationType').value) {
                    showStep(1);
                } else if (!document.getElementById('description').value || 
                          !document.getElementById('quantity').value || 
                          !document.getElementById('condition').value) {
                    showStep(2);
                } else {
                    showStep(3);
                }
            }
        });

        function showStep(step) {
            // Oculta todos los pasos
            document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
            
            // Muestra el paso especifico
            currentStep = step;
            document.getElementById('step' + step).classList.add('active');
            
            // Actualiza la barra de progreso
            const progress = (step / totalSteps) * 100;
            document.getElementById('stepProgress').style.width = progress + '%';
            
            // Actualiza los campos de texto
            document.getElementById('stepLabel').textContent = 'Paso ' + step + ' de ' + totalSteps + ': ' + getStepTitle(step);
        }
    </script>
</body>
</html>