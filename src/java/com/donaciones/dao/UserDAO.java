package com.donaciones.dao;

import com.donaciones.models.User;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User authenticate(String username, String password) {
        String sql = "CALL sp_authenticate_user(?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, username);
            cstmt.setString(2, password);
            
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public User getUserByUsername(String username) {
        String sql = "CALL sp_get_user_by_username(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, username);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public User getUserById(int id) {
        String sql = "CALL sp_get_user_by_id(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, id);
            ResultSet rs = cstmt.executeQuery();
            
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
        String sql = "CALL sp_get_all_users()";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public boolean addUser(User user) {
        String sql = "CALL sp_insert_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, user.getUsername());
            cstmt.setString(2, user.getPassword());
            cstmt.setString(3, user.getUserType());
            cstmt.setString(4, user.getFirstName());
            cstmt.setString(5, user.getLastName());
            cstmt.setString(6, user.getEmail());
            cstmt.setString(7, user.getPhone());
            cstmt.setString(8, user.getDni());
            cstmt.setDate(9, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null);
            cstmt.setString(10, user.getRegion());
            cstmt.setString(11, user.getDistrict());
            cstmt.setString(12, user.getAddress());
            cstmt.setBoolean(13, user.isNotificationsEnabled());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateUser(User user) {
        String sql = "CALL sp_update_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, user.getId());
            cstmt.setString(2, user.getUsername());
            cstmt.setString(3, user.getPassword());
            cstmt.setString(4, user.getUserType());
            cstmt.setString(5, user.getFirstName());
            cstmt.setString(6, user.getLastName());
            cstmt.setString(7, user.getEmail());
            cstmt.setString(8, user.getPhone());
            cstmt.setString(9, user.getDni());
            cstmt.setDate(10, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null);
            cstmt.setString(11, user.getRegion());
            cstmt.setString(12, user.getDistrict());
            cstmt.setString(13, user.getAddress());
            cstmt.setBoolean(14, user.isActive());
            cstmt.setBoolean(15, user.isNotificationsEnabled());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteUser(int id) {
        String sql = "CALL sp_delete_user(?)";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, id);
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> getEmployeeUsernames() {
        List<String> employees = new ArrayList<>();
        String sql = "CALL sp_get_employee_usernames()";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(rs.getString("username"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public List<String[]> getEmployeesWithNames() {
        List<String[]> employees = new ArrayList<>();
        String sql = "CALL sp_get_employees_with_names()";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                String[] employee = new String[3];
                employee[0] = rs.getString("username");
                employee[1] = rs.getString("first_name");
                employee[2] = rs.getString("last_name");
                employees.add(employee);
            }
            
        } catch (SQLException e) {
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