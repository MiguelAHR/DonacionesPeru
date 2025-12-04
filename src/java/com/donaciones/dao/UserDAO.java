package com.donaciones.dao;

import com.donaciones.models.User;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User authenticate(String username, String password) {
        String sql = "SELECT u.*, r.id as rol_id FROM usuarios u " +
                    "LEFT JOIN roles r ON u.rol_id = r.id " +
                    "WHERE u.username = ? AND u.password = ? AND u.active = 1";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public User getUserByUsername(String username) {
        String sql = "SELECT u.*, r.id as rol_id FROM usuarios u " +
                    "LEFT JOIN roles r ON u.rol_id = r.id " +
                    "WHERE u.username = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario por username: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public User getUserById(int id) {
        String sql = "SELECT u.*, r.id as rol_id FROM usuarios u " +
                    "LEFT JOIN roles r ON u.rol_id = r.id " +
                    "WHERE u.id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.id as rol_id FROM usuarios u " +
                    "LEFT JOIN roles r ON u.rol_id = r.id " +
                    "ORDER BY u.registration_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo todos los usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    public boolean addUser(User user) {
        String sql = "INSERT INTO usuarios (username, password, user_type, first_name, " +
                    "last_name, email, phone, dni, birth_date, region, district, " +
                    "address, registration_date, active, notifications_enabled, rol_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
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
            pstmt.setTimestamp(13, new Timestamp(user.getRegistrationDate().getTime()));
            pstmt.setBoolean(14, user.isActive());
            pstmt.setBoolean(15, user.isNotificationsEnabled());
            pstmt.setObject(16, user.getRolId(), Types.INTEGER);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error agregando usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE usuarios SET username = ?, password = ?, user_type = ?, " +
                    "first_name = ?, last_name = ?, email = ?, phone = ?, dni = ?, " +
                    "birth_date = ?, region = ?, district = ?, address = ?, " +
                    "active = ?, notifications_enabled = ?, rol_id = ? " +
                    "WHERE id = ?";
        
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
            pstmt.setBoolean(13, user.isActive());
            pstmt.setBoolean(14, user.isNotificationsEnabled());
            pstmt.setObject(15, user.getRolId(), Types.INTEGER);
            pstmt.setInt(16, user.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // NUEVO MÉTODO: Actualizar solo la imagen de perfil
    public boolean updateUserProfileImage(int userId, String profileImage) {
        String sql = "UPDATE usuarios SET profile_image = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, profileImage);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando imagen de perfil: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public List<String> getEmployeeUsernames() {
        List<String> employees = new ArrayList<>();
        String sql = "SELECT username FROM usuarios WHERE user_type = 'empleado' AND active = 1";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                employees.add(rs.getString("username"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo nombres de empleados: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    public List<String[]> getEmployeesWithNames() {
        List<String[]> employees = new ArrayList<>();
        String sql = "SELECT username, first_name, last_name FROM usuarios " +
                    "WHERE user_type = 'empleado' AND active = 1";
        
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
            System.err.println("Error obteniendo empleados con nombres: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    // NUEVO MÉTODO: Obtener usuarios por tipo
    public List<User> getUsersByType(String userType) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.id as rol_id FROM usuarios u " +
                    "LEFT JOIN roles r ON u.rol_id = r.id " +
                    "WHERE u.user_type = ? AND u.active = 1 " +
                    "ORDER BY u.registration_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuarios por tipo: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    // NUEVO MÉTODO: Contar usuarios por tipo
    public int countUsersByType(String userType) {
        String sql = "SELECT COUNT(*) as count FROM usuarios WHERE user_type = ? AND active = 1";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error contando usuarios por tipo: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // NUEVO MÉTODO: Buscar usuarios
    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.id as rol_id FROM usuarios u " +
                    "LEFT JOIN roles r ON u.rol_id = r.id " +
                    "WHERE (u.username LIKE ? OR u.first_name LIKE ? OR u.last_name LIKE ? " +
                    "OR u.email LIKE ? OR u.dni LIKE ?) AND u.active = 1 " +
                    "ORDER BY u.registration_date DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);
            pstmt.setString(4, likeTerm);
            pstmt.setString(5, likeTerm);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error buscando usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
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
        user.setRolId(rs.getInt("rol_id"));
        
        // NUEVO: Manejar imagen de perfil (si la columna existe)
        try {
            String profileImage = rs.getString("profile_image");
            user.setProfileImage(profileImage);
        } catch (SQLException e) {
            // Si la columna no existe, usar imagen por defecto
            user.setProfileImage("/images/default-profile.png");
        }
        
        return user;
    }
}