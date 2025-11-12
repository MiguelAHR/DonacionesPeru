package com.donaciones.dao;

import com.donaciones.models.Request;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {
    
    public List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        String sql = "CALL sp_get_all_requests()";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
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
        String sql = "CALL sp_get_requests_by_user(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, username);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public boolean addRequest(Request request) {
        String sql = "CALL sp_insert_request(?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, request.getType());
            cstmt.setString(2, request.getDescription());
            cstmt.setString(3, request.getLocation());
            cstmt.setString(4, request.getRequestedBy());
            cstmt.setInt(5, request.getPriority());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRequest(Request request) {
        String sql = "CALL sp_update_request(?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, request.getId());
            cstmt.setString(2, request.getType());
            cstmt.setString(3, request.getDescription());
            cstmt.setString(4, request.getLocation());
            cstmt.setString(5, request.getStatus());
            cstmt.setString(6, request.getAssignedTo());
            cstmt.setString(7, request.getNotes());
            cstmt.setDate(8, request.getCompletionDate() != null ? 
                new java.sql.Date(request.getCompletionDate().getTime()) : null);
            cstmt.setInt(9, request.getPriority());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Request getRequest(int id) {
        String sql = "CALL sp_get_request(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, id);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                return mapRequest(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateRequestStatus(int requestId, String newStatus) {
        String sql = "CALL sp_update_request_status(?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, requestId);
            cstmt.setString(2, newStatus);
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean assignRequestToEmployee(int requestId, String employeeUsername) {
        String sql = "CALL sp_assign_request_to_employee(?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, requestId);
            cstmt.setString(2, employeeUsername);
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteRequest(int requestId) {
        String sql = "CALL sp_delete_request(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, requestId);
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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