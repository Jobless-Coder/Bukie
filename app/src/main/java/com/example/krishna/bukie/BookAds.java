package com.example.krishna.bukie;

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
public List<String> bookpicslist;
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

    public BookAds(String coverpic, String date, String booktitle, String price, String bookcategory) {
        this.coverpic = coverpic;
        this.date = date;
        this.booktitle = booktitle;
        this.price = price;
        this.bookcategory = bookcategory;
    }
    protected BookAds(Parcel in){
        coverpic = in.readString();
        date = in.readString();
        booktitle = in.readString();
       price = in.readString();
        bookcategory = in.readString();
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
        dest.writeString(bookcategory);
        dest.writeString(coverpic);
        dest.writeString(booktitle);
        dest.writeString(date);
        dest.writeString(price);

    }
}
