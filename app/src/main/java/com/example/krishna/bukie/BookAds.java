package com.example.krishna.bukie;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class BookAds implements Parcelable{


private long timestamp;
private String title;
private long price;
private String category;
private String coverpic;
private String publisher;
private String author;
private String desc;
private String sellerid;
private String adid;
private String sellerpic;
private String sellerfullname;
private Integer viewcounter=0;
private List<String> tagList;
private boolean isactive;
private boolean issold;
public String coverpicthumb;


    public String getCoverpicthumb() {
        if(coverpicthumb.equals(""))
            return coverpic;
        return coverpicthumb;
    }

    public void setBookcoverpicthumb(String bookcoverpicthumb) {
        this.coverpicthumb = bookcoverpicthumb;
    }


    public boolean isIsactive() {
        return isactive;
    }

    public boolean isIssold() {
        return issold;
    }

    public Integer getViewcounter() {
        return viewcounter;
    }

    public void setViewcounter(Integer viewcounter) {
        this.viewcounter = viewcounter;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String isbn;

    public BookAds(long timestamp, String title, long price, String category, String coverpic, String publisher, String author, String desc, String sellerid, String adid, String sellerpic, String sellerfullname, List<String> bookpicslist, String isbn, List<String> tagList, boolean isactive, boolean issold) {
        this.timestamp = timestamp;
        this.title = title;
        this.price = price;
        this.category = category;
        this.coverpic = coverpic;
        this.publisher = publisher;
        this.author = author;
        this.desc = desc;
        this.sellerid = sellerid;
        this.adid = adid;
        this.sellerpic = sellerpic;
        this.sellerfullname = sellerfullname;
        this.bookpicslist = bookpicslist;
        this.isbn = isbn;
        this.tagList=tagList;
        this.isactive=isactive;
        this.issold=issold;
        this.coverpicthumb = "";
    }




    public List<String> getTagList() {
        return tagList;
    }

    public String getSellerfullname() {
        return sellerfullname;
    }




    public String getSellerpic() {
        return sellerpic;
    }

    public String getSellerid() {
        return sellerid;
    }

    public String getAdid() {
        return adid;
    }

    public List<String> bookpicslist=new ArrayList<String>();

    public BookAds(){

    }


    public List<String> getBookpicslist() {
        return bookpicslist;
    }


    protected BookAds(Parcel in){
        this.timestamp = in.readLong();
        this.title = in.readString();
        this.category = in.readString();
        this.price = in.readLong();
        this.bookpicslist = new ArrayList<String>();
        in.readList(bookpicslist,String.class.getClassLoader());
        this.adid=in.readString();
        this.sellerid =in.readString();
        this.sellerpic=in.readString();
        this.sellerfullname=in.readString();
        this.coverpic =in.readString();
        this.author =in.readString();
        this.publisher =in.readString();
        this.desc =in.readString();
        this.isbn = in.readString();
        this.coverpicthumb = in.readString();



    }

    public String getCoverpic() {
        return coverpic;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getAuthor() {
        return author;
    }

    public String getDesc() {
        return desc;
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


    public long getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public long getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeString(this.title);
        dest.writeString(this.category);
        dest.writeLong(this.price);
        dest.writeList(this.bookpicslist);
        dest.writeString(this.adid);
        dest.writeString(this.sellerid);
        dest.writeString(this.sellerpic);
        dest.writeString(this.sellerfullname);
        dest.writeString(this.coverpic);
        dest.writeString(this.author);
        dest.writeString(this.publisher);
        dest.writeString(this.desc);
        dest.writeString(this.isbn);
        dest.writeString(this.coverpicthumb);




    }
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof BookAds)) return false;
        BookAds o = (BookAds) obj;
        return this.getAdid() .equals(o.getAdid());

    }


}
