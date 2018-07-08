package com.example.krishna.bukie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.krishna.bukie.Fragments.GPS;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="helloo" ;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 21;
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
    private View camera,attach,send,emoji,rootview,keyboard,sendbtn,camerabtn;
    EmojiPopup emojiPopup;
    private boolean emojikeyboard=true;
    private final Handler handler = new Handler();
    private boolean isNetworkLocation, isGPSLocation;
    private boolean togglesend=false;
    private String msg,date;
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
        //camerabtn=findViewById(R.id.camerabtn);
        sendbtn=(View)findViewById(R.id.sendbtn);
        camera=findViewById(R.id.camera);
        attach=findViewById(R.id.attach);
        send=(View)findViewById(R.id.send);
        emoji=findViewById(R.id.emoji);
        rootview=findViewById(R.id.root);
        keyboard=findViewById(R.id.keyboard);
        keyboard.setOnClickListener(this);
        emoji.setOnClickListener(this);
        //camerabtn.setOnClickListener(this);
        attach.setOnClickListener(this);
        sendbtn.setOnClickListener(this);
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
                            togglesend=true;
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
                            togglesend=false;
                        }
                    });


                    anim.start();

                }

            }
        });
        chatbox.setImeOptions(EditorInfo.IME_ACTION_SEND);
        chatbox.setRawInputType(InputType.TYPE_CLASS_TEXT);

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

        chatbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    msg = chatbox.getText().toString().trim();
                    if (TextUtils.isEmpty(msg) == false) {
                        Date d = new Date();

                        SimpleDateFormat ft =
                                new SimpleDateFormat("hh:mm a");
                        date = ft.format(d);
                        MessageItem m = new MessageItem(msg, date, username, d.getTime() + "");
                        fh.sendMessage(m);

                        chatbox.setText("");
                    }
                    handled = true;
                }
                return handled;
            }
        });




    }

    public void onDestroy() {
        super.onDestroy();
        fh.stopListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatactivitymenu, menu);


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
                /*Intent intent=new Intent(ChatActivity.this,MapActivity.class);
                startActivity(intent);*/
                LocationManager mListener = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(mListener != null){
                    isGPSLocation = mListener.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    isNetworkLocation = mListener.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    Log.e("gps, network", String.valueOf(isGPSLocation + "," + isNetworkLocation));
                }
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        if(isGPSLocation){
                            Intent intent = new Intent(ChatActivity.this, MapActivity.class);
                            intent.putExtra("provider", LocationManager.GPS_PROVIDER);
                            startActivity(intent);
                            finish();
                        }else if(isNetworkLocation){
                            Intent intent = new Intent(ChatActivity.this, MapActivity.class);
                            intent.putExtra("provider", LocationManager.NETWORK_PROVIDER);
                            startActivity(intent);
                            finish();
                        }else{
                            //Device location is not set
                            PermissionUtils.LocationSettingDialog.newInstance().show(getSupportFragmentManager(), "Setting");
                        }
                    }
                }, 1500);
                break;

            default:
                break;
        }
        return true;
    }
    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendbtn:
                if(togglesend==true) {

                    msg = chatbox.getText().toString().trim();
                    if (TextUtils.isEmpty(msg) == false) {
                        Date d = new Date();

                        SimpleDateFormat ft =
                                new SimpleDateFormat("hh:mm a");
                        date = ft.format(d);
                        MessageItem m = new MessageItem(msg, date, username, d.getTime() + "");
                        fh.sendMessage(m);

                        chatbox.setText("");
                    }
                }
                //Toast.makeText(this, ""+date, Toast.LENGTH_SHORT).show();

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

