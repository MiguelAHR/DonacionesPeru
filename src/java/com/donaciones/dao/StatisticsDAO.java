package com.donaciones.dao;

import conexion.Conexion;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StatisticsDAO {
    
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM usuarios) as total_users, " +
                "(SELECT COUNT(*) FROM donadores) as total_donors, " +
                "(SELECT COUNT(*) FROM receptores) as total_receivers, " +
                "(SELECT COUNT(*) FROM donaciones) as total_donations, " +
                "(SELECT COUNT(*) FROM donaciones WHERE status = 'pending') as pending_donations, " +
                "(SELECT COUNT(*) FROM solicitudes WHERE status = 'pending') as pending_requests";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                stats.put("total_users", rs.getInt("total_users"));
                stats.put("total_donors", rs.getInt("total_donors"));
                stats.put("total_receivers", rs.getInt("total_receivers"));
                stats.put("total_donations", rs.getInt("total_donations"));
                stats.put("pending_donations", rs.getInt("pending_donations"));
                stats.put("pending_requests", rs.getInt("pending_requests"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    public Map<String, Long> getDonationsByType() {
        Map<String, Long> donationsByType = new HashMap<>();
        String sql = "SELECT type, COUNT(*) as count FROM donaciones GROUP BY type";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                donationsByType.put(rs.getString("type"), rs.getLong("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donationsByType;
    }
    
    public Map<String, Long> getDonationsByRegion() {
        Map<String, Long> donationsByRegion = new HashMap<>();
        String sql = "SELECT location, COUNT(*) as count FROM donaciones GROUP BY location";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                donationsByRegion.put(rs.getString("location"), rs.getLong("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donationsByRegion;
    }
}