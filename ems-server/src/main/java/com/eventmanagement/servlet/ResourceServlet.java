package com.eventmanagement.servlet;

import com.eventmanagement.model.Resource;
import com.eventmanagement.service.ResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/api/resources")
public class ResourceServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ResourceServlet.class.getName());
    private final ResourceService service = new ResourceService();
    private final ObjectMapper mapper = new ObjectMapper();

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
            Resource resource = mapper.readValue(req.getReader(), Resource.class);
            service.add(resource);
            writeJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Ресурс успішно додано"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Resource resource = mapper.readValue(req.getReader(), Resource.class);
            service.update(resource);
            writeJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Ресурс оновлено"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            service.delete(id);
            writeJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Ресурс видалено"));
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
            LOG.severe("Помилка в ResourceServlet: " + e.getMessage());
        }
        writeJson(resp, status, Map.of("error", msg != null ? msg : "Некоректний запит"));
    }
}
