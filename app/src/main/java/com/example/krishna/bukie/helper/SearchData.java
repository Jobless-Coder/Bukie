package com.example.krishna.bukie.helper;

public class SearchData {

    public long timestamp;
    public boolean textsearch;
    public String query;
    /*
     * Timestamp to store then this search has occured
     * textsearch is true if it was a text search, false if barcode scan
     * query is the search text may if be string search or isbn
     */

    public SearchData(String query, long timestamp, boolean text)
    {
        this.query = query;
        this.textsearch = text;
        this.timestamp = timestamp;
    }

    //suit yourself Firebase
    public SearchData()
    {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isTextsearch() {
        return textsearch;
    }

    public void setTextsearch(boolean textsearch) {
        this.textsearch = textsearch;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
