package com.example.krishna.bukie;

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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.krishna.bukie.Fragments.ChatFragment;
import com.example.krishna.bukie.Fragments.HomeFragment;
import com.example.krishna.bukie.Fragments.ProfileFragment;
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
    AHBottomNavigation bottomNavigation;
    AHBottomNavigationItem item1,item2,item3;
    FrameLayout frameLayout;
    View toolbarview,tabsview;
    View toolbar;
    ViewGroup toolbargroup;
    private DrawerLayout mDrawerLayout;
    ActionBar actionbar;
    private int mposition;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private String uid;
    DatabaseReference connectedRef;
    ValueEventListener listener;
    //for handling dynamic link book ads requests
    private String domain="https://bm.in";
    private com.google.firebase.database.Query buyerQuery,sellerQuery;
    private int count=0;
    private ValueEventListener sellerListener,buyerListener;
    private HashSet<String> unseenChatList=new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        tabsview=findViewById(R.id.header);


        frameLayout =findViewById(R.id.frame);
        toolbar=findViewById(R.id.toolbar);

        toolbargroup=findViewById(R.id.toolbar_layout);

        checkForDynamicLinks();
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

    private void checkForDynamicLinks() {

        Intent intent = getIntent();
        if(intent!=null && intent.getData()!=null)
        {

            String link = intent.getData().toString();
            if(link.contains("ads"))
            {
                link = link.substring(link.indexOf("ads/")+4);
                FirebaseFirestore.getInstance().
                        collection("bookads").
                        document(link).
                        get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        BookAds b = documentSnapshot.toObject(BookAds.class);
                        Intent intent = new Intent(HomePageActivity.this, DisplayAdActivity.class);
                        intent.putExtra("bookads", b);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        HomePageActivity.this.startActivity(intent);
                    }
                })
                ;
            }
        }


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }


                        if (deepLink != null) {
                            Toast.makeText(HomePageActivity.this, "Deep link found!"+deepLink.toString(), Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d("Dynamic links", "getDynamicLink: no link found");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Dynamic links", "getDynamicLink:onFailure", e);
                    }
                });
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
        item1 = new AHBottomNavigationItem("Home", R.drawable.home, R.color.colorAccent);
        item2 = new AHBottomNavigationItem("Chats", R.drawable.chat, R.color.violet);
        item3 = new AHBottomNavigationItem("Profile", R.drawable.profile, R.color.colorAccent);
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

                            toolbargroup.removeAllViews();
                            toolbarview = getLayoutInflater().inflate(R.layout.toolbar_myprofile, toolbargroup, false);
                            toolbargroup.addView(toolbarview);
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


    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
    @Override
    public void onBackPressed() {
        if (mposition>0) {
            Fragment fragment = new HomeFragment();
            bottomNavigation.setCurrentItem(0);
            loadFragment(fragment);


        } else {
            super.onBackPressed();
        }
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
        final OnDisconnect onDisconnectRef = presenceRef.onDisconnect();
        //onDisconnectRef.cancel();
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
                            unseenChatList.add(myChatsStatus.getChatid());
                            bottomNavigation.setNotification(count + "", 1);

                        } else if (!last_message.getSender().equals(uid) && last_message.getStatus().equals("seen") && count > 0&&unseenChatList.contains(myChatsStatus.getChatid())) {
                            count--;
                            unseenChatList.remove(myChatsStatus.getChatid());
                            if (count == 0)
                                bottomNavigation.setNotification("", 1);
                            else

                                bottomNavigation.setNotification("" + count, 1);
                        }
                    } /*else if (last_message.getSender() .equals( uid) &&count>0&&unseenChatList.contains(myChatsStatus.getChatid())) {

                    count--;
                    //unseenChatList.remove(myChatsStatus.getChatid());
                    bottomNavigation.setNotification("1", count);
                }*/
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
                //  String k=dataSnapshot.child("adid").getValue().toString();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    MyChatsStatus myChatsStatus = postSnapshot.getValue(MyChatsStatus.class);
                    LastMessage last_message = myChatsStatus.getLast_message();
                  //  Log.i("klli", "ffff");
                    if (last_message != null) {


                       /* if (!last_message.getSender().equals(uid) && last_message.getStatus().equals("sent")) {
                            count += 1;
                            bottomNavigation.setNotification(""+count, 1);
                            //  bottomNavigation.setNotification("1", 5);

                        } else if (!last_message.getSender().equals(uid) && last_message.getStatus().equals("seen") && count > 0) {
                            count--;
                            if(count==0)
                                bottomNavigation.setNotification("",1);
                            else
                                // Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                                bottomNavigation.setNotification(""+count, 1);
                        }*/
                        if (!last_message.getSender().equals(uid) && last_message.getStatus().equals("sent")&&!unseenChatList.contains(myChatsStatus.getChatid()) ){
                            count += 1;
                            unseenChatList.add(myChatsStatus.getChatid());
                            bottomNavigation.setNotification(count + "", 1);

                        } else if (!last_message.getSender().equals(uid) && last_message.getStatus().equals("seen") && count > 0&&unseenChatList.contains(myChatsStatus.getChatid())) {
                            count--;
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