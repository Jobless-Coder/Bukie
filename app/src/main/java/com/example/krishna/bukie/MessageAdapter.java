package com.example.krishna.bukie;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int TYPE_MESSAGE = 0 ;
    private static final int TYPE_CONTACT = 1;
    private static final int TYPE_CAMERA = 2;
    private static final int TYPE_LOCATION = 3;
    private static final int TYPE_GALLERY = 4;
    private List<MessageItem> messageItemList;
   private Context context;
   private View itemView2;
   String previous_user="-1",previous_user2;
   String current_user="";
   private String type,daydetails;
   private MessageItemClickListener onClickListener;
   private SimpleDateFormat formatter;

    private MessageItem messageItem;
    private Date current_date,previous_date=null;

    public MessageAdapter(List<MessageItem> messageItemList, Context context,MessageItemClickListener messageItemClickListener) {
        this.messageItemList = messageItemList;
        this.context = context;
        this.onClickListener=messageItemClickListener;
        setHasStableIds(true);

    }
    public void SetItemList(List<MessageItem> messageItemList){
        this.messageItemList=messageItemList;
    }
    public int getLastMessagePosition(){
        if(messageItemList!=null)
       return messageItemList.size()-1;
        else
            return -1;

    }

    @Override
    public long getItemId(int position) {
        return messageItemList.get(position).hashCode();
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
            return TYPE_GALLERY;

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
            case TYPE_GALLERY:
                view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_gallery,parent,false);
                return new GalleryVieHolder(view);



        }


        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        Timestamp timestamp;
    messageItem=messageItemList.get(position);
    current_user=messageItem.getUid();
    timestamp=new Timestamp(Long.parseLong(messageItem.getTimestamp()));
    current_date=new Date(timestamp.getTime());
    MessageViewHolder messageViewHolder=null;
    CameraViewHolder cameraViewHolder=null;
    ContactViewHolder contactViewHolder=null;
    LocationViewHolder locationViewHolder=null;
    GalleryVieHolder galleryVieHolder=null;
    if (messageItem.getType().compareTo("message")==0)
     messageViewHolder= (MessageViewHolder) holder;
    if(messageItem.getType().equals("camera"))
    cameraViewHolder= (CameraViewHolder) holder;
    if(messageItem.getType().equals("contact"))
    contactViewHolder= (ContactViewHolder) holder;
    if(messageItem.getType().equals("location"))
    locationViewHolder= (LocationViewHolder) holder;
        if(messageItem.getType().equals("gallery"))
            galleryVieHolder= (GalleryVieHolder) holder;


        SharedPreferences sharedPreferences=context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String uid=sharedPreferences.getString("uid",null);
        Resources r = context.getResources();

    if(position>0) {
        previous_user=messageItemList.get(position - 1).getUid();
         timestamp=new Timestamp(Long.parseLong(messageItemList.get(position-1).getTimestamp()));
        previous_date=new Date(timestamp.getTime());
    }
    else {
        previous_user = "-1";
        previous_date=new Date(0);

    }
    String daydetails2;
        formatter = new SimpleDateFormat("dd MMMM -yy");
        daydetails = formatter.format(current_date);
        daydetails2=formatter.format(previous_date);
    if(daydetails2.compareTo(daydetails)!=0){

        daydetails=daydetails.replace("-","'");
        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.dayll.setVisibility(View.VISIBLE);
            messageViewHolder.day.setText(daydetails);

        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.dayll.setVisibility(View.VISIBLE);
            contactViewHolder.day.setText(daydetails);

        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.dayll.setVisibility(View.VISIBLE);
            locationViewHolder.day.setText(daydetails);

        }
        if(messageItem.getType().compareTo("gallery")==0){
            galleryVieHolder.dayll.setVisibility(View.VISIBLE);
            galleryVieHolder.day.setText(daydetails);

        }
        if(messageItem.getType().compareTo("camera")==0){
            cameraViewHolder.dayll.setVisibility(View.VISIBLE);
            cameraViewHolder.day.setText(daydetails);

        }

    }
    else {
        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.dayll.setVisibility(View.GONE);


        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.dayll.setVisibility(View.GONE);

        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.dayll.setVisibility(View.GONE);

        }
        if(messageItem.getType().compareTo("gallery")==0){
            galleryVieHolder.dayll.setVisibility(View.GONE);

        }
        if(messageItem.getType().compareTo("camera")==0){
            cameraViewHolder.dayll.setVisibility(View.GONE);

        }

    }



    if(current_user.compareTo(uid)==0&&current_user.compareTo(previous_user)==0)
    {
        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson2.setVisibility(View.INVISIBLE);
            messageViewHolder.rlson1.setVisibility(View.VISIBLE);
            messageViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            messageViewHolder.messagebody1.setText(messageItem.getMessage_body());
            messageViewHolder.time1.setText(messageItem.getTime());
            if(messageItem.getStatus().equals("seen"))
                messageViewHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                messageViewHolder.seenicon.setImageResource(R.drawable.ic_text_sent);



        }

        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson2.setVisibility(View.GONE);
            contactViewHolder.rlson1.setVisibility(View.VISIBLE);
            contactViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            contactViewHolder.time1.setText(messageItem.getTime());
            if(messageItem.getContact().getPicture()!=null)
                Glide.with(context).load(messageItem.getContact().getPicture()).into(contactViewHolder.contactphoto1);


            contactViewHolder.contactname1.setText(messageItem.getContact().getName());
            if(messageItem.getStatus().equals("seen"))
                contactViewHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                contactViewHolder.seenicon.setImageResource(R.drawable.ic_text_sent);
        }

        if(messageItem.getType().compareTo("camera")==0){
            Glide.clear(cameraViewHolder.camerapic2);
            cameraViewHolder.rlson2.setVisibility(View.GONE);
            cameraViewHolder.rlson1.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            cameraViewHolder.time1.setText(messageItem.getTime());
            if(messageItem.getStatus().equals("seen"))
                cameraViewHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                cameraViewHolder.seenicon.setImageResource(R.drawable.ic_text_sent);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic1);
        }

        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson2.setVisibility(View.GONE);
            locationViewHolder.rlson1.setVisibility(View.VISIBLE);
            locationViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            locationViewHolder.time1.setText(messageItem.getTime());
            locationViewHolder.locationdesc1.setText(messageItem.getGeopoint().getLocality());
            String mapurl=locationViewHolder.getMapUrl(messageItem.getGeopoint().getLatitude(),messageItem.getGeopoint().getLongitude());
            Glide.clear(locationViewHolder.location1);
            Glide.with(context).load(mapurl).into(locationViewHolder.location1);

            if(messageItem.getStatus().equals("seen"))
                locationViewHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                locationViewHolder.seenicon.setImageResource(R.drawable.ic_text_sent);
            Log.i("mapppy",mapurl);


        }
        if(messageItem.getType().compareTo("gallery")==0){
            galleryVieHolder.rlson2.setVisibility(View.GONE);
            galleryVieHolder.rlson1.setVisibility(View.VISIBLE);
            galleryVieHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            galleryVieHolder.time1.setText(messageItem.getTime());

            if(messageItem.getStatus().equals("seen"))
                galleryVieHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                galleryVieHolder.seenicon.setImageResource(R.drawable.ic_text_sent);

            if(messageItem.getImageurl().size()<3) {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105*messageItem.getImageurl().size(), r.getDisplayMetrics());
                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView1.setNumColumns(messageItem.getImageurl().size());


            }

            else {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, r.getDisplayMetrics());
                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView1.setNumColumns(3);
            }
            galleryVieHolder.gridView1.setAdapter(new ImageAdapter(context,messageItem.getImageurl()));

        }

    }
    else if(current_user.compareTo(uid)==0&&(current_user.compareTo(previous_user)!=0)){

        if(messageItem.getType().compareTo("message")==0) {

            messageViewHolder.rlson2.setVisibility(View.GONE);
            messageViewHolder.rlson1.setVisibility(View.VISIBLE);
            messageViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            messageViewHolder.time1.setText(messageItem.getTime());
            messageViewHolder.messagebody1.setText(messageItem.getMessage_body());
            if(messageItem.getStatus().equals("seen"))
                messageViewHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                messageViewHolder.seenicon.setImageResource(R.drawable.ic_text_sent);


        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson2.setVisibility(View.GONE);
            contactViewHolder.rlson1.setVisibility(View.VISIBLE);
            contactViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            contactViewHolder.time1.setText(messageItem.getTime());
            contactViewHolder.contactname1.setText(messageItem.getContact().getName());
            if(messageItem.getContact().getPicture()!=null)
                Glide.with(context).load(messageItem.getContact().getPicture()).into(contactViewHolder.contactphoto1);

            if(messageItem.getStatus().equals("seen"))
                contactViewHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                contactViewHolder.seenicon.setImageResource(R.drawable.ic_text_sent);


        }
        if(messageItem.getType().compareTo("camera")==0){
            Glide.clear(cameraViewHolder.camerapic1);
            cameraViewHolder.rlson2.setVisibility(View.GONE);
            cameraViewHolder.rlson1.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            cameraViewHolder.time1.setText(messageItem.getTime());
            if(messageItem.getStatus().equals("seen"))
                cameraViewHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                cameraViewHolder.seenicon.setImageResource(R.drawable.ic_text_sent);

            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic1);

        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson2.setVisibility(View.GONE);
            locationViewHolder.rlson1.setVisibility(View.VISIBLE);
            locationViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            locationViewHolder.time1.setText(messageItem.getTime());
            String mapurl=locationViewHolder.getMapUrl(messageItem.getGeopoint().getLatitude(),messageItem.getGeopoint().getLongitude());
            Glide.clear(locationViewHolder.location1);
            Glide.with(context).load(mapurl).into(locationViewHolder.location1);

            if(messageItem.getStatus().equals("seen"))
                locationViewHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                locationViewHolder.seenicon.setImageResource(R.drawable.ic_text_sent);
            locationViewHolder.locationdesc1.setText(messageItem.getGeopoint().getLocality());
        }
        if(messageItem.getType().compareTo("gallery")==0){
            galleryVieHolder.rlson2.setVisibility(View.GONE);
            galleryVieHolder.rlson1.setVisibility(View.VISIBLE);
            galleryVieHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            galleryVieHolder.time1.setText(messageItem.getTime());

            if(messageItem.getStatus().equals("seen"))
                galleryVieHolder.seenicon.setImageResource(R.drawable.ic_text_seen);
            else
                galleryVieHolder.seenicon.setImageResource(R.drawable.ic_text_sent);
            if(messageItem.getImageurl().size()==2) {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105*2, r.getDisplayMetrics());

                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView1.setNumColumns(2);


            }

            else {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, r.getDisplayMetrics());
                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView1.setNumColumns(3);
            }
            galleryVieHolder.gridView1.setAdapter(new ImageAdapter(context,messageItem.getImageurl()));



        }



    }
    else if(current_user.compareTo(uid)!=0&&(current_user.compareTo(previous_user)!=0)){

        //this block is for handling chats in the left (receiver) side

        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson1.setVisibility(View.GONE);
            messageViewHolder.rlson2.setVisibility(View.VISIBLE);
            messageViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            messageViewHolder.time2.setText(messageItem.getTime());
            messageViewHolder.messagebody2.setText(messageItem.getMessage_body());

        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson1.setVisibility(View.GONE);
            contactViewHolder.rlson2.setVisibility(View.VISIBLE);
            contactViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);

            contactViewHolder.time2.setText(messageItem.getTime());
            if(messageItem.getContact().getPicture()!=null)
                Glide.with(context).load(messageItem.getContact().getPicture()).into(contactViewHolder.contactphoto2);
            contactViewHolder.contactname2.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            Glide.clear(cameraViewHolder.camerapic2);
            cameraViewHolder.rlson1.setVisibility(View.GONE);
            cameraViewHolder.rlson2.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            cameraViewHolder.time2.setText(messageItem.getTime());
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic2);



        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson1.setVisibility(View.GONE);
            locationViewHolder.rlson2.setVisibility(View.VISIBLE);
            locationViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            locationViewHolder.time2.setText(messageItem.getTime());

            String mapurl=locationViewHolder.getMapUrl(messageItem.getGeopoint().getLatitude(),messageItem.getGeopoint().getLongitude());
            Glide.clear(locationViewHolder.location2);
            Glide.with(context).load(mapurl).into(locationViewHolder.location2);
            locationViewHolder.locationdesc2.setText(messageItem.getGeopoint().getLocality());
        }
        if(messageItem.getType().compareTo("gallery")==0){
            galleryVieHolder.rlson1.setVisibility(View.GONE);
            galleryVieHolder.rlson2.setVisibility(View.VISIBLE);
            galleryVieHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            galleryVieHolder.time2.setText(messageItem.getTime());
            if(messageItem.getImageurl().size()==2) {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105*messageItem.getImageurl().size(), r.getDisplayMetrics());
                galleryVieHolder.ll21.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView2.setNumColumns(messageItem.getImageurl().size());


            }

            else {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, r.getDisplayMetrics());
                galleryVieHolder.ll21.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView2.setNumColumns(3);
            }
            galleryVieHolder.gridView2.setAdapter(new ImageAdapter(context,messageItem.getImageurl()));

        }

    }
    else if(current_user.compareTo(uid)!=0&&current_user.compareTo(previous_user)==0){
       // holder.rlson1.setVisibility(View.GONE);

        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson1.setVisibility(View.GONE);
            messageViewHolder.rlson2.setVisibility(View.VISIBLE);
            messageViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            messageViewHolder.time2.setText(messageItem.getTime());
            //messageViewHolder.messagebody2.setVisibility(View.VISIBLE);
            messageViewHolder.messagebody2.setText(messageItem.getMessage_body());
        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson1.setVisibility(View.GONE);
            contactViewHolder.rlson2.setVisibility(View.VISIBLE);
            contactViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            contactViewHolder.time2.setText(messageItem.getTime());
            contactViewHolder.contactname2.setText(messageItem.getContact().getName());
            if(messageItem.getContact().getPicture()!=null)
                Glide.with(context).load(messageItem.getContact().getPicture()).into(contactViewHolder.contactphoto2);



        }
        if(messageItem.getType().compareTo("camera")==0){
            Glide.clear(cameraViewHolder.camerapic2);
            cameraViewHolder.rlson1.setVisibility(View.GONE);
            cameraViewHolder.rlson2.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            cameraViewHolder.time2.setText(messageItem.getTime());
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic2);


        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson1.setVisibility(View.GONE);
            locationViewHolder.rlson2.setVisibility(View.VISIBLE);
            locationViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            locationViewHolder.time2.setText(messageItem.getTime());
            String mapurl=locationViewHolder.getMapUrl(messageItem.getGeopoint().getLatitude(),messageItem.getGeopoint().getLongitude());
            Glide.clear(locationViewHolder.location2);
            Glide.with(context).load(mapurl).into(locationViewHolder.location2);
            locationViewHolder.locationdesc2.setText(messageItem.getGeopoint().getLocality());
            locationViewHolder.locationdesc2.setText(messageItem.getGeopoint().getLocality());
        }
        if(messageItem.getType().compareTo("gallery")==0){
            galleryVieHolder.rlson1.setVisibility(View.GONE);
            galleryVieHolder.rlson2.setVisibility(View.VISIBLE);
            galleryVieHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            galleryVieHolder.time2.setText(messageItem.getTime());
            if(messageItem.getImageurl().size()==2) {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110*2, r.getDisplayMetrics());
                galleryVieHolder.ll21.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView2.setNumColumns(2);


            }

            else {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, r.getDisplayMetrics());
                galleryVieHolder.ll21.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView2.setNumColumns(3);
            }

            galleryVieHolder.gridView2.setAdapter(new ImageAdapter(context,messageItem.getImageurl()));

        }


    }

    }

    @Override
    public int getItemCount() {
        return messageItemList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public EmojiconTextView messagebody1,messagebody2;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2,day;
        public LinearLayout llx;
        public ImageView seenicon;
        public View dayll;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messagebody1=itemView.findViewById(R.id.message_body1);
            messagebody2=itemView.findViewById(R.id.message_body2);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            rlson2=itemView.findViewById(R.id.rellayoutson2);
            rlfather=itemView.findViewById(R.id.rellayoutfather);
            time1=itemView.findViewById(R.id.time1);
            time2=itemView.findViewById(R.id.time2);
            dayll=itemView.findViewById(R.id.dayll);
            day=itemView.findViewById(R.id.day);
            llx = itemView.findViewById(R.id.llx);
            seenicon = itemView.findViewById(R.id.seenicon);


        }
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder{
        public TextView contactname1,savecontact1,contactname2,savecontact2;
        public ImageView contactphoto1,contactphoto2;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2,day;
        public View dayll;
        public ImageView seenicon;

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
            dayll=itemView.findViewById(R.id.dayll);
            day=itemView.findViewById(R.id.day);
            seenicon = itemView.findViewById(R.id.seenicon);

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
        public View locationview1,locationview2,dayll;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2,day;
        public ImageView location1,location2;
        public ImageView seenicon;

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
            dayll=itemView.findViewById(R.id.dayll);
            day=itemView.findViewById(R.id.day);
            location1=itemView.findViewById(R.id.location1);
            location2=itemView.findViewById(R.id.location2);
            seenicon=itemView.findViewById(R.id.seenicon);

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

        public String getMapUrl(String latitude,String longitude) {
            String mapUrl="https://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+"&zoom=16&size=300x300&markers=color:red|size:mid|"+latitude+","+longitude+"&key="+context.getString(R.string.API_KEY);

            return mapUrl;
        }
    }
    public class CameraViewHolder extends RecyclerView.ViewHolder{
        public ImageView camerapic2,camerapic1;
        public View cameraview1,cameraview2,dayll;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2,day;
        public ImageView seenicon;

        public CameraViewHolder(View itemView) {
            super(itemView);
            camerapic2=itemView.findViewById(R.id.camerapic2);
            camerapic1=itemView.findViewById(R.id.camerapic1);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            rlson2=itemView.findViewById(R.id.rellayoutson2);
            rlfather=itemView.findViewById(R.id.rellayoutfather);
            time1=itemView.findViewById(R.id.time1);
            time2=itemView.findViewById(R.id.time2);
            dayll=itemView.findViewById(R.id.dayll);
            day=itemView.findViewById(R.id.day);
            cameraview1=itemView.findViewById(R.id.cameraview1);
            cameraview2=itemView.findViewById(R.id.cameraview2);
            seenicon = itemView.findViewById(R.id.seenicon);

            cameraview1.setOnClickListener(new View.OnClickListener() {
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
            });

        }
    }
    public class GalleryVieHolder extends RecyclerView.ViewHolder{
        public StaticGridView gridView1,gridView2;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2,day;
        public LinearLayout ll1,ll21,dayll;
        public ImageView seenicon;
        public GalleryVieHolder(View itemView) {
            super(itemView);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            rlson2=itemView.findViewById(R.id.rellayoutson2);
            rlfather=itemView.findViewById(R.id.rellayoutfather);
            time1=itemView.findViewById(R.id.time1);
            time2=itemView.findViewById(R.id.time2);
            day=itemView.findViewById(R.id.day);
            dayll=itemView.findViewById(R.id.dayll);
           gridView1 =  itemView.findViewById(R.id.gridview1);
           gridView2 =  itemView.findViewById(R.id.gridview2);
           ll1=itemView.findViewById(R.id.ll1);
           ll21=itemView.findViewById(R.id.ll21);
           seenicon = itemView.findViewById(R.id.seenicon);


        }

    }

}
