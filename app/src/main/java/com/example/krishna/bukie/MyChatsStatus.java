package com.example.krishna.bukie;

import android.os.Parcel;
import android.os.Parcelable;

public class    MyChatsStatus implements Parcelable {
    private  String sellerid;
    private String buyerid;
    private String adid;
    private String buyerpic;
    private String sellerfullname;
    private String buyerfullname;
    public LastMessage last_message;
    private String buyerid_isactive;
    private String sellerid_isactive;
    private String coverpicthumb;
    private String chatid;
    public LastMessage getLast_message() {
        return last_message;
    }
    public String getSellerid_isactive() {
        return sellerid_isactive;
    }
    public String getBuyerid_isactive() {
        return buyerid_isactive;
    }
    private String sellerpic;


    public MyChatsStatus(String sellerid, String buyerid, String adid, String coverpic, String chatid, String sellerpic, String buyerpic, String sellerfullname, String buyerfullname, LastMessage last_message) {
        this.sellerid = sellerid;
        this.buyerid = buyerid;
        this.adid = adid;
        this.coverpicthumb = coverpic;
        this.chatid = chatid;
        this.sellerpic = sellerpic;
        this.buyerpic = buyerpic;
        this.sellerfullname = sellerfullname;
        this.buyerfullname = buyerfullname;
        this.last_message = last_message;
    }

    public String getSellerfullname() {
        return sellerfullname;
    }
    public String getBuyerfullname() {
        return buyerfullname;
    }

    public MyChatsStatus(String sellerid, String buyer, String adid, String coverpic, String chatid, String sellerpic, String buyerpic, String sellerfullname, String buyerfullname) {
        this.sellerid = sellerid;
        this.buyerid = buyer;
        this.adid = adid;
        this.coverpicthumb = coverpic;
        this.chatid = chatid;
        this.sellerpic = sellerpic;
        this.buyerpic = buyerpic;
        this.sellerfullname = sellerfullname;
        this.buyerfullname = buyerfullname;
        buyerid_isactive=buyer+"_false";
        sellerid_isactive=sellerid+"_false";
    }

    public MyChatsStatus(){

    }

    protected MyChatsStatus(Parcel in) {
        sellerid = in.readString();
        buyerid = in.readString();
        adid = in.readString();
        coverpicthumb = in.readString();
        chatid = in.readString();
        buyerpic=in.readString();
        sellerpic=in.readString();
        sellerfullname = in.readString();
        buyerfullname = in.readString();
    }

    public static final Creator<MyChatsStatus> CREATOR = new Creator<MyChatsStatus>() {
        @Override
        public MyChatsStatus createFromParcel(Parcel in) {
            return new MyChatsStatus(in);
        }

        @Override
        public MyChatsStatus[] newArray(int size) {
            return new MyChatsStatus[size];
        }
    };

    public String getChatid() {
        return chatid;
    }

    public String getSellerpic() {
        return sellerpic;
    }

    public String getBuyerpic() {
        return buyerpic;
    }

    public String getSellerid() {
        return sellerid;
    }

    public String getBuyerid() {
        return buyerid;
    }

    public String getAdid() {
        return adid;
    }

    public String getCoverpicthumb() {
        return coverpicthumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sellerid);
        dest.writeString(buyerid);
        dest.writeString(adid);
        dest.writeString(coverpicthumb);
        dest.writeString(chatid);
        dest.writeString(buyerpic);
        dest.writeString(sellerpic);
        dest.writeString(sellerfullname);
        dest.writeString(buyerfullname);
       // dest.writeValue(LastMessage.class);
    }



    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof MyChats)) return false;
        MyChatsStatus o = (MyChatsStatus) obj;
        return this.getChatid() .equals(o.getChatid());

    }

}
