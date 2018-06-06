package com.example.krishna.bukie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
   private List<MessageItem> messageItemList;
   private Context context;
   private View itemView2;
   String previous_user="";
   String current_user="";

    public MyAdapter(List<MessageItem> messageItemList, Context context) {
        this.messageItemList = messageItemList;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
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
    current_user=messageItem.getUsername();
    //RelativeLayout rl=itemView2.findViewById(R.id.rellayout);

    if(current_user.compareTo("Indranil")==0&&current_user.compareTo(previous_user)==0)
    {

        holder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
        holder.message_body1.setText(messageItem.getMessage_body());
        holder.time1.setText(messageItem.getTime());
        //holder.rlfather.setGravity(Gravity.RIGHT);
        //holder.lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //holder.lp.gravity=Gravity.RIGHT;
        previous_user=current_user;


    }
    else if(current_user.compareTo("Indranil")==0&&current_user.compareTo(previous_user)!=0){
        holder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
        holder.message_body1.setText(messageItem.getMessage_body());
        holder.time1.setText(messageItem.getTime());
       // holder.rlfather.setGravity(Gravity.RIGHT);
       // holder.lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
       // holder.lp.gravity=Gravity.RIGHT;
        previous_user=current_user;

    }
    else if(current_user.compareTo("Indranil")!=0&&current_user.compareTo(previous_user)!=0){
        holder.rlson1.setVisibility(View.GONE);
        holder.rlson2.setVisibility(View.VISIBLE);
        holder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
        holder.message_body2.setText(messageItem.getMessage_body());
        holder.time2.setText(messageItem.getTime());
        //holder.rlfather.setGravity(Gravity.LEFT);
        //holder.lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        //holder.lp.gravity=Gravity.LEFT;
        previous_user=current_user;

    }
    else if(current_user.compareTo("Indranil")!=0&&current_user.compareTo(previous_user)==0){
        holder.rlson1.setVisibility(View.GONE);
        holder.rlson2.setVisibility(View.VISIBLE);
        holder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
        holder.message_body2.setText(messageItem.getMessage_body());
        holder.time2.setText(messageItem.getTime());
        //holder.rlfather.setGravity(Gravity.LEFT);
        //holder.lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        //holder.lp.gravity=Gravity.LEFT;
        previous_user=current_user;

    }
    /*holder.rlfather.setLayoutParams(holder.lp);
    holder.message_body.setText(messageItem.getMessage_body());
    holder.time.setText(messageItem.getTime());*/
    }

    @Override
    public int getItemCount() {
        if(messageItemList==null)
        {
            messageItemList = new ArrayList<MessageItem>();
        }
        return messageItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message_body1,time1,message_body2,time2;
        public LinearLayout rlfather;
        public RelativeLayout rlson1,rlson2;
        public  LinearLayout.LayoutParams lp;

        public ViewHolder(View itemView) {

            super(itemView);
            //itemView2=itemView;


            message_body1=(TextView)itemView.findViewById(R.id.message_body1);
            time1=(TextView)itemView.findViewById(R.id.time1);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            message_body2=(TextView)itemView.findViewById(R.id.message_body2);
            time2=(TextView)itemView.findViewById(R.id.time2);
            rlson2=(RelativeLayout) itemView.findViewById(R.id.rellayoutson2);
//            rlfather=(LinearLayout) itemView.findViewById(R.id.rellayoutfather);
            //lp = (LinearLayout.LayoutParams) rlson.getLayoutParams();

        }
    }
}
