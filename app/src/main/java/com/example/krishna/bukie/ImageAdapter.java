package com.example.krishna.bukie;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> images;

    // Constructor
    public ImageAdapter(Context c,List<String> images) {
        mContext = c;
        this.images=images;
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            /*imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200,200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);*/
            //imageView.setPadding(8, 8, 8, 8);
            //grid = new View(mContext);
            grid = inflater.inflate(R.layout.view_single_grid, null);

            ImageView imageView = (ImageView)grid.findViewById(R.id.image);

            //imageView.setImageResource(Imageid[position]);
           // Glide.clear(imageView);
            Glide.with(mContext).load(images.get(position)).into(imageView);
            // imageView.setImageResource(images[position]);
        }
        else
        {
            grid = (View) convertView;
        }

        return grid;
    }


    // Keep all Images in array

}
