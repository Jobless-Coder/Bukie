package com.example.krishna.bukie;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class SquareImageAdapter implements ImageUpdateListener{
    private ArrayList<Uri> images;
    private FlexboxLayout flex;
    private Context context;
    private DisplayMetrics metrics;

    public SquareImageAdapter(Context context, FlexboxLayout flexboxLayout, DisplayMetrics metric)
    {
        metrics = metric;
        this.context = context;
        this.flex = flexboxLayout;
        images = new ArrayList<>();
    }

    public void addImage(Uri image) {
        images.add(image);
        flex.addView(new SquareImageView(context,image,metrics,this),0);
    }

    @Override
    public void removeImage(Uri image) {
        images.remove(image);
    }

    public ArrayList<Uri> getImages() {
        return images;
    }


}
