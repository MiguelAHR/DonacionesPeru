package com.donaciones.dao;

import com.donaciones.models.Donor;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {
    
    public boolean addDonor(Donor donor) {
        String sql = "INSERT INTO donadores (user_id, donation_types, comments) VALUES (?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Primero obtener el ID del usuario
            UserDAO userDAO = new UserDAO();
            com.donaciones.models.User user = userDAO.getUserByUsername(donor.getUsername());
            
            if (user != null) {
                pstmt.setInt(1, user.getId());
                pstmt.setString(2, String.join(",", donor.getDonationTypes()));
                pstmt.setString(3, donor.getComments());
                
                return pstmt.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT u.*, d.donation_types, d.comments, d.total_donations, d.last_donation_date " +
                    "FROM usuarios u JOIN donadores d ON u.id = d.user_id";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                donors.add(mapDonor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donors;
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
        
        // Parse donation types
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