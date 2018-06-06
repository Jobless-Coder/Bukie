package com.example.krishna.bukie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
   private List<MessageItem> messageItemList;
   private Context context;

    public MyAdapter(List<MessageItem> messageItemList, Context context) {
        this.messageItemList = messageItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message,parent,false);
        return  new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
    MessageItem messageItem=messageItemList.get(position);
    holder.message_body.setText(messageItem.getMessage_body());
    holder.time.setText(messageItem.getTime());
    }

    @Override
    public int getItemCount() {
        return messageItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message_body,time;
        public ViewHolder(View itemView) {
            super(itemView);
            message_body=(TextView)itemView.findViewById(R.id.message_body);
            time=(TextView)itemView.findViewById(R.id.time);
        }
    }
}
