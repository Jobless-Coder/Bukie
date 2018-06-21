package com.example.krishna.bukie.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.ChatActivity;
import com.example.krishna.bukie.DisplayAdActivity;
import com.example.krishna.bukie.MyChats;
import com.example.krishna.bukie.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ChatFragment extends Fragment {
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private Context context;
    private RecyclerView recyclerView;
    private List<BookAds> bookAdsList;
    private View v;
    private MyChatItemClickListener myChatItemClickListener;
    private String chatid,identity;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        firebaseFirestore=FirebaseFirestore.getInstance();
        context=getContext();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_chat, container,false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getMyChats();

        setHasOptionsMenu(true);


        return v;
    }

    private void getMyChats() {
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        final String username=sharedPreferences.getString("username",null);
        Query query = firebaseFirestore.collection("users").document(username).collection("mychats");
        FirestoreRecyclerOptions<MyChats> response = new FirestoreRecyclerOptions.Builder<MyChats>()
                .setQuery(query, MyChats.class)
                .build();
        firestoreRecyclerAdapter=new FirestoreRecyclerAdapter<MyChats, ChatFragment.MyChatHolder>(response) {
            @Override
            public void onBindViewHolder(final ChatFragment.MyChatHolder holder, int position, MyChats model)
            {

                final MyChats myChats=model;
                //holder.ppcard.setCard
                if(holder.username.getBackground()!=null) {
                    holder.shimmerFrameLayout.startShimmerAnimation();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            holder.shimmerFrameLayout.stopShimmerAnimation();
                            holder.ppcard.setCardBackgroundColor(Color.WHITE);
                            holder.username.setBackground(null);
                            Glide.with(context)
                                    .load(myChats.getCoverpic())
                                    .into(holder.ppic);
                            if(myChats.getBuyer().compareTo(username)==0)
                            holder.username.setText(myChats.getSeller());
                            else
                                holder.username.setText(myChats.getBuyer());
                        }
                    }, 1000);
                }
                else{
            /*holder.bookcategory.setBackground(null);
            holder.bookpic.setBackground(null);
            holder.bookdate.setBackground(null);
            holder.booktitle.setBackground(null);
            holder.bookprice.setBackground(null);*/
                    Glide.with(context)
                            .load(myChats.getCoverpic())
                            .into(holder.ppic);
                    if(myChats.getBuyer().compareTo(username)==0) {
                        identity = "buyer";
                        holder.username.setText(myChats.getSeller());
                    }
                    else
                    {
                        identity = "seller";
                        holder.username.setText(myChats.getBuyer());
                    }
                    }

                holder.chatlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        // Toast.makeText(context, ""+bookAds.getBookcategory(), Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(context, ChatActivity.class);
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("mychats", myChats);
                        intent.putExtra("identity", identity);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        //intent.putExtra("chatid", chatid);


                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @NonNull
            @Override
            public ChatFragment.MyChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               /* View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);

                return new BookHolder(view);*/
                View v= LayoutInflater.from(getContext())
                        .inflate(R.layout.mychatview,parent,false);
                final MyChatHolder myChatHolder=new MyChatHolder(v);

                return myChatHolder;
            }
        };
        firestoreRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }
    public class MyChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ppic;
        public TextView username;
        public ShimmerFrameLayout shimmerFrameLayout;
        public CardView chatlayout,ppcard;

        public MyChatHolder(View itemView) {
            super(itemView);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerlayout);
            chatlayout=itemView.findViewById(R.id.chatlayout);
            ppic=itemView.findViewById(R.id.profilepic);
            username=itemView.findViewById(R.id.username);
            ppcard=itemView.findViewById(R.id.ppcard);


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
