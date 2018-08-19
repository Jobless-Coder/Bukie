package com.example.krishna.bukie;

import java.util.List;

public class MessageItem {
    private String message_body;
    private String time;
    private String uid; //this is the fucking sender u bitch!
    private String timestamp;
    public Geopoint geopoint;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String status;

    public Geopoint getGeopoint() {
        return geopoint;
    }

    public MessageItem(String time, String uid, String timestamp, Geopoint geoPoint, String type,String message_body) {
        this.time = time;
        this.uid = uid;
        this.timestamp = timestamp;
        this.geopoint=geoPoint;
        this.type = type;
        this.status = "sent";
        this.message_body=message_body;
    }

    public List<String> getImageurl() {
        return imageurl;
    }

    Contact contact;
     private String type;
     private List<String> imageurl;

    public MessageItem(String time, String uid, String timestamp, String type, List<String> imageurl,String message_body) {
        this.time = time;
        this.uid = uid;
        this.timestamp = timestamp;
        this.type = type;
        this.imageurl = imageurl;
        this.status = "sent";
        this.message_body=message_body;
    }

    public String getType() {
        return type;
    }

    public MessageItem(String message_body, String time, String uid, String timestamp, String type) {
        this.message_body = message_body;
        this.time = time;
        this.uid = uid;
        this.timestamp = timestamp;
        this.type=type;
        this.status = "sent";
    }

    public Contact getContact() {
        return contact;
    }

    public MessageItem(String time, String uid, String timestamp, Contact contact, String type,String message_body) {
        this.time = time;
        this.uid = uid;
        this.timestamp = timestamp;
        this.contact = contact;
        this.type = type;
        this.status = "sent";
        this.message_body=message_body;
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

    public String getUid() {
        return uid;
    }

    public String getTime() {
        return time;
    }
}
