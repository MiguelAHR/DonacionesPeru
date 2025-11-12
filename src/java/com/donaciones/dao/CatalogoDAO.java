package com.donaciones.dao;

import com.donaciones.models.Catalogo;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {
    
    public List<Catalogo> getAllCatalogItems() {
        List<Catalogo> items = new ArrayList<>();
        String sql = "CALL sp_get_all_catalog_items()";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                items.add(mapCatalogo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public List<Catalogo> getAvailableItems() {
        List<Catalogo> items = new ArrayList<>();
        String sql = "CALL sp_get_available_items()";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                items.add(mapCatalogo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public Catalogo getCatalogoItem(int id) {
        String sql = "CALL sp_get_catalogo_item(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, id);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                return mapCatalogo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addCatalogoItem(Catalogo catalogo) {
        String sql = "CALL sp_add_catalogo_item(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, catalogo.getDonacionId());
            cstmt.setString(2, catalogo.getTitulo());
            cstmt.setString(3, catalogo.getDescripcion());
            cstmt.setString(4, catalogo.getTipo());
            cstmt.setInt(5, catalogo.getCantidad());
            cstmt.setString(6, catalogo.getCondicion());
            cstmt.setString(7, catalogo.getUbicacion());
            cstmt.setString(8, catalogo.getDonante());
            cstmt.setTimestamp(9, new Timestamp(catalogo.getFechaDisponible().getTime()));
            cstmt.setString(10, catalogo.getEstado());
            cstmt.setString(11, catalogo.getImagen());
            cstmt.setString(12, catalogo.getTagsAsString());
            cstmt.setInt(13, catalogo.getPrioridad());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCatalogoItem(Catalogo catalogo) {
        String sql = "CALL sp_update_catalogo_item(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, catalogo.getId());
            cstmt.setString(2, catalogo.getTitulo());
            cstmt.setString(3, catalogo.getDescripcion());
            cstmt.setString(4, catalogo.getTipo());
            cstmt.setInt(5, catalogo.getCantidad());
            cstmt.setString(6, catalogo.getCondicion());
            cstmt.setString(7, catalogo.getUbicacion());
            cstmt.setString(8, catalogo.getDonante());
            cstmt.setTimestamp(9, new Timestamp(catalogo.getFechaDisponible().getTime()));
            cstmt.setString(10, catalogo.getEstado());
            cstmt.setString(11, catalogo.getImagen());
            cstmt.setString(12, catalogo.getTagsAsString());
            cstmt.setInt(13, catalogo.getPrioridad());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateItemStatus(int id, String estado) {
        String sql = "CALL sp_update_item_status(?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, id);
            cstmt.setString(2, estado);
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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