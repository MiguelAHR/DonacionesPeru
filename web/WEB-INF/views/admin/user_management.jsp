<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
<%@page import="com.donaciones.utils.DataManager"%>
<%@page import="java.text.SimpleDateFormat"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Usuarios - Administrador</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .action-buttons .btn {
            margin: 0 2px;
            padding: 0.25rem 0.5rem;
            font-size: 0.875rem;
        }
        .user-form-container {
            max-width: 800px;
            margin: 0 auto;
        }
        .tab-content {
            min-height: 500px;
        }
        .table th {
            background-color: #343a40;
            color: white;
        }
        .badge-user {
            font-size: 0.8em;
            padding: 0.3em 0.6em;
        }
        .user-type-icon {
            width: 30px;
            text-align: center;
        }
        .stat-card {
            border-left: 4px solid #007bff;
            padding: 15px;
            margin-bottom: 15px;
            background: #f8f9fa;
            border-radius: 5px;
        }
        .stat-card.donor {
            border-left-color: #28a745;
        }
        .stat-card.receiver {
            border-left-color: #17a2b8;
        }
        .stat-card.admin {
            border-left-color: #dc3545;
        }
        .stat-card.employee {
            border-left-color: #ffc107;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="../admin/sidebar.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2 class="fw-bold">Gestión de Usuarios</h2>
                        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#userModal" onclick="resetForm()">
                            <i class="fas fa-plus"></i> Nuevo Usuario
                        </button>
                    </div>

                    <!-- Estadísticas rápidas -->
                    <div class="row mb-4">
                        <div class="col-md-3">
                            <div class="stat-card">
                                <h6 class="text-muted mb-1">Total Usuarios</h6>
                                <h3 class="mb-0">
                                    <% 
                                        DataManager dm = DataManager.getInstance();
                                        int totalUsers = dm.getTotalUsers();
                                    %>
                                    <%= totalUsers %>
                                </h3>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stat-card donor">
                                <h6 class="text-muted mb-1">Donadores</h6>
                                <h3 class="mb-0">
                                    <%= dm.getTotalDonors() %>
                                </h3>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stat-card receiver">
                                <h6 class="text-muted mb-1">Receptores</h6>
                                <h3 class="mb-0">
                                    <%= dm.getTotalReceivers() %>
                                </h3>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stat-card admin">
                                <h6 class="text-muted mb-1">Administradores</h6>
                                <h3 class="mb-0">
                                    <%= dm.getTotalAdmins() %>
                                </h3>
                            </div>
                        </div>
                    </div>

                    <!-- Mensajes de éxito/error -->
                    <% 
                        String success = (String) session.getAttribute("successMessage");
                        String error = (String) session.getAttribute("errorMessage");
                        if (success != null) { 
                    %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <%= success %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% session.removeAttribute("successMessage"); %>
                    <% } %>
                    <% if (error != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <%= error %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% session.removeAttribute("errorMessage"); %>
                    <% } %>

                    <!-- Pestañas -->
                    <ul class="nav nav-tabs" id="userTabs">
                        <li class="nav-item">
                            <a class="nav-link active" data-bs-toggle="tab" href="#allUsers">Todos los Usuarios</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="tab" href="#donors">Donadores</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="tab" href="#receivers">Receptores</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="tab" href="#admins">Administradores</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="tab" href="#employees">Empleados</a>
                        </li>
                    </ul>

                    <div class="tab-content mt-3">
                        <!-- Pestaña de todos los usuarios -->
                        <div class="tab-pane fade show active" id="allUsers">
                            <div class="card">
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead>
                                                <tr>
                                                    <th>ID</th>
                                                    <th>Usuario</th>
                                                    <th>Nombre</th>
                                                    <th>Email</th>
                                                    <th>Tipo</th>
                                                    <th>Región</th>
                                                    <th>Registro</th>
                                                    <th>Estado</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                List<User> allUsersList = (List<User>) request.getAttribute("allUsers");
                                                if (allUsersList != null && !allUsersList.isEmpty()) {
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                    for (User user : allUsersList) {
                                                        String userTypeDisplay = "";
                                                        String badgeClass = "bg-secondary";
                                                        
                                                        switch(user.getUserType()) {
                                                            case "admin":
                                                                userTypeDisplay = "Administrador";
                                                                badgeClass = "bg-danger";
                                                                break;
                                                            case "empleado":
                                                                userTypeDisplay = "Empleado";
                                                                badgeClass = "bg-warning";
                                                                break;
                                                            case "donador":
                                                                userTypeDisplay = "Donador";
                                                                badgeClass = "bg-info";
                                                                break;
                                                            case "receptor":
                                                                userTypeDisplay = "Receptor";
                                                                badgeClass = "bg-success";
                                                                break;
                                                            default:
                                                                userTypeDisplay = "Usuario";
                                                                badgeClass = "bg-secondary";
                                                        }
                                                %>
                                                <tr>
                                                    <td><%= user.getId() %></td>
                                                    <td><strong><%= user.getUsername() %></strong></td>
                                                    <td><%= user.getFirstName() %> <%= user.getLastName() %></td>
                                                    <td><%= user.getEmail() %></td>
                                                    <td>
                                                        <span class="badge <%= badgeClass %> badge-user">
                                                            <i class="fas fa-<%= 
                                                                user.getUserType().equals("admin") ? "user-shield" : 
                                                                user.getUserType().equals("empleado") ? "user-tie" : 
                                                                user.getUserType().equals("donador") ? "hand-holding-heart" :
                                                                user.getUserType().equals("receptor") ? "hands-helping" : "user" 
                                                            %>"></i> <%= userTypeDisplay %>
                                                        </span>
                                                    </td>
                                                    <td><%= user.getRegion() != null ? user.getRegion() : "N/A" %></td>
                                                    <td>
                                                        <% if (user.getRegistrationDate() != null) { %>
                                                            <%= dateFormat.format(user.getRegistrationDate()) %>
                                                        <% } else { %>
                                                            N/A
                                                        <% } %>
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-<%= user.isActive() ? "success" : "secondary" %>">
                                                            <%= user.isActive() ? "Activo" : "Inactivo" %>
                                                        </span>
                                                    </td>
                                                    <td class="action-buttons">
                                                        <button class="btn btn-sm btn-warning" 
                                                                onclick="editUser(
                                                                    <%= user.getId() %>, 
                                                                    '<%= user.getUsername() %>',
                                                                    '<%= user.getFirstName() != null ? user.getFirstName().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getLastName() != null ? user.getLastName().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getEmail() != null ? user.getEmail().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getPhone() != null ? user.getPhone().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getDni() != null ? user.getDni().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getBirthDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthDate()) : "" %>',
                                                                    '<%= user.getRegion() != null ? user.getRegion().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getDistrict() != null ? user.getDistrict().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getAddress() != null ? user.getAddress().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getUserType() %>',
                                                                    <%= user.isActive() %>,
                                                                    <%= user.isNotificationsEnabled() %>
                                                                )">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-info" 
                                                                onclick="toggleUserStatus(<%= user.getId() %>, <%= !user.isActive() %>)">
                                                            <i class="fas fa-<%= user.isActive() ? "pause" : "play" %>"></i>
                                                        </button>
                                                        <% if (!session.getAttribute("username").equals(user.getUsername())) { %>
                                                            <button class="btn btn-sm btn-danger" 
                                                                    onclick="confirmDelete(<%= user.getId() %>, '<%= user.getUsername().replace("'", "\\'") %>')">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        <% } %>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                } else {
                                                %>
                                                <tr>
                                                    <td colspan="9" class="text-center text-muted py-4">
                                                        <i class="fas fa-users fa-2x mb-3"></i><br>
                                                        No hay usuarios registrados
                                                    </td>
                                                </tr>
                                                <%
                                                }
                                                %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Pestaña de donadores -->
                        <div class="tab-pane fade" id="donors">
                            <div class="card">
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Usuario</th>
                                                    <th>Nombre</th>
                                                    <th>Email</th>
                                                    <th>Teléfono</th>
                                                    <th>DNI</th>
                                                    <th>Región</th>
                                                    <th>Registro</th>
                                                    <th>Estado</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                List<User> donorsList = new ArrayList<>();
                                                if (allUsersList != null) {
                                                    for (User user : allUsersList) {
                                                        if ("donador".equals(user.getUserType())) {
                                                            donorsList.add(user);
                                                        }
                                                    }
                                                }
                                                
                                                if (!donorsList.isEmpty()) {
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                    for (User donor : donorsList) {
                                                %>
                                                <tr>
                                                    <td><strong><%= donor.getUsername() %></strong></td>
                                                    <td><%= donor.getFirstName() %> <%= donor.getLastName() %></td>
                                                    <td><%= donor.getEmail() %></td>
                                                    <td><%= donor.getPhone() != null ? donor.getPhone() : "N/A" %></td>
                                                    <td><%= donor.getDni() != null ? donor.getDni() : "N/A" %></td>
                                                    <td><%= donor.getRegion() != null ? donor.getRegion() : "N/A" %></td>
                                                    <td>
                                                        <% if (donor.getRegistrationDate() != null) { %>
                                                            <%= dateFormat.format(donor.getRegistrationDate()) %>
                                                        <% } else { %>
                                                            N/A
                                                        <% } %>
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-<%= donor.isActive() ? "success" : "secondary" %>">
                                                            <%= donor.isActive() ? "Activo" : "Inactivo" %>
                                                        </span>
                                                    </td>
                                                    <td class="action-buttons">
                                                        <button class="btn btn-sm btn-warning" 
                                                                onclick="editUser(
                                                                    <%= donor.getId() %>, 
                                                                    '<%= donor.getUsername() %>',
                                                                    '<%= donor.getFirstName() != null ? donor.getFirstName().replace("'", "\\'") : "" %>',
                                                                    '<%= donor.getLastName() != null ? donor.getLastName().replace("'", "\\'") : "" %>',
                                                                    '<%= donor.getEmail() != null ? donor.getEmail().replace("'", "\\'") : "" %>',
                                                                    '<%= donor.getPhone() != null ? donor.getPhone().replace("'", "\\'") : "" %>',
                                                                    '<%= donor.getDni() != null ? donor.getDni().replace("'", "\\'") : "" %>',
                                                                    '<%= donor.getBirthDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(donor.getBirthDate()) : "" %>',
                                                                    '<%= donor.getRegion() != null ? donor.getRegion().replace("'", "\\'") : "" %>',
                                                                    '<%= donor.getDistrict() != null ? donor.getDistrict().replace("'", "\\'") : "" %>',
                                                                    '<%= donor.getAddress() != null ? donor.getAddress().replace("'", "\\'") : "" %>',
                                                                    'donador',
                                                                    <%= donor.isActive() %>,
                                                                    <%= donor.isNotificationsEnabled() %>
                                                                )">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-info" 
                                                                onclick="toggleUserStatus(<%= donor.getId() %>, <%= !donor.isActive() %>)">
                                                            <i class="fas fa-<%= donor.isActive() ? "pause" : "play" %>"></i>
                                                        </button>
                                                        <% if (!session.getAttribute("username").equals(donor.getUsername())) { %>
                                                            <button class="btn btn-sm btn-danger" 
                                                                    onclick="confirmDelete(<%= donor.getId() %>, '<%= donor.getUsername().replace("'", "\\'") %>')">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        <% } %>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                } else {
                                                %>
                                                <tr>
                                                    <td colspan="9" class="text-center text-muted py-4">
                                                        <i class="fas fa-hand-holding-heart fa-2x mb-3"></i><br>
                                                        No hay donadores registrados
                                                    </td>
                                                </tr>
                                                <%
                                                }
                                                %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Pestaña de receptores -->
                        <div class="tab-pane fade" id="receivers">
                            <div class="card">
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Usuario</th>
                                                    <th>Nombre</th>
                                                    <th>Email</th>
                                                    <th>Teléfono</th>
                                                    <th>DNI</th>
                                                    <th>Región</th>
                                                    <th>Dirección</th>
                                                    <th>Estado</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                List<User> receiversList = new ArrayList<>();
                                                if (allUsersList != null) {
                                                    for (User user : allUsersList) {
                                                        if ("receptor".equals(user.getUserType())) {
                                                            receiversList.add(user);
                                                        }
                                                    }
                                                }
                                                
                                                if (!receiversList.isEmpty()) {
                                                    for (User receiver : receiversList) {
                                                %>
                                                <tr>
                                                    <td><strong><%= receiver.getUsername() %></strong></td>
                                                    <td><%= receiver.getFirstName() %> <%= receiver.getLastName() %></td>
                                                    <td><%= receiver.getEmail() %></td>
                                                    <td><%= receiver.getPhone() != null ? receiver.getPhone() : "N/A" %></td>
                                                    <td><%= receiver.getDni() != null ? receiver.getDni() : "N/A" %></td>
                                                    <td><%= receiver.getRegion() != null ? receiver.getRegion() : "N/A" %></td>
                                                    <td><%= receiver.getAddress() != null ? (receiver.getAddress().length() > 30 ? receiver.getAddress().substring(0, 30) + "..." : receiver.getAddress()) : "N/A" %></td>
                                                    <td>
                                                        <span class="badge bg-<%= receiver.isActive() ? "success" : "secondary" %>">
                                                            <%= receiver.isActive() ? "Activo" : "Inactivo" %>
                                                        </span>
                                                    </td>
                                                    <td class="action-buttons">
                                                        <button class="btn btn-sm btn-warning" 
                                                                onclick="editUser(
                                                                    <%= receiver.getId() %>, 
                                                                    '<%= receiver.getUsername() %>',
                                                                    '<%= receiver.getFirstName() != null ? receiver.getFirstName().replace("'", "\\'") : "" %>',
                                                                    '<%= receiver.getLastName() != null ? receiver.getLastName().replace("'", "\\'") : "" %>',
                                                                    '<%= receiver.getEmail() != null ? receiver.getEmail().replace("'", "\\'") : "" %>',
                                                                    '<%= receiver.getPhone() != null ? receiver.getPhone().replace("'", "\\'") : "" %>',
                                                                    '<%= receiver.getDni() != null ? receiver.getDni().replace("'", "\\'") : "" %>',
                                                                    '<%= receiver.getBirthDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(receiver.getBirthDate()) : "" %>',
                                                                    '<%= receiver.getRegion() != null ? receiver.getRegion().replace("'", "\\'") : "" %>',
                                                                    '<%= receiver.getDistrict() != null ? receiver.getDistrict().replace("'", "\\'") : "" %>',
                                                                    '<%= receiver.getAddress() != null ? receiver.getAddress().replace("'", "\\'") : "" %>',
                                                                    'receptor',
                                                                    <%= receiver.isActive() %>,
                                                                    <%= receiver.isNotificationsEnabled() %>
                                                                )">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-info" 
                                                                onclick="toggleUserStatus(<%= receiver.getId() %>, <%= !receiver.isActive() %>)">
                                                            <i class="fas fa-<%= receiver.isActive() ? "pause" : "play" %>"></i>
                                                        </button>
                                                        <% if (!session.getAttribute("username").equals(receiver.getUsername())) { %>
                                                            <button class="btn btn-sm btn-danger" 
                                                                    onclick="confirmDelete(<%= receiver.getId() %>, '<%= receiver.getUsername().replace("'", "\\'") %>')">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        <% } %>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                } else {
                                                %>
                                                <tr>
                                                    <td colspan="9" class="text-center text-muted py-4">
                                                        <i class="fas fa-hands-helping fa-2x mb-3"></i><br>
                                                        No hay receptores registrados
                                                    </td>
                                                </tr>
                                                <%
                                                }
                                                %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Pestaña de administradores -->
                        <div class="tab-pane fade" id="admins">
                            <div class="card">
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Usuario</th>
                                                    <th>Nombre</th>
                                                    <th>Email</th>
                                                    <th>Teléfono</th>
                                                    <th>DNI</th>
                                                    <th>Registro</th>
                                                    <th>Estado</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                List<User> adminsList = new ArrayList<>();
                                                if (allUsersList != null) {
                                                    for (User user : allUsersList) {
                                                        if ("admin".equals(user.getUserType())) {
                                                            adminsList.add(user);
                                                        }
                                                    }
                                                }
                                                
                                                if (!adminsList.isEmpty()) {
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                    for (User admin : adminsList) {
                                                %>
                                                <tr>
                                                    <td><strong><%= admin.getUsername() %></strong></td>
                                                    <td><%= admin.getFirstName() %> <%= admin.getLastName() %></td>
                                                    <td><%= admin.getEmail() %></td>
                                                    <td><%= admin.getPhone() != null ? admin.getPhone() : "N/A" %></td>
                                                    <td><%= admin.getDni() != null ? admin.getDni() : "N/A" %></td>
                                                    <td>
                                                        <% if (admin.getRegistrationDate() != null) { %>
                                                            <%= dateFormat.format(admin.getRegistrationDate()) %>
                                                        <% } else { %>
                                                            N/A
                                                        <% } %>
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-<%= admin.isActive() ? "success" : "secondary" %>">
                                                            <%= admin.isActive() ? "Activo" : "Inactivo" %>
                                                        </span>
                                                    </td>
                                                    <td class="action-buttons">
                                                        <% if (session.getAttribute("username").equals(admin.getUsername())) { %>
                                                            <span class="text-muted">Tu usuario</span>
                                                        <% } else { %>
                                                            <button class="btn btn-sm btn-warning" 
                                                                    onclick="editUser(
                                                                        <%= admin.getId() %>, 
                                                                        '<%= admin.getUsername() %>',
                                                                        '<%= admin.getFirstName() != null ? admin.getFirstName().replace("'", "\\'") : "" %>',
                                                                        '<%= admin.getLastName() != null ? admin.getLastName().replace("'", "\\'") : "" %>',
                                                                        '<%= admin.getEmail() != null ? admin.getEmail().replace("'", "\\'") : "" %>',
                                                                        '<%= admin.getPhone() != null ? admin.getPhone().replace("'", "\\'") : "" %>',
                                                                        '<%= admin.getDni() != null ? admin.getDni().replace("'", "\\'") : "" %>',
                                                                        '<%= admin.getBirthDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(admin.getBirthDate()) : "" %>',
                                                                        '<%= admin.getRegion() != null ? admin.getRegion().replace("'", "\\'") : "" %>',
                                                                        '<%= admin.getDistrict() != null ? admin.getDistrict().replace("'", "\\'") : "" %>',
                                                                        '<%= admin.getAddress() != null ? admin.getAddress().replace("'", "\\'") : "" %>',
                                                                        'admin',
                                                                        <%= admin.isActive() %>,
                                                                        <%= admin.isNotificationsEnabled() %>
                                                                    )">
                                                                <i class="fas fa-edit"></i>
                                                            </button>
                                                        <% } %>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                } else {
                                                %>
                                                <tr>
                                                    <td colspan="8" class="text-center text-muted py-4">
                                                        <i class="fas fa-user-shield fa-2x mb-3"></i><br>
                                                        No hay administradores registrados
                                                    </td>
                                                </tr>
                                                <%
                                                }
                                                %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Pestaña de empleados -->
                        <div class="tab-pane fade" id="employees">
                            <div class="card">
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Usuario</th>
                                                    <th>Nombre</th>
                                                    <th>Email</th>
                                                    <th>Teléfono</th>
                                                    <th>DNI</th>
                                                    <th>Región</th>
                                                    <th>Estado</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                List<User> employeesList = new ArrayList<>();
                                                if (allUsersList != null) {
                                                    for (User user : allUsersList) {
                                                        if ("empleado".equals(user.getUserType())) {
                                                            employeesList.add(user);
                                                        }
                                                    }
                                                }
                                                
                                                if (!employeesList.isEmpty()) {
                                                    for (User employee : employeesList) {
                                                %>
                                                <tr>
                                                    <td><strong><%= employee.getUsername() %></strong></td>
                                                    <td><%= employee.getFirstName() %> <%= employee.getLastName() %></td>
                                                    <td><%= employee.getEmail() %></td>
                                                    <td><%= employee.getPhone() != null ? employee.getPhone() : "N/A" %></td>
                                                    <td><%= employee.getDni() != null ? employee.getDni() : "N/A" %></td>
                                                    <td><%= employee.getRegion() != null ? employee.getRegion() : "N/A" %></td>
                                                    <td>
                                                        <span class="badge bg-<%= employee.isActive() ? "success" : "secondary" %>">
                                                            <%= employee.isActive() ? "Activo" : "Inactivo" %>
                                                        </span>
                                                    </td>
                                                    <td class="action-buttons">
                                                        <button class="btn btn-sm btn-warning" 
                                                                onclick="editUser(
                                                                    <%= employee.getId() %>, 
                                                                    '<%= employee.getUsername() %>',
                                                                    '<%= employee.getFirstName() != null ? employee.getFirstName().replace("'", "\\'") : "" %>',
                                                                    '<%= employee.getLastName() != null ? employee.getLastName().replace("'", "\\'") : "" %>',
                                                                    '<%= employee.getEmail() != null ? employee.getEmail().replace("'", "\\'") : "" %>',
                                                                    '<%= employee.getPhone() != null ? employee.getPhone().replace("'", "\\'") : "" %>',
                                                                    '<%= employee.getDni() != null ? employee.getDni().replace("'", "\\'") : "" %>',
                                                                    '<%= employee.getBirthDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(employee.getBirthDate()) : "" %>',
                                                                    '<%= employee.getRegion() != null ? employee.getRegion().replace("'", "\\'") : "" %>',
                                                                    '<%= employee.getDistrict() != null ? employee.getDistrict().replace("'", "\\'") : "" %>',
                                                                    '<%= employee.getAddress() != null ? employee.getAddress().replace("'", "\\'") : "" %>',
                                                                    'empleado',
                                                                    <%= employee.isActive() %>,
                                                                    <%= employee.isNotificationsEnabled() %>
                                                                )">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-info" 
                                                                onclick="toggleUserStatus(<%= employee.getId() %>, <%= !employee.isActive() %>)">
                                                            <i class="fas fa-<%= employee.isActive() ? "pause" : "play" %>"></i>
                                                        </button>
                                                        <% if (!session.getAttribute("username").equals(employee.getUsername())) { %>
                                                            <button class="btn btn-sm btn-danger" 
                                                                    onclick="confirmDelete(<%= employee.getId() %>, '<%= employee.getUsername().replace("'", "\\'") %>')">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        <% } %>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                } else {
                                                %>
                                                <tr>
                                                    <td colspan="8" class="text-center text-muted py-4">
                                                        <i class="fas fa-user-tie fa-2x mb-3"></i><br>
                                                        No hay empleados registrados
                                                    </td>
                                                </tr>
                                                <%
                                                }
                                                %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal para Crear/Editar Usuario -->
    <div class="modal fade" id="userModal" tabindex="-1" aria-labelledby="userModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="userModalLabel">Nuevo Usuario</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="userForm" action="<%= request.getContextPath() %>/userManagement" method="post">
                    <input type="hidden" name="action" id="formAction" value="create">
                    <input type="hidden" name="userId" id="userId">
                    
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="username" class="form-label">Usuario *</label>
                                    <input type="text" class="form-control" id="username" name="username" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="password" class="form-label">Contraseña *</label>
                                    <input type="password" class="form-control" id="password" name="password" required>
                                    <small class="form-text text-muted">Mínimo 6 caracteres</small>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="firstName" class="form-label">Nombres *</label>
                                    <input type="text" class="form-control" id="firstName" name="firstName" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="lastName" class="form-label">Apellidos *</label>
                                    <input type="text" class="form-control" id="lastName" name="lastName" required>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email *</label>
                                    <input type="email" class="form-control" id="email" name="email" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="phone" class="form-label">Teléfono</label>
                                    <input type="tel" class="form-control" id="phone" name="phone">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="dni" class="form-label">DNI</label>
                                    <input type="text" class="form-control" id="dni" name="dni" maxlength="8">
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="birthDate" class="form-label">Fecha de Nacimiento</label>
                                    <input type="date" class="form-control" id="birthDate" name="birthDate">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="userType" class="form-label">Tipo de Usuario *</label>
                                    <select class="form-select" id="userType" name="userType" required>
                                        <option value="">Seleccionar tipo</option>
                                        <option value="admin">Administrador</option>
                                        <option value="empleado">Empleado</option>
                                        <option value="donador">Donador</option>
                                        <option value="receptor">Receptor</option>
                                        <option value="usuario">Usuario Regular</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="region" class="form-label">Región</label>
                                    <select class="form-select" id="region" name="region">
                                        <option value="">Seleccionar región</option>
                                        <option value="Lima">Lima</option>
                                        <option value="Arequipa">Arequipa</option>
                                        <option value="Cusco">Cusco</option>
                                        <option value="Chiclayo">Chiclayo</option>
                                        <option value="Tacna">Tacna</option>
                                        <option value="Ayacucho">Ayacucho</option>
                                        <option value="Piura">Piura</option>
                                        <option value="Trujillo">Trujillo</option>
                                        <option value="Puno">Puno</option>
                                        <option value="Huancayo">Huancayo</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="district" class="form-label">Distrito</label>
                                    <input type="text" class="form-control" id="district" name="district">
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="address" class="form-label">Dirección</label>
                                    <input type="text" class="form-control" id="address" name="address">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3 form-check">
                                    <input type="checkbox" class="form-check-input" id="active" name="active" value="true" checked>
                                    <label class="form-check-label" for="active">Usuario Activo</label>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3 form-check">
                                    <input type="checkbox" class="form-check-input" id="notifications" name="notifications" value="true" checked>
                                    <label class="form-check-label" for="notifications">Notificaciones Activadas</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar Usuario</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal de Confirmación de Eliminación -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Confirmar Eliminación</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    ¿Está seguro de que desea eliminar al usuario <strong id="deleteUsername"></strong>?
                    <p class="text-danger mt-2"><small>Esta acción no se puede deshacer.</small></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <form id="deleteForm" action="<%= request.getContextPath() %>/userManagement" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="userId" id="deleteUserId">
                        <button type="submit" class="btn btn-danger">Eliminar</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal para Cambiar Estado -->
    <div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="statusModalLabel">Cambiar Estado</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    ¿Está seguro de <strong id="statusActionText"></strong> al usuario?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <form id="statusForm" action="<%= request.getContextPath() %>/userManagement" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="toggleStatus">
                        <input type="hidden" name="userId" id="statusUserId">
                        <input type="hidden" name="activate" id="statusActivate">
                        <button type="submit" class="btn btn-primary">Confirmar</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function resetForm() {
            document.getElementById('userForm').reset();
            document.getElementById('userModalLabel').textContent = 'Nuevo Usuario';
            document.getElementById('formAction').value = 'create';
            document.getElementById('userId').value = '';
            document.getElementById('password').required = true;
            document.getElementById('password').value = '';
            document.getElementById('active').checked = true;
            document.getElementById('notifications').checked = true;
        }

        function editUser(userId, username, firstName, lastName, email, phone, dni, birthDate, region, district, address, userType, active, notifications) {
            document.getElementById('userId').value = userId;
            document.getElementById('username').value = username;
            document.getElementById('password').value = '';
            document.getElementById('password').required = false;
            document.getElementById('firstName').value = firstName;
            document.getElementById('lastName').value = lastName;
            document.getElementById('email').value = email;
            document.getElementById('phone').value = phone;
            document.getElementById('dni').value = dni;
            document.getElementById('birthDate').value = birthDate;
            document.getElementById('region').value = region;
            document.getElementById('district').value = district;
            document.getElementById('address').value = address;
            document.getElementById('userType').value = userType;
            document.getElementById('active').checked = active;
            document.getElementById('notifications').checked = notifications;

            document.getElementById('userModalLabel').textContent = 'Editar Usuario: ' + username;
            document.getElementById('formAction').value = 'update';

            const modal = new bootstrap.Modal(document.getElementById('userModal'));
            modal.show();
        }

        function toggleUserStatus(userId, activate) {
            document.getElementById('statusUserId').value = userId;
            document.getElementById('statusActivate').value = activate;
            document.getElementById('statusActionText').textContent = activate ? 'activar' : 'desactivar';
            
            const modal = new bootstrap.Modal(document.getElementById('statusModal'));
            modal.show();
        }

        function confirmDelete(userId, username) {
            document.getElementById('deleteUserId').value = userId;
            document.getElementById('deleteUsername').textContent = username;

            const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
            modal.show();
        }

        // Validación del formulario
        document.getElementById('userForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const formAction = document.getElementById('formAction').value;
            
            // Si es creación o si se cambió la contraseña en edición
            if ((formAction === 'create' || password.trim() !== '') && password.length < 6) {
                e.preventDefault();
                alert('La contraseña debe tener al menos 6 caracteres.');
                return false;
            }
            
            return true;
        });

        // Activar pestañas
        document.addEventListener('DOMContentLoaded', function() {
            var tabEl = document.querySelectorAll('a[data-bs-toggle="tab"]');
            tabEl.forEach(function(tab) {
                tab.addEventListener('shown.bs.tab', function (event) {
                    event.target.classList.add('active');
                });
            });
        });
    </script>
</body>
</html>