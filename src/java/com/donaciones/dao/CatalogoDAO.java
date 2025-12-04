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
            System.err.println("Error obteniendo todos los items del catálogo: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    public List<Catalogo> getAvailableItems() {
        List<Catalogo> items = new ArrayList<>();
        String sql = "SELECT * FROM catalogo WHERE estado = 'disponible' ORDER BY prioridad, fecha_ingreso DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(mapCatalogo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo items disponibles: " + e.getMessage());
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
            System.err.println("Error obteniendo item del catálogo ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addCatalogoItem(Catalogo catalogo) {
        String sql = "INSERT INTO catalogo (donacion_id, titulo, descripcion, tipo, cantidad, " +
                    "condicion, ubicacion, donante, fecha_ingreso, fecha_disponible, " +
                    "estado, imagen, tags, prioridad) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, catalogo.getDonacionId());
            pstmt.setString(2, catalogo.getTitulo());
            pstmt.setString(3, catalogo.getDescripcion());
            pstmt.setString(4, catalogo.getTipo());
            pstmt.setInt(5, catalogo.getCantidad());
            pstmt.setString(6, catalogo.getCondicion());
            pstmt.setString(7, catalogo.getUbicacion());
            pstmt.setString(8, catalogo.getDonante());
            pstmt.setTimestamp(9, new Timestamp(catalogo.getFechaIngreso().getTime()));
            pstmt.setTimestamp(10, new Timestamp(catalogo.getFechaDisponible().getTime()));
            pstmt.setString(11, catalogo.getEstado());
            pstmt.setString(12, catalogo.getImagen());
            pstmt.setString(13, catalogo.getTagsAsString());
            pstmt.setInt(14, catalogo.getPrioridad());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        catalogo.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error agregando item al catálogo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateCatalogoItem(Catalogo catalogo) {
        String sql = "UPDATE catalogo SET " +
                    "titulo = ?, descripcion = ?, tipo = ?, cantidad = ?, " +
                    "condicion = ?, ubicacion = ?, donante = ?, fecha_disponible = ?, " +
                    "estado = ?, imagen = ?, tags = ?, prioridad = ? " +
                    "WHERE id = ?";
        
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
            System.err.println("Error actualizando item del catálogo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateItemStatus(int id, String estado) {
        String sql = "UPDATE catalogo SET estado = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando estado del item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteCatalogoItem(int id) {
        String sql = "DELETE FROM catalogo WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando item del catálogo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // MÉTODO NUEVO: Obtener solo la ruta de la imagen
    public String getImagenPath(int itemId) {
        String sql = "SELECT imagen FROM catalogo WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("imagen");
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo ruta de imagen: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // MÉTODO NUEVO: Actualizar solo la imagen
    public boolean updateImagen(int itemId, String imagenPath) {
        String sql = "UPDATE catalogo SET imagen = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, imagenPath);
            pstmt.setInt(2, itemId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando imagen: " + e.getMessage());
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