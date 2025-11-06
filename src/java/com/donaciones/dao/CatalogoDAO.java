package com.donaciones.dao;

import com.donaciones.models.Catalogo;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {
    
    public List<Catalogo> getAllCatalogItems() {
        List<Catalogo> items = new ArrayList<>();
        String sql = "SELECT * FROM catalogo ORDER BY fecha_ingreso DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(mapCatalogo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener items del catálogo: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    public List<Catalogo> getAvailableItems() {
        List<Catalogo> items = new ArrayList<>();
        String sql = "SELECT * FROM catalogo WHERE estado = 'disponible' ORDER BY prioridad DESC, fecha_ingreso DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(mapCatalogo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener items disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    public Catalogo getCatalogoItem(int id) {
        String sql = "SELECT * FROM catalogo WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapCatalogo(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener item del catálogo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addCatalogoItem(Catalogo catalogo) {
        String sql = "INSERT INTO catalogo (donacion_id, titulo, descripcion, tipo, cantidad, condicion, " +
                    "ubicacion, donante, fecha_disponible, estado, imagen, tags, prioridad) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, catalogo.getDonacionId());
            pstmt.setString(2, catalogo.getTitulo());
            pstmt.setString(3, catalogo.getDescripcion());
            pstmt.setString(4, catalogo.getTipo());
            pstmt.setInt(5, catalogo.getCantidad());
            pstmt.setString(6, catalogo.getCondicion());
            pstmt.setString(7, catalogo.getUbicacion());
            pstmt.setString(8, catalogo.getDonante());
            pstmt.setTimestamp(9, new Timestamp(catalogo.getFechaDisponible().getTime()));
            pstmt.setString(10, catalogo.getEstado());
            pstmt.setString(11, catalogo.getImagen());
            pstmt.setString(12, catalogo.getTagsAsString());
            pstmt.setInt(13, catalogo.getPrioridad());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar item al catálogo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCatalogoItem(Catalogo catalogo) {
        String sql = "UPDATE catalogo SET titulo=?, descripcion=?, tipo=?, cantidad=?, condicion=?, " +
                    "ubicacion=?, donante=?, fecha_disponible=?, estado=?, imagen=?, tags=?, prioridad=? " +
                    "WHERE id=?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, catalogo.getTitulo());
            pstmt.setString(2, catalogo.getDescripcion());
            pstmt.setString(3, catalogo.getTipo());
            pstmt.setInt(4, catalogo.getCantidad());
            pstmt.setString(5, catalogo.getCondicion());
            pstmt.setString(6, catalogo.getUbicacion());
            pstmt.setString(7, catalogo.getDonante());
            pstmt.setTimestamp(8, new Timestamp(catalogo.getFechaDisponible().getTime()));
            pstmt.setString(9, catalogo.getEstado());
            pstmt.setString(10, catalogo.getImagen());
            pstmt.setString(11, catalogo.getTagsAsString());
            pstmt.setInt(12, catalogo.getPrioridad());
            pstmt.setInt(13, catalogo.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar item del catálogo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateItemStatus(int id, String estado) {
        String sql = "UPDATE catalogo SET estado = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Catalogo> searchCatalogItems(String tipo, String ubicacion, String estado) {
        List<Catalogo> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM catalogo WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        
        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND tipo = ?");
            parameters.add(tipo);
        }
        if (ubicacion != null && !ubicacion.isEmpty()) {
            sql.append(" AND ubicacion = ?");
            parameters.add(ubicacion);
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND estado = ?");
            parameters.add(estado);
        }
        
        sql.append(" ORDER BY prioridad DESC, fecha_ingreso DESC");
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(mapCatalogo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar items del catálogo: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    public boolean itemExistsForDonation(int donacionId) {
        String sql = "SELECT COUNT(*) as count FROM catalogo WHERE donacion_id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, donacionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private Catalogo mapCatalogo(ResultSet rs) throws SQLException {
        Catalogo catalogo = new Catalogo();
        catalogo.setId(rs.getInt("id"));
        catalogo.setDonacionId(rs.getInt("donacion_id"));
        catalogo.setTitulo(rs.getString("titulo"));
        catalogo.setDescripcion(rs.getString("descripcion"));
        catalogo.setTipo(rs.getString("tipo"));
        catalogo.setCantidad(rs.getInt("cantidad"));
        catalogo.setCondicion(rs.getString("condicion"));
        catalogo.setUbicacion(rs.getString("ubicacion"));
        catalogo.setDonante(rs.getString("donante"));
        catalogo.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
        catalogo.setFechaDisponible(rs.getTimestamp("fecha_disponible"));
        catalogo.setEstado(rs.getString("estado"));
        catalogo.setImagen(rs.getString("imagen"));
        
        String tags = rs.getString("tags");
        if (tags != null) {
            catalogo.setTagsFromString(tags);
        }
        
        catalogo.setPrioridad(rs.getInt("prioridad"));
        return catalogo;
    }
}