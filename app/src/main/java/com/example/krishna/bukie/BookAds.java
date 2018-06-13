package com.example.krishna.bukie;

import java.util.Date;

public class BookAds {

/*
* Objects of this class, encapsulates a single chat message
* There are two constructors, consider using the BookAds(String message)
* This constructor adds timestamp for current date and time automatically in contrast to other one with two parameters
* The default constructor is purely for use of firebase, and not recommended for any kind of usage.
*/

public String bookpic;
public String date;
public String booktitle;
public String price;
public String bookcategory;

    public BookAds(String bookpic, String date, String booktitle, String price, String bookcategory) {
        this.bookpic = bookpic;
        this.date = date;
        this.booktitle = booktitle;
        this.price = price;
        this.bookcategory = bookcategory;
    }

    public String getBookpic() {
        return bookpic;
    }

    public String getDate() {
        return date;
    }

    public String getBooktitle() {
        return booktitle;
    }

    public String getPrice() {
        return price;
    }

    public String getBookcategory() {
        return bookcategory;
    }
}
