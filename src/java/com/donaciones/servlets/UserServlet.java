package com.donaciones.servlets;

import com.donaciones.models.User;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            case "new":
                showNewUserForm(request, response);
                break;
            case "edit":
                showEditUserForm(request, response);
                break;
            case "view":
                showUserDetails(request, response);
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
            case "create":
                createUser(request, response);
                break;
            case "update":
                updateUser(request, response);
                break;
            case "delete":
                deleteUser(request, response);
                break;
            case "changestatus":
                changeUserStatus(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/users?action=list");
        }
    }
    
    private void showNewUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check permissions
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/views/admin/user_form.jsp").forward(request, response);
    }
    
    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check permissions
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=missing_id");
            return;
        }
        
        try {
            int userId = Integer.parseInt(idParam);
            User user = DataManager.getInstance().getUser(userId);
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=user_not_found");
                return;
            }
            
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/admin/user_form.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=invalid_id");
        }
    }
    
    private void showUserDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=missing_id");
            return;
        }
        
        try {
            int userId = Integer.parseInt(idParam);
            User user = DataManager.getInstance().getUser(userId);
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=user_not_found");
                return;
            }
            
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/admin/user_details.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=invalid_id");
        }
    }
    
    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get form parameters
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String userType = request.getParameter("userType");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String dni = request.getParameter("dni");
            String birthDateStr = request.getParameter("birthDate");
            String region = request.getParameter("region");
            String district = request.getParameter("district");
            String address = request.getParameter("address");
            String notifications = request.getParameter("notifications");
            String active = request.getParameter("active");
            
            // Validate required fields
            if (username == null || password == null || userType == null || 
                email == null || dni == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=new&error=missing_fields");
                return;
            }
            
            // Validate password match
            if (!password.equals(confirmPassword)) {
                response.sendRedirect(request.getContextPath() + "/users?action=new&error=password_mismatch");
                return;
            }
            
            // Validate DNI format
            if (!dni.matches("\\d{8}")) {
                response.sendRedirect(request.getContextPath() + "/users?action=new&error=invalid_dni");
                return;
            }
            
            // Check if username already exists
            if (DataManager.getInstance().getUser(username) != null) {
                response.sendRedirect(request.getContextPath() + "/users?action=new&error=username_exists");
                return;
            }
            
            // Create user
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setUserType(userType);
            user.setFirstName(firstName != null ? firstName : "");
            user.setLastName(lastName != null ? lastName : "");
            user.setEmail(email);
            user.setPhone(phone != null ? phone : "");
            user.setDni(dni);
            
            // Parse birth date if provided
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date birthDate = sdf.parse(birthDateStr);
                    user.setBirthDate(birthDate);
                } catch (ParseException e) {
                    // If date is invalid, set to null
                    user.setBirthDate(null);
                }
            }
            
            user.setRegion(region != null ? region : "");
            user.setDistrict(district != null ? district : "");
            user.setAddress(address != null ? address : "");
            
            // Handle boolean parameters
            boolean notificationsEnabled = notifications != null && "on".equals(notifications);
            user.setNotificationsEnabled(notificationsEnabled);
            
            boolean isActive = active != null && "on".equals(active);
            user.setActive(isActive);
            
            // Save user
            boolean success = DataManager.getInstance().addUser(user);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&success=created");
            } else {
                response.sendRedirect(request.getContextPath() + "/users?action=new&error=create_failed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/users?action=new&error=server_error");
        }
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=missing_id");
                return;
            }
            
            int userId = Integer.parseInt(idParam);
            User existingUser = DataManager.getInstance().getUser(userId);
            
            if (existingUser == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=user_not_found");
                return;
            }
            
            // Get form parameters
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String userType = request.getParameter("userType");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String dni = request.getParameter("dni");
            String birthDateStr = request.getParameter("birthDate");
            String region = request.getParameter("region");
            String district = request.getParameter("district");
            String address = request.getParameter("address");
            String notifications = request.getParameter("notifications");
            String active = request.getParameter("active");
            
            // Validate required fields
            if (username == null || userType == null || email == null || dni == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=edit&id=" + userId + "&error=missing_fields");
                return;
            }
            
            // Update user
            existingUser.setUsername(username);
            
            // Only update password if provided
            if (password != null && !password.trim().isEmpty()) {
                existingUser.setPassword(password);
            }
            
            existingUser.setUserType(userType);
            existingUser.setFirstName(firstName != null ? firstName : "");
            existingUser.setLastName(lastName != null ? lastName : "");
            existingUser.setEmail(email);
            existingUser.setPhone(phone != null ? phone : "");
            existingUser.setDni(dni);
            
            // Parse birth date if provided
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date birthDate = sdf.parse(birthDateStr);
                    existingUser.setBirthDate(birthDate);
                } catch (ParseException e) {
                    // If date is invalid, set to null
                    existingUser.setBirthDate(null);
                }
            }
            
            existingUser.setRegion(region != null ? region : "");
            existingUser.setDistrict(district != null ? district : "");
            existingUser.setAddress(address != null ? address : "");
            
            // Handle boolean parameters
            boolean notificationsEnabled = notifications != null && "on".equals(notifications);
            existingUser.setNotificationsEnabled(notificationsEnabled);
            
            boolean isActive = active != null && "on".equals(active);
            existingUser.setActive(isActive);
            
            // Save user
            boolean success = DataManager.getInstance().updateUser(existingUser);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/users?action=edit&id=" + userId + "&error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=server_error");
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=missing_id");
                return;
            }
            
            int userId = Integer.parseInt(idParam);
            
            // Check if trying to delete own account
            HttpSession session = request.getSession();
            User currentUser = DataManager.getInstance().getUser((String) session.getAttribute("username"));
            
            if (currentUser != null && currentUser.getId() == userId) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=cannot_delete_self");
                return;
            }
            
            boolean success = DataManager.getInstance().deleteUser(userId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=server_error");
        }
    }
    
    private void changeUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            String statusParam = request.getParameter("status");
            
            if (idParam == null || statusParam == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=missing_params");
                return;
            }
            
            int userId = Integer.parseInt(idParam);
            boolean active = "activate".equals(statusParam);
            
            User user = DataManager.getInstance().getUser(userId);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=user_not_found");
                return;
            }
            
            // Check if trying to deactivate own account
            HttpSession session = request.getSession();
            User currentUser = DataManager.getInstance().getUser((String) session.getAttribute("username"));
            
            if (currentUser != null && currentUser.getId() == userId && !active) {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=cannot_deactivate_self");
                return;
            }
            
            user.setActive(active);
            boolean success = DataManager.getInstance().updateUser(user);
            
            if (success) {
                String successMsg = active ? "activated" : "deactivated";
                response.sendRedirect(request.getContextPath() + "/users?action=list&success=" + successMsg);
            } else {
                response.sendRedirect(request.getContextPath() + "/users?action=list&error=status_change_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/users?action=list&error=server_error");
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
        
        try {
            // Get users with type 'donador'
            java.util.List<User> donors = DataManager.getInstance().getAllUsers().stream()
                    .filter(user -> "donador".equals(user.getUserType()))
                    .collect(java.util.stream.Collectors.toList());
            
            request.setAttribute("users", donors);
            request.setAttribute("userTypeFilter", "donador");
            request.setAttribute("pageTitle", "Donadores");
            request.getRequestDispatcher("/WEB-INF/views/admin/users_list.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=loading_donors");
        }
    }
    
    private void showReceivers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check permissions
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        try {
            // Get users with type 'receptor'
            java.util.List<User> receivers = DataManager.getInstance().getAllUsers().stream()
                    .filter(user -> "receptor".equals(user.getUserType()))
                    .collect(java.util.stream.Collectors.toList());
            
            request.setAttribute("users", receivers);
            request.setAttribute("userTypeFilter", "receptor");
            request.setAttribute("pageTitle", "Receptores");
            request.getRequestDispatcher("/WEB-INF/views/admin/users_list.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=loading_receivers");
        }
    }
    
    private void showAllUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check permissions - only admin can see all users
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        try {
            // Get all users from DataManager
            java.util.List<User> users = DataManager.getInstance().getAllUsers();
            request.setAttribute("users", users);
            request.setAttribute("pageTitle", "Todos los Usuarios");
            request.getRequestDispatcher("/WEB-INF/views/admin/users_list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=loading_users");
        }
    }
    
    // Helper method to get user by ID
    private User getUser(int userId) {
        return DataManager.getInstance().getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElse(null);
    }
}