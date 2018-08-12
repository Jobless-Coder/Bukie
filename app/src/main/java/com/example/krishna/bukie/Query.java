package com.example.krishna.bukie;

public class Query {
    private String search;

    private Sort[] sort=new Sort[0];
    private Filter[] filter=new Filter[0];

    public Query(String search, Sort[] sort, Filter[] filter) {
        this.search = search;
        this.sort = sort;
        this.filter = filter;
    }

    public String getSearch() {
        return search;
    }

    public Sort[] getSort() {
        return sort;
    }

    public Filter[] getFilter() {
        return filter;
    }

    public Query(String search, Sort[] sort) {
        this.search = search;
        this.sort = sort;
    }

    public Query(String search, Filter[] filter) {
        this.search = search;
        this.filter = filter;
    }
    public String toString2(){
        return "search: "+getSearch()+" sort: "+getSort().toString()+" filter: "+getFilter().toString();
    }
}


