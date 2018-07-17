package com.example.krishna.bukie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.krishna.bukie.Fragments.MyChatItemClickListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;


public class MyChatsAdapter extends RecyclerView.Adapter<MyChatsAdapter.MyChatHolder> {
    List<MyChats> myChatsList;
    Context context;
    String identity,username;
    MyChatItemClickListener myChatItemClickListener;

    public MyChatsAdapter(List<MyChats> myChatsList, Context context,String username) {
        this.myChatsList =myChatsList ;
        this.context = context;
        this.username=username;

    }

    @NonNull
    @Override
    public MyChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context)
                .inflate(R.layout.mychatview,parent,false);
        final MyChatHolder myChatHolder=new MyChatHolder(v);

        return myChatHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyChatHolder holder, int position) {
        final MyChats myChats=myChatsList.get(position);

        //Toast.makeText(context, "hello"+myChats.getBuyer()+identityuser, Toast.LENGTH_SHORT).show();
        if(holder.username.getBackground()!=null) {
            holder.shimmerFrameLayout.startShimmerAnimation();
            Glide.with(context)
                    .load(myChats.getCoverpic())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.shimmerFrameLayout.stopShimmerAnimation();
                            holder.ppcard.setCardBackgroundColor(Color.WHITE);
                            holder.ppcard2.setCardBackgroundColor(Color.WHITE);
                            holder.username.setBackground(null);

                            if(myChats.getBuyer().compareTo(username)==0) {
                                identity = "buyer";
                                holder.username.setText(myChats.getSellerfullname());
                                Glide.with(context).load(myChats.getSellerpic()).into(holder.ppic);
                            }
                            else
                            {
                                identity = "seller";
                                holder.username.setText(myChats.getBuyerfullname());
                                Glide.with(context).load(myChats.getBuyerpic()).into(holder.ppic);
                            }
                            return false;
                        }
                    })
                    .into(holder.adpic);



        }
        else{

            Glide.with(context)
                    .load(myChats.getCoverpic())
                    .into(holder.adpic);
            if(myChats.getBuyer().compareTo(username)==0) {
                identity = "buyer";
                holder.username.setText(myChats.getSellerfullname());
                Glide.with(context).load(myChats.getSellerpic()).into(holder.ppic);

            }
            else
            {
                identity = "seller";
                holder.username.setText(myChats.getBuyerfullname());
                Glide.with(context).load(myChats.getBuyerpic()).into(holder.ppic);

            }
        }

        holder.chatlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(myChats.getBuyer().compareTo(username)==0) {
                    identity = "buyer";
                }
                else
                    identity="seller";
                //Toast.makeText(context, "hello"+identity+myChats.getBuyerfullname(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("mychats", myChats);
                intent.putExtra("identity", identity);
                intent.putExtra("isMap","0");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return myChatsList.size();
    }
    public class MyChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ppic,adpic;
        public TextView username;
        public ShimmerFrameLayout shimmerFrameLayout;
        public CardView chatlayout,ppcard,ppcard2;

        public MyChatHolder(View itemView) {
            super(itemView);
            //Toast.makeText(context, "new chat", Toast.LENGTH_SHORT).show();
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerlayout);
            chatlayout=itemView.findViewById(R.id.chatlayout);
            ppic=itemView.findViewById(R.id.profilepic);
            username=itemView.findViewById(R.id.username);
            ppcard=itemView.findViewById(R.id.ppcard);
            ppcard2=itemView.findViewById(R.id.ppcard2);
            adpic=itemView.findViewById(R.id.adpic);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myChatItemClickListener.onItemClick(v, getAdapterPosition());

                }
            });
        }


        @Override
        public void onClick(View v) {

        }
    }
}
