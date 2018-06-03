package com.example.krishna.bukie;

import java.util.Date;

public class Chat {

String message;
String date;


    public Chat(String message)
    {
        this.message = message;
        Date d = new Date();
        date = d.toString();
    }

    public Chat(String message, String date)
    {
        this.date = date;
        this.message = message;
    }
}
