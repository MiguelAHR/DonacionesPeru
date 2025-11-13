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
            System.out.println("ERROR RequestDAO - Error obteniendo todas las solicitudes: " + e.getMessage());
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
            System.out.println("ERROR RequestDAO - Error obteniendo solicitudes por usuario: " + e.getMessage());
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
            System.out.println("ERROR RequestDAO - Error insertando solicitud: " + e.getMessage());
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
            
            int result = cstmt.executeUpdate();
            System.out.println("DEBUG RequestDAO - Solicitud actualizada, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error actualizando solicitud: " + e.getMessage());
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
            System.out.println("ERROR RequestDAO - Error obteniendo solicitud: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Request> getRequestsByStatus(String status) {
        List<Request> requests = new ArrayList<>();
        String sql = "CALL sp_get_requests_by_status(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, status);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapRequest(rs));
            }
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error obteniendo solicitudes por estado: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    public List<Request> getRequestsByFilters(String status, String type, String location) {
        List<Request> requests = new ArrayList<>();
        String sql = "CALL sp_get_requests_by_filters(?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, status);
            cstmt.setString(2, type);
            cstmt.setString(3, location);
            
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                requests.add(mapRequest(rs));
            }
            
            System.out.println("DEBUG RequestDAO - Solicitudes encontradas con filtros: " + requests.size());
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error filtrando solicitudes: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    public boolean deleteRequest(int requestId) {
        System.out.println("DEBUG RequestDAO - Iniciando eliminación de solicitud ID: " + requestId);
        
        String sql = "CALL sp_delete_request(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, requestId);
            int result = cstmt.executeUpdate();
            
            System.out.println("DEBUG RequestDAO - Resultado de eliminación - Filas afectadas: " + result);
            
            if (result > 0) {
                System.out.println("DEBUG RequestDAO - ÉXITO: Solicitud " + requestId + " eliminada correctamente");
                return true;
            } else {
                System.out.println("DEBUG RequestDAO - FALLO: No se pudo eliminar solicitud " + requestId);
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error SQL eliminando solicitud " + requestId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRequestStatus(int requestId, String newStatus) {
        String sql = "CALL sp_update_request_status(?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, requestId);
            cstmt.setString(2, newStatus);
            
            int result = cstmt.executeUpdate();
            System.out.println("DEBUG RequestDAO - Estado actualizado, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error actualizando estado: " + e.getMessage());
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
            
            int result = cstmt.executeUpdate();
            System.out.println("DEBUG RequestDAO - Solicitud asignada a empleado, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error asignando solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getRequestCountByStatus(String status) {
        String sql = "CALL sp_get_requests_by_status(?)";
        int count = 0;
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, status);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error contando solicitudes por estado: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    public List<Request> getRequestsByEmployee(String employeeUsername) {
        List<Request> requests = new ArrayList<>();
        String sql = "CALL sp_get_requests_by_employee(?)";
        
        System.out.println("DEBUG RequestDAO - Buscando solicitudes para empleado: " + employeeUsername);
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, employeeUsername);
            ResultSet rs = cstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                requests.add(mapRequest(rs));
                count++;
            }
            
            System.out.println("DEBUG RequestDAO - Solicitudes del empleado encontradas: " + count);
            
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error obteniendo solicitudes por empleado: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    public List<Request> getAvailableRequests() {
        List<Request> requests = new ArrayList<>();
        String sql = "CALL sp_get_available_requests()";
        
        System.out.println("DEBUG RequestDAO - Buscando solicitudes disponibles");
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                requests.add(mapRequest(rs));
                count++;
            }
            
            System.out.println("DEBUG RequestDAO - Solicitudes disponibles encontradas: " + count);
            
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error obteniendo solicitudes disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    public List<Request> getRequestsByEmployeeWithFilters(String employeeUsername, String status, String type, String location) {
        List<Request> requests = new ArrayList<>();
        String sql = "CALL sp_get_requests_by_employee_with_filters(?, ?, ?, ?)";

        System.out.println("DEBUG RequestDAO - Consulta con filtros para empleado: " + employeeUsername);
        System.out.println("DEBUG RequestDAO - Filtros - Status: " + status + ", Type: " + type + ", Location: " + location);

        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, employeeUsername);
            cstmt.setString(2, status);
            cstmt.setString(3, type);
            cstmt.setString(4, location);

            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                requests.add(mapRequest(rs));
            }
            
            System.out.println("DEBUG RequestDAO - Solicitudes del empleado con filtros: " + requests.size());
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error filtrando solicitudes del empleado: " + e.getMessage());
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