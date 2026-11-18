package com.eventmanagement.client;

import com.eventmanagement.model.Resource;
import com.eventmanagement.model.Task;
import com.eventmanagement.model.TaskStatus;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class CsvImportTask extends javafx.concurrent.Task<Integer> {
    private static final Logger LOG = Logger.getLogger(CsvImportTask.class.getName());

    private final Path tasksCsvPath;
    private final Path resourcesCsvPath;
    private final ApiClient apiClient;

    public CsvImportTask(Path tasksCsvPath, Path resourcesCsvPath, ApiClient apiClient) {
        this.tasksCsvPath = tasksCsvPath;
        this.resourcesCsvPath = resourcesCsvPath;
        this.apiClient = apiClient;
    }

    @Override
    protected Integer call() throws Exception {
        if (!Files.exists(tasksCsvPath)) {
            throw new IllegalArgumentException("Файл не знайдено: " + tasksCsvPath);
        }
        if (!Files.exists(resourcesCsvPath)) {
            throw new IllegalArgumentException("Файл не знайдено: " + resourcesCsvPath);
        }

        List<String> taskLines = Files.readAllLines(tasksCsvPath, StandardCharsets.UTF_8);
        List<String> resourceLines = Files.readAllLines(resourcesCsvPath, StandardCharsets.UTF_8);

        int totalRows = Math.max(1, (taskLines.size() - 1) + (resourceLines.size() - 1));
        int processed = 0;
        int importedCount = 0;

        // 1. Імпорт завдань
        for (int i = 1; i < taskLines.size(); i++) {
            String line = taskLines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            if (parts.length < 8) {
                LOG.warning("Пропущено некоректний рядок tasks.csv #" + (i + 1));
                continue;
            }

            Task task = new Task();
            task.setEventId(Integer.parseInt(parts[0].trim()));
            task.setResponsibleId(Integer.parseInt(parts[1].trim()));
            task.setTitle(parts[2].trim());
            task.setDescription(parts[3].trim());
            task.setDeadline(LocalDateTime.parse(parts[4].trim()));
            task.setStatus(TaskStatus.CREATED);
            task.setF1Closeness(Double.parseDouble(parts[5].trim()));
            task.setF2Criticality(Double.parseDouble(parts[6].trim()));
            task.setF3Dependent(Double.parseDouble(parts[7].trim()));

            apiClient.addTask(task);
            importedCount++;
            processed++;
            updateProgress(processed, totalRows);
            updateMessage("Імпорт завдань та ресурсів... (" + importedCount + ")");
            Thread.sleep(150); // для наочності прогрес-бару
        }

        // 2. Імпорт ресурсів
        for (int i = 1; i < resourceLines.size(); i++) {
            String line = resourceLines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            if (parts.length < 4) {
                LOG.warning("Пропущено некоректний рядок resources.csv #" + (i + 1));
                continue;
            }

            Resource resource = new Resource();
            resource.setTaskId(Integer.parseInt(parts[0].trim()));
            resource.setName(parts[1].trim());
            resource.setType(parts[2].trim());
            resource.setQuantity(Integer.parseInt(parts[3].trim()));

            apiClient.addResource(resource);
            importedCount++;
            processed++;
            updateProgress(processed, totalRows);
            updateMessage("Імпорт завдань та ресурсів... (" + importedCount + ")");
            Thread.sleep(150);
        }

        updateProgress(1, 1);
        updateMessage("Готово! Успішно імпортовано записів: " + importedCount);
        LOG.info("CSV-імпорт завершено. Імпортовано: " + importedCount);
        return importedCount;
    }
}
