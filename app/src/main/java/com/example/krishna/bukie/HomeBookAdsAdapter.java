
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.krishna.bukie.Fragments.BookItemClickListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class HomeBookAdsAdapter extends RecyclerView.Adapter<HomeBookAdsAdapter.BookHolder>{
    private List<BookAds> bookAdsList;
    private Context context;
    BookItemClickListener bookItemClickListener;

    public HomeBookAdsAdapter(List<BookAds> bookAdsList, Context context) {
        this.bookAdsList =bookAdsList ;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public HomeBookAdsAdapter.BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context)
                .inflate(R.layout.bookadview,parent,false);
        final BookHolder bookHolder=new BookHolder(v);

        return bookHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final BookHolder holder, int position) {
        final BookAds bookAds=bookAdsList.get(position);

        if(holder.bookprice.getBackground()!=null) {

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
                            holder.bookcategory.setBackground(null);
                            holder.bookpic.setBackground(null);
                            holder.bookdate.setBackground(null);
                            holder.booktitle.setBackground(null);
                            holder.bookprice.setBackground(null);
                            holder.booktitle.setText(bookAds.getBooktitle());
                            holder.bookprice.setText(bookAds.getPrice());
                            holder.bookdate.setText(bookAds.getDate());
                            holder.bookcategory.setText(bookAds.getBookcategory());
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
            holder.bookdate.setText(bookAds.getDate());
            holder.bookcategory.setText(bookAds.getBookcategory());

        }
        holder.selectad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(context, DisplayAdActivity.class);
                intent.putExtra("bookads", bookAds);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //getActivity().finish();
            }
        });




    }





    @Override
    public int getItemCount() {

        return bookAdsList.size();
    }

    public class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView bookpic;
        public TextView bookprice,bookdate,bookcategory,booktitle;
        public ShimmerFrameLayout shimmerFrameLayout;
        public View selectad;


        public BookHolder(View itemView) {
            super(itemView);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerlayout);
            bookpic=(ImageView)itemView.findViewById(R.id.bookpic);
            bookcategory=itemView.findViewById(R.id.bookcategory);
            bookdate=itemView.findViewById(R.id.bookdate);
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

        @Override
        public void onClick(View v) {

        }
    }
}


