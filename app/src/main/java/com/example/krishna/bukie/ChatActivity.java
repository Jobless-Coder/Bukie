package com.example.krishna.bukie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiButton;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

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
    private View camera,attach,send,emoji,rootview,keyboard;
    EmojiPopup emojiPopup;
    private boolean emojikeyboard=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_chat);
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        username=sharedPreferences.getString("username",null);
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

       //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


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

        camera=findViewById(R.id.camera);
        attach=findViewById(R.id.attach);
        send=(View)findViewById(R.id.send);
        emoji=findViewById(R.id.emoji);
        rootview=findViewById(R.id.root);
        keyboard=findViewById(R.id.keyboard);
        keyboard.setOnClickListener(this);
        emoji.setOnClickListener(this);
        camera.setOnClickListener(this);
        attach.setOnClickListener(this);
        send.setOnClickListener(this);
        emojiPopup = EmojiPopup.Builder.fromRootView(rootview).build((EmojiEditText) chatbox);



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
        chatbox.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(before ==0 && s.length()>0){
                    int cx = camera.getWidth() / 2;
                    int cy = camera.getHeight() / 2;
                    float initialRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(camera, cx, cy, initialRadius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            camera.setVisibility(View.INVISIBLE);
                            send.setVisibility(View.VISIBLE);
                        }
                    });


                    anim.start();

                }
                if(before >0 && s.length()==0){
                    int cx = send.getWidth() / 2;
                    int cy = send.getHeight() / 2;
                    float initialRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(send, cx, cy, initialRadius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            send.setVisibility(View.INVISIBLE);
                            camera.setVisibility(View.VISIBLE);
                        }
                    });


                    anim.start();

                }

            }
        });

        chatbox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();

                chatbox.getWindowVisibleDisplayFrame(r);

                int heightDiff = chatbox.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    if(emojikeyboard==true&&keyboard.getVisibility()==View.VISIBLE)
                    {
                        //Toast.makeText(context, "ghk", Toast.LENGTH_SHORT).show();
                        //emojiPopup.dismiss();
                        int cx2 = keyboard.getWidth() / 2;
                        int cy2 = keyboard.getHeight() / 2;
                        float initialRadius2 = (float) Math.hypot(cx2, cy2);
                        Animator anim2 = ViewAnimationUtils.createCircularReveal(keyboard, cx2, cy2, initialRadius2, 0);
                        anim2.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                keyboard.setVisibility(View.INVISIBLE);
                                emoji.setVisibility(View.VISIBLE);
                            }
                        });


                        anim2.start();
                        emojikeyboard=true;
                    }

                    //enter your code here
                }else{
                    //enter code for hid
                }
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
            case R.id.camera:
                break;
            case R.id.attach:
                break;
            case R.id.emoji:
                emojiPopup.toggle(); // Toggles visibility of the Popup.
                    int cx = emoji.getWidth() / 2;
                    int cy = emoji.getHeight() / 2;
                    float initialRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(emoji, cx, cy, initialRadius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            emoji.setVisibility(View.INVISIBLE);
                            keyboard.setVisibility(View.VISIBLE);
                        }
                    });


                    anim.start();
                    emojikeyboard=true;


                break;
            case R.id.keyboard:
                if(emojiPopup.isShowing()){
                    emojiPopup.dismiss();
                    int cx2 = keyboard.getWidth() / 2;
                    int cy2 = keyboard.getHeight() / 2;
                    float initialRadius2 = (float) Math.hypot(cx2, cy2);
                    Animator anim2 = ViewAnimationUtils.createCircularReveal(keyboard, cx2, cy2, initialRadius2, 0);
                    anim2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            keyboard.setVisibility(View.INVISIBLE);
                            emoji.setVisibility(View.VISIBLE);
                        }
                    });


                    anim2.start();
                    emojikeyboard=true;

                }

                break;

            default:
                break;
        }
    }
}

