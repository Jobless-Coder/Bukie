package com.example.krishna.bukie;

import java.util.List;

public class MessageItem {
    private String message_body;
    private String uid; //this is the fucking sender u bitch!
    private long timestamp;
    private Geopoint geopoint;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public Geopoint getGeopoint() {
        return geopoint;
    }

    public MessageItem( String uid, long timestamp, Geopoint geoPoint, String type,String message_body) {
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

    public MessageItem(String uid, long timestamp, String type, List<String> imageurl,String message_body) {

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

    public MessageItem(String message_body, String uid, long timestamp, String type) {
        this.message_body = message_body;
        this.uid = uid;
        this.timestamp = timestamp;
        this.type=type;
        this.status = "sent";
    }

    public Contact getContact() {
        return contact;
    }

    public MessageItem( String uid, long timestamp, Contact contact, String type,String message_body) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.contact = contact;
        this.type = type;
        this.status = "sent";
        this.message_body=message_body;
    }

    public long getTimestamp() {
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


}
