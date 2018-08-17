package com.example.krishna.bukie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class ImageViewPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    private  List<String> booksUrl=new ArrayList<>();

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.view_displayimage,container,false);
        com.jsibbold.zoomage.ZoomageView imageView=view.findViewById(R.id.imageView);
        final LinearLayout ll=view.findViewById(R.id.ll);
        final me.zhanghai.android.materialprogressbar.MaterialProgressBar progressBar=view.findViewById(R.id.progress_bar);
        Glide.with(context)
                .load(booksUrl.get(position)).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                ll.setVisibility(View.GONE);
                return false;
            }
        })
                .into(imageView);
        container.addView(view);
        return view;
    }

    public ImageViewPagerAdapter(Context context, List<String> booksUrl) {
        this.context = context;
        this.booksUrl=booksUrl;
    }



    @Override
    public int getCount() {
        return booksUrl.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==(RelativeLayout)object);
    }


}
