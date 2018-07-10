package com.example.krishna.bukie;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SquareImageView extends LinearLayout {
Uri imageLink;
String imageId;
DisplayMetrics metrics;
Context context;
    public SquareImageView(Context context) {

        super(context);
        this.context = context;
        init();
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {

        super(context);
        this.context = context;
        init();
    }

    public SquareImageView(Context context, Uri image, DisplayMetrics metrics)
    {
        super(context);
        this.metrics = metrics;
        this.imageLink = image;
        this.context = context;
        init();
    }

    public SquareImageView(Context context, Uri image, String id)
    {
        super(context);
        imageId = id;
        imageLink = image;
        this.context = context;
        init();
    }

    public void init()
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null)
        {
            inflater.inflate(R.layout.square_ad_image, this);
        }
        else {
            Toast.makeText(context, "Error inflating the class", Toast.LENGTH_SHORT).show();
            return;
        }

        this.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //adapter.removeItem(imageId);//TODO: not done yet
                //SquareImageView.this.setVisibility(GONE);
                ((ViewGroup)SquareImageView.this.getParent()).removeView(SquareImageView.this);
            }
        });

        if(imageLink!=null)
            ((ImageView)this.findViewById(R.id.squareimage)).setImageURI(imageLink);

        if(metrics!=null)
        {
            setLayoutParams(new LayoutParams(metrics.widthPixels/5, metrics.widthPixels/5));
        }
    }
}
