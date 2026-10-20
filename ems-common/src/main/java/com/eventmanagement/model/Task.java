package com.eventmanagement.model;

import java.time.LocalDateTime;

public class Task {
    private int id;
    private int eventId;
    private int responsibleId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private TaskStatus status = TaskStatus.CREATED;

    // Нормовані фактори (0-100)
    private double f1Closeness;
    private double f2Criticality;
    private double f3Dependent;
    private double integralIndex;

    public Task() {}

    // Метод для розрахунку інтегрального індексу за твоєю формулою
    public void calculateIntegralIndex() {
        this.integralIndex = (0.40 * f1Closeness) + (0.35 * f2Criticality) + (0.25 * f3Dependent);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getResponsibleId() { return responsibleId; }
    public void setResponsibleId(int responsibleId) { this.responsibleId = responsibleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public double getF1Closeness() { return f1Closeness; }
    public void setF1Closeness(double f1Closeness) {
        this.f1Closeness = f1Closeness;
        calculateIntegralIndex();
    }

    public double getF2Criticality() { return f2Criticality; }
    public void setF2Criticality(double f2Criticality) {
        this.f2Criticality = f2Criticality;
        calculateIntegralIndex();
    }

    public double getF3Dependent() { return f3Dependent; }
    public void setF3Dependent(double f3Dependent) {
        this.f3Dependent = f3Dependent;
        calculateIntegralIndex();
    }

    public double getIntegralIndex() { return integralIndex; }
    public void setIntegralIndex(double integralIndex) { this.integralIndex = integralIndex; }
}
