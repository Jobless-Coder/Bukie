package com.example.krishna.bukie;

public class ChatNotifs
{
    String message,receiver_uid,sender_fullname,profilepic,identity;
    MyChatsStatus myChatsStatus;

    public String getIdentity() {
        return identity;
    }

    public MyChatsStatus getMyChatsStatus() {
        return myChatsStatus;
    }

    /*public ChatNotifs(String message, String receiver_uid, String sender_fullname) {
        this.message = message;
        this.receiver_uid = receiver_uid;
        this.sender_fullname = sender_fullname;
    }*/

    public String getProilepic() {
        return profilepic;
    }


    public ChatNotifs(String message, String receiver_uid, String sender_fullname, String proilepic,MyChatsStatus myChatsStatus,String identity) {
        this.message = message;
        this.receiver_uid = receiver_uid;
        this.sender_fullname = sender_fullname;
        this.profilepic = proilepic;
        this.identity=identity;
        this.myChatsStatus=myChatsStatus;
    }

    public String getMessage() {
        return message;
    }


    public String getReceiver_uid() {
        return receiver_uid;
    }


    public String getSender_fullname() {
        return sender_fullname;
    }


}
