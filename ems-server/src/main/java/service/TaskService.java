package com.eventmanagement.service;

import com.eventmanagement.dao.EventDao;
import com.eventmanagement.dao.ResponsiblePersonDao;
import com.eventmanagement.dao.TaskDao;
import com.eventmanagement.model.Task;
import com.eventmanagement.model.TaskStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TaskService {
    private static final Logger LOG = Logger.getLogger(TaskService.class.getName());

    private final TaskDao taskDao = new TaskDao();
    private final EventDao eventDao = new EventDao();
    private final ResponsiblePersonDao personDao = new ResponsiblePersonDao();

    public List<Task> getAll() throws SQLException {
        return taskDao.getAll();
    }

    public void add(Task task) throws SQLException {
        validate(task);
        requireRelations(task);
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.CREATED);
        }
        task.setIntegralIndex(IntegralIndexCalculator.calculate(
                task.getF1Closeness(), task.getF2Criticality(), task.getF3Dependent()));
        taskDao.add(task);
        LOG.info("Створено нове завдання: " + task.getTitle());
    }

    public void update(Task task) throws SQLException {
        validate(task);
        Task existing = taskDao.getById(task.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Завдання з ID=" + task.getId() + " не знайдено");
        }
        requireRelations(task);
        if (task.getStatus() != existing.getStatus() && !isAllowedTransition(existing.getStatus(), task.getStatus())) {
            throw new IllegalArgumentException("Недопустимий перехід стану: " + existing.getStatus() + " -> " + task.getStatus());
        }
        task.setIntegralIndex(IntegralIndexCalculator.calculate(
                task.getF1Closeness(), task.getF2Criticality(), task.getF3Dependent()));
        taskDao.update(task);
        LOG.info("Оновлено завдання ID=" + task.getId());
    }

    public void changeStatus(int id, TaskStatus nextStatus) throws SQLException {
        if (nextStatus == null) {
            throw new IllegalArgumentException("Новий стан не може бути порожнім");
        }
        Task current = taskDao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Завдання з ID=" + id + " не знайдено");
        }
        if (!isAllowedTransition(current.getStatus(), nextStatus)) {
            throw new IllegalArgumentException("Недопустимий перехід стану: " + current.getStatus() + " -> " + nextStatus);
        }
        taskDao.updateStatus(id, nextStatus);
        LOG.info("Змінено стан завдання ID=" + id + " на " + nextStatus);
    }

    public void delete(int id) throws SQLException {
        taskDao.delete(id);
        LOG.info("Видалено завдання ID=" + id);
    }

    public static boolean isAllowedTransition(TaskStatus from, TaskStatus to) {
        return (from == TaskStatus.CREATED && to == TaskStatus.ASSIGNED)
                || (from == TaskStatus.ASSIGNED && to == TaskStatus.IN_PROGRESS)
                || (from == TaskStatus.IN_PROGRESS && to == TaskStatus.DONE);
    }

    public static void validate(Task task) {
        if (task == null || task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Назва завдання є обов'язковою");
        }
        if (task.getEventId() <= 0 || task.getResponsibleId() <= 0) {
            throw new IllegalArgumentException("Потрібно вказати коректні ID події та відповідальної особи");
        }
        if (task.getDeadline() == null) {
            throw new IllegalArgumentException("Дедлайн завдання є обов'язковим");
        }
        IntegralIndexCalculator.calculate(task.getF1Closeness(), task.getF2Criticality(), task.getF3Dependent());
    }

    private void requireRelations(Task task) throws SQLException {
        if (eventDao.getById(task.getEventId()) == null) {
            throw new IllegalArgumentException("Подію з ID=" + task.getEventId() + " не знайдено");
        }
        if (personDao.getById(task.getResponsibleId()) == null) {
            throw new IllegalArgumentException("Відповідальну особу з ID=" + task.getResponsibleId() + " не знайдено");
        }
    }
}
