package com.example.krishna.bukie;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<MessageItem> messageItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Toolbar","Clicked");
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.reyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageItemList=new ArrayList<>();
        MessageItem messageItem2=new MessageItem("Hello nibbas xD","4:00am");
        for(int i=0;i<10;i++){

            messageItemList.add(messageItem2);
            //Toast.makeText(this, "hello"+messageItem2.getMessage_body()+i, Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, messageItemList.get(9).getMessage_body()+"", Toast.LENGTH_SHORT).show();
        adapter=new MyAdapter(messageItemList,this);
        recyclerView.setAdapter(adapter);


        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);



        //toolbar.setTitle("Hello");
        //toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(toolbar);
        ActionMenuView amv = (ActionMenuView) toolbar.findViewById(R.id.amvMenu);
        amv.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       ActionBar ab=getSupportActionBar();
       //ab.setIcon(R.drawable.boy);
       ab.setDisplayHomeAsUpEnabled(true);*/



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);


        MenuItem searchItem = menu.findItem(R.id.share_location);
        searchItem = menu.findItem(R.id.delete_chat);
        searchItem = menu.findItem(R.id.block_user);

        SearchView searchView =
                (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.share_location:
                Toast.makeText(this, "search selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected

            default:
                break;
        }
        return true;
    }
}

