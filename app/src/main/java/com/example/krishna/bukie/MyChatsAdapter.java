package com.example.krishna.bukie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.krishna.bukie.chat.MyChatItemClickListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class MyChatsAdapter extends RecyclerView.Adapter<MyChatsAdapter.MyChatHolder> {
    List<MyChats> myChatsList;
    Context context;
    String identity, uid;
    MyChatItemClickListener myChatItemClickListener;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference1,databaseReference2,databaseReference=firebaseDatabase.getReference();
    ValueEventListener valueEventListener;

    public MyChatsAdapter(List<MyChats> myChatsList, Context context,String uid) {
        this.myChatsList =myChatsList ;
        this.context = context;
        this.uid = uid;

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
    public void onBindViewHolder(@NonNull final MyChatHolder holder, final int position) {
        final MyChats myChats=myChatsList.get(position);
        databaseReference1=firebaseDatabase.getReference().child("users");
        databaseReference2=firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid());
        holder.status.setVisibility(View.GONE);
        final String[] hello = new String[1];

        //Toast.makeText(context, "hello"+myChats.getBuyerid()+identityuser, Toast.LENGTH_SHORT).show();
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

                            if(myChats.getBuyerid().compareTo(uid)==0) {
                                identity = "buyer";
                                holder.username.setText(myChats.getSellerfullname());
                                hello[0] =myChats.getSellerid();
                              /*  if(databaseReference1.child(myChats.getSellerid()).child("last_seen").equals("online")){
                                    holder.status.setVisibility(View.VISIBLE);
                                }*/

                                Glide.with(context).load(myChats.getSellerpic()).into(holder.ppic);

                            }
                            else
                            {
                                identity = "seller";
                                hello[0] =myChats.getBuyerid();
                                holder.username.setText(myChats.getBuyerfullname());
                               /* if(databaseReference1.child(myChats.getBuyerid()).child("last_seen").equals("online")){
                                    holder.status.setVisibility(View.VISIBLE);
                                }*/
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
            if(myChats.getBuyerid().compareTo(uid)==0) {
                identity = "buyer";
                holder.username.setText(myChats.getSellerfullname());
                hello[0] =myChats.getSellerid();
                /*ValueEventListener valueEventListener=
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.toString().equals("online")){
                            holder.status.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/
               /* if(){

                }*/
              // databaseReference1.removeEventListener();
                Glide.with(context).load(myChats.getSellerpic()).into(holder.ppic);

            }
            else
            {
                identity = "seller";
                holder.username.setText(myChats.getBuyerfullname());
                hello[0] =myChats.getBuyerid();
                /*DatabaseReference databaseReference=databaseReference1.child(myChats.getSellerid()).child("last_seen");
                ValueEventListener valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.toString().equals("online")){
                            holder.status.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                databaseReference.addListenerForSingleValueEvent(valueEventListener);*/
                /*if(databaseReference1.child(myChats.getBuyerid()).child("last_seen").equals("online")){
                    holder.status.setVisibility(View.VISIBLE);
                }*/
                Glide.with(context).load(myChats.getBuyerpic()).into(holder.ppic);

            }
        }

        holder.chatlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(myChats.getBuyerid().compareTo(uid)==0) {
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
        //String k=hello[0];
        if(hello[0]!=null) {

            databaseReference = databaseReference1.child(hello[0]).child("last_seen");
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue().toString().equals("online")) {
                        //myChatsList.get(position).setSellerfullname("hello");
                       // myChatsList.get(position).setBuyerfullname("hellonoob");
                        Log.i("status","online");

                        holder.status.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            databaseReference.addListenerForSingleValueEvent(valueEventListener);

        }

    }
    public void getStatus(){

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyChatHolder holder) {
        //databaseReference.removeEventListener(valueEventListener);
        super.onViewDetachedFromWindow(holder);
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
        public TextView username,time;
        public ShimmerFrameLayout shimmerFrameLayout;
        public CardView chatlayout,ppcard,ppcard2,status;

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
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        //databaseReference = databaseReference1.child(hello[0]).child("last_seen");
       // databaseReference.removeEventListener(valueEventListener);

        super.onDetachedFromRecyclerView(recyclerView);
    }
}
