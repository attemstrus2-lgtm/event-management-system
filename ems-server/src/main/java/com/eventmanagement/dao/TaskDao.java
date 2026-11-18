package com.eventmanagement.dao;

import com.eventmanagement.config.DatabaseConfig;
import com.eventmanagement.model.Task;
import com.eventmanagement.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {

    public List<Task> getAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tasks.add(map(rs));
            }
        }
        return tasks;
    }

    public Task getById(int id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void add(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (event_id, responsible_id, title, description, deadline, status, f1_closeness, f2_criticality, f3_dependent, integral_index) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            bind(pstmt, task);
            pstmt.executeUpdate();
        }
    }

    public void update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET event_id = ?, responsible_id = ?, title = ?, description = ?, deadline = ?, status = ?, f1_closeness = ?, f2_criticality = ?, f3_dependent = ?, integral_index = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            bind(pstmt, task);
            pstmt.setInt(11, task.getId());
            pstmt.executeUpdate();
        }
    }

    public void updateStatus(int id, TaskStatus status) throws SQLException {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private void bind(PreparedStatement pstmt, Task task) throws SQLException {
        pstmt.setInt(1, task.getEventId());
        pstmt.setInt(2, task.getResponsibleId());
        pstmt.setString(3, task.getTitle());
        pstmt.setString(4, task.getDescription());
        pstmt.setTimestamp(5, task.getDeadline() != null ? Timestamp.valueOf(task.getDeadline()) : null);
        pstmt.setString(6, task.getStatus().name());
        pstmt.setDouble(7, task.getF1Closeness());
        pstmt.setDouble(8, task.getF2Criticality());
        pstmt.setDouble(9, task.getF3Dependent());
        pstmt.setDouble(10, task.getIntegralIndex());
    }

    private Task map(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setEventId(rs.getInt("event_id"));
        task.setResponsibleId(rs.getInt("responsible_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        Timestamp ts = rs.getTimestamp("deadline");
        if (ts != null) {
            task.setDeadline(ts.toLocalDateTime());
        }
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setF1Closeness(rs.getDouble("f1_closeness"));
        task.setF2Criticality(rs.getDouble("f2_criticality"));
        task.setF3Dependent(rs.getDouble("f3_dependent"));
        task.setIntegralIndex(rs.getDouble("integral_index"));
        return task;
    }
}
