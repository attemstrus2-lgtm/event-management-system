package com.eventmanagement.dao;

import com.eventmanagement.config.DatabaseConfig;
import com.eventmanagement.model.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDao {

    public List<Resource> getAll() throws SQLException {
        List<Resource> list = new ArrayList<>();
        String sql = "SELECT * FROM resources ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Resource getById(int id) throws SQLException {
        String sql = "SELECT * FROM resources WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void add(Resource r) throws SQLException {
        String sql = "INSERT INTO resources (task_id, name, type, quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, r.getTaskId());
            pstmt.setString(2, r.getName());
            pstmt.setString(3, r.getType());
            pstmt.setInt(4, r.getQuantity());
            pstmt.executeUpdate();
        }
    }

    public void update(Resource r) throws SQLException {
        String sql = "UPDATE resources SET task_id = ?, name = ?, type = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, r.getTaskId());
            pstmt.setString(2, r.getName());
            pstmt.setString(3, r.getType());
            pstmt.setInt(4, r.getQuantity());
            pstmt.setInt(5, r.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM resources WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Resource map(ResultSet rs) throws SQLException {
        return new Resource(
                rs.getInt("id"),
                rs.getInt("task_id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getInt("quantity")
        );
    }
}
