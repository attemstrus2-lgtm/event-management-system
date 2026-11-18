package com.eventmanagement.service;

import com.eventmanagement.dao.ResourceDao;
import com.eventmanagement.dao.TaskDao;
import com.eventmanagement.model.Resource;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ResourceService {
    private static final Logger LOG = Logger.getLogger(ResourceService.class.getName());
    private final ResourceDao dao = new ResourceDao();
    private final TaskDao taskDao = new TaskDao();

    public List<Resource> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(Resource r) throws SQLException {
        validate(r);
        if (taskDao.getById(r.getTaskId()) == null) {
            throw new IllegalArgumentException("Завдання з ID=" + r.getTaskId() + " не знайдено");
        }
        dao.add(r);
        LOG.info("Додано ресурс: " + r.getName());
    }

    public void update(Resource r) throws SQLException {
        validate(r);
        if (taskDao.getById(r.getTaskId()) == null) {
            throw new IllegalArgumentException("Завдання з ID=" + r.getTaskId() + " не знайдено");
        }
        dao.update(r);
        LOG.info("Оновлено ресурс ID=" + r.getId());
    }

    public void delete(int id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено ресурс ID=" + id);
    }

    public static void validate(Resource r) {
        if (r == null || r.getName() == null || r.getName().isBlank()
                || r.getType() == null || r.getType().isBlank()) {
            throw new IllegalArgumentException("Назва та тип ресурсу є обов'язковими");
        }
        if (r.getTaskId() <= 0 || r.getQuantity() < 0) {
            throw new IllegalArgumentException("Некоректний ID завдання або кількість ресурсу");
        }
    }
}
