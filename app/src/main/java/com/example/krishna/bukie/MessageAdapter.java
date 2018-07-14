package com.example.krishna.bukie;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int TYPE_MESSAGE = 0 ;
    private static final int TYPE_CONTACT = 1;
    private static final int TYPE_CAMERA = 2;
    private static final int TYPE_LOCATION = 3;
    private List<MessageItem> messageItemList;
   private Context context;
   private View itemView2;
   String previous_user="-1",previous_user2;
   String current_user="";
   private String type;
   private MessageItemClickListener onClickListener;

    private MessageItem messageItem;

    public MessageAdapter(List<MessageItem> messageItemList, Context context,MessageItemClickListener messageItemClickListener) {
        this.messageItemList = messageItemList;
        this.context = context;
        this.onClickListener=messageItemClickListener;
        //Toast.makeText(context, ""+messageItemList.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        String type=messageItemList.get(position).getType();
        if(type.compareTo("message")==0)
            return TYPE_MESSAGE;
        else if(type.compareTo("contact")==0)

        return  TYPE_CONTACT;
        else if (type.compareTo("camera")==0)
            return TYPE_CAMERA;
        else if(type.compareTo("location")==0)
            return TYPE_LOCATION;
        else
            return -1;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TYPE_MESSAGE:
               view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_message,parent,false);
        return  new MessageViewHolder(view);

            case TYPE_CONTACT:
                view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_contact,parent,false);
                return new ContactViewHolder(view);

            case TYPE_CAMERA:
                view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_camera,parent,false);
                return new CameraViewHolder(view);

            case TYPE_LOCATION:
                view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_location,parent,false);
                return new LocationViewHolder(view);


        }


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
    messageItem=messageItemList.get(position);
    current_user=messageItem.getUsername();
        MessageViewHolder messageViewHolder=null;
        CameraViewHolder cameraViewHolder=null;
        ContactViewHolder contactViewHolder=null;
        LocationViewHolder locationViewHolder=null;
    if (messageItem.getType().compareTo("message")==0)
     messageViewHolder= (MessageViewHolder) holder;
    if(messageItem.getType().equals("camera"))
    cameraViewHolder= (CameraViewHolder) holder;
    if(messageItem.getType().equals("contact"))
    contactViewHolder= (ContactViewHolder) holder;
    if(messageItem.getType().equals("location"))
    locationViewHolder= (LocationViewHolder) holder;

       // Toast.makeText(context, ""+current_user, Toast.LENGTH_SHORT).show();
  //  holder.rlfather.setTag(messageItem.getType()+"");
        SharedPreferences sharedPreferences=context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String username=sharedPreferences.getString("username",null);


    if(position>0) {
        previous_user=messageItemList.get(position - 1).getUsername();
    }
    else
        previous_user="-1";



    if(current_user.compareTo(username)==0&&current_user.compareTo(previous_user)==0)
    {
        if(messageItem.getType().compareTo("message")==0) {

            messageViewHolder.rlson1.setVisibility(View.VISIBLE);
            messageViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            messageViewHolder.time1.setText(messageItem.getTime());
            //messageViewHolder.messagebody1.setVisibility(View.VISIBLE);
            messageViewHolder.messagebody1.setText(messageItem.getMessage_body());

        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson1.setVisibility(View.VISIBLE);
            contactViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            contactViewHolder.time1.setText(messageItem.getTime());
            //contactViewHolder.contactview1.setVisibility(View.VISIBLE);
            contactViewHolder.contactname1.setText(messageItem.getContact().getName());

        }
        if(messageItem.getType().compareTo("camera")==0){
            cameraViewHolder.rlson1.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            cameraViewHolder.time1.setText(messageItem.getTime());
            //holder.cameraview1.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic1);
            // holder.camerapic.setImageResource();
        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson1.setVisibility(View.VISIBLE);
            locationViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            locationViewHolder.time1.setText(messageItem.getTime());
            locationViewHolder.locationview1.setVisibility(View.VISIBLE);
            locationViewHolder.locationdesc1.setText(messageItem.getGeopoint().getLocality());
        }



    }
    else if(current_user.compareTo(username)==0&&(current_user.compareTo(previous_user)!=0)){

        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson1.setVisibility(View.VISIBLE);
            messageViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            messageViewHolder.time1.setText(messageItem.getTime());
            //messageViewHolder.messagebody1.setVisibility(View.VISIBLE);
            messageViewHolder.messagebody1.setText(messageItem.getMessage_body());
        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson1.setVisibility(View.VISIBLE);
            contactViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            contactViewHolder.time1.setText(messageItem.getTime());
            //contactViewHolder.contactview1.setVisibility(View.VISIBLE);
            contactViewHolder.contactname1.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            cameraViewHolder.rlson1.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            cameraViewHolder.time1.setText(messageItem.getTime());
           // cameraViewHolder.cameraview1.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic1);
           // holder.camerapic.setImageResource();
        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson1.setVisibility(View.VISIBLE);
            locationViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            locationViewHolder.time1.setText(messageItem.getTime());
            //locationViewHolder.locationview1.setVisibility(View.VISIBLE);
            locationViewHolder.locationdesc1.setText(messageItem.getGeopoint().getLocality());
        }



    }
    else if(current_user.compareTo(username)!=0&&(current_user.compareTo(previous_user)!=0)){
       // holder.rlson1.setVisibility(View.GONE);

        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson2.setVisibility(View.VISIBLE);
            messageViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            messageViewHolder.time2.setText(messageItem.getTime());
            //messageViewHolder.messagebody2.setVisibility(View.VISIBLE);
            messageViewHolder.messagebody2.setText(messageItem.getMessage_body());

        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson2.setVisibility(View.VISIBLE);
            contactViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            contactViewHolder.time2.setText(messageItem.getTime());
            //contactViewHolder.contactview2.setVisibility(View.VISIBLE);
            contactViewHolder.contactname2.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            cameraViewHolder.rlson2.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            cameraViewHolder.time2.setText(messageItem.getTime());
            //cameraViewHolder.cameraview2.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic2);
            // holder.camerapic.setImageResource();


        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson2.setVisibility(View.VISIBLE);
            locationViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            locationViewHolder.time2.setText(messageItem.getTime());
            //locationViewHolder.locationview2.setVisibility(View.VISIBLE);
            locationViewHolder.locationdesc2.setText(messageItem.getGeopoint().getLocality());
        }

    }
    else if(current_user.compareTo(username)!=0&&current_user.compareTo(previous_user)==0){
       // holder.rlson1.setVisibility(View.GONE);

        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson2.setVisibility(View.VISIBLE);
            messageViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            messageViewHolder.time2.setText(messageItem.getTime());
            //messageViewHolder.messagebody2.setVisibility(View.VISIBLE);
            messageViewHolder.messagebody2.setText(messageItem.getMessage_body());
        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson2.setVisibility(View.VISIBLE);
            contactViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            contactViewHolder.time2.setText(messageItem.getTime());
           // contactViewHolder.contactview2.setVisibility(View.VISIBLE);
            contactViewHolder.contactname2.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            cameraViewHolder.rlson2.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            cameraViewHolder.time2.setText(messageItem.getTime());
            //cameraViewHolder.cameraview2.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic2);
            // holder.camerapic.setImageResource();


        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson2.setVisibility(View.VISIBLE);
            locationViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            locationViewHolder.time2.setText(messageItem.getTime());
            //locationViewHolder.locationview2.setVisibility(View.VISIBLE);
            locationViewHolder.locationdesc2.setText(messageItem.getGeopoint().getLocality());
        }


    }

    }

    @Override
    public int getItemCount() {
        return messageItemList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messagebody1,messagebody2;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messagebody1=itemView.findViewById(R.id.message_body1);
            messagebody2=itemView.findViewById(R.id.message_body2);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            rlson2=itemView.findViewById(R.id.rellayoutson2);
            rlfather=itemView.findViewById(R.id.rellayoutfather);
            time1=itemView.findViewById(R.id.time1);
            time2=itemView.findViewById(R.id.time2);



        }
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder{
        public TextView contactname1,savecontact1,contactname2,savecontact2;
        public ImageView contactphoto1,contactphoto2;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2;

        public ContactViewHolder(View itemView) {
            super(itemView);
            savecontact1=itemView.findViewById(R.id.savecontact1);
            contactname1=itemView.findViewById(R.id.contactname1);
            contactphoto1=itemView.findViewById(R.id.contactphoto1);
            savecontact2=itemView.findViewById(R.id.savecontact2);
            contactname2=itemView.findViewById(R.id.contactname2);
            contactphoto2=itemView.findViewById(R.id.contactphoto2);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            rlson2=itemView.findViewById(R.id.rellayoutson2);
            rlfather=itemView.findViewById(R.id.rellayoutfather);
            time1=itemView.findViewById(R.id.time1);
            time2=itemView.findViewById(R.id.time2);

            savecontact1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onSaveContact(v,getAdapterPosition());
                }
            });
            savecontact2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onSaveContact(v,getAdapterPosition());
                }
            });


        }
    }
    public class LocationViewHolder extends RecyclerView.ViewHolder{
        public TextView locationdesc1;
        public TextView locationdesc2;
        public View locationview1,locationview2;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2;

        public LocationViewHolder(View itemView) {
            super(itemView);
            locationdesc1=itemView.findViewById(R.id.locationdesc1);
            locationdesc2=itemView.findViewById(R.id.locationdesc2);
            locationview1=itemView.findViewById(R.id.locationview1);
            locationview2=itemView.findViewById(R.id.locationview2);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            rlson2=itemView.findViewById(R.id.rellayoutson2);
            rlfather=itemView.findViewById(R.id.rellayoutfather);
            time1=itemView.findViewById(R.id.time1);
            time2=itemView.findViewById(R.id.time2);

            locationview1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onLocation(v,getAdapterPosition());
                }
            });
            locationview2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onLocation(v,getAdapterPosition());
                }
            });


        }
    }
    public class CameraViewHolder extends RecyclerView.ViewHolder{
        public ImageView camerapic2,camerapic1;
        public View cameraview1,cameraview2;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2;

        public CameraViewHolder(View itemView) {
            super(itemView);
            camerapic2=itemView.findViewById(R.id.camerapic2);
            camerapic1=itemView.findViewById(R.id.camerapic1);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            rlson2=itemView.findViewById(R.id.rellayoutson2);
            rlfather=itemView.findViewById(R.id.rellayoutfather);
            time1=itemView.findViewById(R.id.time1);
            time2=itemView.findViewById(R.id.time2);

            /*cameraview1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onCameraImage(v,getAdapterPosition());
                }
            });
            cameraview2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onCameraImage(v,getAdapterPosition());
                }
            });*/

        }
    }
}
