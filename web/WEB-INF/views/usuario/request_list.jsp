<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Solicitudes - Usuario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .page-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 40px 0;
        }
        .request-card {
            border-radius: 10px;
            border: none;
            box-shadow: 0 3px 10px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
            margin-bottom: 15px;
        }
        .request-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        .priority-badge {
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
                        <i class="fas fa-hands-helping me-2"></i>Mis Solicitudes
                    </h1>
                    <p class="lead mb-0">Revisa el estado de tus solicitudes de ayuda</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <a href="${pageContext.request.contextPath}/donations?action=newRequest" class="btn btn-light btn-lg">
                        <i class="fas fa-plus me-2"></i>Nueva Solicitud
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container my-4">
        <!-- Estadísticas -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card text-white bg-success">
                    <div class="card-body text-center">
                        <h3>${totalUserRequests}</h3>
                        <p class="mb-0">Total Solicitudes</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-warning">
                    <div class="card-body text-center">
                        <h3>${pendingRequests}</h3>
                        <p class="mb-0">Pendientes</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-info">
                    <div class="card-body text-center">
                        <h3>${inProgressRequests}</h3>
                        <p class="mb-0">En Proceso</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-primary">
                    <div class="card-body text-center">
                        <h3>${completedRequests}</h3>
                        <p class="mb-0">Completadas</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Lista de Solicitudes -->
        <div class="card border-0 shadow-sm">
            <div class="card-header bg-white border-0 py-3">
                <h5 class="fw-bold mb-0">
                    <i class="fas fa-list-alt me-2 text-success"></i>
                    Historial de Mis Solicitudes
                </h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty userRequests}">
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tipo</th>
                                        <th>Descripción</th>
                                        <th>Prioridad</th>
                                        <th>Estado</th>
                                        <th>Ubicación</th>
                                        <th>Fecha</th>
                                        <th>Asignado a</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="request" items="${userRequests}">
                                        <tr>
                                            <td>#${request.id}</td>
                                            <td>${request.type}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${request.description.length() > 50}">
                                                        ${request.description.substring(0, 50)}...
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${request.description}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="priority-badge ${request.priorityBadge}">
                                                    ${request.priorityText}
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge bg-${request.status == 'pending' ? 'warning' : request.status == 'in_progress' ? 'info' : 'success'}">
                                                    ${request.formattedStatus}
                                                </span>
                                            </td>
                                            <td>${request.location}</td>
                                            <td>
                                                <small>${request.requestDate}</small>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty request.assignedTo}">
                                                        <span class="badge bg-info">${request.assignedTo}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Sin asignar</span>
                                                    </c:otherwise>
                                                </c:choose>
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
                            <h4 class="text-muted">No hay solicitudes registradas</h4>
                            <p class="text-muted">Aún no has realizado ninguna solicitud de ayuda.</p>
                            <a href="${pageContext.request.contextPath}/donations?action=newRequest" class="btn btn-success btn-lg mt-3">
                                <i class="fas fa-plus me-2"></i>Crear Primera Solicitud
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
            <a href="${pageContext.request.contextPath}/donations?action=newRequest" class="btn btn-success">
                <i class="fas fa-plus me-2"></i>Nueva Solicitud
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>