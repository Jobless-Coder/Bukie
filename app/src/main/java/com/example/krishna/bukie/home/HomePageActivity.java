package com.example.krishna.bukie.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.DisplayAdActivity;
import com.example.krishna.bukie.LastMessage;
import com.example.krishna.bukie.MyChatsStatus;
import com.example.krishna.bukie.MyFirebaseInstanceIDService;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.home.chat.ChatFragment;
import com.example.krishna.bukie.home.profile.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashSet;

public class HomePageActivity extends AppCompatActivity {

    private final String TAG = "HomePageActivity";

    private AHBottomNavigation bottomNavigation;
    private AHBottomNavigationItem item1,item2,item3;
    private FrameLayout frameLayout;
    private View buynoti,sellnoti,tabsview;
    private TextView buynotitxt,sellnotitxt;
    private ViewGroup toolbargroup;
    private DrawerLayout mDrawerLayout;
    private int mposition;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private String uid;
    private DatabaseReference connectedRef;
    private ValueEventListener listener;
    private int buychat=0,sellchat=0;
    //for handling dynamic link book ads requests
    private String domain="https://bm.in";
    private com.google.firebase.database.Query buyerQuery,sellerQuery;
    private int count=0;
    private ValueEventListener sellerListener,buyerListener;
    private HashSet<String> unseenChatList=new HashSet<>();
    private String temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        buynoti=findViewById(R.id.buynoti);
        sellnoti=findViewById(R.id.sellnoti);
        buynotitxt=findViewById(R.id.buynotitxt);
        sellnotitxt=findViewById(R.id.sellnotitxt);
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        tabsview=findViewById(R.id.header);
        frameLayout =findViewById(R.id.frame);
        toolbargroup=findViewById(R.id.toolbar_layout);
        bottomNavigation=findViewById(R.id.bottom_navigation);
        firebaseDatabase=FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("last_seen");

        MyFirebaseInstanceIDService myFirebaseInstanceIDService=new MyFirebaseInstanceIDService(uid);
          myFirebaseInstanceIDService.onTokenRefresh();
        mposition=0;
        loadFragment(new HomeFragment());

        initializeViews();

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    private void initializeViews() {
        item1 = new AHBottomNavigationItem("Home", R.drawable.home);
        item2 = new AHBottomNavigationItem("Chats", R.drawable.chat);
        item3 = new AHBottomNavigationItem("Profile", R.drawable.profile);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#ffffff"));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.black));
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Fragment fragment = null;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (position){

                    case 0:
                        if(mposition!=0) {

                            fragment = new HomeFragment();
                            mposition = 0;
                        }

                        break;

                    case 1:
                        if(mposition!=1) {

                            fragment = new ChatFragment();
                            mposition = 1;
                        }

                        break;

                    case 2:
                        if(mposition!=2) {
                            fragment = new ProfileFragment();
                            mposition = 2;
                        }

                        break;

                        default:

                            break;

                }
                return loadFragment(fragment);
            }
        });

    }


    @Override
    protected void onDestroy() {
        connectedRef.removeEventListener(listener);
        Date d=new Date();
        firebaseDatabase.getReference().child("users").child(uid).child("last_seen").setValue(d.getTime()+"");
        super.onDestroy();
    }


    @Override
    protected void onStart() {

        firebaseDatabase.getReference().child("users").child(uid).child("last_seen").setValue("online");
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("last_seen");
        presenceRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        listener=(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    firebaseDatabase.getReference().child("users").child(uid).child("last_seen").setValue("online");

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        connectedRef.addValueEventListener(listener);

        super.onStart();
    }




    @Override
    protected void onResume() {
        buyerQuery=firebaseDatabase.getReference().child("chat_status").orderByChild("buyerid_isactive").equalTo(uid+"_true");

        buyerListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    MyChatsStatus myChatsStatus = postSnapshot.getValue(MyChatsStatus.class);
                    LastMessage last_message = myChatsStatus.getLast_message();
                    if (last_message != null) {
                        if (!last_message.getSender().equals(uid) && last_message.getStatus().equals("sent")&&!unseenChatList.contains(myChatsStatus.getChatid()) ){
                            count += 1;
                            buychat++;
                            if(buychat>99)
                                temp=buychat+"+";
                            else
                                temp=""+buychat;
                           // if(isChatFrag)
                            {
                                if(buynoti.getVisibility()==View.VISIBLE){
                                    buynotitxt.setText(temp);
                                }
                                else {
                                    buynoti.setVisibility(View.VISIBLE);
                                    buynotitxt.setText(temp);
                                }
                            }
                            unseenChatList.add(myChatsStatus.getChatid());
                            bottomNavigation.setNotification(count + "", 1);

                        } else if ((!last_message.getSender().equals(uid) && last_message.getStatus().equals("seen") && count > 0&&unseenChatList.contains(myChatsStatus.getChatid())&&buychat>0)||(last_message.getSender().equals(uid)&&unseenChatList.contains(myChatsStatus.getChatid()))) {
                            count--;
                            buychat--;
                            if(buychat>99)
                                temp=buychat+"+";
                            else
                                temp=""+buychat;
                            //if(isChatFrag)
                            {
                                if(buychat>0&&buynoti.getVisibility()==View.VISIBLE){
                                    buynotitxt.setText(temp);
                                }
                                else if(buychat==0&&buynoti.getVisibility()==View.VISIBLE){
                                    buynoti.setVisibility(View.GONE);

                                }
                                else {
                                    buynoti.setVisibility(View.VISIBLE);
                                    buynotitxt.setText(temp);
                                }
                            }
                            unseenChatList.remove(myChatsStatus.getChatid());
                            if (count == 0)
                                bottomNavigation.setNotification("", 1);
                            else

                                bottomNavigation.setNotification("" + count, 1);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        sellerQuery=firebaseDatabase.getReference().child("chat_status").orderByChild("sellerid_isactive").equalTo(uid+"_true");
        sellerListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    MyChatsStatus myChatsStatus = postSnapshot.getValue(MyChatsStatus.class);
                    LastMessage last_message = myChatsStatus.getLast_message();
                    if (last_message != null) {


                        if (!last_message.getSender().equals(uid) && last_message.getStatus().equals("sent")&&!unseenChatList.contains(myChatsStatus.getChatid()) ){
                            count += 1;
                            sellchat++;
                            if(sellchat>99)
                                temp=sellchat+"+";
                            else
                                temp=""+sellchat;
                           // if(isChatFrag)
                            {
                                if(sellnoti.getVisibility()==View.VISIBLE){
                                    sellnotitxt.setText(temp);
                                }
                                else {
                                    sellnoti.setVisibility(View.VISIBLE);
                                    sellnotitxt.setText(temp);
                                }
                            }
                            unseenChatList.add(myChatsStatus.getChatid());
                            bottomNavigation.setNotification(count + "", 1);

                        } else if ((!last_message.getSender().equals(uid) && last_message.getStatus().equals("seen") && count > 0&&unseenChatList.contains(myChatsStatus.getChatid())||(last_message.getSender().equals(uid)&&unseenChatList.contains(myChatsStatus.getChatid())))) {
                            count--;
                            if(sellchat>0)
                                sellchat--;
                            if(sellchat>99)
                                temp=sellchat+"+";
                            else
                                temp=""+sellchat;
                            //if(isChatFrag)
                            {
                                if(sellchat>0&&sellnoti.getVisibility()==View.VISIBLE){
                                    sellnotitxt.setText(temp);
                                }
                                else if(sellchat==0&&sellnoti.getVisibility()==View.VISIBLE){
                                    sellnoti.setVisibility(View.GONE);

                                }
                                else {
                                    sellnoti.setVisibility(View.VISIBLE);
                                    sellnotitxt.setText(temp);
                                }
                            }

                            unseenChatList.remove(myChatsStatus.getChatid());
                            if (count == 0)
                                bottomNavigation.setNotification("", 1);
                            else

                                bottomNavigation.setNotification("" + count, 1);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        sellerQuery.addValueEventListener(sellerListener);
        buyerQuery.addValueEventListener(buyerListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sellerQuery.removeEventListener(sellerListener);
         buyerQuery.removeEventListener(buyerListener);
        super.onPause();
    }
}
