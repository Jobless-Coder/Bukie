package com.example.krishna.bukie.chat;

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private Context context;
    private RecyclerView recyclerView;
    private List<BookAds> bookAdsList;
    private View v;
    private MyChatItemClickListener myChatItemClickListener;
    private String chatid,identity,uid;
    private boolean buyfrag=true;
    private ViewGroup toolbargroup;
    private View toolbarview,buy,sell,tabsview;
    private MyChatsAdapter myChatsAdapter;
    private List<MyChats> myChatsList=new ArrayList<>();
    private List<MyChats> removeMyChatsList=new ArrayList<>();
    private ListenerRegistration listenerRegistration;
    FirebaseFirestoreSettings settings;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    @Override
    public void onDestroyView() {
//        listenerRegistration.remove();
        super.onDestroyView();

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        firebaseFirestore=FirebaseFirestore.getInstance();
        context=getContext();
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
       // db.setFirestoreSettings(settings);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_chat, container,false);
        toolbargroup=getActivity().findViewById(R.id.toolbar_layout);
        toolbargroup.removeAllViews();
        toolbarview= getActivity().getLayoutInflater().inflate(R.layout.toolbar_mychats,toolbargroup,false);
        tabsview=getActivity().findViewById(R.id.header);
        tabsview.setVisibility(View.VISIBLE);
        toolbargroup.addView(toolbarview);
        getActivity().findViewById(R.id.header2).setVisibility(View.GONE);
        getActivity().findViewById(R.id.header3).setVisibility(View.GONE);
        //toolbargroup.findViewById(R.id.header).setVisibility(View.VISIBLE);
      /*  FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);*/
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
        setHasOptionsMenu(true);


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
                //if (myChats.isActive()) {
                    Date d = new Date(myChats.getLast_message().getTime());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                    final String timecast = simpleDateFormat.format(d);

                    //added by Krishna : 21st August, 2018
                    //the following code makes sure unread texts are highlighted
                    if(!myChats.last_message.getStatus().equalsIgnoreCase("seen"))
                    {
                        SharedPreferences sharedPreferences=getContext().getSharedPreferences("UserInfo",MODE_PRIVATE);
                        uid=sharedPreferences.getString("uid",null);
                        if(!myChats.last_message.getSender().equals(uid))
                        {
                            holder.message.setTypeface(null, Typeface.BOLD);
                            holder.message.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
                            holder.username.setTypeface(null, Typeface.BOLD);
                            holder.time.setTypeface(null, Typeface.BOLD);
                            holder.time.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));

                        }
                    }
                    //Toast.makeText(context, "hello"+myChats.getBuyerid()+identityuser, Toast.LENGTH_SHORT).show();
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
                                           /* if (myChats.getLast_message().getType().equals("message"))
                                                holder.message.setText("You :" + myChats.getLast_message().getMessage_body());
                                            else if (myChats.getLast_message().getType().equals("gallery"))
                                                holder.message.setText("You : sent photos");
                                            else if (myChats.getLast_message().getType().equals("location"))
                                                holder.message.setText("You : sent a location");
                                            else if (myChats.getLast_message().getType().equals("contact"))
                                                holder.message.setText("You : sent a contact");
                                            else
                                                holder.message.setText("You : sent a photo");*/
                                        } else {

                                            holder.status.setVisibility(View.GONE);
                                            holder.message.setText(myChats.getLast_message().getMessage_body());
                                            /*if (myChats.getLast_message().getType().equals("message"))
                                                holder.message.setText(myChats.getLast_message().getMessage_body());
                                            else if (myChats.getLast_message().getType().equals("gallery"))
                                                holder.message.setText("Sent photos");
                                            else if (myChats.getLast_message().getType().equals("location"))
                                                holder.message.setText("Sent a location");
                                            else if (myChats.getLast_message().getType().equals("contact"))
                                                holder.message.setText("Sent a contact");
                                            else
                                                holder.message.setText("Sent a photo");*/


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
                            /*if (myChats.getLast_message().getType().equals("message"))
                                holder.message.setText("You :" + myChats.getLast_message().getMessage_body());
                            else if (myChats.getLast_message().getType().equals("gallery"))
                                holder.message.setText("You : sent photos");
                            else if (myChats.getLast_message().getType().equals("location"))
                                holder.message.setText("You : sent a location");
                            else if (myChats.getLast_message().getType().equals("contact"))
                                holder.message.setText("You : sent a contact");
                            else
                                holder.message.setText("You : sent a photo");*/
                        } else {
                            holder.status.setVisibility(View.GONE);
                            holder.message.setText(myChats.getLast_message().getMessage_body());
                           /* if (myChats.getLast_message().getType().equals("message"))
                                holder.message.setText(myChats.getLast_message().getMessage_body());
                            else if (myChats.getLast_message().getType().equals("gallery"))
                                holder.message.setText("Sent photos");
                            else if (myChats.getLast_message().getType().equals("location"))
                                holder.message.setText("Sent a location");
                            else if (myChats.getLast_message().getType().equals("contact"))
                                holder.message.setText("Sent a contact");
                            else
                                holder.message.setText("Sent a photo");*/
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
                            //Toast.makeText(context, "hello"+identity+myChats.getBuyerfullname(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("mychats", myChats);
                            intent.putExtra("identity", identity);
                          //  intent.putExtra("isMap", "0");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            //for temporary fixing highlighted chats and converting them to normal

                            TextView username,time,message;
                            username=v.findViewById(R.id.username);
                            time=v.findViewById(R.id.time);
                            message=v.findViewById(R.id.message);
                            username.setTypeface(Typeface.DEFAULT);
                            time.setTypeface(Typeface.DEFAULT);
                            message.setTypeface(Typeface.DEFAULT);
                            //refresh();

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
    public void refresh(){
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
    public class MyChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ppic,adpic,status;
        public TextView username,time,message;
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
                  // listenerRegistration.remove();
                    firebaseRecyclerAdapter.stopListening();

                   getMyChats("buyerid");

                }

                break;
            case R.id.sell:
                if(buyfrag==true){
                    buy.setSelected(false);
                    sell.setSelected(true);
                    buyfrag=false;
                  // listenerRegistration.remove();
                    firebaseRecyclerAdapter.stopListening();
                   getMyChats("sellerid");
                }


                break;
            default:
                break;
        }


    }





}
