<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="col-md-3 col-lg-2 sidebar">
    <div class="p-3">
        <div class="text-center mb-4">
            <i class="fas fa-user-tie fa-3x text-white mb-2"></i>
            <h5 class="text-white fw-bold">Panel Empleado</h5>
            <small class="text-white-50">
                Bienvenido, <%= session.getAttribute("username")%>
            </small>
        </div>

        <nav class="nav flex-column">
            <a class="nav-link <%= request.getServletPath() != null && request.getServletPath().contains("/profile") ? "active" : ""%>" 
               href="${pageContext.request.contextPath}/profile">
                <i class="fas fa-user me-2"></i>Mi Perfil
            </a>
            <a class="nav-link <%= request.getServletPath() != null && request.getServletPath().contains("/dashboard") ? "active" : ""%>" 
               href="${pageContext.request.contextPath}/dashboard">
                <i class="fas fa-tachometer-alt me-2"></i>Dashboard
            </a>
            <a class="nav-link <%= request.getServletPath() != null && request.getServletPath().contains("/donations") && (request.getQueryString() == null || request.getQueryString().contains("action=list")) ? "active" : ""%>" 
               href="${pageContext.request.contextPath}/donations?action=list">
                <i class="fas fa-gift me-2"></i>Gestionar Donaciones
            </a>
            <a class="nav-link <%= request.getServletPath() != null && request.getServletPath().contains("/requestManagement") ? "active" : ""%>" 
               href="${pageContext.request.contextPath}/requestManagement?action=list">
                <i class="fas fa-list-alt me-2"></i>Gestión de Solicitudes
            </a>
            <a class="nav-link <%= request.getServletPath() != null && request.getServletPath().contains("/users") && request.getQueryString() != null && request.getQueryString().contains("newDonor") ? "active" : ""%>" 
               href="${pageContext.request.contextPath}/users?action=newDonor">
                <i class="fas fa-hand-holding-heart me-2"></i>Registrar Donador
            </a>
            <a class="nav-link <%= request.getServletPath() != null && request.getServletPath().contains("/users") && request.getQueryString() != null && request.getQueryString().contains("newReceiver") ? "active" : ""%>" 
               href="${pageContext.request.contextPath}/users?action=newReceiver">
                <i class="fas fa-hands-helping me-2"></i>Registrar Receptor
            </a>
            <a class="nav-link <%= request.getServletPath() != null && request.getServletPath().contains("/donations") && request.getQueryString() != null && request.getQueryString().contains("action=new") ? "active" : ""%>" 
               href="${pageContext.request.contextPath}/donations?action=new">
                <i class="fas fa-plus-circle me-2"></i>Nueva Donación
            </a>
            <a class="nav-link <%= request.getServletPath() != null && request.getServletPath().contains("/reports") ? "active" : ""%>" 
               href="${pageContext.request.contextPath}/reports">
                <i class="fas fa-chart-bar me-2"></i>Mis Reportes
            </a>
            <hr class="text-white-50">
            <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
            </a>
        </nav>
    </div>
</div>

<style>
    .sidebar {
        min-height: 100vh;
        background: linear-gradient(180deg, #ffc107 0%, #e0a800 100%);
    }
    .sidebar .nav-link {
        color: rgba(0,0,0,0.7);
        padding: 15px 20px;
        border-radius: 10px;
        margin: 5px 10px;
        transition: all 0.3s ease;
    }
    .sidebar .nav-link:hover, .sidebar .nav-link.active {
        background: rgba(0,0,0,0.1);
        color: #000;
        transform: translateX(5px);
    }
</style>