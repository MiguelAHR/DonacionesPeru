package com.donaciones.utils;

import com.donaciones.dao.UserDAO;
import com.donaciones.dao.DonationDAO;
import com.donaciones.dao.RequestDAO;
import com.donaciones.dao.DonorDAO;
import com.donaciones.dao.ReceiverDAO;
import com.donaciones.models.User;
import com.donaciones.models.Donation;
import com.donaciones.models.Request;
import com.donaciones.models.Donor;
import com.donaciones.models.Receiver;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static DataManager instance;
    private UserDAO userDAO;
    private DonationDAO donationDAO;
    private RequestDAO requestDAO;
    private DonorDAO donorDAO;
    private ReceiverDAO receiverDAO;

    private DataManager() {
        userDAO = new UserDAO();
        donationDAO = new DonationDAO();
        requestDAO = new RequestDAO();
        donorDAO = new DonorDAO();
        receiverDAO = new ReceiverDAO();
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
    
    public void addUser(User user) {
        userDAO.addUser(user);
    }

    // Donation operations
    public void addDonation(Donation donation) {
        donationDAO.addDonation(donation);
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

    public void updateDonation(Donation donation) {
        donationDAO.updateDonation(donation);
    }
    
    public List<Donation> getDonationsByStatus(String status) {
        return donationDAO.getDonationsByStatus(status);
    }
    
    public List<Donation> getDonationsByEmployee(String employeeUsername) {
        return donationDAO.getDonationsByEmployee(employeeUsername);
    }

    // Request operations - CORREGIDOS
    public void addRequest(Request request) {
        requestDAO.addRequest(request);
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

    public void updateRequest(Request request) {
        requestDAO.updateRequest(request);
    }
    
    public List<Request> getRequestsByStatus(String status) {
        return requestDAO.getRequestsByStatus(status);
    }

    // Donor operations
    public void addDonor(Donor donor) {
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
        
        userDAO.addUser(user);
        donorDAO.addDonor(donor);
    }

    public List<Donor> getAllDonors() {
        return donorDAO.getAllDonors();
    }

    // Receiver operations
    public void addReceiver(Receiver receiver) {
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
        
        userDAO.addUser(user);
        receiverDAO.addReceiver(receiver);
    }
    
    public List<Receiver> getAllReceivers() {
        return receiverDAO.getAllReceivers();
    }

    // Statistics methods for dashboard
    public int getTotalDonations() {
        return donationDAO.getAllDonations().size();
    }
    
    public int getTotalActiveDonations() {
        return donationDAO.getDonationsByStatus("active").size();
    }
    
    public int getTotalPendingDonations() {
        return donationDAO.getDonationsByStatus("pending").size();
    }

    public int getTotalDonors() {
        return donorDAO.getTotalDonors();
    }

    public int getTotalReceivers() {
        return receiverDAO.getTotalReceivers();
    }

    public int getTotalUsers() {
        return userDAO.getTotalUsers();
    }
    
    public int getTotalAdmins() {
        return userDAO.getUsersByType("admin");
    }
    
    public int getTotalEmployees() {
        return userDAO.getUsersByType("empleado");
    }
    
    public int getTotalRegularUsers() {
        return userDAO.getUsersByType("usuario");
    }
    
    // Report methods
    public Map<String, Long> getDonationsByType() {
        Map<String, Long> donationsByType = new HashMap<>();
        List<Donation> donations = getAllDonations();
        
        for (Donation donation : donations) {
            donationsByType.merge(donation.getType(), 1L, Long::sum);
        }
        return donationsByType;
    }
    
    public Map<String, Long> getDonationsByLocation() {
        Map<String, Long> donationsByLocation = new HashMap<>();
        List<Donation> donations = getAllDonations();
        
        for (Donation donation : donations) {
            donationsByLocation.merge(donation.getLocation(), 1L, Long::sum);
        }
        return donationsByLocation;
    }
    
    public Map<String, Long> getDonationsByStatusForReports() {
        Map<String, Long> donationsByStatus = new HashMap<>();
        List<Donation> donations = getAllDonations();
        
        for (Donation donation : donations) {
            donationsByStatus.merge(donation.getStatus(), 1L, Long::sum);
        }
        return donationsByStatus;
    }
    
    public long getRecentDonations() {
        List<Donation> donations = getAllDonations();
        long recentCount = 0;
        long oneMonthAgo = System.currentTimeMillis() - (30L * 24L * 60L * 60L * 1000L);
        
        for (Donation donation : donations) {
            if (donation.getCreatedDate() != null && 
                donation.getCreatedDate().getTime() > oneMonthAgo) {
                recentCount++;
            }
        }
        return recentCount;
    }
    
    // Employee-specific statistics
    public long getEmployeeDonations(String employeeUsername) {
        return donationDAO.getDonationsByEmployee(employeeUsername).size();
    }
    
    public long getEmployeeActiveDonations(String employeeUsername) {
        List<Donation> employeeDonations = donationDAO.getDonationsByEmployee(employeeUsername);
        return employeeDonations.stream()
                .filter(d -> "active".equals(d.getStatus()) || "in_progress".equals(d.getStatus()))
                .count();
    }
    
    public long getEmployeeCompletedDonations(String employeeUsername) {
        List<Donation> employeeDonations = donationDAO.getDonationsByEmployee(employeeUsername);
        return employeeDonations.stream()
                .filter(d -> "completed".equals(d.getStatus()))
                .count();
    }
    
    public Map<String, Long> getEmployeeDonationsByType(String employeeUsername) {
        Map<String, Long> donationsByType = new HashMap<>();
        List<Donation> employeeDonations = donationDAO.getDonationsByEmployee(employeeUsername);
        
        for (Donation donation : employeeDonations) {
            donationsByType.merge(donation.getType(), 1L, Long::sum);
        }
        return donationsByType;
    }
    
    public Map<String, Long> getEmployeeDonationsByLocation(String employeeUsername) {
        Map<String, Long> donationsByLocation = new HashMap<>();
        List<Donation> employeeDonations = donationDAO.getDonationsByEmployee(employeeUsername);
        
        for (Donation donation : employeeDonations) {
            donationsByLocation.merge(donation.getLocation(), 1L, Long::sum);
        }
        return donationsByLocation;
    }
}