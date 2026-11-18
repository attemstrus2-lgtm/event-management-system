# Інформаційна система організації подій і контролю завдань (Варіант №23)

Курсова робота з дисципліни «Кросплатформене програмування».  
Виконав: студент групи 204-ТН Струсь Артем Павлович.

## Архітектура та технології
- **Java 21**, **Apache Maven** (багатомодульний проєкт: `ems-common`, `ems-server`, `ems-client`)
- **Серверна частина (`ems-server`):** Servlet API 4.0.1 (Apache Tomcat 9.x), шари `Servlet -> Service -> DAO -> JDBC`, MySQL 8.x, Jackson
- **Клієнтська частина (`ems-client`):** JavaFX 21, Apache HttpClient 5, фоновий CSV-імпорт (`JavaFX Task` + `ProgressBar`)
- **Тестування:** JUnit 5 (10 модульних тестів)

## Інструкція запуску
1. **База даних:** виконати скрипт `database.sql` у MySQL (`event_management_db`). За потреби задати змінні середовища `DB_URL`, `DB_USER`, `DB_PASSWORD`.
2. **Сервер:** зібрати проєкт командою `mvn clean install` та розгорнути `ems-server` на Apache Tomcat 9.x з контекстом `/`.
3. **Клієнт:** запустити клас `com.eventmanagement.client.Main` (модуль `ems-client`). Для демонстрації CSV-імпорту використовуються файли `ems-client/sample-data/tasks.csv` та `ems-client/sample-data/resources.csv`.
