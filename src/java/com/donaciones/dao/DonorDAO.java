package com.donaciones.dao;

import com.donaciones.models.Donor;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {
    
    public boolean addDonor(Donor donor) {
        String sql = "INSERT INTO donadores (user_id, donation_types, comments, total_donations, last_donation_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, donor.getId());
            pstmt.setString(2, String.join(",", donor.getDonationTypes()));
            pstmt.setString(3, donor.getComments());
            pstmt.setInt(4, donor.getTotalDonations());
            pstmt.setDate(5, donor.getLastDonationDate() != null ? 
                new java.sql.Date(donor.getLastDonationDate().getTime()) : null);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar donador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT u.*, d.donation_types, d.comments, d.total_donations, d.last_donation_date " +
                    "FROM usuarios u INNER JOIN donadores d ON u.id = d.user_id " +
                    "WHERE u.user_type = 'donador' ORDER BY u.registration_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                donors.add(mapDonor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener donadores: " + e.getMessage());
            e.printStackTrace();
        }
        return donors;
    }
    
    public Donor getDonorByUserId(int userId) {
        String sql = "SELECT u.*, d.donation_types, d.comments, d.total_donations, d.last_donation_date " +
                    "FROM usuarios u INNER JOIN donadores d ON u.id = d.user_id " +
                    "WHERE u.id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapDonor(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener donador: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateDonor(Donor donor) {
        String sql = "UPDATE donadores SET donation_types = ?, comments = ?, total_donations = ?, last_donation_date = ? WHERE user_id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.join(",", donor.getDonationTypes()));
            pstmt.setString(2, donor.getComments());
            pstmt.setInt(3, donor.getTotalDonations());
            pstmt.setDate(4, donor.getLastDonationDate() != null ? 
                new java.sql.Date(donor.getLastDonationDate().getTime()) : null);
            pstmt.setInt(5, donor.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar donador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteDonor(int userId) {
        String sql = "DELETE FROM donadores WHERE user_id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar donador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalDonors() {
        String sql = "SELECT COUNT(*) as total FROM donadores";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Donor mapDonor(ResultSet rs) throws SQLException {
        Donor donor = new Donor();
        
        // Mapear campos de usuario
        donor.setId(rs.getInt("id"));
        donor.setUsername(rs.getString("username"));
        donor.setPassword(rs.getString("password"));
        donor.setUserType(rs.getString("user_type"));
        donor.setFirstName(rs.getString("first_name"));
        donor.setLastName(rs.getString("last_name"));
        donor.setEmail(rs.getString("email"));
        donor.setPhone(rs.getString("phone"));
        donor.setDni(rs.getString("dni"));
        donor.setBirthDate(rs.getDate("birth_date"));
        donor.setRegion(rs.getString("region"));
        donor.setDistrict(rs.getString("district"));
        donor.setAddress(rs.getString("address"));
        donor.setRegistrationDate(rs.getTimestamp("registration_date"));
        donor.setActive(rs.getBoolean("active"));
        donor.setNotificationsEnabled(rs.getBoolean("notifications_enabled"));
        
        // Mapear campos específicos de donador
        String donationTypes = rs.getString("donation_types");
        if (donationTypes != null && !donationTypes.isEmpty()) {
            donor.setDonationTypes(java.util.Arrays.asList(donationTypes.split(",")));
        }
        
        donor.setComments(rs.getString("comments"));
        donor.setTotalDonations(rs.getInt("total_donations"));
        donor.setLastDonationDate(rs.getDate("last_donation_date"));
        
        return donor;
    }
}