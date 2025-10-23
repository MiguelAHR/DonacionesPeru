package com.donaciones.servlets;

import com.donaciones.dao.UserDAO;
import com.donaciones.dao.DonorDAO;
import com.donaciones.dao.ReceiverDAO;
import com.donaciones.models.User;
import com.donaciones.models.Donor;
import com.donaciones.models.Receiver;
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
    private DonorDAO donorDAO;
    private ReceiverDAO receiverDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        donorDAO = new DonorDAO();
        receiverDAO = new ReceiverDAO();
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
            // Cargar todos los datos
            request.setAttribute("allUsers", userDAO.getAllUsers());
            request.setAttribute("allDonors", donorDAO.getAllDonors());
            request.setAttribute("allReceivers", receiverDAO.getAllReceivers());
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
                case "createDonor":
                    createDonor(request, response);
                    break;
                case "createReceiver":
                    createReceiver(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=" + e.getMessage());
        }
    }
    
    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        try {
            User user = extractUserFromRequest(request);
            
            // Validaciones básicas
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Username+requerido");
                return;
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Password+requerido");
                return;
            }
            
            if (user.getPassword().length() < 6) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Password+debe+tener+mínimo+6+caracteres");
                return;
            }
            
            // Validar que el username no exista
            User existingUser = userDAO.getUserByUsername(user.getUsername());
            if (existingUser != null) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Usuario+ya+existe");
                return;
            }
            
            boolean success = userDAO.addUser(user);
            if (success) {
                // Si es donador o receptor, crear también en la tabla específica
                if ("donador".equals(user.getUserType())) {
                    createDonorRecord(user);
                } else if ("receptor".equals(user.getUserType())) {
                    createReceiverRecord(user);
                }
                
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&success=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Error+al+crear+usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=" + e.getMessage());
        }
    }
    
    private void createDonorRecord(User user) {
        try {
            Donor donor = new Donor();
            donor.setId(user.getId());
            donor.setUsername(user.getUsername());
            donor.setPassword(user.getPassword());
            donor.setUserType(user.getUserType());
            donor.setFirstName(user.getFirstName());
            donor.setLastName(user.getLastName());
            donor.setEmail(user.getEmail());
            donor.setPhone(user.getPhone());
            donor.setDni(user.getDni());
            donor.setBirthDate(user.getBirthDate());
            donor.setRegion(user.getRegion());
            donor.setDistrict(user.getDistrict());
            donor.setAddress(user.getAddress());
            donor.setRegistrationDate(user.getRegistrationDate());
            donor.setActive(user.isActive());
            donor.setNotificationsEnabled(user.isNotificationsEnabled());
            
            donorDAO.addDonor(donor);
        } catch (Exception e) {
            System.err.println("Error creando registro de donador: " + e.getMessage());
        }
    }
    
    private void createReceiverRecord(User user) {
        try {
            Receiver receiver = new Receiver();
            receiver.setId(user.getId());
            receiver.setUsername(user.getUsername());
            receiver.setPassword(user.getPassword());
            receiver.setUserType(user.getUserType());
            receiver.setFirstName(user.getFirstName());
            receiver.setLastName(user.getLastName());
            receiver.setEmail(user.getEmail());
            receiver.setPhone(user.getPhone());
            receiver.setDni(user.getDni());
            receiver.setBirthDate(user.getBirthDate());
            receiver.setRegion(user.getRegion());
            receiver.setDistrict(user.getDistrict());
            receiver.setAddress(user.getAddress());
            receiver.setRegistrationDate(user.getRegistrationDate());
            receiver.setActive(user.isActive());
            receiver.setNotificationsEnabled(user.isNotificationsEnabled());
            
            // Valores por defecto para receptor
            receiver.setFamilySize(1);
            receiver.setChildren(0);
            receiver.setEconomicSituation("baja");
            receiver.setDataConsent(false);
            receiver.setVerified(false);
            receiver.setVerificationStatus("pending");
            
            receiverDAO.addReceiver(receiver);
        } catch (Exception e) {
            System.err.println("Error creando registro de receptor: " + e.getMessage());
        }
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        try {
            String userIdStr = request.getParameter("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=ID+de+usuario+requerido");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            User existingUser = userDAO.getUserById(userId);
            if (existingUser == null) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Usuario+no+encontrado");
                return;
            }
            
            User user = extractUserFromRequest(request);
            user.setId(userId);
            
            // Para actualización, no cambiar la contraseña si está vacía
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                // Validar longitud de password si se está cambiando
                if (user.getPassword().length() < 6) {
                    response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Password+debe+tener+mínimo+6+caracteres");
                    return;
                }
            }
            
            boolean success = userDAO.updateUser(user);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&success=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Error+al+actualizar+usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=" + e.getMessage());
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        try {
            String userIdStr = request.getParameter("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=ID+de+usuario+requerido");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            
            // No permitir eliminar al usuario admin actual
            HttpSession session = request.getSession(false);
            String currentUsername = (String) session.getAttribute("username");
            User userToDelete = userDAO.getUserById(userId);
            
            if (userToDelete != null && userToDelete.getUsername().equals(currentUsername)) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=No+puede+eliminar+su+propio+usuario");
                return;
            }
            
            boolean success = userDAO.deleteUser(userId);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&success=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=Error+al+eliminar+usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/userManagement?action=list&error=" + e.getMessage());
        }
    }
    
    private void createDonor(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        try {
            // Obtener parámetros del formulario específico de donador
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
            
            // Crear usuario base primero
            User user = new User();
            user.setUsername(dni);
            user.setPassword("donor123"); // Password por defecto
            user.setUserType("donador");
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setDni(dni);
            user.setRegion(region);
            user.setDistrict(district);
            user.setAddress(address);
            
            // Parse birth date
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date birthDate = sdf.parse(birthDateStr);
                user.setBirthDate(birthDate);
            }
            
            // Guardar usuario
            boolean userSuccess = userDAO.addUser(user);
            if (userSuccess) {
                // Crear donador específico
                User createdUser = userDAO.getUserByUsername(dni);
                if (createdUser != null) {
                    Donor donor = new Donor();
                    donor.setId(createdUser.getId());
                    donor.setUsername(createdUser.getUsername());
                    donor.setPassword(createdUser.getPassword());
                    donor.setUserType(createdUser.getUserType());
                    donor.setFirstName(createdUser.getFirstName());
                    donor.setLastName(createdUser.getLastName());
                    donor.setEmail(createdUser.getEmail());
                    donor.setPhone(createdUser.getPhone());
                    donor.setDni(createdUser.getDni());
                    donor.setBirthDate(createdUser.getBirthDate());
                    donor.setRegion(createdUser.getRegion());
                    donor.setDistrict(createdUser.getDistrict());
                    donor.setAddress(createdUser.getAddress());
                    
                    if (donationTypes != null) {
                        donor.setDonationTypes(java.util.Arrays.asList(donationTypes));
                    }
                    donor.setComments(comments);
                    
                    boolean donorSuccess = donorDAO.addDonor(donor);
                    if (donorSuccess) {
                        response.sendRedirect(request.getContextPath() + "/users?action=newDonor&success=true");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/users?action=newDonor&error=Error+al+crear+donador");
                    }
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/users?action=newDonor&error=Error+al+crear+usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/users?action=newDonor&error=" + e.getMessage());
        }
    }
    
    private void createReceiver(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        try {
            // Obtener parámetros del formulario específico de receptor
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
            
            // Crear usuario base primero
            User user = new User();
            user.setUsername(dni);
            user.setPassword("receiver123"); // Password por defecto
            user.setUserType("receptor");
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setDni(dni);
            user.setRegion(region);
            user.setDistrict(district);
            user.setAddress(address);
            
            // Parse birth date
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date birthDate = sdf.parse(birthDateStr);
                user.setBirthDate(birthDate);
            }
            
            // Guardar usuario
            boolean userSuccess = userDAO.addUser(user);
            if (userSuccess) {
                // Crear receptor específico
                User createdUser = userDAO.getUserByUsername(dni);
                if (createdUser != null) {
                    Receiver receiver = new Receiver();
                    receiver.setId(createdUser.getId());
                    receiver.setUsername(createdUser.getUsername());
                    receiver.setPassword(createdUser.getPassword());
                    receiver.setUserType(createdUser.getUserType());
                    receiver.setFirstName(createdUser.getFirstName());
                    receiver.setLastName(createdUser.getLastName());
                    receiver.setEmail(createdUser.getEmail());
                    receiver.setPhone(createdUser.getPhone());
                    receiver.setDni(createdUser.getDni());
                    receiver.setBirthDate(createdUser.getBirthDate());
                    receiver.setRegion(createdUser.getRegion());
                    receiver.setDistrict(createdUser.getDistrict());
                    receiver.setAddress(createdUser.getAddress());
                    
                    // Campos específicos de receptor
                    if (familySizeStr != null && !familySizeStr.isEmpty()) {
                        if (familySizeStr.equals("6+")) {
                            receiver.setFamilySize(6);
                        } else {
                            receiver.setFamilySize(Integer.parseInt(familySizeStr));
                        }
                    }
                    
                    if (childrenStr != null && !childrenStr.isEmpty()) {
                        receiver.setChildren(Integer.parseInt(childrenStr));
                    }
                    
                    receiver.setEconomicSituation(economicSituation);
                    
                    if (needs != null && !needs.isEmpty()) {
                        receiver.setNeeds(java.util.Arrays.asList(needs.split(",")));
                    }
                    
                    receiver.setNeedsDescription(needsDescription);
                    receiver.setDataConsent("on".equals(dataConsent));
                    receiver.setVerified(false);
                    receiver.setVerificationStatus("pending");
                    
                    boolean receiverSuccess = receiverDAO.addReceiver(receiver);
                    if (receiverSuccess) {
                        response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&success=true");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&error=Error+al+crear+receptor");
                    }
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&error=Error+al+crear+usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/users?action=newReceiver&error=" + e.getMessage());
        }
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
        user.setActive("true".equals(request.getParameter("active")));
        user.setNotificationsEnabled("true".equals(request.getParameter("notifications")));
        
        return user;
    }
}