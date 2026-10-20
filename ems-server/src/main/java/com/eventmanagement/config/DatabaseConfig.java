package com.eventmanagement.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    // Налаштування підключення до MySQL сервера Laragon
    private static final String URL = "jdbc:mysql://localhost:3306/event_management_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // У Laragon для root пароль порожній

    static {
        try {
            // Реєстрація драйвера MySQL в системі
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер MySQL не знайдено!", e);
        }
    }

    // Метод для отримання активного з'єднання з базою
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
