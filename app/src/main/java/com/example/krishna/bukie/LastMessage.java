package com.example.krishna.bukie;

public class LastMessage {
        private String message_body,type,sender,status;
    private Long time;



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage_body() {

        return message_body;
    }

    public Long getTime() {
        return time;
    }
    public LastMessage(){

    }


    public LastMessage(String message_body, Long time, String type, String sender, String status) {
        this.message_body = message_body;
        this.time = time;
        this.type = type;
        this.sender = sender;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }



   /* @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message_body);
        dest.writeString(time);
        dest.writeString(type);
        dest.writeString(sender);
        dest.writeString(status);
    }*/
}
