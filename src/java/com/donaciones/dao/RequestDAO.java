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
            System.out.println("ERROR RequestDAO - Error obteniendo todas las solicitudes: " + e.getMessage());
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
            System.out.println("ERROR RequestDAO - Error obteniendo solicitudes por usuario: " + e.getMessage());
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
            System.out.println("ERROR RequestDAO - Error insertando solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRequest(Request request) {
        String sql = "UPDATE solicitudes SET type=?, description=?, location=?, status=?, assigned_to=?, notes=?, completion_date=?, priority=? WHERE id=?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, request.getType());
            pstmt.setString(2, request.getDescription());
            pstmt.setString(3, request.getLocation());
            pstmt.setString(4, request.getStatus());
            pstmt.setString(5, request.getAssignedTo());
            pstmt.setString(6, request.getNotes());
            pstmt.setDate(7, request.getCompletionDate() != null ? 
                new java.sql.Date(request.getCompletionDate().getTime()) : null);
            pstmt.setInt(8, request.getPriority());
            pstmt.setInt(9, request.getId());
            
            int result = pstmt.executeUpdate();
            System.out.println("DEBUG RequestDAO - Solicitud actualizada, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error actualizando solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
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
            System.out.println("ERROR RequestDAO - Error obteniendo solicitud: " + e.getMessage());
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
            System.out.println("ERROR RequestDAO - Error obteniendo solicitudes por estado: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    // MÉTODO NUEVO: Filtrar solicitudes
    public List<Request> getRequestsByFilters(String status, String type, String location) {
        List<Request> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM solicitudes WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        try {
            if (status != null && !status.isEmpty()) {
                sql.append(" AND status = ?");
                parameters.add(status);
            }
            if (type != null && !type.isEmpty()) {
                sql.append(" AND type = ?");
                parameters.add(type);
            }
            if (location != null && !location.isEmpty()) {
                sql.append(" AND location = ?");
                parameters.add(location);
            }
            
            sql.append(" ORDER BY request_date DESC");

            System.out.println("DEBUG RequestDAO - Consulta con filtros: " + sql.toString());
            System.out.println("DEBUG RequestDAO - Parámetros: " + parameters);

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                for (int i = 0; i < parameters.size(); i++) {
                    pstmt.setObject(i + 1, parameters.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    requests.add(mapRequest(rs));
                }
                
                System.out.println("DEBUG RequestDAO - Solicitudes encontradas con filtros: " + requests.size());
            }
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error filtrando solicitudes: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    // MÉTODO NUEVO: Eliminar solicitud
    public boolean deleteRequest(int requestId) {
        System.out.println("DEBUG RequestDAO - Iniciando eliminación de solicitud ID: " + requestId);
        
        String sql = "DELETE FROM solicitudes WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, requestId);
            int result = pstmt.executeUpdate();
            
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

    // MÉTODO NUEVO: Actualizar estado
    public boolean updateRequestStatus(int requestId, String newStatus) {
        String sql = "UPDATE solicitudes SET status = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);
            
            int result = pstmt.executeUpdate();
            System.out.println("DEBUG RequestDAO - Estado actualizado, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error actualizando estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // MÉTODO NUEVO: Asignar empleado
    public boolean assignRequestToEmployee(int requestId, String employeeUsername) {
        String sql = "UPDATE solicitudes SET assigned_to = ?, status = 'in_progress' WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeUsername);
            pstmt.setInt(2, requestId);
            
            int result = pstmt.executeUpdate();
            System.out.println("DEBUG RequestDAO - Solicitud asignada a empleado, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error asignando solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // MÉTODO NUEVO: Obtener conteo por estado
    public int getRequestCountByStatus(String status) {
        String sql = "SELECT COUNT(*) as count FROM solicitudes WHERE status = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("ERROR RequestDAO - Error contando solicitudes por estado: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // MÉTODO NUEVO: Obtener solicitudes por empleado asignado
    public List<Request> getRequestsByEmployee(String employeeUsername) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes WHERE assigned_to = ? ORDER BY request_date DESC";
        
        System.out.println("DEBUG RequestDAO - Buscando solicitudes para empleado: " + employeeUsername);
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeUsername);
            ResultSet rs = pstmt.executeQuery();
            
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

    // MÉTODO NUEVO: Obtener solicitudes disponibles (sin asignar)
    public List<Request> getAvailableRequests() {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes WHERE assigned_to IS NULL OR assigned_to = '' ORDER BY request_date DESC";
        
        System.out.println("DEBUG RequestDAO - Buscando solicitudes disponibles");
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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

    // MÉTODO NUEVO: Filtrar solicitudes por empleado con filtros
    public List<Request> getRequestsByEmployeeWithFilters(String employeeUsername, String status, String type, String location) {
        List<Request> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM solicitudes WHERE assigned_to = ?");
        List<Object> parameters = new ArrayList<>();
        parameters.add(employeeUsername);

        try {
            if (status != null && !status.isEmpty()) {
                sql.append(" AND status = ?");
                parameters.add(status);
            }
            if (type != null && !type.isEmpty()) {
                sql.append(" AND type = ?");
                parameters.add(type);
            }
            if (location != null && !location.isEmpty()) {
                sql.append(" AND location = ?");
                parameters.add(location);
            }
            
            sql.append(" ORDER BY request_date DESC");

            System.out.println("DEBUG RequestDAO - Consulta con filtros para empleado: " + sql.toString());
            System.out.println("DEBUG RequestDAO - Parámetros: " + parameters);

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                for (int i = 0; i < parameters.size(); i++) {
                    pstmt.setObject(i + 1, parameters.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    requests.add(mapRequest(rs));
                }
                
                System.out.println("DEBUG RequestDAO - Solicitudes del empleado con filtros: " + requests.size());
            }
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