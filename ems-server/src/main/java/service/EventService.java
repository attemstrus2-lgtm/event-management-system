package com.eventmanagement.service;

import com.eventmanagement.dao.EventDao;
import com.eventmanagement.model.Event;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class EventService {
    private static final Logger LOG = Logger.getLogger(EventService.class.getName());
    private final EventDao dao = new EventDao();

    public List<Event> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(Event event) throws SQLException {
        validate(event);
        dao.add(event);
        LOG.info("Додано подію: " + event.getTitle());
    }

    public void update(Event event) throws SQLException {
        validate(event);
        dao.update(event);
        LOG.info("Оновлено подію ID=" + event.getId());
    }

    public void delete(int id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено подію ID=" + id);
    }

    public static void validate(Event event) {
        if (event == null || event.getTitle() == null || event.getTitle().isBlank()) {
            throw new IllegalArgumentException("Назва події є обов'язковою");
        }
        if (event.getScheduledDate() == null) {
            throw new IllegalArgumentException("Дата проведення події є обов'язковою");
        }
    }
}
