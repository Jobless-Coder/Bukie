package com.example.krishna.bukie;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    private String fullname, ppic,tmpuser,identity,username;
    private TextView username2;
    private MyChats myChats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        username=sharedPreferences.getString("username",null);
       // username=sharedPreferences.getString("username",null);
        //username=sharedPreferences.getString("username",null);
       // Log.d("usernamechaat",username);
        Bundle bundle = getIntent().getExtras();
        myChats = bundle.getParcelable("mychats");
        identity=bundle.getString("identity");
        if(identity.compareTo("buyer")==0){
            tmpuser=myChats.getSeller();
            ppic=myChats.getSellerpic();
            fullname=myChats.getSellerfullname();
        }

        else{
            tmpuser=myChats.getBuyer();
            ppic=myChats.getBuyerpic();
            fullname=myChats.getBuyerfullname();

        }

        chatbox=(EditText)findViewById(R.id.chatbox);

       getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        TextView username2=(TextView)findViewById(R.id.username);
        ImageView pp=(ImageView)findViewById(R.id.profile_pic);
        username2.setText(fullname);
        Glide.with(getApplicationContext()).load(ppic).into(pp);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=getApplicationContext();


        recyclerView=(RecyclerView)findViewById(R.id.reyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageItemList=new ArrayList<>();

        View send=(View)findViewById(R.id.send);
        send.setOnClickListener(this);



        fh = new FirebaseHelper(myChats.getChatid(), myChats.getSeller(), myChats.getBuyer(), username, new IncomingMessageListener(){
                 public void receiveIncomingMessage(MessageItem ch)
          {
              messageItemList.add(ch);
              adapter=new MessageAdapter(messageItemList,context);
              recyclerView.setAdapter(adapter);
              recyclerView.scrollToPosition(messageItemList.size()-1);
          }

              });
        //fh.getPreviousTexts();
        fh.startListening();
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                //if(messageItemList!=null)
                recyclerView.scrollToPosition(messageItemList.size()-1);
                //Toast.makeText(ChatActivity.this, "hello", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(this, "search selected", Toast.LENGTH_SHORT)
                        //.show();
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

                msg=chatbox.getText().toString().trim();
                if(TextUtils.isEmpty(msg)==false) {
                    Date d = new Date();

                    SimpleDateFormat ft =
                            new SimpleDateFormat("hh:mm a");
                    date = ft.format(d);
                    MessageItem m = new MessageItem(msg, date, username,d.getTime()+"");
                    fh.sendMessage(m);

                    chatbox.setText("");
                }
                //Toast.makeText(this, ""+date, Toast.LENGTH_SHORT).show();

                break;

            default:
                break;
        }
    }
}

