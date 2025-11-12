package com.donaciones.dao;

import com.donaciones.models.Donation;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {

    public List<Donation> getAllDonations() {
        List<Donation> donations = new ArrayList<>();
        String sql = "CALL sp_get_all_donations()";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getDonationsByUser(String username) {
        List<Donation> donations = new ArrayList<>();
        String sql = "CALL sp_get_donations_by_user(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, username);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public boolean addDonation(Donation donation) {
        String sql = "CALL sp_insert_donation(?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, donation.getType());
            cstmt.setString(2, donation.getDescription());
            cstmt.setInt(3, donation.getQuantity());
            cstmt.setString(4, donation.getCondition());
            cstmt.setString(5, donation.getLocation());
            cstmt.setString(6, donation.getAddress());
            cstmt.setString(7, donation.getDonorUsername());
            cstmt.setString(8, donation.getStatus());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDonation(Donation donation) {
        String sql = "CALL sp_update_donation(?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, donation.getId());
            cstmt.setString(2, donation.getType());
            cstmt.setString(3, donation.getDescription());
            cstmt.setInt(4, donation.getQuantity());
            cstmt.setString(5, donation.getCondition());
            cstmt.setString(6, donation.getLocation());
            cstmt.setString(7, donation.getStatus());
            cstmt.setString(8, donation.getEmployeeUsername());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDonationStatus(int donationId, String newStatus) {
        String sql = "CALL sp_update_donation_status(?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, donationId);
            cstmt.setString(2, newStatus);
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignDonationToEmployee(int donationId, String employeeUsername) {
        String sql = "CALL sp_assign_donation_to_employee(?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, donationId);
            cstmt.setString(2, employeeUsername);
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Donation getDonation(int id) {
        String sql = "CALL sp_get_donation(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, id);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                return mapDonation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Donation> getDonationsByStatus(String status) {
        List<Donation> donations = new ArrayList<>();
        String sql = "CALL sp_get_donations_by_status(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, status);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getDonationsByEmployee(String employeeUsername) {
        List<Donation> donations = new ArrayList<>();
        String sql = "CALL sp_get_donations_by_employee(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, employeeUsername);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getAvailableDonations() {
        List<Donation> donations = new ArrayList<>();
        String sql = "CALL sp_get_available_donations()";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public boolean deleteDonation(int donationId) {
        String sql = "CALL sp_delete_donation(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, donationId);
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Donation mapDonation(ResultSet rs) throws SQLException {
        Donation donation = new Donation();
        donation.setId(rs.getInt("id"));
        donation.setType(rs.getString("type"));
        donation.setDescription(rs.getString("description"));
        donation.setQuantity(rs.getInt("quantity"));
        donation.setCondition(rs.getString("item_condition"));
        donation.setLocation(rs.getString("location"));
        donation.setDonorUsername(rs.getString("donor_username"));
        donation.setStatus(rs.getString("status"));
        donation.setEmployeeUsername(rs.getString("employee_assigned"));
        donation.setCreatedDate(rs.getTimestamp("created_date"));
        donation.setDonationDate(rs.getTimestamp("donation_date"));
        
        String address = rs.getString("address");
        if (address != null) {
            donation.setAddress(address);
        }
        
        return donation;
    }
}