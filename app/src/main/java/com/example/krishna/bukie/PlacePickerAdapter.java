package com.example.krishna.bukie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.krishna.bukie.Geopoint;
import com.example.krishna.bukie.R;

import java.util.List;

public class PlacePickerAdapter extends ArrayAdapter<Geopoint> {
    private List<Geopoint> nearbyList;
    private List<Boolean> iselected;
    private Context context;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.view_single_placepicker,parent,false);
        Geopoint geopoint=nearbyList.get(position);
        TextView textView=listItem.findViewById(R.id.textView);
        textView.setText(geopoint.getLocality());
        RadioButton radioButton=listItem.findViewById(R.id.radiobtn);
        radioButton.setChecked(iselected.get(position));


        return listItem;

    }

    public PlacePickerAdapter(Context context, List<Geopoint> nearbyList,List<Boolean> iselected){
        super(context,0,nearbyList);
        this.context=context;
        this.nearbyList=nearbyList;
        this.iselected=iselected;

    }
}
