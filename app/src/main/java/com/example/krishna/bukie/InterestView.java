package com.example.krishna.bukie;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InterestView extends LinearLayout{

    private String interest="hey you";
    private boolean selected = false;
    private CardView cv;TextView tv;

    public InterestView(@NonNull Context context) {
        //super(context);
        this(context, "hey you");
    }

    public InterestView(Context context, String inte)
    {
        super(context);
        interest = inte;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null)
        {
            inflater.inflate(R.layout.interest_bubble, this);
        }
        else {
            Toast.makeText(context, "Error inflating the class", Toast.LENGTH_SHORT).show();
            return;
        }


        /*
        TextView tv = new TextView(context);
        tv.setText(interest);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        tv.setLayoutParams(params);
        tv.setTextSize(20);
        this.addView(tv);
        setLayoutParams(params);
        */

        tv = this.findViewById(R.id.interest);
        tv.setText(inte);
        cv = findViewById(R.id.intercard);
        //cv.setRadius(cv.getHeight()/2);
        //Toast.makeText(context, "height obtained is "+cv.getHeight(), Toast.LENGTH_SHORT).show();

    }

    public InterestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        //super(context, attrs);
        this(context, "hey you");
    }

    public void toggle(){
        selected = !selected;
        if(selected)
        {
            cv.setCardBackgroundColor(getResources().getColor(R.color.deep_grey));
            tv.setTextColor(getResources().getColor(R.color.white));
        }
        else
        {
            tv.setTextColor(getResources().getColor(R.color.deep_grey));
            cv.setCardBackgroundColor(getResources().getColor(R.color.white));
        }
        //Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    public String getInterest()
    {
        return interest;
    }

    public boolean isSelected()
    {
        return selected;
    }
}
