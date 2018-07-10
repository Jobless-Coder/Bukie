package com.example.krishna.bukie;

import java.util.List;

public class MessageItem {
    private String message_body;
    private String time;
    private String username;
    private String timestamp;
    public Geopoint geopoint;

    public Geopoint getGeopoint() {
        return geopoint;
    }

    public MessageItem(String time, String username, String timestamp, Geopoint geoPoint, String type) {
        this.time = time;
        this.username = username;
        this.timestamp = timestamp;
        this.geopoint=geoPoint;
        this.type = type;
    }

    public List<String> getImageurl() {
        return imageurl;
    }

    Contact contact;
     private String type;
     private List<String> imageurl;

    public MessageItem(String time, String username, String timestamp, String type, List<String> imageurl) {
        this.time = time;
        this.username = username;
        this.timestamp = timestamp;
        this.type = type;
        this.imageurl = imageurl;
    }

    public String getType() {
        return type;
    }

    public MessageItem(String message_body, String time, String username, String timestamp, String type) {
        this.message_body = message_body;
        this.time = time;
        this.username = username;
        this.timestamp = timestamp;
        this.type=type;

    }

    public Contact getContact() {
        return contact;
    }

    public MessageItem(String time, String username, String timestamp, Contact contact, String type) {
        this.time = time;
        this.username = username;
        this.timestamp = timestamp;
        this.contact = contact;
        this.type = type;
    }

    public MessageItem(String message_body, String time, String username) {
        this.message_body = message_body;
        this.time = time;
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public MessageItem()
    {
        //Savour yourself, Firebase!
    }


    public String getMessage_body() {
        return message_body;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }
}
