package com.example.krishna.bukie;

public class Contact {
    private String name,phoneno,picture;

    public Contact(String name, String phoneno, String picture) {
        this.name = name;
        this.phoneno = phoneno;
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
    }

    public Contact() {
    }

    public Contact(String name, String phoneno) {
        this.name = name;
        this.phoneno = phoneno;
    }

    public String getName() {
        return name;
    }

    public String getPhoneno() {
        return phoneno;
    }
}
