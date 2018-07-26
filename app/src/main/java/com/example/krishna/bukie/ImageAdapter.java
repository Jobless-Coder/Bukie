package com.example.krishna.bukie;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
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


    public View getView(final int position, View convertView, ViewGroup parent) {
        View grid;
        ImageView imageView;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {


            grid = inflater.inflate(R.layout.view_single_grid, null);

             imageView = (ImageView)grid.findViewById(R.id.image);


            Glide.with(mContext).load(images.get(position)).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext,ImageViewActivity.class);
                    //  ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(activity,view,"image");
                    intent.putExtra("position",position);
                    intent.putStringArrayListExtra("url", (ArrayList<String>) images);
                    mContext.startActivity(intent/*,activityOptions.toBundle()*/);

                }
            });

        }
        else
        {
            grid = (View) convertView;
        }


        return grid;
    }




}
