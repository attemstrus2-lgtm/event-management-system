package com.eventmanagement.model;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String title;
    private String description;
    private LocalDateTime scheduledDate;

    public Event() {}

    public Event(int id, String title, String description, LocalDateTime scheduledDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.scheduledDate = scheduledDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
}
