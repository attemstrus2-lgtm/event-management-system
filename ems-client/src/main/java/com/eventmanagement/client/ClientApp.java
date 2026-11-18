package com.eventmanagement.client;

import com.eventmanagement.model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class ClientApp extends Application {
    private static final Logger LOG = Logger.getLogger(ClientApp.class.getName());
    private final ApiClient api = new ApiClient();

    private final TableView<Event> eventTable = new TableView<>();
    private final TableView<ResponsiblePerson> personTable = new TableView<>();
    private final TableView<Task> taskTable = new TableView<>();
    private final TableView<Resource> resourceTable = new TableView<>();

    private final ProgressBar progressBar = new ProgressBar(0);
    private final Label importStatusLabel = new Label("Статус CSV: Очікування запуску");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Інформаційна система організації подій і контролю завдань (Варіант №23) — Струсь А.П.");

        TabPane tabs = new TabPane(
                new Tab("Події", buildEventsTab()),
                new Tab("Відповідальні особи", buildPersonsTab()),
                new Tab("Завдання та життєвий цикл", buildTasksTab()),
                new Tab("Ресурси", buildResourcesTab())
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        refreshAll();

        Scene scene = new Scene(tabs, 1100, 650);
        stage.setScene(scene);
        stage.show();
    }

    // ================= 1. ПОДІЇ =================
    private Pane buildEventsTab() {
        TableColumn<Event, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Event, String> cTitle = new TableColumn<>("Назва події");
        cTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Event, String> cDesc = new TableColumn<>("Опис");
        cDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Event, LocalDateTime> cDate = new TableColumn<>("Дата проведення");
        cDate.setCellValueFactory(new PropertyValueFactory<>("scheduledDate"));
        eventTable.getColumns().setAll(cId, cTitle, cDesc, cDate);

        TextField tfTitle = new TextField();
        tfTitle.setPromptText("Назва події");
        TextField tfDesc = new TextField();
        tfDesc.setPromptText("Опис події");
        TextField tfDate = new TextField("2026-11-20T10:00:00");
        tfDate.setPromptText("YYYY-MM-DDTHH:MM:SS");

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                tfTitle.setText(n.getTitle());
                tfDesc.setText(n.getDescription());
                tfDate.setText(n.getScheduledDate() != null ? n.getScheduledDate().toString() : "");
            }
        });

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addEvent(new Event(0, tfTitle.getText(), tfDesc.getText(), LocalDateTime.parse(tfDate.getText().trim())));
            refreshEvents();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            Event sel = requireSelection(eventTable);
            sel.setTitle(tfTitle.getText());
            sel.setDescription(tfDesc.getText());
            sel.setScheduledDate(LocalDateTime.parse(tfDate.getText().trim()));
            api.updateEvent(sel);
            refreshEvents();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            Event sel = requireSelection(eventTable);
            api.deleteEvent(sel.getId());
            refreshAll();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> safe(this::refreshEvents));

        VBox form = new VBox(8, new Label("Керування подіями:"), tfTitle, tfDesc, tfDate,
                new HBox(8, btnAdd, btnUpdate, btnDelete, btnRefresh));
        form.setPrefWidth(320);

        HBox layout = new HBox(15, eventTable, form);
        HBox.setHgrow(eventTable, Priority.ALWAYS);
        layout.setPadding(new Insets(15));
        return layout;
    }

    // ================= 2. ВІДПОВІДАЛЬНІ ОСОБИ =================
    private Pane buildPersonsTab() {
        TableColumn<ResponsiblePerson, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ResponsiblePerson, String> cName = new TableColumn<>("ПІБ");
        cName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        TableColumn<ResponsiblePerson, String> cEmail = new TableColumn<>("Email");
        cEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<ResponsiblePerson, String> cPos = new TableColumn<>("Посада");
        cPos.setCellValueFactory(new PropertyValueFactory<>("position"));
        personTable.getColumns().setAll(cId, cName, cEmail, cPos);

        TextField tfName = new TextField();
        tfName.setPromptText("ПІБ");
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("Email");
        TextField tfPos = new TextField();
        tfPos.setPromptText("Посада");

        personTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                tfName.setText(n.getFullName());
                tfEmail.setText(n.getEmail());
                tfPos.setText(n.getPosition());
            }
        });

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addPerson(new ResponsiblePerson(0, tfName.getText(), tfEmail.getText(), tfPos.getText()));
            refreshPersons();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            ResponsiblePerson sel = requireSelection(personTable);
            sel.setFullName(tfName.getText());
            sel.setEmail(tfEmail.getText());
            sel.setPosition(tfPos.getText());
            api.updatePerson(sel);
            refreshPersons();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            ResponsiblePerson sel = requireSelection(personTable);
            api.deletePerson(sel.getId());
            refreshAll();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> safe(this::refreshPersons));

        VBox form = new VBox(8, new Label("Відповідальні особи:"), tfName, tfEmail, tfPos,
                new HBox(8, btnAdd, btnUpdate, btnDelete, btnRefresh));
        form.setPrefWidth(320);

        HBox layout = new HBox(15, personTable, form);
        HBox.setHgrow(personTable, Priority.ALWAYS);
        layout.setPadding(new Insets(15));
        return layout;
    }

    // ================= 3. ЗАВДАННЯ + ЖИТТЄВИЙ ЦИКЛ + CSV =================
    private Pane buildTasksTab() {
        TableColumn<Task, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Task, Integer> cEv = new TableColumn<>("Event ID");
        cEv.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        TableColumn<Task, Integer> cResp = new TableColumn<>("Resp ID");
        cResp.setCellValueFactory(new PropertyValueFactory<>("responsibleId"));
        TableColumn<Task, String> cTitle = new TableColumn<>("Назва");
        cTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Task, TaskStatus> cStatus = new TableColumn<>("Стан");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Task, Double> cF1 = new TableColumn<>("F1");
        cF1.setCellValueFactory(new PropertyValueFactory<>("f1Closeness"));
        TableColumn<Task, Double> cF2 = new TableColumn<>("F2");
        cF2.setCellValueFactory(new PropertyValueFactory<>("f2Criticality"));
        TableColumn<Task, Double> cF3 = new TableColumn<>("F3");
        cF3.setCellValueFactory(new PropertyValueFactory<>("f3Dependent"));
        TableColumn<Task, Double> cIdx = new TableColumn<>("Індекс (I)");
        cIdx.setCellValueFactory(new PropertyValueFactory<>("integralIndex"));
        taskTable.getColumns().setAll(cId, cEv, cResp, cTitle, cStatus, cF1, cF2, cF3, cIdx);

        TextField tfEventId = new TextField("1");
        tfEventId.setPromptText("ID події");
        TextField tfRespId = new TextField("1");
        tfRespId.setPromptText("ID відповідального");
        TextField tfTitle = new TextField();
        tfTitle.setPromptText("Назва завдання");
        TextField tfDesc = new TextField();
        tfDesc.setPromptText("Опис");
        TextField tfDeadline = new TextField("2026-11-15T09:00:00");
        tfDeadline.setPromptText("Дедлайн YYYY-MM-DDTHH:MM:SS");
        TextField tfF1 = new TextField("70");
        tfF1.setPromptText("F1 (0..100)");
        TextField tfF2 = new TextField("80");
        tfF2.setPromptText("F2 (0..100)");
        TextField tfF3 = new TextField("50");
        tfF3.setPromptText("F3 (0..100)");

        ComboBox<TaskStatus> cbNextStatus = new ComboBox<>();
        cbNextStatus.getItems().setAll(TaskStatus.values());
        cbNextStatus.setValue(TaskStatus.ASSIGNED);

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                tfEventId.setText(String.valueOf(n.getEventId()));
                tfRespId.setText(String.valueOf(n.getResponsibleId()));
                tfTitle.setText(n.getTitle());
                tfDesc.setText(n.getDescription());
                tfDeadline.setText(n.getDeadline() != null ? n.getDeadline().toString() : "");
                tfF1.setText(String.valueOf(n.getF1Closeness()));
                tfF2.setText(String.valueOf(n.getF2Criticality()));
                tfF3.setText(String.valueOf(n.getF3Dependent()));
                cbNextStatus.setValue(n.getStatus());
            }
        });

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            Task t = buildTaskFromFields(0, tfEventId, tfRespId, tfTitle, tfDesc, tfDeadline, tfF1, tfF2, tfF3);
            t.setStatus(TaskStatus.CREATED);
            api.addTask(t);
            refreshTasks();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            Task sel = requireSelection(taskTable);
            Task updated = buildTaskFromFields(sel.getId(), tfEventId, tfRespId, tfTitle, tfDesc, tfDeadline, tfF1, tfF2, tfF3);
            updated.setStatus(sel.getStatus());
            api.updateTask(updated);
            refreshTasks();
        }));

        Button btnStatus = new Button("Змінити стан");
        btnStatus.setOnAction(e -> safe(() -> {
            Task sel = requireSelection(taskTable);
            api.changeTaskStatus(sel.getId(), cbNextStatus.getValue());
            refreshTasks();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            Task sel = requireSelection(taskTable);
            api.deleteTask(sel.getId());
            refreshAll();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> safe(this::refreshTasks));

        Button btnImportCsv = new Button("Імпортувати CSV (Завдання + Ресурси)");
        btnImportCsv.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white;");
        btnImportCsv.setOnAction(e -> startCsvImport(btnImportCsv));

        progressBar.setMaxWidth(Double.MAX_VALUE);

        VBox rightForm = new VBox(6,
                new Label("Параметри завдання:"),
                tfEventId, tfRespId, tfTitle, tfDesc, tfDeadline, tfF1, tfF2, tfF3,
                new HBox(6, btnAdd, btnUpdate, btnDelete, btnRefresh),
                new Separator(),
                new Label("Життєвий цикл (CREATED -> ASSIGNED -> IN_PROGRESS -> DONE):"),
                new HBox(6, cbNextStatus, btnStatus)
        );
        rightForm.setPrefWidth(340);

        VBox leftSide = new VBox(10, taskTable, new HBox(10, btnImportCsv, importStatusLabel), progressBar);
        VBox.setVgrow(taskTable, Priority.ALWAYS);

        HBox layout = new HBox(15, leftSide, rightForm);
        HBox.setHgrow(leftSide, Priority.ALWAYS);
        layout.setPadding(new Insets(15));
        return layout;
    }

    // ================= 4. РЕСУРСИ =================
    private Pane buildResourcesTab() {
        TableColumn<Resource, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Resource, Integer> cTaskId = new TableColumn<>("Task ID");
        cTaskId.setCellValueFactory(new PropertyValueFactory<>("taskId"));
        TableColumn<Resource, String> cName = new TableColumn<>("Назва ресурсу");
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Resource, String> cType = new TableColumn<>("Тип");
        cType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Resource, Integer> cQty = new TableColumn<>("Кількість");
        cQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        resourceTable.getColumns().setAll(cId, cTaskId, cName, cType, cQty);

        TextField tfTaskId = new TextField("1");
        tfTaskId.setPromptText("ID завдання");
        TextField tfName = new TextField();
        tfName.setPromptText("Назва ресурсу");
        TextField tfType = new TextField();
        tfType.setPromptText("Тип ресурсу");
        TextField tfQty = new TextField("1");
        tfQty.setPromptText("Кількість");

        resourceTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                tfTaskId.setText(String.valueOf(n.getTaskId()));
                tfName.setText(n.getName());
                tfType.setText(n.getType());
                tfQty.setText(String.valueOf(n.getQuantity()));
            }
        });

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addResource(new Resource(0, Integer.parseInt(tfTaskId.getText().trim()),
                    tfName.getText(), tfType.getText(), Integer.parseInt(tfQty.getText().trim())));
            refreshResources();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            Resource sel = requireSelection(resourceTable);
            sel.setTaskId(Integer.parseInt(tfTaskId.getText().trim()));
            sel.setName(tfName.getText());
            sel.setType(tfType.getText());
            sel.setQuantity(Integer.parseInt(tfQty.getText().trim()));
            api.updateResource(sel);
            refreshResources();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            Resource sel = requireSelection(resourceTable);
            api.deleteResource(sel.getId());
            refreshResources();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> safe(this::refreshResources));

        VBox form = new VBox(8, new Label("Керування ресурсами:"), tfTaskId, tfName, tfType, tfQty,
                new HBox(8, btnAdd, btnUpdate, btnDelete, btnRefresh));
        form.setPrefWidth(320);

        HBox layout = new HBox(15, resourceTable, form);
        HBox.setHgrow(resourceTable, Priority.ALWAYS);
        layout.setPadding(new Insets(15));
        return layout;
    }

    // ================= ДОПОМІЖНІ МЕТОДИ =================
    private Task buildTaskFromFields(int id, TextField ev, TextField resp, TextField title,
                                     TextField desc, TextField deadline, TextField f1, TextField f2, TextField f3) {
        Task t = new Task();
        t.setId(id);
        t.setEventId(Integer.parseInt(ev.getText().trim()));
        t.setResponsibleId(Integer.parseInt(resp.getText().trim()));
        t.setTitle(title.getText());
        t.setDescription(desc.getText());
        t.setDeadline(LocalDateTime.parse(deadline.getText().trim()));
        t.setF1Closeness(Double.parseDouble(f1.getText().trim()));
        t.setF2Criticality(Double.parseDouble(f2.getText().trim()));
        t.setF3Dependent(Double.parseDouble(f3.getText().trim()));
        return t;
    }

    private void startCsvImport(Button btn) {
        Path tasksPath = resolveCsvPath("tasks.csv");
        Path resourcesPath = resolveCsvPath("resources.csv");

        CsvImportTask importTask = new CsvImportTask(tasksPath, resourcesPath, api);
        btn.setDisable(true);
        progressBar.progressProperty().bind(importTask.progressProperty());
        importStatusLabel.textProperty().bind(importTask.messageProperty());

        importTask.setOnSucceeded(e -> {
            cleanupBindings(btn);
            refreshAll();
        });
        importTask.setOnFailed(e -> {
            cleanupBindings(btn);
            importStatusLabel.setText("Помилка CSV-імпорту");
            Throwable ex = importTask.getException();
            showError(ex != null ? ex.getMessage() : "Невідома помилка імпорту");
        });

        Thread thread = new Thread(importTask, "csv-import-thread");
        thread.setDaemon(true);
        thread.start();
    }

    private Path resolveCsvPath(String fileName) {
        Path direct = Path.of("sample-data", fileName);
        if (direct.toFile().exists()) return direct;
        return Path.of("ems-client", "sample-data", fileName);
    }

    private void cleanupBindings(Button btn) {
        progressBar.progressProperty().unbind();
        importStatusLabel.textProperty().unbind();
        btn.setDisable(false);
    }

    private void refreshAll() {
        safe(() -> {
            refreshEvents();
            refreshPersons();
            refreshTasks();
            refreshResources();
        });
    }

    private void refreshEvents() throws Exception {
        eventTable.getItems().setAll(api.getAllEvents());
    }

    private void refreshPersons() throws Exception {
        personTable.getItems().setAll(api.getAllPersons());
    }

    private void refreshTasks() throws Exception {
        taskTable.getItems().setAll(api.getAllTasks());
    }

    private void refreshResources() throws Exception {
        resourceTable.getItems().setAll(api.getAllResources());
    }

    private <T> T requireSelection(TableView<T> tv) {
        T item = tv.getSelectionModel().getSelectedItem();
        if (item == null) {
            throw new IllegalArgumentException("Спочатку виберіть запис у таблиці");
        }
        return item;
    }

    private void safe(ThrowingRunnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning("Помилка операції: " + e.getMessage());
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message != null ? message : "Помилка виконання операції", ButtonType.OK);
        alert.setHeaderText("Помилка");
        alert.showAndWait();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
