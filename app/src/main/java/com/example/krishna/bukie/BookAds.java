package com.example.krishna.bukie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class BookAds implements Parcelable{


public String coverpic;
public String date;
public String booktitle;
public String price;
public String bookcategory;
public String bookpic;
public List<String> bookpicslist=new ArrayList<String>();

public BookAds(){

    }

    public String getCoverpic() {
        return coverpic;
    }

    public BookAds(String date, String booktitle, String price, String bookcategory, List<String> bookpicslist, String coverpic, String bookpic) {
        this.date = date;
        this.booktitle = booktitle;
        this.price = price;
        this.bookcategory = bookcategory;
        this.bookpicslist = bookpicslist;
        this.coverpic=coverpic;
        this.bookpic=bookpic;

    }

    public List<String> getBookpicslist() {
        return bookpicslist;
    }


    protected BookAds(Parcel in){
        this.date = in.readString();
        this.booktitle = in.readString();
        this.bookcategory = in.readString();
        this.price = in.readString();
        this.coverpic = in.readString();
        this.bookpicslist = new ArrayList<String>();
        in.readList(bookpicslist,String.class.getClassLoader());
        //bookpicslist=in.readList(boo);
    }
    public static final Creator<BookAds> CREATOR = new Creator<BookAds>() {
        @Override
        public BookAds createFromParcel(Parcel in) {
            return new BookAds(in);
        }

        @Override
        public BookAds[] newArray(int size) {
            return new BookAds[size];
        }
    };

    public String getBookpic() {
        return coverpic;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.booktitle);
        dest.writeString(this.bookcategory);
        dest.writeString(this.price);
        dest.writeString(this.coverpic);
        dest.writeList(this.bookpicslist);

    }
}
