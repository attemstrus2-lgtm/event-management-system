package com.eventmanagement.dao;

import com.eventmanagement.config.DatabaseConfig;
import com.eventmanagement.model.ResponsiblePerson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResponsiblePersonDao {

    public List<ResponsiblePerson> getAll() throws SQLException {
        List<ResponsiblePerson> list = new ArrayList<>();
        String sql = "SELECT * FROM responsible_persons ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public ResponsiblePerson getById(int id) throws SQLException {
        String sql = "SELECT * FROM responsible_persons WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void add(ResponsiblePerson p) throws SQLException {
        String sql = "INSERT INTO responsible_persons (full_name, email, position) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getFullName());
            pstmt.setString(2, p.getEmail());
            pstmt.setString(3, p.getPosition());
            pstmt.executeUpdate();
        }
    }

    public void update(ResponsiblePerson p) throws SQLException {
        String sql = "UPDATE responsible_persons SET full_name = ?, email = ?, position = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getFullName());
            pstmt.setString(2, p.getEmail());
            pstmt.setString(3, p.getPosition());
            pstmt.setInt(4, p.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM responsible_persons WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private ResponsiblePerson map(ResultSet rs) throws SQLException {
        return new ResponsiblePerson(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("position")
        );
    }
}
