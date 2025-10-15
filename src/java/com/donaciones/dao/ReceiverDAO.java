package com.donaciones.dao;

import com.donaciones.models.Receiver;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiverDAO {
    
    public boolean addReceiver(Receiver receiver) {
        String sql = "INSERT INTO receptores (user_id, family_size, children, economic_situation, needs, needs_description, data_consent) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Primero obtener el ID del usuario
            UserDAO userDAO = new UserDAO();
            com.donaciones.models.User user = userDAO.getUserByUsername(receiver.getUsername());
            
            if (user != null) {
                pstmt.setInt(1, user.getId());
                pstmt.setInt(2, receiver.getFamilySize());
                pstmt.setInt(3, receiver.getChildren());
                pstmt.setString(4, receiver.getEconomicSituation());
                pstmt.setString(5, String.join(",", receiver.getNeeds()));
                pstmt.setString(6, receiver.getNeedsDescription());
                pstmt.setBoolean(7, receiver.isDataConsent());
                
                return pstmt.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Receiver> getAllReceivers() {
        List<Receiver> receivers = new ArrayList<>();
        String sql = "SELECT u.*, r.family_size, r.children, r.economic_situation, r.needs, r.needs_description, " +
                    "r.data_consent, r.verified, r.verification_status, r.verification_date, r.verified_by, r.received_donations " +
                    "FROM usuarios u JOIN receptores r ON u.id = r.user_id";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                receivers.add(mapReceiver(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receivers;
    }
    
    public int getTotalReceivers() {
        String sql = "SELECT COUNT(*) as total FROM receptores";
        
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
    
    private Receiver mapReceiver(ResultSet rs) throws SQLException {
        Receiver receiver = new Receiver();
        receiver.setId(rs.getInt("id"));
        receiver.setUsername(rs.getString("username"));
        receiver.setPassword(rs.getString("password"));
        receiver.setUserType(rs.getString("user_type"));
        receiver.setFirstName(rs.getString("first_name"));
        receiver.setLastName(rs.getString("last_name"));
        receiver.setEmail(rs.getString("email"));
        receiver.setPhone(rs.getString("phone"));
        receiver.setDni(rs.getString("dni"));
        receiver.setBirthDate(rs.getDate("birth_date"));
        receiver.setRegion(rs.getString("region"));
        receiver.setDistrict(rs.getString("district"));
        receiver.setAddress(rs.getString("address"));
        
        receiver.setFamilySize(rs.getInt("family_size"));
        receiver.setChildren(rs.getInt("children"));
        receiver.setEconomicSituation(rs.getString("economic_situation"));
        
        // Parse needs
        String needs = rs.getString("needs");
        if (needs != null && !needs.isEmpty()) {
            receiver.setNeeds(java.util.Arrays.asList(needs.split(",")));
        }
        
        receiver.setNeedsDescription(rs.getString("needs_description"));
        receiver.setDataConsent(rs.getBoolean("data_consent"));
        receiver.setVerified(rs.getBoolean("verified"));
        receiver.setVerificationStatus(rs.getString("verification_status"));
        receiver.setVerificationDate(rs.getDate("verification_date"));
        receiver.setVerifiedBy(rs.getString("verified_by"));
        receiver.setReceivedDonations(rs.getInt("received_donations"));
        
        return receiver;
    }
}