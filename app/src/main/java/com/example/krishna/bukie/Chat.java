package com.example.krishna.bukie;

import java.util.Date;

public class Chat {

/*
* Objects of this class, encapsulates a single chat message
* There are two constructors, consider using the Chat(String message)
* This constructor adds timestamp for current date and time automatically in contrast to other one with two parameters
* The default constructor is purely for use of firebase, and not recommended for any kind of usage.
*/

public String message;
public String date;


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

    public String getMessage()
    {
        return this.message;
    }

    public String getDate()
    {
        return this.date;
    }
}
