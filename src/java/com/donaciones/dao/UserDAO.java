package com.donaciones.dao;

import com.donaciones.models.User;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ? AND active = TRUE";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY registration_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public boolean addUser(User user) {
        String sql = "INSERT INTO usuarios (username, password, user_type, first_name, last_name, email, phone, dni, birth_date, region, district, address, notifications_enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getUserType());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getPhone());
            pstmt.setString(8, user.getDni());
            pstmt.setDate(9, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null);
            pstmt.setString(10, user.getRegion());
            pstmt.setString(11, user.getDistrict());
            pstmt.setString(12, user.getAddress());
            pstmt.setBoolean(13, user.isNotificationsEnabled());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) as total FROM usuarios";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getUsersByType(String userType) {
        String sql = "SELECT COUNT(*) as total FROM usuarios WHERE user_type = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // NUEVO MÉTODO: Obtener lista de empleados con nombres completos
    public List<String[]> getEmployeesWithNames() {
        List<String[]> employees = new ArrayList<>();
        String sql = "SELECT username, first_name, last_name FROM usuarios WHERE user_type = 'empleado' ORDER BY first_name, last_name";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String[] employee = new String[3];
                employee[0] = rs.getString("username");
                employee[1] = rs.getString("first_name");
                employee[2] = rs.getString("last_name");
                employees.add(employee);
            }
            
        } catch (SQLException e) {
            System.out.println("ERROR UserDAO - Error obteniendo empleados con nombres: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    // NUEVO MÉTODO: Obtener solo usernames de empleados
    public List<String> getEmployeeUsernames() {
        List<String> employees = new ArrayList<>();
        String sql = "SELECT username FROM usuarios WHERE user_type = 'empleado' ORDER BY username";
        
        System.out.println("DEBUG UserDAO - Obteniendo lista de empleados");

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                employees.add(rs.getString("username"));
                count++;
            }
            System.out.println("DEBUG UserDAO - Empleados encontrados: " + count);
            
        } catch (SQLException e) {
            System.out.println("ERROR UserDAO - Error obteniendo empleados: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setUserType(rs.getString("user_type"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setDni(rs.getString("dni"));
        user.setBirthDate(rs.getDate("birth_date"));
        user.setRegion(rs.getString("region"));
        user.setDistrict(rs.getString("district"));
        user.setAddress(rs.getString("address"));
        user.setRegistrationDate(rs.getTimestamp("registration_date"));
        user.setActive(rs.getBoolean("active"));
        user.setNotificationsEnabled(rs.getBoolean("notifications_enabled"));
        return user;
    }
}