package com.example.krishna.bukie;

public class Filter {
    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Filter(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Filter() {
    }

    private String type;
    private String value;

}