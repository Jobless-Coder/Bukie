
package com.example.krishna.bukie.home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.example.krishna.bukie.DisplayAdActivity;
import com.example.krishna.bukie.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeBookAdsAdapter extends RecyclerView.Adapter<HomeBookAdsAdapter.BookHolder>{
    private List<BookAds> bookAdsList;
    private Context context;
    private  BookItemClickListener bookItemClickListener;
    private boolean isHome;
    private String date,price;


    public HomeBookAdsAdapter(List<BookAds> bookAdsList, Context context,boolean isHome) {
        this.bookAdsList =bookAdsList ;
        this.context = context;
        this.isHome=isHome;
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
    public void onBindViewHolder(@NonNull final BookHolder holder, final int position) {
        final BookAds bookAds=bookAdsList.get(position);
        Date date2=new Date(bookAds.getTimestamp());
        SimpleDateFormat timeformat=new SimpleDateFormat("dd MMMM yyyy");
        price="₹ " + bookAds.getPrice();
        date=timeformat.format(date2);

        if(holder.bookprice.getBackground()!=null) {

            holder.shimmerFrameLayout.startShimmerAnimation();

            Glide.with(context)
                    .load(bookAds.getCoverpic())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.shimmerFrameLayout.stopShimmerAnimation();
                            holder.bookauthor.setBackground(null);
                            holder.bookpic.setBackground(null);
                            holder.bookdate.setBackground(null);
                            holder.booktitle.setBackground(null);
                            holder.bookprice.setBackground(null);
                            holder.booktitle.setText(bookAds.getTitle());
                            holder.bookprice.setText(price);
                            holder.bookdate.setText(date);
                            holder.bookauthor.setText(getSecondaryTextAvailable(bookAds));
                            return false;
                        }
                    })
                    .into(holder.bookpic);

        }
        else{

            Glide.with(context)
                    .load(bookAds.getCoverpic())
                    .into(holder.bookpic);
            holder.booktitle.setText(bookAds.getTitle());
            holder.bookprice.setText(price);
            holder.bookdate.setText(date);
            holder.bookauthor.setText(getSecondaryTextAvailable(bookAds));

        }
        holder.selectad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {



                Intent intent = new Intent(context, DisplayAdActivity.class);
                intent.putExtra("bookads", bookAds);
                if(isHome)
                    intent.putExtra("from","home");
                else
                    intent.putExtra("from","search");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });




    }

    private String getSecondaryTextAvailable(BookAds bookAds) {
        if(bookAds.getAuthor() != null)
        {
            return bookAds.getAuthor();
        }
        else if(bookAds.getPublisher() != null)
        {
            return bookAds.getPublisher();
        }
        return bookAds.getCategory();
    }


    @Override
    public int getItemCount() {

        return bookAdsList.size();
    }

    public class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView bookpic;
        public TextView bookprice,bookdate,bookauthor,booktitle;
        public ShimmerFrameLayout shimmerFrameLayout;
        public View selectad;


        public BookHolder(View itemView) {
            super(itemView);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerlayout);
            bookpic=(ImageView)itemView.findViewById(R.id.bookpic);
            bookauthor=itemView.findViewById(R.id.author);
            bookdate=itemView.findViewById(R.id.bookdate);
            bookprice=itemView.findViewById(R.id.bookprice);
            booktitle=itemView.findViewById(R.id.title);
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


