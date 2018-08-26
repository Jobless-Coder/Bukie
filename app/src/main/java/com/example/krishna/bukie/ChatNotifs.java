package com.example.krishna.bukie;

public class ChatNotifs
{
    String message,receiver_uid,identity;
    MyChatsStatus myChatsStatus;

    public String getIdentity() {
        return identity;
    }

    public MyChatsStatus getMyChatsStatus() {
        return myChatsStatus;
    }


    public ChatNotifs(String message, String receiver_uid,MyChatsStatus myChatsStatus,String identity) {
        this.message = message;
        this.receiver_uid = receiver_uid;
        this.identity=identity;
        this.myChatsStatus=myChatsStatus;
    }

    public String getMessage() {
        return message;
    }


    public String getReceiver_uid() {
        return receiver_uid;
    }

}
