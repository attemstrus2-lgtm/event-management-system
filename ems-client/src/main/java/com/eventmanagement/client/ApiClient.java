package com.eventmanagement.client;

import com.eventmanagement.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = System.getenv().getOrDefault("SERVER_URL", "http://localhost:8080/api");
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // --- EVENTS ---
    public List<Event> getAllEvents() throws IOException {
        return sendGet("/events", new TypeReference<List<Event>>() {});
    }

    public void addEvent(Event event) throws IOException {
        sendWithBody(new HttpPost(BASE_URL + "/events"), event);
    }

    public void updateEvent(Event event) throws IOException {
        sendWithBody(new HttpPut(BASE_URL + "/events"), event);
    }

    public void deleteEvent(int id) throws IOException {
        sendDelete("/events?id=" + id);
    }

    // --- RESPONSIBLE PERSONS ---
    public List<ResponsiblePerson> getAllPersons() throws IOException {
        return sendGet("/persons", new TypeReference<List<ResponsiblePerson>>() {});
    }

    public void addPerson(ResponsiblePerson person) throws IOException {
        sendWithBody(new HttpPost(BASE_URL + "/persons"), person);
    }

    public void updatePerson(ResponsiblePerson person) throws IOException {
        sendWithBody(new HttpPut(BASE_URL + "/persons"), person);
    }

    public void deletePerson(int id) throws IOException {
        sendDelete("/persons?id=" + id);
    }

    // --- TASKS ---
    public List<Task> getAllTasks() throws IOException {
        return sendGet("/tasks", new TypeReference<List<Task>>() {});
    }

    public void addTask(Task task) throws IOException {
        sendWithBody(new HttpPost(BASE_URL + "/tasks"), task);
    }

    public void updateTask(Task task) throws IOException {
        sendWithBody(new HttpPut(BASE_URL + "/tasks"), task);
    }

    public void changeTaskStatus(int id, TaskStatus status) throws IOException {
        HttpPut request = new HttpPut(BASE_URL + "/tasks?action=status&id=" + id + "&status=" + status.name());
        executeAndCheck(request);
    }

    public void deleteTask(int id) throws IOException {
        sendDelete("/tasks?id=" + id);
    }

    // --- RESOURCES ---
    public List<Resource> getAllResources() throws IOException {
        return sendGet("/resources", new TypeReference<List<Resource>>() {});
    }

    public void addResource(Resource resource) throws IOException {
        sendWithBody(new HttpPost(BASE_URL + "/resources"), resource);
    }

    public void updateResource(Resource resource) throws IOException {
        sendWithBody(new HttpPut(BASE_URL + "/resources"), resource);
    }

    public void deleteResource(int id) throws IOException {
        sendDelete("/resources?id=" + id);
    }

    // --- HELPERS ---
    private <T> T sendGet(String path, TypeReference<T> typeRef) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + path);
            try (CloseableHttpResponse response = client.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (response.getCode() >= 400) {
                    throw new IOException(extractError(body, response.getCode()));
                }
                return mapper.readValue(body, typeRef);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Помилка мережі: " + e.getMessage(), e);
        }
    }

    private void sendWithBody(HttpUriRequestBase request, Object payload) throws IOException {
        String json = mapper.writeValueAsString(payload);
        request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        executeAndCheck(request);
    }

    private void sendDelete(String path) throws IOException {
        executeAndCheck(new HttpDelete(BASE_URL + path));
    }

    private void executeAndCheck(HttpUriRequestBase request) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            String body = response.getEntity() != null ? EntityUtils.toString(response.getEntity(), "UTF-8") : "";
            if (response.getCode() >= 400) {
                throw new IOException(extractError(body, response.getCode()));
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Помилка мережі: " + e.getMessage(), e);
        }
    }

    private String extractError(String body, int code) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (Exception ignored) {
        }
        return "HTTP помилка " + code;
    }
}
