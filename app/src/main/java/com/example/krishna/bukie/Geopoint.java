package com.example.krishna.bukie;

import android.os.Parcel;
import android.os.Parcelable;

public class Geopoint implements Parcelable{
    String latitude;
    String longitude;
    String locality;

    public Geopoint(String latitude, String longitude, String locality) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
    }

    protected Geopoint(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
        locality = in.readString();
    }

    public static final Creator<Geopoint> CREATOR = new Creator<Geopoint>() {
        @Override
        public Geopoint createFromParcel(Parcel in) {
            return new Geopoint(in);
        }

        @Override
        public Geopoint[] newArray(int size) {
            return new Geopoint[size];
        }
    };

    public String getLocality() {
        return locality;
    }

    public Geopoint() {
    }

  /*  public Geopoint(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }*/

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(locality);
    }
}
