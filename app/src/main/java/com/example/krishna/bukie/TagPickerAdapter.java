package com.example.krishna.bukie;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TagPickerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> tagList = new ArrayList<>();
    private List<Boolean> isTagList = new ArrayList<>();

    public TagPickerAdapter(@NonNull Context context,  List<String> tagList,List<Boolean>isTaglist) {
        super(context, 0 , tagList);
        mContext = context;
        //tagList = tagList;
        this.tagList=tagList;
        this.isTagList=isTaglist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.view_tags_pick,parent,false);

        String currentTag = tagList.get(position);

        /*ImageView image = (ImageView)listItem.findViewById(R.id.imageView_poster);
        image.setImageResource(currentMovie.getmImageDrawable());*/

        TextView textView = (TextView) listItem.findViewById(R.id.textView);
        textView.setText(currentTag);
        CheckBox checkBox=listItem.findViewById(R.id.checkbox);
        Log.i("ghk",isTagList.toString());
        checkBox.setChecked(isTagList.get(position));
        /*TextView release = (TextView) listItem.findViewById(R.id.textView_release);
        release.setText(currentMovie.getmRelease());*/

        return listItem;
    }
}

