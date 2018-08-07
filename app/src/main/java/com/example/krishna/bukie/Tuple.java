package com.example.krishna.bukie;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tuple implements Parcelable {
    public String text;
    public int position;
    public List<Tuple> chipList;

    public Tuple(String text, int position) {

        this.text = text;
        this.position = position;
    }

    public Tuple(List<Tuple> chipList) {
        this.chipList = chipList;
      //
    }

    public List<Tuple> getChipList() {
       // Log.i("hellokk",chipList.get(0).getText());
        return chipList;
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return position == tuple.position &&
                Objects.equals(text, tuple.text);
    }

    @Override
    public int hashCode() {

        return Objects.hash(text, position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       dest.writeString(this.text);
        dest.writeInt(this.position);
        dest.writeList(this.chipList);
    }

    protected Tuple(Parcel in) {
      this.text = in.readString();
       this.position = in.readInt();
        this.chipList = new ArrayList<Tuple>();
        in.readList(this.chipList, Tuple.class.getClassLoader());
    }

    public static final Parcelable.Creator<Tuple> CREATOR = new Parcelable.Creator<Tuple>() {
        @Override
        public Tuple createFromParcel(Parcel source) {
            return new Tuple(source);
        }

        @Override
        public Tuple[] newArray(int size) {
            return new Tuple[size];
        }
    };
}
