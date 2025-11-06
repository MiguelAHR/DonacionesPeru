package com.donaciones.utils;

import com.donaciones.dao.CatalogoDAO;
import com.donaciones.dao.UserDAO;
import com.donaciones.dao.DonationDAO;
import com.donaciones.dao.RequestDAO;
import com.donaciones.dao.DonorDAO;
import com.donaciones.dao.ReceiverDAO;
import com.donaciones.dao.ReporteDAO;
import com.donaciones.dao.RoleDAO;
import com.donaciones.dao.StatisticsDAO;
import com.donaciones.models.User;
import com.donaciones.models.Donation;
import com.donaciones.models.Request;
import com.donaciones.models.Donor;
import com.donaciones.models.Receiver;
import com.donaciones.models.Catalogo;
import com.donaciones.models.Reporte;
import com.donaciones.models.Role;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManager {

    private static DataManager instance;
    private UserDAO userDAO;
    private DonationDAO donationDAO;
    private RequestDAO requestDAO;
    private DonorDAO donorDAO;
    private ReceiverDAO receiverDAO;
    private RoleDAO roleDAO;
    private CatalogoDAO catalogoDAO;
    private ReporteDAO reporteDAO;
    private StatisticsDAO statisticsDAO;

    private DataManager() {
        userDAO = new UserDAO();
        donationDAO = new DonationDAO();
        requestDAO = new RequestDAO();
        donorDAO = new DonorDAO();
        receiverDAO = new ReceiverDAO();
        roleDAO = new RoleDAO();
        catalogoDAO = new CatalogoDAO();
        reporteDAO = new ReporteDAO();
        statisticsDAO = new StatisticsDAO();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // User operations
    public User authenticateUser(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    public User getUser(String username) {
        return userDAO.getUserByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean addUser(User user) {
        return userDAO.addUser(user);
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id);
    }

    // Donation operations
    public boolean addDonation(Donation donation) {
        try {
            return donationDAO.addDonation(donation);
        } catch (Exception e) {
            System.out.println("ERROR DataManager - Error en addDonation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Donation getDonation(int id) {
        return donationDAO.getDonation(id);
    }

    public List<Donation> getAllDonations() {
        return donationDAO.getAllDonations();
    }

    public List<Donation> getDonationsByUser(String username) {
        return donationDAO.getDonationsByUser(username);
    }

    public boolean updateDonation(Donation donation) {
        return donationDAO.updateDonation(donation);
    }

    public List<Donation> getDonationsByStatus(String status) {
        return donationDAO.getDonationsByStatus(status);
    }

    public List<Donation> getDonationsByEmployee(String employeeUsername) {
        return donationDAO.getDonationsByEmployee(employeeUsername);
    }

    public List<Donation> getAvailableDonations() {
        return donationDAO.getAvailableDonations();
    }

    public boolean assignDonationToEmployee(int donationId, String employeeUsername) {
        return donationDAO.assignDonationToEmployee(donationId, employeeUsername);
    }

    public boolean updateDonationStatus(int donationId, String newStatus) {
        return donationDAO.updateDonationStatus(donationId, newStatus);
    }

    public boolean deleteDonation(int donationId) {
        return donationDAO.deleteDonation(donationId);
    }

    // Request operations
    public boolean addRequest(Request request) {
        return requestDAO.addRequest(request);
    }

    public List<Request> getAllRequests() {
        return requestDAO.getAllRequests();
    }

    public List<Request> getRequestsByUser(String username) {
        return requestDAO.getRequestsByUser(username);
    }

    public Request getRequest(int id) {
        return requestDAO.getRequest(id);
    }

    public boolean updateRequest(Request request) {
        return requestDAO.updateRequest(request);
    }

    public List<Request> getRequestsByStatus(String status) {
        return requestDAO.getRequestsByStatus(status);
    }

    public List<Request> getRequestsByEmployee(String employeeUsername) {
        return requestDAO.getRequestsByEmployee(employeeUsername);
    }

    public List<Request> getAvailableRequests() {
        return requestDAO.getAvailableRequests();
    }

    public boolean assignRequestToEmployee(int requestId, String employeeUsername) {
        return requestDAO.assignRequestToEmployee(requestId, employeeUsername);
    }

    public boolean updateRequestStatus(int requestId, String newStatus) {
        return requestDAO.updateRequestStatus(requestId, newStatus);
    }

    public boolean deleteRequest(int requestId) {
        return requestDAO.deleteRequest(requestId);
    }

    // Donor operations
    public boolean addDonor(Donor donor) {
        // Primero creamos el usuario
        User user = new User();
        user.setUsername(donor.getUsername());
        user.setPassword(donor.getPassword());
        user.setUserType("donador");
        user.setFirstName(donor.getFirstName());
        user.setLastName(donor.getLastName());
        user.setEmail(donor.getEmail());
        user.setPhone(donor.getPhone());
        user.setDni(donor.getDni());
        user.setBirthDate(donor.getBirthDate());
        user.setRegion(donor.getRegion());
        user.setDistrict(donor.getDistrict());
        user.setAddress(donor.getAddress());
        user.setNotificationsEnabled(donor.isNotificationsEnabled());

        if (userDAO.addUser(user)) {
            // Luego agregamos el donador
            return donorDAO.addDonor(donor);
        }
        return false;
    }

    public List<Donor> getAllDonors() {
        return donorDAO.getAllDonors();
    }

    public Donor getDonorByUserId(int userId) {
        return donorDAO.getDonorByUserId(userId);
    }

    public boolean updateDonor(Donor donor) {
        return donorDAO.updateDonor(donor);
    }

    public boolean deleteDonor(int userId) {
        return donorDAO.deleteDonor(userId);
    }

    // Receiver operations
    public boolean addReceiver(Receiver receiver) {
        // Primero creamos el usuario
        User user = new User();
        user.setUsername(receiver.getUsername());
        user.setPassword(receiver.getPassword());
        user.setUserType("receptor");
        user.setFirstName(receiver.getFirstName());
        user.setLastName(receiver.getLastName());
        user.setEmail(receiver.getEmail());
        user.setPhone(receiver.getPhone());
        user.setDni(receiver.getDni());
        user.setBirthDate(receiver.getBirthDate());
        user.setRegion(receiver.getRegion());
        user.setDistrict(receiver.getDistrict());
        user.setAddress(receiver.getAddress());
        user.setNotificationsEnabled(receiver.isNotificationsEnabled());

        if (userDAO.addUser(user)) {
            // Luego agregamos el receptor
            return receiverDAO.addReceiver(receiver);
        }
        return false;
    }

    public List<Receiver> getAllReceivers() {
        return receiverDAO.getAllReceivers();
    }

    public Receiver getReceiverByUserId(int userId) {
        return receiverDAO.getReceiverByUserId(userId);
    }

    public boolean updateReceiver(Receiver receiver) {
        return receiverDAO.updateReceiver(receiver);
    }

    public boolean deleteReceiver(int userId) {
        return receiverDAO.deleteReceiver(userId);
    }

    // Role operations
    public List<Role> getAllRoles() {
        return roleDAO.getAllRoles();
    }

    public Role getRoleById(int id) {
        return roleDAO.getRoleById(id);
    }

    public Role getRoleByNombre(String nombre) {
        return roleDAO.getRoleByNombre(nombre);
    }

    public boolean addRole(Role role) {
        return roleDAO.addRole(role);
    }

    public boolean updateRole(Role role) {
        return roleDAO.updateRole(role);
    }

    public boolean deleteRole(int id) {
        return roleDAO.deleteRole(id);
    }

    // Catalogo operations
    public List<Catalogo> getAllCatalogItems() {
        return catalogoDAO.getAllCatalogItems();
    }

    public List<Catalogo> getAvailableCatalogItems() {
        return catalogoDAO.getAvailableItems();
    }

    public Catalogo getCatalogoItem(int id) {
        return catalogoDAO.getCatalogoItem(id);
    }

    public boolean addCatalogoItem(Catalogo catalogo) {
        return catalogoDAO.addCatalogoItem(catalogo);
    }

    public boolean updateCatalogoItem(Catalogo catalogo) {
        return catalogoDAO.updateCatalogoItem(catalogo);
    }

    public boolean updateCatalogoItemStatus(int id, String estado) {
        return catalogoDAO.updateItemStatus(id, estado);
    }

    public List<Catalogo> searchCatalogItems(String tipo, String ubicacion, String estado) {
        return catalogoDAO.searchCatalogItems(tipo, ubicacion, estado);
    }

    public boolean moveCompletedDonationToCatalog(int donationId) {
        try {
            Donation donation = donationDAO.getDonation(donationId);
            if (donation != null && "completed".equals(donation.getStatus())) {
                // Verificar si ya existe en el catálogo
                if (!catalogoDAO.itemExistsForDonation(donationId)) {
                    Catalogo catalogo = new Catalogo();
                    catalogo.setDonacionId(donationId);
                    catalogo.setTitulo(donation.getDescription());
                    catalogo.setDescripcion(donation.getDescription());
                    catalogo.setTipo(donation.getType());
                    catalogo.setCantidad(donation.getQuantity());
                    catalogo.setCondicion(donation.getCondition());
                    catalogo.setUbicacion(donation.getLocation());
                    catalogo.setDonante(donation.getDonorUsername());
                    catalogo.setEstado("disponible");
                    // Puedes setear más campos si es necesario

                    return catalogoDAO.addCatalogoItem(catalogo);
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error moviendo donación al catálogo: " + e.getMessage());
            return false;
        }
    }

    // Reporte operations
    public List<Reporte> getAllReports() {
        return reporteDAO.getAllReports();
    }

    public Reporte getReportById(int id) {
        return reporteDAO.getReportById(id);
    }

    public List<Reporte> getReportsByUser(String username) {
        return reporteDAO.getReportsByUser(username);
    }

    public boolean addReport(Reporte reporte) {
        return reporteDAO.addReport(reporte);
    }

    public boolean updateReport(Reporte reporte) {
        return reporteDAO.updateReport(reporte);
    }

    public boolean updateReportStatus(int id, String estado, String rutaArchivo) {
        return reporteDAO.updateReportStatus(id, estado, rutaArchivo);
    }

    public List<Reporte> getReportsByType(String tipo) {
        return reporteDAO.getReportsByType(tipo);
    }

    public List<Reporte> getRecentReports(int limit) {
        return reporteDAO.getRecentReports(limit);
    }

    // Statistics methods for dashboard
    public Map<String, Integer> getDashboardStats() {
        try {
            if (statisticsDAO != null) {
                Map<String, Integer> stats = statisticsDAO.getDashboardStats();
                if (stats != null && !stats.isEmpty()) {
                    return stats;
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo estadísticas del dashboard: " + e.getMessage());
        }
        
        // Estadísticas por defecto si hay error
        Map<String, Integer> defaultStats = new HashMap<>();
        defaultStats.put("total_donations", getTotalDonations());
        defaultStats.put("total_donors", getTotalDonors());
        defaultStats.put("total_receivers", getTotalReceivers());
        defaultStats.put("pending_donations", getTotalPendingDonations());
        defaultStats.put("pending_requests", getPendingRequestsCount());
        return defaultStats;
    }

    public int getTotalDonations() {
        try {
            return donationDAO.getAllDonations().size();
        } catch (Exception e) {
            System.err.println("Error obteniendo total de donaciones: " + e.getMessage());
            return 0;
        }
    }

    public int getTotalActiveDonations() {
        try {
            return donationDAO.getDonationsByStatus("active").size();
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones activas: " + e.getMessage());
            return 0;
        }
    }

    public int getTotalPendingDonations() {
        try {
            return donationDAO.getDonationsByStatus("pending").size();
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones pendientes: " + e.getMessage());
            return 0;
        }
    }

    public int getTotalDonors() {
        try {
            if (donorDAO != null) {
                return donorDAO.getAllDonors().size();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error obteniendo total de donadores: " + e.getMessage());
            return 0;
        }
    }

    public int getTotalReceivers() {
        try {
            if (receiverDAO != null) {
                return receiverDAO.getAllReceivers().size();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error obteniendo total de receptores: " + e.getMessage());
            return 0;
        }
    }

    public int getTotalUsers() {
        try {
            return userDAO.getAllUsers().size();
        } catch (Exception e) {
            System.err.println("Error obteniendo total de usuarios: " + e.getMessage());
            return 0;
        }
    }

    public int getTotalAdmins() {
        try {
            List<User> users = userDAO.getAllUsers();
            return (int) users.stream()
                    .filter(user -> "admin".equalsIgnoreCase(user.getUserType()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error obteniendo total de administradores: " + e.getMessage());
            return 0;
        }
    }

    public int getTotalEmployees() {
        try {
            List<User> users = userDAO.getAllUsers();
            return (int) users.stream()
                    .filter(user -> "empleado".equalsIgnoreCase(user.getUserType()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error obteniendo total de empleados: " + e.getMessage());
            return 0;
        }
    }

    public int getTotalRegularUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            return (int) users.stream()
                    .filter(user -> "usuario".equalsIgnoreCase(user.getUserType()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error obteniendo total de usuarios regulares: " + e.getMessage());
            return 0;
        }
    }

    // Report methods
    public Map<String, Long> getDonationsByType() {
        try {
            if (statisticsDAO != null) {
                Map<String, Long> result = statisticsDAO.getDonationsByType();
                if (result != null) return result;
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones por tipo: " + e.getMessage());
        }
        
        // Implementación alternativa
        Map<String, Long> donationsByType = new HashMap<>();
        try {
            List<Donation> donations = getAllDonations();
            for (Donation donation : donations) {
                String type = donation.getType();
                if (type != null) {
                    donationsByType.merge(type, 1L, Long::sum);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en implementación alternativa de donaciones por tipo: " + e.getMessage());
        }
        return donationsByType;
    }

    public Map<String, Long> getDonationsByLocation() {
        try {
            if (statisticsDAO != null) {
                Map<String, Long> result = statisticsDAO.getDonationsByRegion();
                if (result != null) return result;
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones por ubicación: " + e.getMessage());
        }
        
        // Implementación alternativa
        Map<String, Long> donationsByLocation = new HashMap<>();
        try {
            List<Donation> donations = getAllDonations();
            for (Donation donation : donations) {
                String location = donation.getLocation();
                if (location != null) {
                    donationsByLocation.merge(location, 1L, Long::sum);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en implementación alternativa de donaciones por ubicación: " + e.getMessage());
        }
        return donationsByLocation;
    }

    public Map<String, Long> getDonationsByStatusForReports() {
        Map<String, Long> donationsByStatus = new HashMap<>();
        try {
            List<Donation> donations = getAllDonations();
            for (Donation donation : donations) {
                String status = donation.getStatus();
                if (status != null) {
                    donationsByStatus.merge(status, 1L, Long::sum);
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones por estado: " + e.getMessage());
        }
        return donationsByStatus;
    }

    public long getRecentDonations() {
        try {
            List<Donation> donations = getAllDonations();
            long recentCount = 0;
            long oneMonthAgo = System.currentTimeMillis() - (30L * 24L * 60L * 60L * 1000L);

            for (Donation donation : donations) {
                if (donation.getCreatedDate() != null
                        && donation.getCreatedDate().getTime() > oneMonthAgo) {
                    recentCount++;
                }
            }
            return recentCount;
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones recientes: " + e.getMessage());
            return 0;
        }
    }

    // Employee-specific statistics
    public long getEmployeeDonations(String employeeUsername) {
        try {
            return donationDAO.getDonationsByEmployee(employeeUsername).size();
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones del empleado: " + e.getMessage());
            return 0;
        }
    }

    public long getEmployeeActiveDonations(String employeeUsername) {
        try {
            List<Donation> employeeDonations = donationDAO.getDonationsByEmployee(employeeUsername);
            return employeeDonations.stream()
                    .filter(d -> "active".equals(d.getStatus()) || "in_progress".equals(d.getStatus()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones activas del empleado: " + e.getMessage());
            return 0;
        }
    }

    public long getEmployeeCompletedDonations(String employeeUsername) {
        try {
            List<Donation> employeeDonations = donationDAO.getDonationsByEmployee(employeeUsername);
            return employeeDonations.stream()
                    .filter(d -> "completed".equals(d.getStatus()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones completadas del empleado: " + e.getMessage());
            return 0;
        }
    }

    public Map<String, Long> getEmployeeDonationsByType(String employeeUsername) {
        Map<String, Long> donationsByType = new HashMap<>();
        try {
            List<Donation> employeeDonations = donationDAO.getDonationsByEmployee(employeeUsername);
            for (Donation donation : employeeDonations) {
                String type = donation.getType();
                if (type != null) {
                    donationsByType.merge(type, 1L, Long::sum);
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones del empleado por tipo: " + e.getMessage());
        }
        return donationsByType;
    }

    public Map<String, Long> getEmployeeDonationsByLocation(String employeeUsername) {
        Map<String, Long> donationsByLocation = new HashMap<>();
        try {
            List<Donation> employeeDonations = donationDAO.getDonationsByEmployee(employeeUsername);
            for (Donation donation : employeeDonations) {
                String location = donation.getLocation();
                if (location != null) {
                    donationsByLocation.merge(location, 1L, Long::sum);
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo donaciones del empleado por ubicación: " + e.getMessage());
        }
        return donationsByLocation;
    }

    // Catalog statistics
    public int getTotalCatalogItemsCount() {
        try {
            return catalogoDAO.getAllCatalogItems().size();
        } catch (Exception e) {
            System.err.println("Error obteniendo total de items del catálogo: " + e.getMessage());
            return 0;
        }
    }

    public int getAvailableCatalogItemsCount() {
        try {
            return catalogoDAO.getAvailableItems().size();
        } catch (Exception e) {
            System.err.println("Error obteniendo items disponibles del catálogo: " + e.getMessage());
            return 0;
        }
    }

    public int getAssignedCatalogItemsCount() {
        try {
            List<Catalogo> allItems = catalogoDAO.getAllCatalogItems();
            return (int) allItems.stream()
                    .filter(item -> "asignado".equals(item.getEstado()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error obteniendo items asignados del catálogo: " + e.getMessage());
            return 0;
        }
    }

    public int getDeliveredCatalogItemsCount() {
        try {
            List<Catalogo> allItems = catalogoDAO.getAllCatalogItems();
            return (int) allItems.stream()
                    .filter(item -> "entregado".equals(item.getEstado()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error obteniendo items entregados del catálogo: " + e.getMessage());
            return 0;
        }
    }

    // Request statistics
    public int getTotalRequestsCount() {
        try {
            return requestDAO.getAllRequests().size();
        } catch (Exception e) {
            System.err.println("Error obteniendo total de solicitudes: " + e.getMessage());
            return 0;
        }
    }

    public int getPendingRequestsCount() {
        try {
            return requestDAO.getRequestsByStatus("pending").size();
        } catch (Exception e) {
            System.err.println("Error obteniendo solicitudes pendientes: " + e.getMessage());
            return 0;
        }
    }

    public int getInProgressRequestsCount() {
        try {
            return requestDAO.getRequestsByStatus("in_progress").size();
        } catch (Exception e) {
            System.err.println("Error obteniendo solicitudes en progreso: " + e.getMessage());
            return 0;
        }
    }

    public int getCompletedRequestsCount() {
        try {
            return requestDAO.getRequestsByStatus("completed").size();
        } catch (Exception e) {
            System.err.println("Error obteniendo solicitudes completadas: " + e.getMessage());
            return 0;
        }
    }

    // Métodos adicionales para compatibilidad
    public int getUsersByType(String userType) {
        try {
            List<User> users = userDAO.getAllUsers();
            return (int) users.stream()
                    .filter(user -> userType.equalsIgnoreCase(user.getUserType()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error obteniendo usuarios por tipo: " + e.getMessage());
            return 0;
        }
    }
}