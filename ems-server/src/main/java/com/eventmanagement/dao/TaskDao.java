package com.eventmanagement.dao;

import com.eventmanagement.config.DatabaseConfig;
import com.eventmanagement.model.Task;
import com.eventmanagement.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {

    // Метод для отримання всіх завдань із бази даних
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setEventId(rs.getInt("event_id"));
                task.setResponsibleId(rs.getInt("responsible_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
                task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                task.setF1Closeness(rs.getDouble("f1_closeness"));
                task.setF2Criticality(rs.getDouble("f2_criticality"));
                task.setF3Dependent(rs.getDouble("f3_dependent"));
                task.setIntegralIndex(rs.getDouble("integral_index")); // беремо згенерований MySQL індекс
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Метод для додавання нового завдання
    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (event_id, responsible_id, title, description, deadline, status, f1_closeness, f2_criticality, f3_dependent) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, task.getEventId());
            pstmt.setInt(2, task.getResponsibleId());
            pstmt.setString(3, task.getTitle());
            pstmt.setString(4, task.getDescription());
            pstmt.setTimestamp(5, Timestamp.valueOf(task.getDeadline()));
            pstmt.setString(6, task.getStatus().name());
            pstmt.setDouble(7, task.getF1Closeness());
            pstmt.setDouble(8, task.getF2Criticality());
            pstmt.setDouble(9, task.getF3Dependent());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
