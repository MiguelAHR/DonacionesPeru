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
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="../admin/sidebar.jsp" />
            
            <div class="col-md-9 col-lg-10 main-content">
                <div class="p-4">
                    <h2 class="fw-bold mb-4">Gestión de Usuarios</h2>

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
                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <th>Usuario</th>
                                                <th>Nombre</th>
                                                <th>Email</th>
                                                <th>Tipo</th>
                                                <th>Región</th>
                                                <th>Estado</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                            List<User> allUsers = (List<User>) request.getAttribute("allUsers");
                                            if (allUsers != null) {
                                                for (User user : allUsers) {
                                            %>
                                            <tr>
                                                <td><%= user.getUsername() %></td>
                                                <td><%= user.getFirstName() %> <%= user.getLastName() %></td>
                                                <td><%= user.getEmail() %></td>
                                                <td>
                                                    <span class="badge bg-<%= 
                                                        user.getUserType().equals("admin") ? "danger" : 
                                                        user.getUserType().equals("empleado") ? "warning" : "info" %>">
                                                        <%= user.getUserType() %>
                                                    </span>
                                                </td>
                                                <td><%= user.getRegion() != null ? user.getRegion() : "N/A" %></td>
                                                <td>
                                                    <span class="badge bg-<%= user.isActive() ? "success" : "secondary" %>">
                                                        <%= user.isActive() ? "Activo" : "Inactivo" %>
                                                    </span>
                                                </td>
                                            </tr>
                                            <%
                                                }
                                            }
                                            %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <!-- Pestaña de donadores -->
                        <div class="tab-pane fade" id="donors">
                            <div class="card">
                                <div class="card-body">
                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <th>Usuario</th>
                                                <th>Nombre</th>
                                                <th>Email</th>
                                                <th>Tipos de Donación</th>
                                                <th>Total Donaciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                            List<Donor> allDonors = (List<Donor>) request.getAttribute("allDonors");
                                            if (allDonors != null) {
                                                for (Donor donor : allDonors) {
                                            %>
                                            <tr>
                                                <td><%= donor.getUsername() %></td>
                                                <td><%= donor.getFirstName() %> <%= donor.getLastName() %></td>
                                                <td><%= donor.getEmail() %></td>
                                                <td><%= donor.getDonationTypesAsString() %></td>
                                                <td><span class="badge bg-primary"><%= donor.getTotalDonations() %></span></td>
                                            </tr>
                                            <%
                                                }
                                            }
                                            %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <!-- Pestaña de receptores -->
                        <div class="tab-pane fade" id="receivers">
                            <div class="card">
                                <div class="card-body">
                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <th>Usuario</th>
                                                <th>Nombre</th>
                                                <th>Email</th>
                                                <th>Necesidades</th>
                                                <th>Estado</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                            List<Receiver> allReceivers = (List<Receiver>) request.getAttribute("allReceivers");
                                            if (allReceivers != null) {
                                                for (Receiver receiver : allReceivers) {
                                            %>
                                            <tr>
                                                <td><%= receiver.getUsername() %></td>
                                                <td><%= receiver.getFirstName() %> <%= receiver.getLastName() %></td>
                                                <td><%= receiver.getEmail() %></td>
                                                <td><%= receiver.getNeedsAsString() %></td>
                                                <td>
                                                    <span class="badge bg-<%= 
                                                        receiver.isVerified() ? "success" : 
                                                        "pending".equals(receiver.getVerificationStatus()) ? "warning" : "danger" %>">
                                                        <%= receiver.getFormattedVerificationStatus() %>
                                                    </span>
                                                </td>
                                            </tr>
                                            <%
                                                }
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
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