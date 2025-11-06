package com.donaciones.dao;

import com.donaciones.models.Reporte;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {
    
    public List<Reporte> getAllReports() {
        List<Reporte> reports = new ArrayList<>();
        String sql = "SELECT * FROM reportes ORDER BY fecha_generacion DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reports.add(mapReporte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reportes: " + e.getMessage());
            e.printStackTrace();
        }
        return reports;
    }
    
    public Reporte getReportById(int id) {
        String sql = "SELECT * FROM reportes WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapReporte(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reporte: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Reporte> getReportsByUser(String username) {
        List<Reporte> reports = new ArrayList<>();
        String sql = "SELECT * FROM reportes WHERE generado_por = ? ORDER BY fecha_generacion DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reports.add(mapReporte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reportes por usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return reports;
    }
    
    public boolean addReport(Reporte reporte) {
        String sql = "INSERT INTO reportes (titulo, descripcion, tipo_reporte, generado_por, parametros, " +
                    "datos, formato, estado, ruta_archivo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, reporte.getTitulo());
            pstmt.setString(2, reporte.getDescripcion());
            pstmt.setString(3, reporte.getTipoReporte());
            pstmt.setString(4, reporte.getGeneradoPor());
            pstmt.setString(5, reporte.getParametros().toString());
            pstmt.setString(6, reporte.getDatos());
            pstmt.setString(7, reporte.getFormato());
            pstmt.setString(8, reporte.getEstado());
            pstmt.setString(9, reporte.getRutaArchivo());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar reporte: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateReport(Reporte reporte) {
        String sql = "UPDATE reportes SET titulo=?, descripcion=?, tipo_reporte=?, parametros=?, " +
                    "datos=?, formato=?, estado=?, ruta_archivo=? WHERE id=?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, reporte.getTitulo());
            pstmt.setString(2, reporte.getDescripcion());
            pstmt.setString(3, reporte.getTipoReporte());
            pstmt.setString(4, reporte.getParametros().toString());
            pstmt.setString(5, reporte.getDatos());
            pstmt.setString(6, reporte.getFormato());
            pstmt.setString(7, reporte.getEstado());
            pstmt.setString(8, reporte.getRutaArchivo());
            pstmt.setInt(9, reporte.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar reporte: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateReportStatus(int id, String estado, String rutaArchivo) {
        String sql = "UPDATE reportes SET estado = ?, ruta_archivo = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            pstmt.setString(2, rutaArchivo);
            pstmt.setInt(3, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del reporte: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Reporte> getReportsByType(String tipo) {
        List<Reporte> reports = new ArrayList<>();
        String sql = "SELECT * FROM reportes WHERE tipo_reporte = ? ORDER BY fecha_generacion DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tipo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reports.add(mapReporte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reportes por tipo: " + e.getMessage());
            e.printStackTrace();
        }
        return reports;
    }
    
    public List<Reporte> getRecentReports(int limit) {
        List<Reporte> reports = new ArrayList<>();
        String sql = "SELECT * FROM reportes ORDER BY fecha_generacion DESC LIMIT ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reports.add(mapReporte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reportes recientes: " + e.getMessage());
            e.printStackTrace();
        }
        return reports;
    }
    
    private Reporte mapReporte(ResultSet rs) throws SQLException {
        Reporte reporte = new Reporte();
        reporte.setId(rs.getInt("id"));
        reporte.setTitulo(rs.getString("titulo"));
        reporte.setDescripcion(rs.getString("descripcion"));
        reporte.setTipoReporte(rs.getString("tipo_reporte"));
        reporte.setFechaGeneracion(rs.getTimestamp("fecha_generacion"));
        reporte.setGeneradoPor(rs.getString("generado_por"));
        // Los parámetros se mapearían de string a Map (se puede implementar con JSON)
        reporte.setDatos(rs.getString("datos"));
        reporte.setFormato(rs.getString("formato"));
        reporte.setEstado(rs.getString("estado"));
        reporte.setRutaArchivo(rs.getString("ruta_archivo"));
        return reporte;
    }
}