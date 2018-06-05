package com.example.krishna.bukie;

public class MessageItem {
    private String message_body;
    private String time;

    public MessageItem(String message_body, String time) {
        this.message_body = message_body;
        this.time = time;
    }

    public String getMessage_body() {
        return message_body;
    }

    public String getTime() {
        return time;
    }
}
