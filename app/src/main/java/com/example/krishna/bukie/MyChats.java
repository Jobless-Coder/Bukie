package com.example.krishna.bukie;

import android.os.Parcel;
import android.os.Parcelable;

public class MyChats implements Parcelable{
    private  String seller;
    private String buyer;
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

    public MyChats(String seller, String buyer, String adid, String coverpic, String chatid, String sellerpic, String buyerpic, String sellerfullname, String buyerfullname) {
        this.seller = seller;
        this.buyer = buyer;
        this.adid = adid;
        this.coverpic = coverpic;
        this.chatid = chatid;
        this.sellerpic = sellerpic;
        this.buyerpic = buyerpic;
        this.sellerfullname = sellerfullname;
        this.buyerfullname = buyerfullname;
    }

   /* public MyChats(String seller, String buyer, String adid, String coverpic, String chatid, String sellerpic, String buyerpic) {
        this.seller = seller;
        this.buyer = buyer;
        this.adid = adid;
        this.coverpic = coverpic;
        this.chatid = chatid;
        this.sellerpic = sellerpic;
        this.buyerpic = buyerpic;
    }*/


    public MyChats(){

    }

    protected MyChats(Parcel in) {
        seller = in.readString();
        buyer = in.readString();
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
    /* public MyChats(String seller, String buyer, String adid, String coverpic) {
        this.seller = seller;
        buyer = buyer;
        this.adid = adid;

        this.coverpic = coverpic;
    }*/

    public String getSeller() {
        return seller;
    }

    public String getBuyer() {
        return buyer;
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
        dest.writeString(seller);
        dest.writeString(buyer);
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
