package com.example.krishna.bukie;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Tag_Picker_Activity extends AppCompatActivity {
    private ListView listView;
    private RecyclerView recyclerView;
    private HashSet<String> tagSet=new HashSet<>();
    private List<String> tagList=new ArrayList<>();
    private List<Boolean> istagList=new ArrayList<>();
   private TagPickerAdapter tagPickerAdapter;
   private ChipAdapter chipAdapter;
   private List<Tuple> chipList=new ArrayList<>();
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_picker);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        listView=findViewById(R.id.listview);
        recyclerView=findViewById(R.id.recyclerview);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        recyclerView.setLayoutManager(layoutManager);
        chipAdapter=new ChipAdapter(this, chipList, new TagItemListener() {
            @Override
            public void onRemoveClick(String text, int position) {
                chipList.remove(new Tuple(text,position));
                istagList.set(position,false);
                tagPickerAdapter.notifyDataSetChanged();
                chipAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(chipAdapter);
        FloatingActionButton floatingActionButton=findViewById(R.id.select_tags);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tag_Picker_Activity.this, PostnewadActivity.class);
                intent.putExtra("isHome", 2);
                Tuple chips=new Tuple(chipList);
                Toast.makeText(Tag_Picker_Activity.this, ""+chipList.get(0).getText(), Toast.LENGTH_SHORT).show();

                intent.putExtra("chips", chips);
        /*intent.putExtra("latitude", mCurrentLocation.getLatitude()+"");
        intent.putExtra("longitude", mCurrentLocation.getLongitude()+"");
        intent.putExtra("locality", result);*/
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });


        tagPickerAdapter=new TagPickerAdapter(Tag_Picker_Activity.this,tagList,istagList);
        listView.setAdapter(tagPickerAdapter);
        firebaseDatabase.getReference().child("interests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    tagList.add(dataSnapshot1.getValue().toString());
                    istagList.add(false);
                    tagPickerAdapter.notifyDataSetChanged();
                   Log.d("Locations updated", "location: " +istagList.toString()); //log
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(istagList.get(position)==false){
                    istagList.set(position,true);
                    chipList.add(new Tuple(tagList.get(position),position));
                    chipAdapter.notifyDataSetChanged();
                    Toast.makeText(Tag_Picker_Activity.this, ""+istagList, Toast.LENGTH_SHORT).show();
                }
                else {
                    istagList.set(position,false);
                    chipList.remove(new Tuple(tagList.get(position),position));
                    chipAdapter.notifyDataSetChanged();
                }
                tagPickerAdapter.notifyDataSetChanged();
            }
        });


    }


}
