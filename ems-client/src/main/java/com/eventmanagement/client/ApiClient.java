package com.eventmanagement.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.eventmanagement.model.Task;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.List;

public class ApiClient {

    private static final String SERVER_URL = "http://localhost:8080/api/tasks";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiClient() {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<Task> getAllTasks() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(SERVER_URL);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
                return objectMapper.readValue(jsonResponse, new TypeReference<List<Task>>() {});
            }
        } catch (Exception e) {
            throw new IOException("Помилка зв'язку з сервером при отриманні задач: " + e.getMessage());
        }
    }

    public boolean addTask(Task task) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(SERVER_URL);
            String jsonRequestBody = objectMapper.writeValueAsString(task);
            request.setEntity(new StringEntity(jsonRequestBody, org.apache.hc.core5.http.ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getCode() == 201;
            }
        } catch (Exception e) {
            throw new IOException("Помилка зв'язку з сервером при додаванні задачі: " + e.getMessage());
        }
    }
}
