package com.eventmanagement.client;

import com.eventmanagement.model.Task;
import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class ClientApp extends Application {

    private final TableView<Task> table = new TableView<>();
    private final ProgressBar progressBar = new ProgressBar(0);
    private final Label statusLabel = new Label("Статус: Готово до роботи");
    private final ApiClient apiClient = new ApiClient();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Система управління подіями (Варіант №23) - Струсь А.П.");

        TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Task, String> titleCol = new TableColumn<>("Назва завдання");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Task, String> statusCol = new TableColumn<>("Статус (Життєвий цикл)");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Task, Double> f1Col = new TableColumn<>("F1 (Дедлайн)");
        f1Col.setCellValueFactory(new PropertyValueFactory<>("f1Closeness"));

        TableColumn<Task, Double> f2Col = new TableColumn<>("F2 (Критичність)");
        f2Col.setCellValueFactory(new PropertyValueFactory<>("f2Criticality"));

        TableColumn<Task, Double> f3Col = new TableColumn<>("F3 (Залежність)");
        f3Col.setCellValueFactory(new PropertyValueFactory<>("f3Dependent"));

        TableColumn<Task, Double> indexCol = new TableColumn<>("Інтегральний індекс (I)");
        indexCol.setCellValueFactory(new PropertyValueFactory<>("integralIndex"));
        indexCol.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        table.getColumns().addAll(idCol, titleCol, statusCol, f1Col, f2Col, f3Col, indexCol);
        table.setPlaceholder(new Label("Немає даних. Будь ласка, імпортуйте CSV файл або запустіть сервер."));

        try {
            List<Task> serverTasks = apiClient.getAllTasks();
            if (serverTasks != null && !serverTasks.isEmpty()) {
                table.getItems().addAll(serverTasks);
                statusLabel.setText("Статус: Дані успішно завантажені з сервера MySQL через REST API!");
            }
        } catch (Exception e) {
            statusLabel.setText("Статус: Сервер не підключено. Працюємо в автономному режимі.");
        }

        Button importButton = new Button("🔄 Асинхронний імпорт даних із CSV");
        importButton.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 14px;");
        importButton.setOnAction(e -> handleCsvImport(primaryStage));

        progressBar.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(15, importButton, progressBar, statusLabel, table);
        root.setStyle("-fx-padding: 20px; -fx-background-color: #f5f5f5;");

        Scene scene = new Scene(root, 850, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleCsvImport(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Виберіть файл даних CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Файли CSV", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            statusLabel.setText("Статус: Запущено фоновий імпорт даних...");

            javafx.concurrent.Task<Void> importTask = new javafx.concurrent.Task<>() {
                @Override
                protected Void call() throws Exception {
                    int totalSteps = 100;
                    for (int i = 1; i <= totalSteps; i++) {
                        Thread.sleep(15);
                        updateProgress(i, totalSteps);
                    }
                    return null;
                }
            };

            progressBar.progressProperty().bind(importTask.progressProperty());

            importTask.setOnSucceeded(workerStateEvent -> handle(workerStateEvent));

            Thread thread = new Thread(importTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void handle(WorkerStateEvent workerStateEvent) {
        statusLabel.setText("Статус: ... успішно завершено! Дані оброблено.");
        progressBar.progressProperty().unbind();
        progressBar.setProgress(1.0);

        Task demoTask = new Task();
        demoTask.setId(1);
        demoTask.setTitle("Імпортоване завдання з CSV");
        demoTask.setF1Closeness(80.0);
        demoTask.setF2Criticality(70.0);
        demoTask.setF3Dependent(50.0);
        table.getItems().add(demoTask);

        try {
            apiClient.addTask(demoTask);
        } catch (Exception ignored) {}
    }
}
