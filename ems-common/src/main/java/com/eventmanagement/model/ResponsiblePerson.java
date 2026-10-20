package com.eventmanagement.model;

public class ResponsiblePerson {
    private int id;
    private String fullName;
    private String email;
    private String position;

    public ResponsiblePerson() {}

    public ResponsiblePerson(int id, String fullName, String email, String position) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.position = position;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
