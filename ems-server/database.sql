DROP DATABASE IF EXISTS event_management_db;
CREATE DATABASE event_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE event_management_db;

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

CREATE TABLE tasks (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       event_id INT NOT NULL,
                       responsible_id INT NOT NULL,
                       title VARCHAR(150) NOT NULL,
                       description TEXT,
                       deadline DATETIME NOT NULL,
                       status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
                       f1_closeness DOUBLE NOT NULL DEFAULT 0.0,
                       f2_criticality DOUBLE NOT NULL DEFAULT 0.0,
                       f3_dependent DOUBLE NOT NULL DEFAULT 0.0,
                       integral_index DOUBLE NOT NULL DEFAULT 0.0,
                       CONSTRAINT fk_tasks_events FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                       CONSTRAINT fk_tasks_responsible FOREIGN KEY (responsible_id) REFERENCES responsible_persons(id) ON DELETE CASCADE
);

CREATE TABLE resources (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           task_id INT NOT NULL,
                           name VARCHAR(100) NOT NULL,
                           type VARCHAR(50) NOT NULL,
                           quantity INT NOT NULL DEFAULT 0,
                           CONSTRAINT fk_resources_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

INSERT INTO responsible_persons (full_name, email, position) VALUES
                                                                 ('Струсь Артем Павлович', 'artem.strus@univ.edu.ua', 'Головний організатор'),
                                                                 ('Іваненко Іван Іванович', 'ivan.ivanenko@univ.edu.ua', 'Технічний спеціаліст');

INSERT INTO events (title, description, scheduled_date) VALUES
                                                            ('ІТ-Конференція 2026', 'Щорічна науково-практична подія університету.', '2026-11-15 10:00:00'),
                                                            ('Хакатон KPP-2026', 'Командне змагання з розробки програмного забезпечення.', '2026-12-01 09:00:00');

INSERT INTO tasks (event_id, responsible_id, title, description, deadline, status, f1_closeness, f2_criticality, f3_dependent, integral_index) VALUES
                                                                                                                                                   (1, 1, 'Налаштування мережі Wi-Fi', 'Перевірити покриття в актовій залі.', '2026-11-15 08:00:00', 'ASSIGNED', 85.0, 90.0, 50.0, 78.0),
                                                                                                                                                   (1, 2, 'Підготовка проєктора та звуку', 'Тестування мультимедійного обладнання.', '2026-11-15 09:00:00', 'CREATED', 60.0, 80.0, 40.0, 62.0);

INSERT INTO resources (task_id, name, type, quantity) VALUES
                                                          (1, 'Маршрутизатор Wi-Fi 6', 'Мережеве обладнання', 3),
                                                          (2, 'Проєктор Epson', 'Мультимедіа', 2);
