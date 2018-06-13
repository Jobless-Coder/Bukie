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
import android.widget.TextView;
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
    private EditText chatbox;
    private int count;
    private String adId, usernameseller, usernamebuyer, usernameofuser,tmpuser;
    private TextView username2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatbox=(EditText)findViewById(R.id.chatbox);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
       getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        TextView username2=(TextView)findViewById(R.id.username);

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

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                //if(messageItemList!=null)
                recyclerView.scrollToPosition(messageItemList.size()-1);
                Toast.makeText(ChatActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });

        messageItemList=new ArrayList<>();
        MessageItem messageItem2=new MessageItem("Hello nibbas xD","4:00am","Indranil");
        MessageItem messageItem3=new MessageItem("okay Nibbas","5:00am","Krishna");
        View send=(View)findViewById(R.id.send);
        send.setOnClickListener(this);
        //count=recyclerView.getAdapter().getItemCount()-1;
         adId="123456";usernameseller="Indranil";usernamebuyer="Krishna";usernameofuser="Indranil";
         if(usernameofuser.compareTo(usernamebuyer)==0)
             tmpuser=usernameseller;
         else
             tmpuser=usernamebuyer;
        username2.setText(tmpuser);
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

        chatbox.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                /*LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // you may want to play with the offset parameter
                layoutManager.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                chatbox.setFocusableInTouchMode(true);
                /*chatbox.post(() -> {
                    chatbox.requestFocus();
                    UiUtils.showKeyboard(chatbox);
                });*/
            }
        });










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
                //Toast.makeText(this, ""+date, Toast.LENGTH_SHORT).show();

                break;

            default:
                break;
        }
    }
}

