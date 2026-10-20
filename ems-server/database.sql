CREATE DATABASE IF NOT EXISTS event_management_db;
USE event_management_db;

DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS responsible_persons;

CREATE TABLE responsible_persons (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     full_name VARCHAR(150) NOT NULL,
                                     email VARCHAR(100) NOT NULL UNIQUE,
                                     position VARCHAR(100) NOT NULL
);

CREATE TABLE events (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(150) NOT NULL,
                        description TEXT,
                        scheduled_date DATETIME NOT NULL
);

CREATE TABLE resources (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           type VARCHAR(50) NOT NULL,
                           quantity INT NOT NULL DEFAULT 0
);

CREATE TABLE tasks (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       event_id INT NOT NULL,
                       responsible_id INT NOT NULL,
                       title VARCHAR(150) NOT NULL,
                       description TEXT,
                       deadline DATETIME NOT NULL,
                       status ENUM('CREATED', 'ASSIGNED', 'IN_PROGRESS', 'DONE') NOT NULL DEFAULT 'CREATED',
                       f1_closeness DOUBLE NOT NULL DEFAULT 0.0,
                       f2_criticality DOUBLE NOT NULL DEFAULT 0.0,
                       f3_dependent DOUBLE NOT NULL DEFAULT 0.0,
                       integral_index DOUBLE GENERATED ALWAYS AS (0.40 * f1_closeness + 0.35 * f2_criticality + 0.25 * f3_dependent) STORED,
                       CONSTRAINT fk_tasks_events FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                       CONSTRAINT fk_tasks_responsible FOREIGN KEY (responsible_id) REFERENCES responsible_persons(id) ON DELETE CASCADE
);

INSERT INTO responsible_persons (full_name, email, position) VALUES
                                                                 ('Струсь Артем Павлович', 'artem.strus@univ.edu.ua', 'Головний організатор'),
                                                                 ('Іваненко Іван Іванович', 'ivan.ivanenko@univ.edu.ua', 'Технічний спеціаліст');

INSERT INTO events (title, description, scheduled_date) VALUES
    ('ІТ-Конференція 2026', 'Наукова подія.', '2026-11-15 10:00:00');

INSERT INTO resources (name, type, quantity) VALUES
    ('Проектор', 'Мультимедіа', 2);

INSERT INTO tasks (event_id, responsible_id, title, description, deadline, status, f1_closeness, f2_criticality, f3_dependent) VALUES
    (1, 1, 'Налаштування мережі Wi-Fi', 'Перевірити покриття.', '2026-11-15 08:00:00', 'ASSIGNED', 85.0, 90.0, 50.0);
