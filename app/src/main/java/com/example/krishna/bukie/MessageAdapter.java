package com.example.krishna.bukie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements View.OnClickListener {
   private List<MessageItem> messageItemList;
   private Context context;
   private View itemView2;
   String previous_user="-1",previous_user2;
   String current_user="";
   private String type;
   private MessageItemClickListener messageItemClickListener;
    private MessageItem messageItem;

    public MessageAdapter(List<MessageItem> messageItemList, Context context) {
        this.messageItemList = messageItemList;
        this.context = context;
        //Toast.makeText(context, ""+messageItemList.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message,parent,false);
        return  new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {
    messageItem=messageItemList.get(position);
    current_user=messageItem.getUsername();
       // Toast.makeText(context, ""+current_user, Toast.LENGTH_SHORT).show();
    holder.rlfather.setTag(messageItem.getType()+"");
        SharedPreferences sharedPreferences=context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String username=sharedPreferences.getString("username",null);
        //Log.d("username",username);

    if(position>0) {
        previous_user=messageItemList.get(position - 1).getUsername();
        //previous_user2 = messageItemList.get(-1).getUsername();
    }
    else
        previous_user="-1";



    if(current_user.compareTo(username)==0&&current_user.compareTo(previous_user)==0)
    {
        holder.rlson1.setVisibility(View.VISIBLE);
        holder.rlson1.setBackgroundResource(R.drawable.chat_bubbles2);
        holder.time1.setText(messageItem.getTime());
       // Toast.makeText(context, "jkl", Toast.LENGTH_SHORT).show();
        if(messageItem.getType().compareTo("message")==0) {
            holder.message_body1.setVisibility(View.VISIBLE);
            holder.message_body1.setText(messageItem.getMessage_body());

        }
        if(messageItem.getType().compareTo("contact")==0){
            holder.contactview1.setVisibility(View.VISIBLE);
            holder.name.setText(messageItem.getContact().getName());

        }
        if(messageItem.getType().compareTo("camera")==0){
            holder.cameraview1.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(holder.camerapic);
            // holder.camerapic.setImageResource();
        }
        if(messageItem.getType().compareTo("location")==0){
            holder.locationview1.setVisibility(View.VISIBLE);
            holder.locationdesc.setText(messageItem.getGeopoint().getLocality());
        }



    }
    else if(current_user.compareTo(username)==0&&(current_user.compareTo(previous_user)!=0)){
        holder.rlson1.setVisibility(View.VISIBLE);
        holder.rlson1.setBackgroundResource(R.drawable.chat_bubbles1);
        holder.time1.setText(messageItem.getTime());
        if(messageItem.getType().compareTo("message")==0) {
            holder.message_body1.setVisibility(View.VISIBLE);
            holder.message_body1.setText(messageItem.getMessage_body());
        }
        if(messageItem.getType().compareTo("contact")==0){
            holder.contactview1.setVisibility(View.VISIBLE);
            holder.name.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            holder.cameraview1.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(holder.camerapic);
           // holder.camerapic.setImageResource();
        }
        if(messageItem.getType().compareTo("location")==0){
            holder.locationview1.setVisibility(View.VISIBLE);
            holder.locationdesc.setText(messageItem.getGeopoint().getLocality());
        }



    }
    else if(current_user.compareTo(username)!=0&&(current_user.compareTo(previous_user)!=0)){
       // holder.rlson1.setVisibility(View.GONE);
        holder.rlson2.setVisibility(View.VISIBLE);
        //holder.rlson2.setp
        holder.rlson2.setBackgroundResource(R.drawable.chat_bubbles3);
        holder.time2.setText(messageItem.getTime());
        if(messageItem.getType().compareTo("message")==0) {
            holder.message_body2.setVisibility(View.VISIBLE);
            holder.message_body2.setText(messageItem.getMessage_body());

        }
        if(messageItem.getType().compareTo("contact")==0){
            holder.contactview2.setVisibility(View.VISIBLE);
            holder.name.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("location")==0){
            holder.locationview2.setVisibility(View.VISIBLE);
            holder.locationdesc.setText(messageItem.getGeopoint().getLocality());
        }

    }
    else if(current_user.compareTo(username)!=0&&current_user.compareTo(previous_user)==0){
       // holder.rlson1.setVisibility(View.GONE);
        holder.rlson2.setVisibility(View.VISIBLE);
        holder.rlson2.setBackgroundResource(R.drawable.chat_bubbles4);
        holder.time2.setText(messageItem.getTime());
        if(messageItem.getType().compareTo("message")==0) {
            holder.message_body2.setVisibility(View.VISIBLE);
            holder.message_body2.setText(messageItem.getMessage_body());
        }
        if(messageItem.getType().compareTo("contact")==0){
            holder.contactview2.setVisibility(View.VISIBLE);
            holder.name.setText(messageItem.getContact().getName());


        }
        if(messageItem.getType().compareTo("camera")==0){
            holder.cameraview2.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageItem.getImageurl().get(0)).into(holder.camerapic);
            // holder.camerapic.setImageResource();


        }
        if(messageItem.getType().compareTo("location")==0){
            holder.locationview2.setVisibility(View.VISIBLE);
            holder.locationdesc.setText(messageItem.getGeopoint().getLocality());
        }


    }
        holder.rlfather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(holder.rlfather.getTag().toString().compareTo("location")==0){
                    // Creates an Intent that will load a map of San Francisco
                    Geopoint geoPoint=messageItemList.get(position).getGeopoint();
                    String url="geo:"+geoPoint.getLatitude()+","+geoPoint.getLongitude();
                    Log.e("geopoint",url);
                    Uri gmmIntentUri = Uri.parse(url);
                   // Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mapIntent);
                }

            }
        });
       holder.savecontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("jklk",messageItem.getType());
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, messageItemList.get(position).getContact().getPhoneno());
                intent.putExtra(ContactsContract.Intents.Insert.NAME, messageItemList.get(position).getContact().getName());
                //intent.putExtra(ContactsContract.Intents.Insert.EMAIL, bean.getEmailID());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return messageItemList.size();
    }

    @Override
    public void onClick(View v) {


    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message_body1,time1,message_body2,time2,savecontact,name,locationdesc;
        public RelativeLayout rlson1,rlson2,rlfather;
        public LinearLayout contactview1,contactview2,cameraview1,cameraview2,locationview1,locationview2;
        public ImageView photo,camerapic;

        public ViewHolder(View itemView) {

            super(itemView);
            message_body1=(TextView)itemView.findViewById(R.id.message_body1);
            time1=(TextView)itemView.findViewById(R.id.time1);
            rlson1=(RelativeLayout) itemView.findViewById(R.id.rellayoutson1);
            message_body2=(TextView)itemView.findViewById(R.id.message_body2);
            time2=(TextView)itemView.findViewById(R.id.time2);
            rlson2=(RelativeLayout) itemView.findViewById(R.id.rellayoutson2);
           rlfather=(RelativeLayout) itemView.findViewById(R.id.rellayoutfather);
           contactview1=itemView.findViewById(R.id.contactview1);
            contactview2=itemView.findViewById(R.id.contactview2);
            photo=itemView.findViewById(R.id.photo);
            savecontact=itemView.findViewById(R.id.savecontact);
            name=itemView.findViewById(R.id.name);
            camerapic=itemView.findViewById(R.id.camerapic);
            cameraview1=itemView.findViewById(R.id.cameraview1);
            cameraview2=itemView.findViewById(R.id.cameraview2);
            locationview1=itemView.findViewById(R.id.locationview1);
            locationview2=itemView.findViewById(R.id.locationview2);
            locationdesc=itemView.findViewById(R.id.locationdesc);

        }
    }
}
