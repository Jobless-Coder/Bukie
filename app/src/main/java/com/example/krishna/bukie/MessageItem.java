package com.example.krishna.bukie;

public class MessageItem {
    private String message_body;
    private String time;
    private String username;
    private String timestamp;

    public MessageItem(String message_body, String time, String username, String timestamp) {
        this.message_body = message_body;
        this.time = time;
        this.username = username;
        this.timestamp = timestamp;
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
