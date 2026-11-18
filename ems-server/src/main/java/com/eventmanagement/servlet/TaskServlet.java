package com.eventmanagement.servlet;

import com.eventmanagement.model.Task;
import com.eventmanagement.model.TaskStatus;
import com.eventmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/api/tasks")
public class TaskServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(TaskServlet.class.getName());
    private final TaskService service = new TaskService();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, HttpServletResponse.SC_OK, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Task task = mapper.readValue(req.getReader(), Task.class);
            service.add(task);
            writeJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Завдання успішно додано"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("status".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                TaskStatus nextStatus = TaskStatus.valueOf(req.getParameter("status"));
                service.changeStatus(id, nextStatus);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Стан завдання оновлено"));
            } else {
                Task task = mapper.readValue(req.getReader(), Task.class);
                service.update(task);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Завдання оновлено"));
            }
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            service.delete(id);
            writeJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Завдання видалено"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        boolean isClientError = e instanceof IllegalArgumentException;
        int status = isClientError ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String msg = isClientError ? e.getMessage() : "Внутрішня помилка сервера";
        if (!isClientError) {
            LOG.severe("Помилка в TaskServlet: " + e.getMessage());
        }
        writeJson(resp, status, Map.of("error", msg != null ? msg : "Некоректний запит"));
    }
}
