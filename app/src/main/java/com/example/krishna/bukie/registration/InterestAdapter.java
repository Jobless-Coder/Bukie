package com.example.krishna.bukie.registration;

import android.content.Context;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class InterestAdapter {
    private Context mContext;
    private ArrayList<String> mInterests;
    private FlexboxLayout mLayout;
    private InterestView mViews[];

    InterestAdapter(Context context, ArrayList<String> interests, FlexboxLayout parentLayout) {
        this.mContext = context;
        this.mInterests = interests;
        this.mLayout = parentLayout;
        init();
    }

    private void init() {
        mViews = new InterestView[mInterests.size()];
        for (int i = 0; i< mInterests.size(); i++) {
            mViews[i] = new InterestView(mContext, mInterests.get(i));
            mLayout.addView(mViews[i]);
        }
    }

    public ArrayList<String> getSelectedInterests() {
        ArrayList<String> interests = new ArrayList<>();
        for (InterestView v: mViews) {
            if(v.isSelected()) {
                interests.add(v.getInterest());
            }
        }
        return interests;
    }
}
