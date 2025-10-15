<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Donaciones - Usuario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .page-header {
            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
            color: white;
            padding: 40px 0;
        }
        .donation-card {
            border-radius: 10px;
            border: none;
            box-shadow: 0 3px 10px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
            margin-bottom: 15px;
        }
        .donation-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        .status-badge {
            font-size: 0.8rem;
            padding: 5px 10px;
            border-radius: 15px;
        }
    </style>
</head>
<body class="bg-light">
    <!--  Header -->
    <div class="page-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="fw-bold mb-2">
                        <i class="fas fa-gift me-2"></i>Mis Donaciones
                    </h1>
                    <p class="lead mb-0">Gestiona y revisa el estado de tus donaciones</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-light btn-lg">
                        <i class="fas fa-plus me-2"></i>Nueva Donación
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container my-4">
        <!-- Estadísticas -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card text-white bg-primary">
                    <div class="card-body text-center">
                        <h3>${totalDonations}</h3>
                        <p class="mb-0">Total Donaciones</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-success">
                    <div class="card-body text-center">
                        <h3>${activeDonations}</h3>
                        <p class="mb-0">Activas</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-warning">
                    <div class="card-body text-center">
                        <h3>${totalDonations - activeDonations}</h3>
                        <p class="mb-0">Completadas</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-info">
                    <div class="card-body text-center">
                        <h3>${userType}</h3>
                        <p class="mb-0">Mi Rol</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Lista de Donaciones -->
        <div class="card border-0 shadow-sm">
            <div class="card-header bg-white border-0 py-3">
                <h5 class="fw-bold mb-0">
                    <i class="fas fa-list me-2 text-primary"></i>
                    Historial de Mis Donaciones
                </h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty donations}">
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tipo</th>
                                        <th>Descripción</th>
                                        <th>Cantidad</th>
                                        <th>Estado</th>
                                        <th>Ubicación</th>
                                        <th>Fecha</th>
                                        <th>Condición</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="donation" items="${donations}">
                                        <tr>
                                            <td>#${donation.id}</td>
                                            <td>
                                                <i class="fas fa-${donation.type == 'ropa' ? 'tshirt' : donation.type == 'cuadernos' ? 'book' : 'gift'} me-2"></i>
                                                ${donation.type}
                                            </td>
                                            <td>${donation.description}</td>
                                            <td>
                                                <span class="badge bg-primary">${donation.quantity}</span>
                                            </td>
                                            <td>
                                                <span class="status-badge bg-${donation.status == 'active' ? 'success' : donation.status == 'completed' ? 'primary' : 'warning'}">
                                                    ${donation.status}
                                                </span>
                                            </td>
                                            <td>${donation.location}</td>
                                            <td>
                                                <small>${donation.createdDate}</small>
                                            </td>
                                            <td>
                                                <span class="badge bg-secondary">${donation.condition}</span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5">
                            <i class="fas fa-inbox fa-4x text-muted mb-4"></i>
                            <h4 class="text-muted">No hay donaciones registradas</h4>
                            <p class="text-muted">Aún no has realizado ninguna donación.</p>
                            <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-primary btn-lg mt-3">
                                <i class="fas fa-plus me-2"></i>Crear Primera Donación
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Botones de navegación -->
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary me-2">
                <i class="fas fa-arrow-left me-2"></i>Volver al Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/donations?action=new" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i>Nueva Donación
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>