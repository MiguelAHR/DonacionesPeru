package com.donaciones.dao;

import com.donaciones.models.ProfileImage;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileImageDAO {

    // Obtener imagen activa por usuario
    public ProfileImage getActiveImageByUser(int userId) {
        String sql = "CALL sp_get_active_profile_image(?)";
        ProfileImage image = null;

        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, userId);
            ResultSet rs = cstmt.executeQuery();

            if (rs.next()) {
                image = mapResultSetToProfileImage(rs);
            }
            
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error en getActiveImageByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return image;
    }

    // Subir nueva imagen (desactivar las anteriores)
    public boolean uploadImage(ProfileImage image) {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = Conexion.getConnection();
            String sql = "CALL sp_upload_profile_image(?, ?)";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, image.getUserId());
            cstmt.setString(2, image.getImageUrl());

            int rowsAffected = cstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error en uploadImage: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (cstmt != null) cstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error cerrando conexiones: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Obtener historial de imágenes del usuario
    public List<ProfileImage> getUserImageHistory(int userId) {
        List<ProfileImage> images = new ArrayList<>();
        String sql = "CALL sp_get_user_image_history(?)";

        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, userId);
            ResultSet rs = cstmt.executeQuery();

            while (rs.next()) {
                images.add(mapResultSetToProfileImage(rs));
            }
            
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error en getUserImageHistory: " + e.getMessage());
            e.printStackTrace();
        }
        return images;
    }

    // Eliminar imagen (soft delete)
    public boolean deleteImage(int imageId) {
        String sql = "CALL sp_delete_profile_image(?)";

        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, imageId);
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en deleteImage: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private ProfileImage mapResultSetToProfileImage(ResultSet rs) throws SQLException {
        ProfileImage image = new ProfileImage();
        image.setId(rs.getInt("id"));
        image.setUserId(rs.getInt("user_id"));
        image.setImageUrl(rs.getString("image_url"));
        image.setUploadedAt(rs.getTimestamp("uploaded_at"));
        image.setActive(rs.getBoolean("is_active"));
        return image;
    }
}