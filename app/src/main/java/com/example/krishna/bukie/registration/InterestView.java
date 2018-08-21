package com.example.krishna.bukie.registration;

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

import com.example.krishna.bukie.R;

public class InterestView extends LinearLayout{

    private String mInterest = "hey you";
    private boolean mSelected = false;
    private CardView mCardView;
    private TextView mTextView;

    public InterestView(@NonNull Context context) {
        //super(context);
        this(context, "hey you");
    }

    public InterestView(Context context, String interest) {
        super(context);
        this.mInterest = interest;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            inflater.inflate(R.layout.interest_bubble, this);
        } else {
            Toast.makeText(context, "Error inflating the class", Toast.LENGTH_SHORT).show();
            return;
        }

        /*
        TextView mTextView = new TextView(context);
        mTextView.setText(interest);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        mTextView.setLayoutParams(params);
        mTextView.setTextSize(20);
        this.addView(mTextView);
        setLayoutParams(params);
        */

        mTextView = this.findViewById(R.id.interest);
        mTextView.setText(interest);
        mCardView = findViewById(R.id.intercard);
        //mCardView.setRadius(mCardView.getHeight()/2);
        //Toast.makeText(context, "height obtained is "+mCardView.getHeight(), Toast.LENGTH_SHORT).show();

    }

    public InterestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        //super(context, attrs);
        this(context, "hey you");
    }

    public String getInterest() {
        return mInterest;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void toggle() {
        mSelected = !mSelected;
        if (mSelected) {
            mCardView.setCardBackgroundColor(getResources().getColor(R.color.deep_grey));
            mTextView.setTextColor(getResources().getColor(R.color.white));
        } else {
            mTextView.setTextColor(getResources().getColor(R.color.deep_grey));
            mCardView.setCardBackgroundColor(getResources().getColor(R.color.white));
        }
        //Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }
}
