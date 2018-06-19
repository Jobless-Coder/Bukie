package com.example.krishna.bukie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    //private int [] images;
   // private String [] desc;
    private BookAds bookAds;

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
        //super.destroyItem(container, position, object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
       // Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
        layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.displayadimageview,container,false);


        //bookAds.getBookpicslist().add(bookAds.getCoverpic());
        ImageView imageView=view.findViewById(R.id.imageView);
        //TextView textView=view.findViewById(R.id.textview);
        Glide.with(context)
                .load(bookAds.getBookpicslist().get(position))
                .into(imageView);
        //imageView.setImageResource(images[position]);
       // textView.setText(desc[position]);
        container.addView(view);
        return view;
    }

    public ViewPagerAdapter(Context context, BookAds bookAds) {
        this.context = context;
        this.bookAds=bookAds;
    }



    @Override
    public int getCount() {
        return bookAds.getBookpicslist().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==(LinearLayout)object);
    }
}
