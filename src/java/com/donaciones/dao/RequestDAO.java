package com.donaciones.dao;

import com.donaciones.models.Request;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {
    
    public List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes ORDER BY request_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                requests.add(mapRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public List<Request> getRequestsByUser(String username) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes WHERE requested_by = ? ORDER BY request_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public boolean addRequest(Request request) {
        String sql = "INSERT INTO solicitudes (type, description, location, requested_by, priority) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, request.getType());
            pstmt.setString(2, request.getDescription());
            pstmt.setString(3, request.getLocation());
            pstmt.setString(4, request.getRequestedBy());
            pstmt.setInt(5, request.getPriority());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRequest(Request request) {
        String sql = "UPDATE solicitudes SET status=?, assigned_to=?, notes=?, completion_date=? WHERE id=?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, request.getStatus());
            pstmt.setString(2, request.getAssignedTo());
            pstmt.setString(3, request.getNotes());
            pstmt.setDate(4, request.getCompletionDate() != null ? 
                new java.sql.Date(request.getCompletionDate().getTime()) : null);
            pstmt.setInt(5, request.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // MÉTODOS FALTANTES - AGREGADOS PERO MANTENIENDO LA ESTRUCTURA ORIGINAL
    public Request getRequest(int id) {
        String sql = "SELECT * FROM solicitudes WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapRequest(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Request> getRequestsByStatus(String status) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes WHERE status = ? ORDER BY request_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    private Request mapRequest(ResultSet rs) throws SQLException {
        Request request = new Request();
        request.setId(rs.getInt("id"));
        request.setType(rs.getString("type"));
        request.setDescription(rs.getString("description"));
        request.setLocation(rs.getString("location"));
        request.setStatus(rs.getString("status"));
        request.setRequestedBy(rs.getString("requested_by"));
        request.setRequestDate(rs.getTimestamp("request_date"));
        request.setAssignedTo(rs.getString("assigned_to"));
        request.setCompletionDate(rs.getDate("completion_date"));
        request.setNotes(rs.getString("notes"));
        request.setPriority(rs.getInt("priority"));
        return request;
    }
}