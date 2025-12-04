package com.donaciones.servlets;

import com.donaciones.dao.UserDAO;
import com.donaciones.models.User;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserManagementServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userType = (String) session.getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("list".equals(action)) {
            // Cargar todos los usuarios
            request.setAttribute("allUsers", userDAO.getAllUsers());
            request.getRequestDispatcher("/WEB-INF/views/admin/user_management.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
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
        
        String userType = (String) session.getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
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
                case "toggleStatus":
                    toggleUserStatus(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
        }
    }
    
    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        try {
            User user = extractUserFromRequest(request);
            
            // Validaciones básicas
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                session.setAttribute("errorMessage", "Username requerido");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                session.setAttribute("errorMessage", "Password requerido");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            if (user.getPassword().length() < 6) {
                session.setAttribute("errorMessage", "Password debe tener mínimo 6 caracteres");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            // Validar que el username no exista
            User existingUser = userDAO.getUserByUsername(user.getUsername());
            if (existingUser != null) {
                session.setAttribute("errorMessage", "Usuario ya existe");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            // Establecer fecha de registro
            user.setRegistrationDate(new Date());
            
            boolean success = userDAO.addUser(user);
            if (success) {
                session.setAttribute("successMessage", "Usuario creado exitosamente");
            } else {
                session.setAttribute("errorMessage", "Error al crear usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        try {
            String userIdStr = request.getParameter("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                session.setAttribute("errorMessage", "ID de usuario requerido");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            User existingUser = userDAO.getUserById(userId);
            if (existingUser == null) {
                session.setAttribute("errorMessage", "Usuario no encontrado");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            User user = extractUserFromRequest(request);
            user.setId(userId);
            
            // Para actualización, mantener datos existentes si no se proporcionan
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                user.setUsername(existingUser.getUsername());
            }
            
            // Para actualización, no cambiar la contraseña si está vacía
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                // Validar longitud de password si se está cambiando
                if (user.getPassword().length() < 6) {
                    session.setAttribute("errorMessage", "Password debe tener mínimo 6 caracteres");
                    response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                    return;
                }
            }
            
            // Mantener campos que no se actualizan
            if (user.getRegistrationDate() == null) {
                user.setRegistrationDate(existingUser.getRegistrationDate());
            }
            
            boolean success = userDAO.updateUser(user);
            if (success) {
                session.setAttribute("successMessage", "Usuario actualizado exitosamente");
            } else {
                session.setAttribute("errorMessage", "Error al actualizar usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        try {
            String userIdStr = request.getParameter("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                session.setAttribute("errorMessage", "ID de usuario requerido");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            
            // No permitir eliminar al usuario admin actual
            String currentUsername = (String) session.getAttribute("username");
            User userToDelete = userDAO.getUserById(userId);
            
            if (userToDelete != null && userToDelete.getUsername().equals(currentUsername)) {
                session.setAttribute("errorMessage", "No puede eliminar su propio usuario");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            boolean success = userDAO.deleteUser(userId);
            if (success) {
                session.setAttribute("successMessage", "Usuario eliminado exitosamente");
            } else {
                session.setAttribute("errorMessage", "Error al eliminar usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
    }
    
    private void toggleUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        try {
            String userIdStr = request.getParameter("userId");
            String activateStr = request.getParameter("activate");
            
            if (userIdStr == null || userIdStr.isEmpty()) {
                session.setAttribute("errorMessage", "ID de usuario requerido");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            boolean activate = "true".equals(activateStr);
            
            User user = userDAO.getUserById(userId);
            
            if (user == null) {
                session.setAttribute("errorMessage", "Usuario no encontrado");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            // No permitir desactivar al usuario admin actual
            String currentUsername = (String) session.getAttribute("username");
            
            if (user.getUsername().equals(currentUsername) && !activate) {
                session.setAttribute("errorMessage", "No puede desactivar su propio usuario");
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
                return;
            }
            
            // Cambiar estado activo
            user.setActive(activate);
            
            boolean success = userDAO.updateUser(user);
            if (success) {
                String message = activate ? "Usuario activado exitosamente" : "Usuario desactivado exitosamente";
                session.setAttribute("successMessage", message);
            } else {
                session.setAttribute("errorMessage", "Error al cambiar estado del usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
    }
    
    private User extractUserFromRequest(HttpServletRequest request) throws ParseException {
        User user = new User();
        
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        user.setUserType(request.getParameter("userType"));
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
        user.setDni(request.getParameter("dni"));
        user.setRegion(request.getParameter("region"));
        user.setDistrict(request.getParameter("district"));
        user.setAddress(request.getParameter("address"));
        
        // Parse birth date
        String birthDateStr = request.getParameter("birthDate");
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDate = sdf.parse(birthDateStr);
            user.setBirthDate(birthDate);
        }
        
        // Boolean fields
        String activeParam = request.getParameter("active");
        user.setActive("true".equals(activeParam) || "on".equals(activeParam));
        
        String notificationsParam = request.getParameter("notifications");
        user.setNotificationsEnabled("true".equals(notificationsParam) || "on".equals(notificationsParam));
        
        return user;
    }
}