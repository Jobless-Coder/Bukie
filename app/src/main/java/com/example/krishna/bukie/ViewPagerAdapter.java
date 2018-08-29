package com.example.krishna.bukie;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter  {
    private Context context;
    private LayoutInflater layoutInflater;
    //private ImageView imageView;
    private  List<String> booksUrl=new ArrayList<>();
    private Activity activity;


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
        //super.destroyItem(container, position, object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        //return super.instantiateItem(container, position);
       // Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
        layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final View view=layoutInflater.inflate(R.layout.displayadimageview,container,false);


        //bookAds.getBookpicslist().add(bookAds.getCoverpic());
       final ImageView imageView=view.findViewById(R.id.imageView);
        //TextView textView=view.findViewById(R.id.textview);
        //if()
        Glide.with(context)
                .load(booksUrl.get(position))
                .into(imageView);
        //imageView.setImageResource(images[position]);
       // textView.setText(desc[position]);
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ImageViewActivity.class);
              //  ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(activity,view,"image");
                intent.putExtra("position",position);
                intent.putStringArrayListExtra("url", (ArrayList<String>) booksUrl);
                context.startActivity(intent/*,activityOptions.toBundle()*/);

            }
        });
        return view;
    }

    public ViewPagerAdapter(Activity activity,Context context, List<String> booksUrl) {
        this.activity=activity;
        this.context = context;
        this.booksUrl=booksUrl;
    }



    @Override
    public int getCount() {
        return booksUrl.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==(LinearLayout)object);
    }

}
