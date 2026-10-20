package com.eventmanagement.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.eventmanagement.dao.TaskDao;
import com.eventmanagement.model.Task;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/tasks")
public class TaskServlet extends HttpServlet {

    private final TaskDao taskDao = new TaskDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TaskServlet() {
        // Реєструємо модуль для правильної роботи з датами LocalDateTime у JSON
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // Метод GET: Повертає список усіх завдань у форматі JSON
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Task> tasks = taskDao.getAllTasks();
        objectMapper.writeValue(resp.getWriter(), tasks);
    }

    // Метод POST: Приймає нове завдання у форматі JSON та зберігає його в MySQL
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Task task = objectMapper.readValue(req.getReader(), Task.class);
            taskDao.addTask(task);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Завдання успішно додано!\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Некоректні дані запиту: " + e.getMessage() + "\"}");
        }
    }
}
