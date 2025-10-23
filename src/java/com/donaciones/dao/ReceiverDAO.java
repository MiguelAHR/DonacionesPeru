package com.donaciones.dao;

import com.donaciones.models.Receiver;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiverDAO {
    
    public boolean addReceiver(Receiver receiver) {
        String sql = "INSERT INTO receptores (user_id, family_size, children, economic_situation, needs, needs_description, " +
                    "data_consent, verified, verification_status, received_donations) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, receiver.getId());
            pstmt.setInt(2, receiver.getFamilySize());
            pstmt.setInt(3, receiver.getChildren());
            pstmt.setString(4, receiver.getEconomicSituation());
            pstmt.setString(5, String.join(",", receiver.getNeeds()));
            pstmt.setString(6, receiver.getNeedsDescription());
            pstmt.setBoolean(7, receiver.isDataConsent());
            pstmt.setBoolean(8, receiver.isVerified());
            pstmt.setString(9, receiver.getVerificationStatus());
            pstmt.setInt(10, receiver.getReceivedDonations());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar receptor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Receiver> getAllReceivers() {
        List<Receiver> receivers = new ArrayList<>();
        String sql = "SELECT u.*, r.family_size, r.children, r.economic_situation, r.needs, r.needs_description, " +
                    "r.data_consent, r.verified, r.verification_status, r.verification_date, r.verified_by, r.received_donations " +
                    "FROM usuarios u INNER JOIN receptores r ON u.id = r.user_id " +
                    "WHERE u.user_type = 'receptor' ORDER BY u.registration_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                receivers.add(mapReceiver(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener receptores: " + e.getMessage());
            e.printStackTrace();
        }
        return receivers;
    }
    
    public Receiver getReceiverByUserId(int userId) {
        String sql = "SELECT u.*, r.family_size, r.children, r.economic_situation, r.needs, r.needs_description, " +
                    "r.data_consent, r.verified, r.verification_status, r.verification_date, r.verified_by, r.received_donations " +
                    "FROM usuarios u INNER JOIN receptores r ON u.id = r.user_id " +
                    "WHERE u.id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapReceiver(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener receptor: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateReceiver(Receiver receiver) {
        String sql = "UPDATE receptores SET family_size = ?, children = ?, economic_situation = ?, needs = ?, " +
                    "needs_description = ?, data_consent = ?, verified = ?, verification_status = ?, " +
                    "verification_date = ?, verified_by = ?, received_donations = ? WHERE user_id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, receiver.getFamilySize());
            pstmt.setInt(2, receiver.getChildren());
            pstmt.setString(3, receiver.getEconomicSituation());
            pstmt.setString(4, String.join(",", receiver.getNeeds()));
            pstmt.setString(5, receiver.getNeedsDescription());
            pstmt.setBoolean(6, receiver.isDataConsent());
            pstmt.setBoolean(7, receiver.isVerified());
            pstmt.setString(8, receiver.getVerificationStatus());
            pstmt.setDate(9, receiver.getVerificationDate() != null ? 
                new java.sql.Date(receiver.getVerificationDate().getTime()) : null);
            pstmt.setString(10, receiver.getVerifiedBy());
            pstmt.setInt(11, receiver.getReceivedDonations());
            pstmt.setInt(12, receiver.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar receptor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteReceiver(int userId) {
        String sql = "DELETE FROM receptores WHERE user_id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar receptor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        
        // Mapear campos de usuario
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
        receiver.setRegistrationDate(rs.getTimestamp("registration_date"));
        receiver.setActive(rs.getBoolean("active"));
        receiver.setNotificationsEnabled(rs.getBoolean("notifications_enabled"));
        
        // Mapear campos específicos de receptor
        receiver.setFamilySize(rs.getInt("family_size"));
        receiver.setChildren(rs.getInt("children"));
        receiver.setEconomicSituation(rs.getString("economic_situation"));
        
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