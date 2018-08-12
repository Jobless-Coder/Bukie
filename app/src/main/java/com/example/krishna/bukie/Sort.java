package com.example.krishna.bukie;

public class Sort {
    private String type;
    private String order;

    public Sort(String type, String order) {
        this.type = type;
        this.order = order;
    }

    public Sort() {
    }

    public String getType() {
        return type;

    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
