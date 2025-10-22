package com.donaciones.dao;

import com.donaciones.models.Donation;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {

    public List<Donation> getAllDonations() {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donaciones ORDER BY created_date DESC";

        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error obteniendo todas las donaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getDonationsByUser(String username) {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donaciones WHERE donor_username = ? ORDER BY created_date DESC";

        System.out.println("DEBUG DonationDAO - Buscando donaciones para usuario: " + username);

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                donations.add(mapDonation(rs));
                count++;
            }
            System.out.println("DEBUG DonationDAO - Donaciones encontradas: " + count);

        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error al buscar donaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return donations;
    }

    public boolean addDonation(Donation donation) {
        String sql = "INSERT INTO donaciones (type, description, quantity, item_condition, location, address, donor_username, status, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, donation.getType());
            pstmt.setString(2, donation.getDescription());
            pstmt.setInt(3, donation.getQuantity());
            pstmt.setString(4, donation.getCondition());
            pstmt.setString(5, donation.getLocation());
            pstmt.setString(6, donation.getAddress());
            pstmt.setString(7, donation.getDonorUsername());
            pstmt.setString(8, donation.getStatus());

            int result = pstmt.executeUpdate();
            System.out.println("DEBUG DonationDAO - Donación insertada, filas afectadas: " + result);
            return result > 0;

        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error al insertar donación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDonation(Donation donation) {
        String sql = "UPDATE donaciones SET type=?, description=?, quantity=?, item_condition=?, location=?, status=?, employee_assigned=?, address=? WHERE id=?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, donation.getType());
            pstmt.setString(2, donation.getDescription());
            pstmt.setInt(3, donation.getQuantity());
            pstmt.setString(4, donation.getCondition());
            pstmt.setString(5, donation.getLocation());
            pstmt.setString(6, donation.getStatus());
            pstmt.setString(7, donation.getEmployeeUsername());
            pstmt.setString(8, donation.getAddress());
            pstmt.setInt(9, donation.getId());

            int result = pstmt.executeUpdate();
            System.out.println("DEBUG DonationDAO - Donación actualizada, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error actualizando donación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDonationStatus(int donationId, String newStatus) {
        String sql = "UPDATE donaciones SET status = ? WHERE id = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, donationId);

            int result = pstmt.executeUpdate();
            System.out.println("DEBUG DonationDAO - Estado actualizado, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error actualizando estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDonationStatusForEmployee(int donationId, String newStatus, String employeeUsername) {
        String sql = "UPDATE donaciones SET status = ? WHERE id = ? AND employee_assigned = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, donationId);
            pstmt.setString(3, employeeUsername);

            int result = pstmt.executeUpdate();
            System.out.println("DEBUG DonationDAO - Estado actualizado para empleado, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error actualizando estado para empleado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignDonationToEmployee(int donationId, String employeeUsername) {
        String sql = "UPDATE donaciones SET employee_assigned = ?, status = 'in_progress' WHERE id = ? AND (status = 'pending' OR employee_assigned IS NULL)";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeUsername);
            pstmt.setInt(2, donationId);

            int result = pstmt.executeUpdate();
            System.out.println("DEBUG DonationDAO - Donación asignada a empleado, filas afectadas: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error asignando donación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Donation getDonation(int id) {
        String sql = "SELECT * FROM donaciones WHERE id = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapDonation(rs);
            }
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error obteniendo donación: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Donation> getDonationsByStatus(String status) {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donaciones WHERE status = ? ORDER BY created_date DESC";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error obteniendo donaciones por estado: " + e.getMessage());
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getDonationsByEmployee(String employeeUsername) {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donaciones WHERE employee_assigned = ? ORDER BY created_date DESC";

        System.out.println("DEBUG DonationDAO - Buscando donaciones para empleado: " + employeeUsername);

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeUsername);
            ResultSet rs = pstmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                donations.add(mapDonation(rs));
                count++;
            }
            System.out.println("DEBUG DonationDAO - Donaciones del empleado encontradas: " + count);

        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error al buscar donaciones del empleado: " + e.getMessage());
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getAvailableDonations() {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donaciones WHERE status = 'pending' AND (employee_assigned IS NULL OR employee_assigned = '') ORDER BY created_date DESC";

        System.out.println("DEBUG DonationDAO - Buscando donaciones disponibles");

        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            while (rs.next()) {
                donations.add(mapDonation(rs));
                count++;
            }
            System.out.println("DEBUG DonationDAO - Donaciones disponibles encontradas: " + count);

        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error al buscar donaciones disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> searchDonations(String type, String location, String condition) {
        List<Donation> donations = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM donaciones WHERE 1=1");

        if (type != null && !type.isEmpty()) {
            sql.append(" AND type = ?");
        }
        if (location != null && !location.isEmpty()) {
            sql.append(" AND location = ?");
        }
        if (condition != null && !condition.isEmpty()) {
            sql.append(" AND item_condition = ?");
        }
        sql.append(" ORDER BY created_date DESC");

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (type != null && !type.isEmpty()) {
                pstmt.setString(paramIndex++, type);
            }
            if (location != null && !location.isEmpty()) {
                pstmt.setString(paramIndex++, location);
            }
            if (condition != null && !condition.isEmpty()) {
                pstmt.setString(paramIndex++, condition);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error buscando donaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getRecentDonations(int limit) {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donaciones ORDER BY created_date DESC LIMIT ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error obteniendo donaciones recientes: " + e.getMessage());
            e.printStackTrace();
        }
        return donations;
    }

    public int getDonationCountByStatus(String status) {
        String sql = "SELECT COUNT(*) as count FROM donaciones WHERE status = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error contando donaciones por estado: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // MÉTODO PARA FILTRAR DONACIONES
    public List<Donation> getDonationsByFilters(String status, String type, String location) {
        List<Donation> donations = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM donaciones WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        try {
            // Construir la consulta dinámicamente basada en los filtros
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

            sql.append(" ORDER BY created_date DESC");

            System.out.println("DEBUG DonationDAO - Consulta con filtros: " + sql.toString());
            System.out.println("DEBUG DonationDAO - Parámetros: " + parameters);

            try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                // Establecer parámetros
                for (int i = 0; i < parameters.size(); i++) {
                    pstmt.setObject(i + 1, parameters.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    donations.add(mapDonation(rs));
                }

                System.out.println("DEBUG DonationDAO - Donaciones encontradas con filtros: " + donations.size());
            }
        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error filtrando donaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return donations;
    }

    // MÉTODO DELETE CORREGIDO CON MÁS LOGS
    public boolean deleteDonation(int donationId) {
        System.out.println("DEBUG DonationDAO - Iniciando eliminación de donación ID: " + donationId);

        // Primero verificamos si existe la donación
        Donation donation = getDonation(donationId);
        if (donation == null) {
            System.out.println("ERROR DonationDAO - No se encontró donación con ID: " + donationId);
            return false;
        }

        System.out.println("DEBUG DonationDAO - Donación encontrada: " + donation.getDescription());

        String sql = "DELETE FROM donaciones WHERE id = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, donationId);
            int result = pstmt.executeUpdate();

            System.out.println("DEBUG DonationDAO - Resultado de eliminación - Filas afectadas: " + result);

            if (result > 0) {
                System.out.println("DEBUG DonationDAO - ÉXITO: Donación " + donationId + " eliminada correctamente");
                return true;
            } else {
                System.out.println("DEBUG DonationDAO - FALLO: No se pudo eliminar donación " + donationId);
                return false;
            }

        } catch (SQLException e) {
            System.out.println("ERROR DonationDAO - Error SQL eliminando donación " + donationId + ": " + e.getMessage());
            System.out.println("ERROR DonationDAO - SQL State: " + e.getSQLState());
            System.out.println("ERROR DonationDAO - Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    private Donation mapDonation(ResultSet rs) throws SQLException {
        Donation donation = new Donation();
        donation.setId(rs.getInt("id"));
        donation.setType(rs.getString("type"));
        donation.setDescription(rs.getString("description"));
        donation.setQuantity(rs.getInt("quantity"));
        donation.setCondition(rs.getString("item_condition"));
        donation.setLocation(rs.getString("location"));
        donation.setDonorUsername(rs.getString("donor_username"));
        donation.setStatus(rs.getString("status"));
        donation.setEmployeeUsername(rs.getString("employee_assigned"));
        donation.setCreatedDate(rs.getTimestamp("created_date"));
        donation.setDonationDate(rs.getTimestamp("donation_date"));

        String address = rs.getString("address");
        if (address != null) {
            donation.setAddress(address);
        }

        return donation;
    }
}
