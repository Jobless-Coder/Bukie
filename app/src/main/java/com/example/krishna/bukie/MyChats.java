package com.example.krishna.bukie;

import android.os.Parcel;
import android.os.Parcelable;

public class MyChats implements Parcelable{
    private  String sellerid;
    private String buyerid;
    private String adid;
    private String coverpic;
    private String chatid;
    private String sellerpic;
    private String buyerpic;
    private String sellerfullname;
    private String buyerfullname;
    //private int i;

    public String getSellerfullname() {
        return sellerfullname;
    }

    public String getBuyerfullname() {
        return buyerfullname;
    }

    public MyChats(String sellerid, String buyer, String adid, String coverpic, String chatid, String sellerpic, String buyerpic, String sellerfullname, String buyerfullname) {
        this.sellerid = sellerid;
        this.buyerid = buyer;
        this.adid = adid;
        this.coverpic = coverpic;
        this.chatid = chatid;
        this.sellerpic = sellerpic;
        this.buyerpic = buyerpic;
        this.sellerfullname = sellerfullname;
        this.buyerfullname = buyerfullname;
    }

   /* public MyChats(String sellerid, String buyerid, String adid, String coverpic, String chatid, String sellerpic, String buyerpic) {
        this.sellerid = sellerid;
        this.buyerid = buyerid;
        this.adid = adid;
        this.coverpic = coverpic;
        this.chatid = chatid;
        this.sellerpic = sellerpic;
        this.buyerpic = buyerpic;
    }*/


    public MyChats(){

    }

    protected MyChats(Parcel in) {
        sellerid = in.readString();
        buyerid = in.readString();
        adid = in.readString();
        coverpic = in.readString();
        chatid = in.readString();
        buyerpic=in.readString();
        sellerpic=in.readString();
        sellerfullname = in.readString();
        buyerfullname = in.readString();
    }

    public static final Creator<MyChats> CREATOR = new Creator<MyChats>() {
        @Override
        public MyChats createFromParcel(Parcel in) {
            return new MyChats(in);
        }

        @Override
        public MyChats[] newArray(int size) {
            return new MyChats[size];
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
    /* public MyChats(String sellerid, String buyerid, String adid, String coverpic) {
        this.sellerid = sellerid;
        buyerid = buyerid;
        this.adid = adid;

        this.coverpic = coverpic;
    }*/

    public String getSellerid() {
        return sellerid;
    }

    public String getBuyerid() {
        return buyerid;
    }

    public String getAdid() {
        return adid;
    }

    public String getCoverpic() {
        return coverpic;
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
        dest.writeString(coverpic);
        dest.writeString(chatid);
        dest.writeString(buyerpic);
        dest.writeString(sellerpic);
        dest.writeString(sellerfullname);
        dest.writeString(buyerfullname);
    }
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof MyChats)) return false;
        MyChats o = (MyChats) obj;
        return this.getChatid() .equals(o.getChatid());
       // o.buyerfullname=this.buyerfullname;

    }
}
