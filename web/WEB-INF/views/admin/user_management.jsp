<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.donaciones.models.*"%>
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

                    <!-- Mensajes de éxito/error -->
                    <% if (request.getParameter("success") != null) { %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            Operación realizada exitosamente.
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <% } %>
                    <% if (request.getParameter("error") != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            Error al realizar la operación: <%= request.getParameter("error") %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
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
                                                    <th>Estado</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                List<User> allUsers = (List<User>) request.getAttribute("allUsers");
                                                if (allUsers != null && !allUsers.isEmpty()) {
                                                    for (User user : allUsers) {
                                                %>
                                                <tr>
                                                    <td><%= user.getId() %></td>
                                                    <td><strong><%= user.getUsername() %></strong></td>
                                                    <td><%= user.getFirstName() %> <%= user.getLastName() %></td>
                                                    <td><%= user.getEmail() %></td>
                                                    <td>
                                                        <span class="badge bg-<%= 
                                                            user.getUserType().equals("admin") ? "danger" : 
                                                            user.getUserType().equals("empleado") ? "warning" : 
                                                            user.getUserType().equals("donador") ? "info" :
                                                            user.getUserType().equals("receptor") ? "success" : "secondary" %>">
                                                            <%= user.getFormattedUserType() %>
                                                        </span>
                                                    </td>
                                                    <td><%= user.getRegion() != null ? user.getRegion() : "N/A" %></td>
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
                                                                    '<%= user.getFirstName() %>',
                                                                    '<%= user.getLastName() %>',
                                                                    '<%= user.getEmail() %>',
                                                                    '<%= user.getPhone() != null ? user.getPhone() : "" %>',
                                                                    '<%= user.getDni() != null ? user.getDni() : "" %>',
                                                                    '<%= user.getBirthDate() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(user.getBirthDate()) : "" %>',
                                                                    '<%= user.getRegion() != null ? user.getRegion() : "" %>',
                                                                    '<%= user.getDistrict() != null ? user.getDistrict() : "" %>',
                                                                    '<%= user.getAddress() != null ? user.getAddress().replace("'", "\\'") : "" %>',
                                                                    '<%= user.getUserType() %>',
                                                                    <%= user.isActive() %>,
                                                                    <%= user.isNotificationsEnabled() %>
                                                                )">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger" 
                                                                onclick="confirmDelete(<%= user.getId() %>, '<%= user.getUsername() %>')">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                } else {
                                                %>
                                                <tr>
                                                    <td colspan="8" class="text-center text-muted py-4">
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
                                                    <th>Total Donaciones</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                List<Donor> allDonors = (List<Donor>) request.getAttribute("allDonors");
                                                if (allDonors != null && !allDonors.isEmpty()) {
                                                    for (Donor donor : allDonors) {
                                                %>
                                                <tr>
                                                    <td><strong><%= donor.getUsername() %></strong></td>
                                                    <td><%= donor.getFirstName() %> <%= donor.getLastName() %></td>
                                                    <td><%= donor.getEmail() %></td>
                                                    <td><%= donor.getPhone() != null ? donor.getPhone() : "N/A" %></td>
                                                    <td><%= donor.getDni() != null ? donor.getDni() : "N/A" %></td>
                                                    <td><span class="badge bg-primary"><%= donor.getTotalDonations() %></span></td>
                                                    <td class="action-buttons">
                                                        <button class="btn btn-sm btn-warning" 
                                                                onclick="editUser(
                                                                    <%= donor.getId() %>, 
                                                                    '<%= donor.getUsername() %>',
                                                                    '<%= donor.getFirstName() %>',
                                                                    '<%= donor.getLastName() %>',
                                                                    '<%= donor.getEmail() %>',
                                                                    '<%= donor.getPhone() != null ? donor.getPhone() : "" %>',
                                                                    '<%= donor.getDni() != null ? donor.getDni() : "" %>',
                                                                    '<%= donor.getBirthDate() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(donor.getBirthDate()) : "" %>',
                                                                    '<%= donor.getRegion() != null ? donor.getRegion() : "" %>',
                                                                    '<%= donor.getDistrict() != null ? donor.getDistrict() : "" %>',
                                                                    '<%= donor.getAddress() != null ? donor.getAddress().replace("'", "\\'") : "" %>',
                                                                    'donador',
                                                                    <%= donor.isActive() %>,
                                                                    <%= donor.isNotificationsEnabled() %>
                                                                )">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger" 
                                                                onclick="confirmDelete(<%= donor.getId() %>, '<%= donor.getUsername() %>')">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                } else {
                                                %>
                                                <tr>
                                                    <td colspan="7" class="text-center text-muted py-4">
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
                                                    <th>Familia</th>
                                                    <th>Niños</th>
                                                    <th>Situación Económica</th>
                                                    <th>Estado</th>
                                                    <th>Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                List<Receiver> allReceivers = (List<Receiver>) request.getAttribute("allReceivers");
                                                if (allReceivers != null && !allReceivers.isEmpty()) {
                                                    for (Receiver receiver : allReceivers) {
                                                %>
                                                <tr>
                                                    <td><strong><%= receiver.getUsername() %></strong></td>
                                                    <td><%= receiver.getFirstName() %> <%= receiver.getLastName() %></td>
                                                    <td><%= receiver.getEmail() %></td>
                                                    <td><span class="badge bg-info"><%= receiver.getFamilySize() %></span></td>
                                                    <td><span class="badge bg-warning"><%= receiver.getChildren() %></span></td>
                                                    <td><%= receiver.getFormattedEconomicSituation() %></td>
                                                    <td>
                                                        <span class="badge bg-<%= 
                                                            receiver.isVerified() ? "success" : 
                                                            "pending".equals(receiver.getVerificationStatus()) ? "warning" : "danger" %>">
                                                            <%= receiver.getFormattedVerificationStatus() %>
                                                        </span>
                                                    </td>
                                                    <td class="action-buttons">
                                                        <button class="btn btn-sm btn-warning" 
                                                                onclick="editUser(
                                                                    <%= receiver.getId() %>, 
                                                                    '<%= receiver.getUsername() %>',
                                                                    '<%= receiver.getFirstName() %>',
                                                                    '<%= receiver.getLastName() %>',
                                                                    '<%= receiver.getEmail() %>',
                                                                    '<%= receiver.getPhone() != null ? receiver.getPhone() : "" %>',
                                                                    '<%= receiver.getDni() != null ? receiver.getDni() : "" %>',
                                                                    '<%= receiver.getBirthDate() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(receiver.getBirthDate()) : "" %>',
                                                                    '<%= receiver.getRegion() != null ? receiver.getRegion() : "" %>',
                                                                    '<%= receiver.getDistrict() != null ? receiver.getDistrict() : "" %>',
                                                                    '<%= receiver.getAddress() != null ? receiver.getAddress().replace("'", "\\'") : "" %>',
                                                                    'receptor',
                                                                    <%= receiver.isActive() %>,
                                                                    <%= receiver.isNotificationsEnabled() %>
                                                                )">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger" 
                                                                onclick="confirmDelete(<%= receiver.getId() %>, '<%= receiver.getUsername() %>')">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                } else {
                                                %>
                                                <tr>
                                                    <td colspan="8" class="text-center text-muted py-4">
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
                <form id="userForm" action="userManagement" method="post">
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
                                        <option value="usuario">Usuario Regular</option>
                                        <option value="donador">Donador</option>
                                        <option value="receptor">Receptor</option>
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
                    <form id="deleteForm" action="userManagement" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="userId" id="deleteUserId">
                        <button type="submit" class="btn btn-danger">Eliminar</button>
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

        function confirmDelete(userId, username) {
            document.getElementById('deleteUserId').value = userId;
            document.getElementById('deleteUsername').textContent = username;

            const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
            modal.show();
        }

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