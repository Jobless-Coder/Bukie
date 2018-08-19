package com.example.krishna.bukie;

public class ChatNotifs
{
    String message,receiver_uid,sender_fullname,proilepic;

    /*public ChatNotifs(String message, String receiver_uid, String sender_fullname) {
        this.message = message;
        this.receiver_uid = receiver_uid;
        this.sender_fullname = sender_fullname;
    }*/

    public String getProilepic() {
        return proilepic;
    }

    public void setProilepic(String proilepic) {
        this.proilepic = proilepic;
    }

    public ChatNotifs(String message, String receiver_uid, String sender_fullname, String proilepic) {
        this.message = message;
        this.receiver_uid = receiver_uid;
        this.sender_fullname = sender_fullname;
        this.proilepic = proilepic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver_uid() {
        return receiver_uid;
    }

    public void setReceiver_uid(String receiver_uid) {
        this.receiver_uid = receiver_uid;
    }

    public String getSender_fullname() {
        return sender_fullname;
    }

    public void setSender_fullname(String sender_fullname) {
        this.sender_fullname = sender_fullname;
    }
}
