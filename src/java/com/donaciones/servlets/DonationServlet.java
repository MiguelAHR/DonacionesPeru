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
            String pickupAddress = request.getParameter("pickupAddress");

            // Validate required fields
            if (donationType == null || description == null || quantityStr == null
                    || condition == null || location == null) {
                response.sendRedirect(request.getContextPath() + "/donations?action=new&error=missing");
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            // Create donation
            Donation donation = new Donation();
            donation.setDonorUsername(username);
            donation.setType(donationType);
            donation.setDescription(description);
            donation.setQuantity(quantity);
            donation.setCondition(condition);
            donation.setLocation(location);
            
            if (pickupAddress != null && !pickupAddress.trim().isEmpty()) {
                donation.setPickupAddress(pickupAddress);
            }
            
            donation.setStatus("pending");

            DataManager.getInstance().addDonation(donation);

            String userType = (String) request.getSession().getAttribute("userType");
            if ("usuario".equals(userType)) {
                response.sendRedirect(request.getContextPath() + "/donations?action=myDonations&success=created");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?success=donation_created");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/donations?action=new&error=invalid_quantity");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/donations?action=new&error=general");
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
                response.sendRedirect(request.getContextPath() + "/dashboard?error=request_missing_fields");
                return;
            }

            // CORREGIDO: Usar setRequestedBy en lugar de setRequesterUsername
            Request newRequest = new Request();
            newRequest.setRequestedBy(username); // Usando el método correcto
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
            if ("usuario".equals(userType)) {
                response.sendRedirect(request.getContextPath() + "/donations?action=myRequests&success=created");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?success=request_created");
            }

        } catch (Exception e) {
            getServletContext().log("Error creando Request en DonationServlet", e);
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=request_failed");
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
}