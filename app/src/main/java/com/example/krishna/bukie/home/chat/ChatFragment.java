package com.example.krishna.bukie.home.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.ChatActivity;
import com.example.krishna.bukie.MyChats;
import com.example.krishna.bukie.MyChatsAdapter;
import com.example.krishna.bukie.MyChatsStatus;
import com.example.krishna.bukie.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private RecyclerView recyclerView;
    private View v;
    private MyChatItemClickListener myChatItemClickListener;
    private String chatid,identity,uid;
    private boolean buyfrag=true;
    private View buy,sell,tabsview;
    private MyChatsAdapter myChatsAdapter;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private long oneday=86400000l, onemonth=2592000000l,oneyear=31536000000l;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_chat, container,false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("My Chats");
        tabsview=getActivity().findViewById(R.id.header);
        tabsview.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.header2).setVisibility(View.GONE);
        buy=tabsview.findViewById(R.id.buy);
        sell=tabsview.findViewById(R.id.sell);
        buy.setOnClickListener(this);
       sell.setOnClickListener(this);
       sell.setSelected(false);
        buy.setSelected(true);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);

        getMyChats("buyerid");

        return v;
    }


       private void getMyChats(final String identityuser) {

        com.google.firebase.database.Query query = firebaseDatabase.getReference().child("chat_status").orderByChild(identityuser+"_isactive").equalTo(uid+"_true");
        FirebaseRecyclerOptions<MyChatsStatus> options =
                new FirebaseRecyclerOptions.Builder<MyChatsStatus>()
                        .setQuery(query, MyChatsStatus.class)
                        .build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<MyChatsStatus,MyChatHolder>(options) {
            @Override
            public void onBindViewHolder(final MyChatHolder holder, int position, final MyChatsStatus model) {
                final MyChatsStatus myChats = model;
                final String timecast;
                Date currentDate=new Date();
                    Date d = new Date(myChats.getLast_message().getTime());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                    long timeinms=currentDate.getTime()-d.getTime();
                    if(timeinms/oneyear>0) {
                        if(timeinms/oneyear>1)
                            timecast = timeinms / oneyear + " years";
                        else
                        timecast = timeinms / oneyear + " year";

                    }
                    else if(timeinms/onemonth>0) {
                        if(timeinms/onemonth>1)
                            timecast = timeinms / onemonth + " months";
                        else
                            timecast = timeinms / onemonth + " month";
                    }
                    else if(timeinms/oneday>0) {
                        if(timeinms/oneday>1)
                            timecast = timeinms / oneday + " days";
                        else
                        timecast = timeinms / oneday + " day";
                    }

                    else
                    timecast = simpleDateFormat.format(d);

                //added by Krishna : 21st August, 2018
                //the following code makes sure unread texts are highlighted
                //fixed by Indraanil :26th August, 2018
                //deleted onClick and setTypeface to default on seen
                if(!myChats.getLast_message().getSender().equals(uid)&&!myChats.getLast_message().getStatus().equals("seen")){
                    holder.message.setTypeface(null, Typeface.BOLD);
                    holder.message.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
                    holder.username.setTypeface(null, Typeface.BOLD);
                    holder.time.setTypeface(null, Typeface.BOLD);
                    holder.time.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
                }
                else {
                    holder.message.setTypeface(Typeface.DEFAULT);
                    holder.message.setTextColor(ResourcesCompat.getColor(getResources(), R.color.deep_grey, null));
                    holder.username.setTypeface( Typeface.DEFAULT);
                    holder.time.setTypeface( Typeface.DEFAULT);
                    holder.time.setTextColor(ResourcesCompat.getColor(getResources(), R.color.deep_grey, null));
                }


                    if (holder.username.getBackground() != null) {
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
                                        holder.ppcard2.setCardBackgroundColor(Color.WHITE);
                                        holder.username.setBackground(null);
                                        holder.status.setBackground(null);
                                        holder.time.setBackground(null);
                                        holder.message.setBackground(null);
                                        holder.time.setText(timecast);
                                        if (myChats.getLast_message().getSender().equals(uid)) {
                                            holder.status.setVisibility(View.VISIBLE);
                                            if (myChats.getLast_message().getStatus().equals("sent")) {
                                                holder.status.setImageResource(R.drawable.ic_text_sent);
                                                holder.status.setColorFilter(ContextCompat.getColor(context, R.color.deep_grey), PorterDuff.Mode.SRC_IN);
                                            } else {
                                                holder.status.setImageResource(R.drawable.ic_text_seen);
                                                holder.status.setColorFilter(ContextCompat.getColor(context, R.color.deep_blue), PorterDuff.Mode.SRC_IN);
                                            }
                                            holder.message.setText("You : " + myChats.getLast_message().getMessage_body());

                                        } else {

                                            holder.status.setVisibility(View.GONE);
                                            holder.message.setText(myChats.getLast_message().getMessage_body());



                                        }

                                        if (myChats.getBuyerid().compareTo(uid) == 0) {
                                            identity = "buyer";
                                            holder.username.setText(myChats.getSellerfullname());
                                            Glide.with(context).load(myChats.getSellerpic()).into(holder.ppic);
                                        } else {
                                            identity = "seller";
                                            holder.username.setText(myChats.getBuyerfullname());
                                            Glide.with(context).load(myChats.getBuyerpic()).into(holder.ppic);
                                        }

                                        return false;
                                    }
                                })
                                .into(holder.adpic);


                    } else {
                        holder.time.setText(timecast);
                        if (myChats.getLast_message().getSender().equals(uid)) {
                            holder.status.setVisibility(View.VISIBLE);
                            if (myChats.getLast_message().getStatus().equals("sent")) {
                                holder.status.setImageResource(R.drawable.ic_text_sent);
                                holder.status.setColorFilter(ContextCompat.getColor(context, R.color.deep_grey), PorterDuff.Mode.SRC_IN);
                            } else {
                                holder.status.setImageResource(R.drawable.ic_text_seen);
                                holder.status.setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.SRC_IN);
                            }
                            holder.message.setText("You : "+myChats.getLast_message().getMessage_body());

                        } else {
                            holder.status.setVisibility(View.GONE);
                            holder.message.setText(myChats.getLast_message().getMessage_body());

                        }

                        Glide.with(context)
                                .load(myChats.getCoverpic())
                                .into(holder.adpic);
                        if (myChats.getBuyerid().compareTo(uid) == 0) {
                            identity = "buyer";
                            holder.username.setText(myChats.getSellerfullname());
                            Glide.with(context).load(myChats.getSellerpic()).into(holder.ppic);

                        } else {
                            identity = "seller";
                            holder.username.setText(myChats.getBuyerfullname());
                            Glide.with(context).load(myChats.getBuyerpic()).into(holder.ppic);

                        }

                    }

                    holder.chatlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (myChats.getBuyerid().compareTo(uid) == 0) {
                                identity = "buyer";
                            } else
                                identity = "seller";
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("mychats", myChats);
                            intent.putExtra("identity", identity);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);


                        }

                    });


                }
                @Override
                public int getItemViewType ( int position){
                    return position;
                }

                @Override
                public MyChatHolder onCreateViewHolder (@NonNull ViewGroup parent,int viewType){

                    View v = LayoutInflater.from(getContext())
                            .inflate(R.layout.mychatview, parent, false);
                    final MyChatHolder myChatHolder = new MyChatHolder(v);

                    return myChatHolder;
                }

        };
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    public class MyChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ppic,adpic,status;
        public TextView username,time,message;
        public ShimmerFrameLayout shimmerFrameLayout;
        public CardView chatlayout,ppcard,ppcard2;

        public MyChatHolder(View itemView) {
            super(itemView);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerlayout);
            chatlayout=itemView.findViewById(R.id.chatlayout);
            ppic=itemView.findViewById(R.id.profilepic);
            username=itemView.findViewById(R.id.username);
            ppcard=itemView.findViewById(R.id.ppcard);
            ppcard2=itemView.findViewById(R.id.ppcard2);
            adpic=itemView.findViewById(R.id.adpic);
            status=itemView.findViewById(R.id.status);
            time=itemView.findViewById(R.id.time);
            message=itemView.findViewById(R.id.message);


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
   @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buy:

                if(buyfrag==false){
                    buyfrag=true;
                    buy.setSelected(true);
                    sell.setSelected(false);
                    firebaseRecyclerAdapter.stopListening();

                   getMyChats("buyerid");

                }

                break;
            case R.id.sell:
                if(buyfrag==true){
                    buy.setSelected(false);
                    sell.setSelected(true);
                    buyfrag=false;
                    firebaseRecyclerAdapter.stopListening();
                   getMyChats("sellerid");
                }


                break;
            default:
                break;
        }


    }





}
