
/*package com.example.krishna.bukie;

        import android.content.Context;
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
        import com.facebook.shimmer.ShimmerFrameLayout;

        import java.util.List;

public class BookAdsAdapter extends RecyclerView.Adapter<BookAdsAdapter.ViewHolder>{
    private List<BookAds> bookAdsList;
    private Context context;

    public BookAdsAdapter(List<BookAds> bookAdsList, Context context) {
        this.bookAdsList =bookAdsList ;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public BookAdsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookadview,parent,false);
        return  new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final BookAds bookAds=bookAdsList.get(position);
        if(holder.bookprice.getBackground()!=null) {
            holder.shimmerFrameLayout.startShimmerAnimation();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.bookcategory.setBackground(null);
                    holder.bookpic.setBackground(null);
                    holder.bookdate.setBackground(null);
                    holder.booktitle.setBackground(null);
                    holder.bookprice.setBackground(null);
                    Glide.with(context)
                            .load(bookAds.getBookpicslist().get(bookAds.getBookpicslist().size()-1))
                            .into(holder.bookpic);
                    holder.booktitle.setText(bookAds.getBooktitle());
                    holder.bookprice.setText(bookAds.getPrice());
                    holder.bookdate.setText(bookAds.getDate());
                    holder.bookcategory.setText(bookAds.getBookcategory());
                    //bookAds.setShowShimmer(false);
                }
            }, 1000);
        }
        else{

            Glide.with(context)
                    .load(bookAds.getBookpicslist().get(bookAds.getBookpicslist().size()-1))
                    .into(holder.bookpic);
            holder.booktitle.setText(bookAds.getBooktitle());
            holder.bookprice.setText(bookAds.getPrice());
            holder.bookdate.setText(bookAds.getDate());
            holder.bookcategory.setText(bookAds.getBookcategory());

        }




        }





    @Override
    public int getItemCount() {

        return bookAdsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookpic;
        public TextView bookprice,bookdate,bookcategory,booktitle;
        public ShimmerFrameLayout shimmerFrameLayout;

        public ViewHolder(View itemView) {

            super(itemView);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerlayout);
            bookpic=(ImageView)itemView.findViewById(R.id.bookpic);
            bookcategory=itemView.findViewById(R.id.bookcategory);
            bookdate=itemView.findViewById(R.id.bookdate);
            bookprice=itemView.findViewById(R.id.bookprice);
            booktitle=itemView.findViewById(R.id.booktitle);


        }
    }
}*/


