package com.donaciones.servlets;

import com.donaciones.models.Donation;
import com.donaciones.models.Request;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DonationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userType = (String) session.getAttribute("userType");
        String username = (String) session.getAttribute("username");
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        if ("usuario".equals(userType)) {
            if (!"list".equals(action) && !"new".equals(action) && !"newRequest".equals(action)
                    && !"myDonations".equals(action) && !"myRequests".equals(action)) {
                response.sendRedirect(request.getContextPath() + "/donations?action=myDonations");
                return;
            }
        }

        switch (action) {
            case "new":
                request.getRequestDispatcher("/WEB-INF/views/general/donation_form.jsp").forward(request, response);
                break;
            case "newRequest":
                request.getRequestDispatcher("/WEB-INF/views/general/request_form.jsp").forward(request, response);
                break;
            case "myDonations":
                if ("usuario".equals(userType)) {
                    showUserDonations(request, response, username);
                } else {
                    response.sendRedirect(request.getContextPath() + "/donations?action=list");
                }
                break;
            case "myRequests":
                if ("usuario".equals(userType)) {
                    showUserRequests(request, response, username);
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }
                break;
            case "list":
                showDonationList(request, response, userType);
                break;
            case "reports":
                // Solo admin y empleado pueden ver reportes
                if ("admin".equals(userType) || "empleado".equals(userType)) {
                    showReports(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }
                break;
            default:
                showDonationList(request, response, userType);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        String action = request.getParameter("action");
        if (action == null) {
            action = "create";
        }

        switch (action) {
            case "create":
                createDonation(request, response, username);
                break;
            case "newRequest":
                createRequest(request, response, username);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/donations?action=list");
        }
    }

    private void showDonationList(HttpServletRequest request, HttpServletResponse response, String userType)
            throws ServletException, IOException {

        DataManager dm = DataManager.getInstance();
        String username = (String) request.getSession().getAttribute("username");

        List<Donation> donations;

        if ("usuario".equals(userType)) {
            donations = dm.getDonationsByUser(username);
            System.out.println("DEBUG: Donaciones del usuario " + username + ": " + donations.size());
        } else {
            donations = dm.getAllDonations();
            System.out.println("DEBUG: Todas las donaciones: " + donations.size());

            // Aplicar filtros si existen
            String typeFilter = request.getParameter("type");
            String locationFilter = request.getParameter("location");
            String conditionFilter = request.getParameter("condition");

            if (typeFilter != null && !typeFilter.isEmpty()) {
                donations = donations.stream()
                        .filter(d -> typeFilter.equals(d.getType()))
                        .collect(java.util.stream.Collectors.toList());
            }

            if (locationFilter != null && !locationFilter.isEmpty()) {
                donations = donations.stream()
                        .filter(d -> locationFilter.equals(d.getLocation()))
                        .collect(java.util.stream.Collectors.toList());
            }

            if (conditionFilter != null && !conditionFilter.isEmpty()) {
                donations = donations.stream()
                        .filter(d -> conditionFilter.equals(d.getCondition()))
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        if (donations == null) {
            donations = new java.util.ArrayList<>();
        }

        request.setAttribute("donations", donations);
        request.setAttribute("totalDonations", donations.size());
        request.setAttribute("activeDonations", donations.stream()
                .filter(d -> "active".equals(d.getStatus()))
                .count());
        request.setAttribute("userType", userType);

        if ("usuario".equals(userType)) {
            request.getRequestDispatcher("/WEB-INF/views/usuario/donation_list.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/general/donation_list.jsp").forward(request, response);
        }
    }

    private void showUserDonations(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {

        DataManager dm = DataManager.getInstance();
        List<Donation> userDonations = dm.getDonationsByUser(username);

        System.out.println("DEBUG: Donaciones del usuario " + username + ": " + userDonations.size());

        if (userDonations == null) {
            userDonations = new java.util.ArrayList<>();
        }

        request.setAttribute("donations", userDonations);
        request.setAttribute("totalDonations", userDonations.size());
        request.setAttribute("activeDonations", userDonations.stream()
                .filter(d -> "active".equals(d.getStatus()))
                .count());
        request.setAttribute("userType", "usuario");

        request.getRequestDispatcher("/WEB-INF/views/usuario/donation_list.jsp").forward(request, response);
    }

    private void showUserRequests(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {

        DataManager dm = DataManager.getInstance();
        List<Request> userRequests = dm.getRequestsByUser(username);

        System.out.println("DEBUG: Solicitudes del usuario " + username + ": " + userRequests.size());

        if (userRequests == null) {
            userRequests = new java.util.ArrayList<>();
        }

        request.setAttribute("userRequests", userRequests);
        request.setAttribute("totalUserRequests", userRequests.size());
        request.setAttribute("pendingRequests", userRequests.stream()
                .filter(r -> "pending".equals(r.getStatus()))
                .count());
        request.setAttribute("inProgressRequests", userRequests.stream()
                .filter(r -> "in_progress".equals(r.getStatus()))
                .count());
        request.setAttribute("completedRequests", userRequests.stream()
                .filter(r -> "completed".equals(r.getStatus()))
                .count());

        request.getRequestDispatcher("/WEB-INF/views/usuario/request_list.jsp").forward(request, response);
    }

    private void createDonation(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {

        try {
            String donationType = request.getParameter("donationType");
            String description = request.getParameter("description");
            String quantityStr = request.getParameter("quantity");
            String condition = request.getParameter("condition");
            String location = request.getParameter("location");
            String address = request.getParameter("address");

            // Validar campos requeridos
            if (donationType == null || donationType.trim().isEmpty()
                    || description == null || description.trim().isEmpty()
                    || quantityStr == null || quantityStr.trim().isEmpty()
                    || condition == null || condition.trim().isEmpty()
                    || location == null || location.trim().isEmpty()) {

                System.out.println("ERROR: Campos faltantes en la donación");
                // Redirigir de vuelta al formulario con error
                request.setAttribute("errorMessage", "Por favor, completa todos los campos requeridos");
                request.getRequestDispatcher("/WEB-INF/views/general/donation_form.jsp").forward(request, response);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Cantidad inválida: " + quantityStr);
                request.setAttribute("errorMessage", "La cantidad debe ser un número válido mayor a 0");
                request.getRequestDispatcher("/WEB-INF/views/general/donation_form.jsp").forward(request, response);
                return;
            }

            // Crear y guardar donación
            Donation donation = new Donation();
            donation.setDonorUsername(username);
            donation.setType(donationType);
            donation.setDescription(description);
            donation.setQuantity(quantity);
            donation.setCondition(condition);
            donation.setLocation(location);
            donation.setStatus("pending");

            if (address != null && !address.trim().isEmpty()) {
                donation.setAddress(address);
            }

            DataManager dm = DataManager.getInstance();
            boolean success = dm.addDonation(donation);

            if (success) {
                System.out.println("ÉXITO: Donación guardada para usuario: " + username);

                // Determinar dónde redirigir según el tipo de usuario
                String userType = (String) request.getSession().getAttribute("userType");
                String redirectUrl;

                if ("usuario".equals(userType)) {
                    redirectUrl = request.getContextPath() + "/profile?success=donation_created&tab=donations";
                } else {
                    redirectUrl = request.getContextPath() + "/donations?action=list&success=donation_created";
                }

                response.sendRedirect(redirectUrl);
            } else {
                System.out.println("ERROR: No se pudo guardar la donación en la BD");
                request.setAttribute("errorMessage", "Error al guardar la donación en la base de datos");
                request.getRequestDispatcher("/WEB-INF/views/general/donation_form.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.out.println("ERROR EXCEPCIÓN en createDonation: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error interno del servidor: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/general/donation_form.jsp").forward(request, response);
        }
    }

    private void createRequest(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {

        try {
            String requestType = request.getParameter("requestType");
            String requestDescription = request.getParameter("requestDescription");
            String requestLocation = request.getParameter("requestLocation");
            String priorityStr = request.getParameter("priority");

            // Validar campos
            if (requestType == null || requestDescription == null || requestLocation == null
                    || requestType.trim().isEmpty() || requestDescription.trim().isEmpty() || requestLocation.trim().isEmpty()) {
                // CORREGIDO: Redirigir según tipo de usuario
                String userType = (String) request.getSession().getAttribute("userType");
                if ("usuario".equals(userType)) {
                    response.sendRedirect(request.getContextPath() + "/profile?error=request_missing_fields&tab=requests");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard?error=request_missing_fields");
                }
                return;
            }

            Request newRequest = new Request();
            newRequest.setRequestedBy(username);
            newRequest.setType(requestType);
            newRequest.setDescription(requestDescription);
            newRequest.setLocation(requestLocation);
            newRequest.setStatus("pending");

            // Establecer prioridad si se proporciona
            if (priorityStr != null && !priorityStr.isEmpty()) {
                try {
                    newRequest.setPriority(Integer.parseInt(priorityStr));
                } catch (NumberFormatException e) {
                    newRequest.setPriority(3);
                }
            }

            DataManager.getInstance().addRequest(newRequest);

            String userType = (String) request.getSession().getAttribute("userType");

            // CORREGIDO: Redirigir según tipo de usuario
            if ("usuario".equals(userType)) {
                response.sendRedirect(request.getContextPath() + "/profile?success=request_created&tab=requests");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?success=request_created");
            }

        } catch (Exception e) {
            getServletContext().log("Error creando Request en DonationServlet", e);
            e.printStackTrace();
            String userType = (String) request.getSession().getAttribute("userType");
            if ("usuario".equals(userType)) {
                response.sendRedirect(request.getContextPath() + "/profile?error=request_failed&tab=requests");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?error=request_failed");
            }
        }
    }

    private void showReports(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        DataManager dm = DataManager.getInstance();

        request.setAttribute("totalDonations", dm.getTotalDonations());
        request.setAttribute("totalDonors", dm.getTotalDonors());
        request.setAttribute("totalReceivers", dm.getTotalReceivers());

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    // En DonationServlet - AGREGAR ESTOS MÉTODOS
    private String getSuccessMessage(String successType) {
        switch (successType) {
            case "donation_created":
                return "¡Donación creada exitosamente!";
            case "request_created":
                return "¡Solicitud creada exitosamente!";
            default:
                return "Operación completada exitosamente";
        }
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case "missing_fields":
                return "Por favor, completa todos los campos requeridos";
            case "invalid_quantity":
                return "La cantidad debe ser un número válido mayor a 0";
            case "save_failed":
                return "Error al guardar la información en la base de datos";
            case "general":
                return "Ha ocurrido un error inesperado. Intenta nuevamente";
            default:
                return "Ha ocurrido un error. Intenta nuevamente";
        }
    }
}
