package com.example.krishna.bukie;

import android.os.Parcel;
import android.os.Parcelable;

public class MyChats implements Parcelable{
    private  String seller;
    private String buyer;
    private String adid;
    private String coverpic;
    private String chatid;

    public MyChats(String seller, String buyer, String adid, String coverpic, String chatid) {
        this.seller = seller;
        this.buyer = buyer;
        this.adid = adid;
        this.coverpic = coverpic;
        this.chatid = chatid;
    }

    protected MyChats(Parcel in) {
        seller = in.readString();
        buyer = in.readString();
        adid = in.readString();
        coverpic = in.readString();
        chatid = in.readString();
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
    }
}
