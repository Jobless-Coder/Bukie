package com.example.krishna.bukie;

public class Query {
    private String search;
    private String type;
    public Query(String search, String type) {
        this.search = search;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getSearch() {
        return search;
    }


}


