package com.eventmanagement.model;

public class Resource {
    private int id;
    private String name;
    private String type;
    private int quantity;

    public Resource() {}

    public Resource(int id, String name, String type, int quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
