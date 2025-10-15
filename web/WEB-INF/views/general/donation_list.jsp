<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Donaciones - Donaciones Perú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .page-header {
            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
            color: white;
            padding: 60px 0;
        }
        .donation-card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
            margin-bottom: 20px;
        }
        .donation-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        .donation-type-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            color: white;
        }
        .filter-card {
            background: #f8f9fa;
            border-radius: 15px;
            border: none;
        }
        .status-badge {
            font-size: 0.8rem;
            padding: 8px 15px;
            border-radius: 20px;
        }
    </style>
</head>
<body class="bg-light">
    <!-- Page Header -->
    <div class="page-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="fw-bold mb-3">
                        <i class="fas fa-list me-3"></i>
                        Lista de Donaciones
                    </h1>
                    <p class="lead mb-0">Explora todas las donaciones disponibles en tu región</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <% if (session.getAttribute("userType") != null && !session.getAttribute("userType").equals("usuario")) { %>
                        <a href="donations?action=new" class="btn btn-light btn-lg">
                            <i class="fas fa-plus me-2"></i>Nueva Donación
                        </a>
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <div class="container my-5">
        <div class="row">
            <!-- Filtros Sidebar -->
            <div class="col-md-3">
                <div class="card filter-card">
                    <div class="card-body">
                        <h5 class="fw-bold mb-4">
                            <i class="fas fa-filter me-2"></i>Filtros
                        </h5>
                        
                        <form method="get" action="donations">
                            <input type="hidden" name="action" value="list">
                            
                            <div class="mb-4">
                                <label class="form-label fw-bold">Tipo de Donación</label>
                                <select class="form-select" name="type">
                                    <option value="">Todos los tipos</option>
                                    <option value="ropa" <%= "ropa".equals(request.getParameter("type")) ? "selected" : "" %>>Ropa</option>
                                    <option value="cuadernos" <%= "cuadernos".equals(request.getParameter("type")) ? "selected" : "" %>>Cuadernos</option>
                                    <option value="utiles_escolares" <%= "utiles_escolares".equals(request.getParameter("type")) ? "selected" : "" %>>Útiles Escolares</option>
                                    <option value="material_reciclable" <%= "material_reciclable".equals(request.getParameter("type")) ? "selected" : "" %>>Material Reciclable</option>
                                    <option value="ropa_casi_nueva" <%= "ropa_casi_nueva".equals(request.getParameter("type")) ? "selected" : "" %>>Ropa Casi Nueva</option>
                                </select>
                            </div>
                            
                            <div class="mb-4">
                                <label class="form-label fw-bold">Ubicación</label>
                                <select class="form-select" name="location">
                                    <option value="">Todas las regiones</option>
                                    <option value="Lima" <%= "Lima".equals(request.getParameter("location")) ? "selected" : "" %>>Lima</option>
                                    <option value="Arequipa" <%= "Arequipa".equals(request.getParameter("location")) ? "selected" : "" %>>Arequipa</option>
                                    <option value="Cusco" <%= "Cusco".equals(request.getParameter("location")) ? "selected" : "" %>>Cusco</option>
                                    <option value="Trujillo" <%= "Trujillo".equals(request.getParameter("location")) ? "selected" : "" %>>Trujillo</option>
                                    <option value="Chiclayo" <%= "Chiclayo".equals(request.getParameter("location")) ? "selected" : "" %>>Chiclayo</option>
                                    <option value="Piura" <%= "Piura".equals(request.getParameter("location")) ? "selected" : "" %>>Piura</option>
                                </select>
                            </div>
                            
                            <div class="mb-4">
                                <label class="form-label fw-bold">Condición</label>
                                <select class="form-select" name="condition">
                                    <option value="">Todas las condiciones</option>
                                    <option value="nuevo" <%= "nuevo".equals(request.getParameter("condition")) ? "selected" : "" %>>Nuevo</option>
                                    <option value="como_nuevo" <%= "como_nuevo".equals(request.getParameter("condition")) ? "selected" : "" %>>Como Nuevo</option>
                                    <option value="buen_estado" <%= "buen_estado".equals(request.getParameter("condition")) ? "selected" : "" %>>Buen Estado</option>
                                    <option value="usado" <%= "usado".equals(request.getParameter("condition")) ? "selected" : "" %>>Usado</option>
                                </select>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-search me-2"></i>Aplicar Filtros
                                </button>
                                <a href="donations?action=list" class="btn btn-outline-secondary">
                                    <i class="fas fa-times me-2"></i>Limpiar
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
                
                <!-- Estadisticas -->
                <div class="card filter-card mt-4">
                    <div class="card-body text-center">
                        <h6 class="fw-bold mb-3">Estadísticas</h6>
                        <div class="mb-2">
                            <span class="badge bg-primary fs-6">
                                <%= request.getAttribute("totalDonations") != null ? request.getAttribute("totalDonations") : "0" %>
                            </span>
                            <small class="d-block text-muted">Total Donaciones</small>
                        </div>
                        <div class="mb-2">
                            <span class="badge bg-success fs-6">
                                <%= request.getAttribute("activeDonations") != null ? request.getAttribute("activeDonations") : "0" %>
                            </span>
                            <small class="d-block text-muted">Activas</small>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Donaciones  -->
            <div class="col-md-9">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h4 class="fw-bold mb-0">
                        Donaciones Disponibles
                        <span class="badge bg-primary ms-2">
                            <%= request.getAttribute("totalDonations") != null ? request.getAttribute("totalDonations") : "0" %>
                        </span>
                    </h4>
                    
                    <div class="btn-group">
                        <button class="btn btn-outline-secondary active" onclick="toggleView('grid')">
                            <i class="fas fa-th"></i>
                        </button>
                        <button class="btn btn-outline-secondary" onclick="toggleView('list')">
                            <i class="fas fa-list"></i>
                        </button>
                    </div>
                </div>

                <!-- Donaciones Grid -->
                <div id="donationsContainer">
                    <% 
                    List<Donation> donations = (List<Donation>) request.getAttribute("donations");
                    if (donations != null && !donations.isEmpty()) {
                        for (Donation donation : donations) {
                            String iconClass = "";
                            String iconColor = "";
                            
                            switch (donation.getType().toLowerCase()) {
                                case "ropa":
                                    iconClass = "fas fa-tshirt";
                                    iconColor = "bg-primary";
                                    break;
                                case "cuadernos":
                                    iconClass = "fas fa-book";
                                    iconColor = "bg-warning";
                                    break;
                                case "utiles_escolares":
                                    iconClass = "fas fa-pencil-alt";
                                    iconColor = "bg-info";
                                    break;
                                case "material_reciclable":
                                    iconClass = "fas fa-recycle";
                                    iconColor = "bg-success";
                                    break;
                                case "ropa_casi_nueva":
                                    iconClass = "fas fa-star";
                                    iconColor = "bg-danger";
                                    break;
                                default:
                                    iconClass = "fas fa-gift";
                                    iconColor = "bg-secondary";
                            }
                    %>
                        <div class="donation-card card mb-4">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-2 text-center">
                                        <div class="donation-type-icon <%= iconColor %>">
                                            <i class="<%= iconClass %>"></i>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <h5 class="fw-bold mb-2"><%= donation.getType() %></h5>
                                        <p class="text-muted mb-2"><%= donation.getDescription() %></p>
                                        <div class="d-flex align-items-center text-muted">
                                            <i class="fas fa-map-marker-alt me-2"></i>
                                            <span><%= donation.getLocation() %></span>
                                        </div>
                                    </div>
                                    <div class="col-md-2 text-center">
                                        <div class="mb-2">
                                            <span class="fw-bold fs-4 text-primary"><%= donation.getQuantity() %></span>
                                            <small class="d-block text-muted">Cantidad</small>
                                        </div>
                                    </div>
                                    <div class="col-md-2 text-center">
                                        <span class="status-badge bg-success text-white mb-2 d-block">
                                            <%= donation.getCondition() %>
                                        </span>
                                        <small class="text-muted">
                                            <i class="fas fa-calendar me-1"></i>
                                            <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(donation.getCreatedDate()) %>
                                        </small>
                                    </div>
                                </div>
                                
                                <% if (session.getAttribute("userType") != null && !session.getAttribute("userType").equals("usuario")) { %>
                                    <div class="row mt-3">
                                        <div class="col-12 text-end">
                                            <button class="btn btn-outline-primary btn-sm me-2">
                                                <i class="fas fa-eye me-1"></i>Ver Detalles
                                            </button>
                                            <button class="btn btn-outline-success btn-sm">
                                                <i class="fas fa-phone me-1"></i>Contactar
                                            </button>
                                        </div>
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    <% 
                        }
                    } else {
                    %>
                        <div class="text-center py-5">
                            <i class="fas fa-search fa-4x text-muted mb-4"></i>
                            <h4 class="text-muted">No hay donaciones registradas</h4>
                            <p class="text-muted">No se encontraron donaciones que coincidan con los filtros seleccionados.</p>
                            <% if (session.getAttribute("userType") != null && !session.getAttribute("userType").equals("usuario")) { %>
                                <a href="donations?action=new" class="btn btn-primary btn-lg mt-3">
                                    <i class="fas fa-plus me-2"></i>Crear Primera Donación
                                </a>
                            <% } %>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
        
        <!-- Volver -->
        <div class="text-center mt-5">
            <a href="dashboard" class="btn btn-outline-primary">
                <i class="fas fa-arrow-left me-2"></i>Volver al Dashboard
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function toggleView(viewType) {
            document.querySelectorAll('.btn-group .btn').forEach(btn => {
                btn.classList.remove('active');
            });
            event.target.classList.add('active');
            
        }
    </script>
</body>
</html>