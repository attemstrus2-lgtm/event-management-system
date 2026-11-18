package com.eventmanagement.service;

import com.eventmanagement.dao.ResponsiblePersonDao;
import com.eventmanagement.model.ResponsiblePerson;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ResponsiblePersonService {
    private static final Logger LOG = Logger.getLogger(ResponsiblePersonService.class.getName());
    private final ResponsiblePersonDao dao = new ResponsiblePersonDao();

    public List<ResponsiblePerson> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(ResponsiblePerson p) throws SQLException {
        validate(p);
        dao.add(p);
        LOG.info("Додано відповідальну особу: " + p.getFullName());
    }

    public void update(ResponsiblePerson p) throws SQLException {
        validate(p);
        dao.update(p);
        LOG.info("Оновлено відповідальну особу ID=" + p.getId());
    }

    public void delete(int id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено відповідальну особу ID=" + id);
    }

    public static void validate(ResponsiblePerson p) {
        if (p == null || p.getFullName() == null || p.getFullName().isBlank()
                || p.getEmail() == null || p.getEmail().isBlank()
                || p.getPosition() == null || p.getPosition().isBlank()) {
            throw new IllegalArgumentException("Усі поля відповідальної особи є обов'язковими");
        }
    }
}
