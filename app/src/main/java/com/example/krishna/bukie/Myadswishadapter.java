
package com.example.krishna.bukie;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.krishna.bukie.Fragments.BookItemClickListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class Myadswishadapter extends RecyclerView.Adapter<Myadswishadapter.ViewHolder>{
    private List<BookAds> myadslist;
    private Context context;
    private BookItemClickListener bookItemClickListener;

    public Myadswishadapter(List<BookAds> myadslist, Context context) {
        this.myadslist =myadslist ;
        this.context = context;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public Myadswishadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookadviewvertical,parent,false);
        //Toast.makeText(context, "nibbas", Toast.LENGTH_SHORT).show();
        return  new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final BookAds bookAds=myadslist.get(position);

        if(holder.bookprice.getBackground()!=null) {

            //TODO: bookAds.getBookpicslist().size() can be zero when its empty, you forgot to handle that case
            holder.shimmerFrameLayout.startShimmerAnimation();
            Glide.with(context)
                    .load(bookAds.getBookcoverpic())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.shimmerFrameLayout.stopShimmerAnimation();
                            //holder.bookcategory.setBackground(null);
                            holder.bookpic.setBackground(null);
                           // holder.bookdate.setBackground(null);
                            holder.booktitle.setBackground(null);
                            holder.bookprice.setBackground(null);
                            holder.booktitle.setText(bookAds.getBooktitle());
                            holder.bookprice.setText(bookAds.getPrice());
                           // holder.bookdate.setText(bookAds.getDate());
                           // holder.bookcategory.setText(bookAds.getBookcategory());
                            return false;
                        }
                    })
                    .into(holder.bookpic);

        }
        else{

            Glide.with(context)
                    .load(bookAds.getBookcoverpic())
                    .into(holder.bookpic);
            holder.booktitle.setText(bookAds.getBooktitle());
            holder.bookprice.setText(bookAds.getPrice());
           // holder.bookdate.setText(bookAds.getDate());
            //holder.bookcategory.setText(bookAds.getBookcategory());

        }
        holder.selectad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(context, DisplayAdActivity.class);
                intent.putExtra("bookads", bookAds);
                intent.putExtra("editad", true);
                intent.putExtra("isHome", false);
                intent.putExtra("from","wishlist");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });




    }





    @Override
    public int getItemCount() {

        return myadslist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookpic;
        public TextView bookprice,booktitle;
        public ShimmerFrameLayout shimmerFrameLayout;
        public View selectad;

        public ViewHolder(View itemView) {

            super(itemView);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerlayout);
            bookpic=(ImageView)itemView.findViewById(R.id.bookpic);
            bookprice=itemView.findViewById(R.id.bookprice);
            booktitle=itemView.findViewById(R.id.booktitle);
            selectad=itemView.findViewById(R.id.selectad);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookItemClickListener.onItemClick(v, getAdapterPosition());

                }
            });


        }
    }
}


