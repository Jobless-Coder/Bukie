package com.example.krishna.bukie;

import android.content.Context;
import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
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


public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ChipHolder> {

    private Context mContext;
    private List<Tuple> chipList = new ArrayList<>();
    private TagItemListener tagItemListener;

    public ChipAdapter(Context context,List<Tuple> chipList,TagItemListener tagItemListener){
        mContext=context;
        this.chipList=chipList;
        this.tagItemListener=tagItemListener;
    }

    @NonNull
    @Override
    public ChipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_tags_chips,parent,false);
        return  new ChipHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipHolder holder, int position) {
        final Tuple tuple=chipList.get(position);

        holder.textView.setText(tuple.getText());
    }

    @Override
    public int getItemCount() {
        return chipList.size();
    }


    public class ChipHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View close;
        public ChipHolder(View view){
            super(view);
            textView=view.findViewById(R.id.tagText);
            close=view.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tagItemListener.onRemoveClick(chipList.get(getAdapterPosition()).getText(),chipList.get(getAdapterPosition()).getPosition());
                }
            });

        }
    }

}

