package com.donaciones.servlets;

import com.donaciones.models.Donor;
import com.donaciones.models.Receiver;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        switch (action) {
            case "newDonor":
                request.getRequestDispatcher("/WEB-INF/views/general/donor_form.jsp").forward(request, response);
                break;
            case "newReceiver":
                request.getRequestDispatcher("/WEB-INF/views/general/receiver_form.jsp").forward(request, response);
                break;
            case "donors":
                showDonors(request, response);
                break;
            case "receivers":
                showReceivers(request, response);
                break;
            case "list":
                showAllUsers(request, response);
                break;
            default:
                showAllUsers(request, response);
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
        
        String action = request.getParameter("action");
        if (action == null) action = "create";
        
        switch (action) {
            case "createDonor":
                createDonor(request, response);
                break;
            case "createReceiver":
                createReceiver(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/users?action=list");
        }
    }
    
    private void createDonor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get form parameters
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String dni = request.getParameter("dni");
            String birthDateStr = request.getParameter("birthDate");
            String region = request.getParameter("region");
            String district = request.getParameter("district");
            String address = request.getParameter("address");
            String[] donationTypes = request.getParameterValues("donationTypes");
            String comments = request.getParameter("comments");
            String notifications = request.getParameter("notifications");
            
            // Validate required fields
            if (firstName == null || lastName == null || email == null || phone == null || 
                dni == null || birthDateStr == null || region == null || district == null || 
                address == null || donationTypes == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=newDonor&error=missing");
                return;
            }
            
            // Create donor
            Donor donor = new Donor();
            donor.setUsername(dni); 
            donor.setPassword("donor123"); // Default password
            donor.setFirstName(firstName);
            donor.setLastName(lastName);
            donor.setEmail(email);
            donor.setPhone(phone);
            donor.setDni(dni);
            donor.setRegion(region);
            donor.setDistrict(district);
            donor.setAddress(address);
            donor.setComments(comments);
            
            // Handle notifications parameter safely
            boolean notificationsEnabled = notifications != null && "on".equals(notifications);
            donor.setNotificationsEnabled(notificationsEnabled);
            
            // Parse birth date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDate = sdf.parse(birthDateStr);
            donor.setBirthDate(birthDate);
            
            // Set donation types
            donor.setDonationTypes(Arrays.asList(donationTypes));
            
            // Save donor
            DataManager.getInstance().addDonor(donor);
            
            response.sendRedirect(request.getContextPath() + "/users?action=newDonor&success=true");
            
        } catch (ParseException e) {
            response.sendRedirect(request.getContextPath() + "/users?action=newDonor&error=date");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/users?action=newDonor&error=general");
        }
    }
    
    private void createReceiver(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get form parameters
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String dni = request.getParameter("dni");
            String birthDateStr = request.getParameter("birthDate");
            String familySizeStr = request.getParameter("familySize");
            String childrenStr = request.getParameter("children");
            String economicSituation = request.getParameter("economicSituation");
            String region = request.getParameter("region");
            String district = request.getParameter("district");
            String address = request.getParameter("address");
            String needs = request.getParameter("needs");
            String needsDescription = request.getParameter("needsDescription");
            String dataConsent = request.getParameter("dataConsent");
            String notifications = request.getParameter("notifications");
            
            // Validate required fields
            if (firstName == null || lastName == null || email == null || phone == null || 
                dni == null || birthDateStr == null || familySizeStr == null || 
                economicSituation == null || region == null || district == null || 
                address == null || needs == null || needsDescription == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&error=missing");
                return;
            }
            
            // Create receiver
            Receiver receiver = new Receiver();
            receiver.setUsername(dni); // Using DNI as username for simplicity
            receiver.setPassword("receiver123"); // Default password
            receiver.setFirstName(firstName);
            receiver.setLastName(lastName);
            receiver.setEmail(email);
            receiver.setPhone(phone);
            receiver.setDni(dni);
            receiver.setRegion(region);
            receiver.setDistrict(district);
            receiver.setAddress(address);
            receiver.setEconomicSituation(economicSituation);
            receiver.setNeedsDescription(needsDescription);
            
            // Handle boolean parameters safely
            boolean dataConsentEnabled = dataConsent != null && "on".equals(dataConsent);
            receiver.setDataConsent(dataConsentEnabled);
            
            boolean notificationsEnabled = notifications != null && "on".equals(notifications);
            receiver.setNotificationsEnabled(notificationsEnabled);
            
            // Parse birth date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDate = sdf.parse(birthDateStr);
            receiver.setBirthDate(birthDate);
            
            // Parse family size
            if (!familySizeStr.equals("6+")) {
                receiver.setFamilySize(Integer.parseInt(familySizeStr));
            } else {
                receiver.setFamilySize(6);
            }
            
            // Parse children count
            if (childrenStr != null && !childrenStr.isEmpty()) {
                receiver.setChildren(Integer.parseInt(childrenStr));
            }
            
            // Set needs
            if (needs != null && !needs.isEmpty()) {
                receiver.setNeeds(Arrays.asList(needs.split(",")));
            }
            
            // Save receiver
            DataManager.getInstance().addReceiver(receiver);
            
            response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&success=true");
            
        } catch (ParseException e) {
            response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&error=date");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&error=number");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&error=general");
        }
    }
    
    private void showDonors(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check permissions
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // For now, redirect to new donor form
        response.sendRedirect(request.getContextPath() + "/users?action=newDonor");
    }
    
    private void showReceivers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check permissions
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // For now, redirect to new receiver form
        response.sendRedirect(request.getContextPath() + "/users?action=newReceiver");
    }
    
    private void showAllUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check permissions - only admin can see all users
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // For now, redirect to dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}