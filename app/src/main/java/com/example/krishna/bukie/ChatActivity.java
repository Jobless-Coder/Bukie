package com.example.krishna.bukie;

import android.content.Context;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<MessageItem> messageItemList;
    private  FirebaseHelper fh;
    private Context context;
    private String adId, usernameseller, usernamebuyer, usernameofuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=getApplicationContext();
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
        MessageItem messageItem2=new MessageItem("Hello nibbas xD","4:00am","Indranil");
        MessageItem messageItem3=new MessageItem("okay Nibbas","5:00am","Krishna");
        View send=(View)findViewById(R.id.send);
        send.setOnClickListener(this);
        String adId="123456",usernameseller="Indranil",usernamebuyer="Krishna",usernameofuser="Krishna";
        fh = new FirebaseHelper(adId, usernameseller, usernamebuyer, usernameofuser, new IncomingMessageListener(){
                 public void receiveIncomingMessage(MessageItem ch)
          {
              messageItemList.add(ch);
              adapter=new MyAdapter(messageItemList,context);
              recyclerView.setAdapter(adapter);
              recyclerView.scrollToPosition(messageItemList.size()-1);
          }

              });
        fh.startListening();



       /* for(int i=0;i<2;i++){

            messageItemList.add(messageItem2);
            messageItemList.add(messageItem2);
            messageItemList.add(messageItem3);
            messageItemList.add(messageItem3);

        }*/
       /* adapter=new MyAdapter(messageItemList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(messageItemList.size()-1);*/






    }
    public void onDestroy() {
        super.onDestroy();
        fh.stopListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);


        MenuItem searchItem = menu.findItem(R.id.share_location);
        searchItem = menu.findItem(R.id.delete_chat);
        searchItem = menu.findItem(R.id.block_user);

        SearchView searchView =
                (SearchView) searchItem.getActionView();



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_location:
                Toast.makeText(this, "search selected", Toast.LENGTH_SHORT)
                        .show();
                break;

            default:
                break;
        }
        return true;
    }
    String msg,date;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                EditText chatbox=(EditText)findViewById(R.id.chatbox);
                msg=chatbox.getText().toString();
                Date d = new Date();

                SimpleDateFormat ft =
                        new SimpleDateFormat ("hh:mm a");
                date=ft.format(d);
                MessageItem m=new MessageItem(msg,date,"Krishna");
                fh.sendMessage(m);
                /*messageItemList.add(m);
                adapter.notifyItemInserted(messageItemList.size()-1);
                recyclerView.scrollToPosition(messageItemList.size()-1);*/
                chatbox.setText("");
                Toast.makeText(this, ""+date, Toast.LENGTH_SHORT).show();

                break;

            default:
                break;
        }
    }
}

