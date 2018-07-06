package com.example.krishna.bukie;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class InterestAdapter {
    private Context context;
    private ArrayList<String> interList;
    private FlexboxLayout layout;
    private InterestView viewList[];

    public InterestAdapter(Context context, ArrayList<String> inter, FlexboxLayout parentLayout)
    {
        this.context = context;
        interList = inter;
        layout = parentLayout;
        init();
    }

    private void init()
    {
        viewList = new InterestView[interList.size()];
        for(int i =0; i<interList.size();i++)
        {
            viewList[i] = new InterestView(context, interList.get(i));
            layout.addView(viewList[i]);
        }
    }

    public ArrayList<String> getSelectedInterests()
    {
        ArrayList<String> interests = new ArrayList<>();

        for(InterestView v: viewList)
        {
            if(v.isSelected())
            {
                interests.add(v.getInterest());
            }
        }

        return interests;
    }
}