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
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getDonationsByUser(String username) {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donaciones WHERE donor_username = ? ORDER BY created_date DESC";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                donations.add(mapDonation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public boolean addDonation(Donation donation) {
        String sql = "INSERT INTO donaciones (type, description, quantity, item_condition, location, donor_username, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, donation.getType());
            pstmt.setString(2, donation.getDescription());
            pstmt.setInt(3, donation.getQuantity());
            pstmt.setString(4, donation.getCondition());
            pstmt.setString(5, donation.getLocation());
            pstmt.setString(6, donation.getDonorUsername());
            pstmt.setString(7, donation.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDonation(Donation donation) {
        String sql = "UPDATE donaciones SET type=?, description=?, quantity=?, item_condition=?, location=?, status=?, employee_assigned=? WHERE id=?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, donation.getType());
            pstmt.setString(2, donation.getDescription());
            pstmt.setInt(3, donation.getQuantity());
            pstmt.setString(4, donation.getCondition());
            pstmt.setString(5, donation.getLocation());
            pstmt.setString(6, donation.getStatus());
            pstmt.setString(7, donation.getEmployeeUsername());
            pstmt.setInt(8, donation.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
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
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getDonationsByEmployee(String employeeUsername) {
    List<Donation> donations = new ArrayList<>();
    String sql = "SELECT * FROM donaciones WHERE employee_assigned = ? ORDER BY created_date DESC";
    
    try (Connection conn = Conexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, employeeUsername);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            donations.add(mapDonation(rs));
        }
    } catch (SQLException e) {
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
            e.printStackTrace();
        }
        return 0;
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
        donation.setEmployeeUsername(rs.getString("employee_assigned")); // CORREGIDO
        donation.setCreatedDate(rs.getTimestamp("created_date"));
        donation.setDonationDate(rs.getTimestamp("donation_date"));

        // Campos opcionales
        String address = rs.getString("address");
        if (address != null) {
            donation.setAddress(address);
        }

        return donation;
    }
}
