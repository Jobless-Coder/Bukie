package com.example.krishna.bukie;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
   private String type;
   private MessageItemClickListener onClickListener;

    private MessageItem messageItem;

    public MessageAdapter(List<MessageItem> messageItemList, Context context,MessageItemClickListener messageItemClickListener) {
        this.messageItemList = messageItemList;
        this.context = context;
        this.onClickListener=messageItemClickListener;
        setHasStableIds(true);

        //Toast.makeText(context, ""+messageItemList.size(), Toast.LENGTH_SHORT).show();
    }
    public void SetItemList(List<MessageItem> messageItemList){
        this.messageItemList=messageItemList;
    }
  /*  @Override
    public int getViewTypeCount() {
        return getCount();
    }*/


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
        else /*if(type.compareTo("gallery")==0)*/
            return TYPE_GALLERY;
        /*else
            return -1;*/
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
    /*private void resizeGridView(GridView gridView, int items, int columns) {
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        int oneRowHeight = gridView.getHeight();
        int rows = (int) (items / columns);
        params.height = oneRowHeight * rows;
        params.width=100;
        gridView.setLayoutParams(params);
    }*/

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
    messageItem=messageItemList.get(position);
    current_user=messageItem.getUsername();
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
        String username=sharedPreferences.getString("username",null);
        Resources r = context.getResources();

       // float set= (float) Math.ceil(messageItem.getImageurl().size()/3);
       // float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100*set, r.getDisplayMetrics());


    if(position>0) {
        previous_user=messageItemList.get(position - 1).getUsername();
    }
    else
        previous_user="-1";



    if(current_user.compareTo(username)==0&&current_user.compareTo(previous_user)==0)
    {
        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson2.setVisibility(View.GONE);
            messageViewHolder.rlson1.setVisibility(View.VISIBLE);
            messageViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            messageViewHolder.time1.setText(messageItem.getTime());
            //messageViewHolder.messagebody1.setVisibility(View.VISIBLE);
            messageViewHolder.messagebody1.setText(messageItem.getMessage_body());

        }
        /*else {
            messageViewHolder.rlson1.setVisibility(View.GONE);
        }*/
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson2.setVisibility(View.GONE);
            contactViewHolder.rlson1.setVisibility(View.VISIBLE);
            contactViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            contactViewHolder.time1.setText(messageItem.getTime());
            //contactViewHolder.contactview1.setVisibility(View.VISIBLE);
            contactViewHolder.contactname1.setText(messageItem.getContact().getName());

        }

        if(messageItem.getType().compareTo("camera")==0){
            Glide.clear(cameraViewHolder.camerapic2);
            cameraViewHolder.rlson2.setVisibility(View.GONE);
            cameraViewHolder.rlson1.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            cameraViewHolder.time1.setText(messageItem.getTime());
            //holder.cameraview1.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic1);
            // holder.camerapic.setImageResource();
        }
        /*else {
            cameraViewHolder.rlson1.setVisibility(View.GONE);
        }*/
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson2.setVisibility(View.GONE);
            locationViewHolder.rlson1.setVisibility(View.VISIBLE);
            locationViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            locationViewHolder.time1.setText(messageItem.getTime());
            //locationViewHolder.locationview1.setVisibility(View.VISIBLE);
            locationViewHolder.locationdesc1.setText(messageItem.getGeopoint().getLocality());
        }
       /* else {
            locationViewHolder.rlson1.setVisibility(View.GONE);
        }*/
        if(messageItem.getType().compareTo("gallery")==0){
            galleryVieHolder.rlson2.setVisibility(View.GONE);
            galleryVieHolder.rlson1.setVisibility(View.VISIBLE);
            galleryVieHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
            galleryVieHolder.time1.setText(messageItem.getTime());
            if(messageItem.getImageurl().size()<3) {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105*messageItem.getImageurl().size(), r.getDisplayMetrics());
                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView1.setNumColumns(messageItem.getImageurl().size());

                //galleryVieHolder.gridView1.setColumnWidth();
            }
           /* else if(messageItem.getImageurl().size()==1){
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
                float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());
                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, (int)height));
                galleryVieHolder.gridView1.setNumColumns(messageItem.getImageurl().size());

            }*/
            else {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, r.getDisplayMetrics());
                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView1.setNumColumns(3);
            }
            galleryVieHolder.gridView1.setAdapter(new ImageAdapter(context,messageItem.getImageurl()));




        }
        /*else {
            galleryVieHolder.rlson1.setVisibility(View.GONE);
        }*/



    }
    else if(current_user.compareTo(username)==0&&(current_user.compareTo(previous_user)!=0)){

        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson2.setVisibility(View.GONE);
            messageViewHolder.rlson1.setVisibility(View.VISIBLE);
            messageViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            messageViewHolder.time1.setText(messageItem.getTime());
            //messageViewHolder.messagebody1.setVisibility(View.VISIBLE);
            messageViewHolder.messagebody1.setText(messageItem.getMessage_body());
        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson2.setVisibility(View.GONE);
            contactViewHolder.rlson1.setVisibility(View.VISIBLE);
            contactViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            contactViewHolder.time1.setText(messageItem.getTime());
            //contactViewHolder.contactview1.setVisibility(View.VISIBLE);
            contactViewHolder.contactname1.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            Glide.clear(cameraViewHolder.camerapic1);
            cameraViewHolder.rlson2.setVisibility(View.GONE);
            cameraViewHolder.rlson1.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            cameraViewHolder.time1.setText(messageItem.getTime());
           // cameraViewHolder.cameraview1.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic1);
           // holder.camerapic.setImageResource();
        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson2.setVisibility(View.GONE);
            locationViewHolder.rlson1.setVisibility(View.VISIBLE);
            locationViewHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            locationViewHolder.time1.setText(messageItem.getTime());
            //locationViewHolder.locationview1.setVisibility(View.VISIBLE);
            locationViewHolder.locationdesc1.setText(messageItem.getGeopoint().getLocality());
        }
        if(messageItem.getType().compareTo("gallery")==0){
            galleryVieHolder.rlson2.setVisibility(View.GONE);
            galleryVieHolder.rlson1.setVisibility(View.VISIBLE);
            galleryVieHolder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
            galleryVieHolder.time1.setText(messageItem.getTime());
            if(messageItem.getImageurl().size()==2) {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105*2, r.getDisplayMetrics());

                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView1.setNumColumns(2);

                //galleryVieHolder.gridView1.setColumnWidth();
            }
            /*else if(messageItem.getImageurl().size()==1){
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
                float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());
                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, (int)height));
                galleryVieHolder.gridView1.setNumColumns(messageItem.getImageurl().size());

            }*/
            else {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, r.getDisplayMetrics());
                galleryVieHolder.ll1.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView1.setNumColumns(3);
            }
            galleryVieHolder.gridView1.setAdapter(new ImageAdapter(context,messageItem.getImageurl()));
            //galleryVieHolder.gridView1.setAdapter(new ImageAdapter(context,messageItem.getImageurl()));


        }



    }
    else if(current_user.compareTo(username)!=0&&(current_user.compareTo(previous_user)!=0)){
       // holder.rlson1.setVisibility(View.GONE);

        if(messageItem.getType().compareTo("message")==0) {
            messageViewHolder.rlson1.setVisibility(View.GONE);
            messageViewHolder.rlson2.setVisibility(View.VISIBLE);
            messageViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            messageViewHolder.time2.setText(messageItem.getTime());
            //messageViewHolder.messagebody2.setVisibility(View.VISIBLE);
            messageViewHolder.messagebody2.setText(messageItem.getMessage_body());

        }
        if(messageItem.getType().compareTo("contact")==0){
            contactViewHolder.rlson1.setVisibility(View.GONE);
            contactViewHolder.rlson2.setVisibility(View.VISIBLE);
            contactViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            contactViewHolder.time2.setText(messageItem.getTime());
            //contactViewHolder.contactview2.setVisibility(View.VISIBLE);
            contactViewHolder.contactname2.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            Glide.clear(cameraViewHolder.camerapic2);
            cameraViewHolder.rlson1.setVisibility(View.GONE);
            cameraViewHolder.rlson2.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            cameraViewHolder.time2.setText(messageItem.getTime());
            //cameraViewHolder.cameraview2.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic2);
            // holder.camerapic.setImageResource();


        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson1.setVisibility(View.GONE);
            locationViewHolder.rlson2.setVisibility(View.VISIBLE);
            locationViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
            locationViewHolder.time2.setText(messageItem.getTime());
            //locationViewHolder.locationview2.setVisibility(View.VISIBLE);
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

                //galleryVieHolder.gridView1.setColumnWidth();
            }
            /*else if(messageItem.getImageurl().size()==1){
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
                float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());
                galleryVieHolder.ll21.setLayoutParams(new RelativeLayout.LayoutParams((int) width, (int)height));
                galleryVieHolder.gridView2.setNumColumns(messageItem.getImageurl().size());

            }*/
            else {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, r.getDisplayMetrics());
                galleryVieHolder.ll21.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView2.setNumColumns(3);
            }
            galleryVieHolder.gridView2.setAdapter(new ImageAdapter(context,messageItem.getImageurl()));

        }

    }
    else if(current_user.compareTo(username)!=0&&current_user.compareTo(previous_user)==0){
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
           // contactViewHolder.contactview2.setVisibility(View.VISIBLE);
            contactViewHolder.contactname2.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            Glide.clear(cameraViewHolder.camerapic2);
            cameraViewHolder.rlson1.setVisibility(View.GONE);
            cameraViewHolder.rlson2.setVisibility(View.VISIBLE);
            cameraViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            cameraViewHolder.time2.setText(messageItem.getTime());
            //cameraViewHolder.cameraview2.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(cameraViewHolder.camerapic2);
            // holder.camerapic.setImageResource();


        }
        if(messageItem.getType().compareTo("location")==0){
            locationViewHolder.rlson1.setVisibility(View.GONE);
            locationViewHolder.rlson2.setVisibility(View.VISIBLE);
            locationViewHolder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
            locationViewHolder.time2.setText(messageItem.getTime());
            //locationViewHolder.locationview2.setVisibility(View.VISIBLE);
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

                //galleryVieHolder.gridView1.setColumnWidth();
            }
            /*else if(messageItem.getImageurl().size()==1){
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
                float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());
                galleryVieHolder.ll21.setLayoutParams(new RelativeLayout.LayoutParams((int) width, (int)height));
                galleryVieHolder.gridView2.setNumColumns(messageItem.getImageurl().size());

            }*/
            else {
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, r.getDisplayMetrics());
                galleryVieHolder.ll21.setLayoutParams(new RelativeLayout.LayoutParams((int) width, RelativeLayout.LayoutParams.WRAP_CONTENT));
                galleryVieHolder.gridView2.setNumColumns(3);
            }
            //Log.i("niggaparis",messageItem.getImageurl()+""+messageItem.getType());
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
    public class GalleryVieHolder extends RecyclerView.ViewHolder{
        public StaticGridView gridView1,gridView2;
        public RelativeLayout rlson1,rlson2,rlfather;
        public TextView time1,time2;
        public LinearLayout ll1,ll21;
        //private boolean gridresized;
        public GalleryVieHolder(View itemView) {
            super(itemView);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            rlson2=itemView.findViewById(R.id.rellayoutson2);
            rlfather=itemView.findViewById(R.id.rellayoutfather);
            time1=itemView.findViewById(R.id.time1);
            time2=itemView.findViewById(R.id.time2);
           gridView1 =  itemView.findViewById(R.id.gridview1);
           gridView2 =  itemView.findViewById(R.id.gridview2);
          // gridresized=false;
           ll1=itemView.findViewById(R.id.ll1);
           ll21=itemView.findViewById(R.id.ll21);
            //ll1=itemView.findViewById(R.id.ll1);
            //gridView1.setAdapter(new ImageAdapter(context));
            //gridView2.se

        }

    }

}
