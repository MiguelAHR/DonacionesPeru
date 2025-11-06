package com.donaciones.dao;

import com.donaciones.models.Role;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles WHERE activo = TRUE ORDER BY nombre";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                roles.add(mapRole(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener roles: " + e.getMessage());
            e.printStackTrace();
        }
        return roles;
    }
    
    public Role getRoleById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapRole(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener rol: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public Role getRoleByNombre(String nombre) {
        String sql = "SELECT * FROM roles WHERE nombre = ? AND activo = TRUE";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapRole(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener rol por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addRole(Role role) {
        String sql = "INSERT INTO roles (nombre, descripcion, permisos, activo) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.getNombre());
            pstmt.setString(2, role.getDescripcion());
            pstmt.setString(3, role.getPermisosAsString());
            pstmt.setBoolean(4, role.isActivo());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar rol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRole(Role role) {
        String sql = "UPDATE roles SET nombre=?, descripcion=?, permisos=?, activo=? WHERE id=?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.getNombre());
            pstmt.setString(2, role.getDescripcion());
            pstmt.setString(3, role.getPermisosAsString());
            pstmt.setBoolean(4, role.isActivo());
            pstmt.setInt(5, role.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar rol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteRole(int id) {
        String sql = "UPDATE roles SET activo = FALSE WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar rol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private Role mapRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("id"));
        role.setNombre(rs.getString("nombre"));
        role.setDescripcion(rs.getString("descripcion"));
        
        String permisosStr = rs.getString("permisos");
        if (permisosStr != null) {
            role.setPermisosFromString(permisosStr);
        }
        
        role.setActivo(rs.getBoolean("activo"));
        role.setCreatedAt(rs.getTimestamp("created_at"));
        return role;
    }
}