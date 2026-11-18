package com.eventmanagement.servlet;

import com.eventmanagement.model.Event;
import com.eventmanagement.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/api/events")
public class EventServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(EventServlet.class.getName());
    private final EventService service = new EventService();
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
            Event event = mapper.readValue(req.getReader(), Event.class);
            service.add(event);
            writeJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Подію успішно додано"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Event event = mapper.readValue(req.getReader(), Event.class);
            service.update(event);
            writeJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Подію оновлено"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            service.delete(id);
            writeJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Подію видалено"));
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
            LOG.severe("Помилка в EventServlet: " + e.getMessage());
        }
        writeJson(resp, status, Map.of("error", msg != null ? msg : "Некоректний запит"));
    }
}
