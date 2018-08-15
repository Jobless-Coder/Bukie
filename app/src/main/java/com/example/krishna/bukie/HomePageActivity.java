package com.example.krishna.bukie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.Date;

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
    private FirebaseDatabase firebaseDatabase;
    private String uid;
    DatabaseReference connectedRef;
    ValueEventListener listener;
    //for handling dynamic link book ads requests
    private String domain="https://bm.in";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        tabsview=findViewById(R.id.header);


        frameLayout =findViewById(R.id.frame);
        toolbar=findViewById(R.id.toolbar);

        toolbargroup=findViewById(R.id.toolbar_layout);

        /*
        Intent intent = getIntent();
        if(intent!=null && intent.getData()!=null)
        {
            Toast.makeText(this, intent.getData().toString(), Toast.LENGTH_SHORT).show();
        }
        */
        checkForDynamicLinks();

        //setSupportActionBar(toolbar);
        firebaseDatabase=FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
       DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("last_seen");
        /*final OnDisconnect onDisconnectRef = presenceRef.onDisconnect();
        onDisconnectRef.cancel();
        presenceRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
        DatabaseReference connectedRef;
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
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
                System.err.println("Listener was cancelled");
            }
        });*/
       // MyFirebaseMessagingService myFirebaseMessagingService=new MyFirebaseMessagingService();
       // FirebaseMessaging.getInstance().
        //myFirebaseMessagingService.onMessageReceived();
        //firebaseDatabase=FirebaseDatabase.getInstance().

       /*  actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);*/
       /* mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();


                        return true;
                    }
                });*/
        MyFirebaseInstanceIDService myFirebaseInstanceIDService=new MyFirebaseInstanceIDService(uid);
       myFirebaseInstanceIDService.onTokenRefresh();
       // myFirebaseInstanceIDService.onTokenRefresh();
       // FirebaseMessagingService firebaseMessagingService=new FirebaseMessagingService();
        //firebaseMessagingService.onCreate();
        mposition=0;
        loadFragment(new HomeFragment());
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        initializeViews();


    }

    private void checkForDynamicLinks() {

        Intent intent = getIntent();
        if(intent!=null && intent.getData()!=null)
        {
            //Toast.makeText(this, intent.getData().toString(), Toast.LENGTH_SHORT).show();
            String link = intent.getData().toString();
            if(link.contains("ads"))
            {
                link = link.substring(link.indexOf("ads/")+4);
                //Toast.makeText(this, link, Toast.LENGTH_SHORT).show();
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
//        else
//        {
//            Toast.makeText(this, "No data in intent either!", Toast.LENGTH_SHORT).show();
//        }

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // [START_EXCLUDE]
                        // Display deep link in the UI
                        if (deepLink != null) {
                            Toast.makeText(HomePageActivity.this, "Deep link found!"+deepLink.toString(), Toast.LENGTH_SHORT).show();

                            //((TextView) findViewById(R.id.link_view_receive)).setText(deepLink.toString());
                        } else {
                            Log.d("Dynamic links", "getDynamicLink: no link found");
                        }
                        // [END_EXCLUDE]
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
        //switching fragment
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
        //bottomNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.black));
        //bottomNavigation.setColored(true);
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Fragment fragment = null;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (position){

                    case 0:
                        if(mposition!=0) {
                            // tabsview.setVisibility(View.GONE);
                        /*toolbargroup.removeAllViews();
                        toolbarview=getLayoutInflater().inflate(R.layout.toolbar_homepage,toolbargroup,false);
                        toolbargroup.addView(toolbarview);*/
                            fragment = new HomeFragment();
                            mposition = 0;
                        }
                      //  FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        /*transaction.replace(R.id.frame, fragment, "new fragment");
                        transaction.addToBackStack(null);
                        transaction.commit();*/



                        break;
                    case 1:
                        if(mposition!=1) {
                       /*toolbargroup.removeAllViews();
                        toolbarview=getLayoutInflater().inflate(R.layout.toolbar_mychats,toolbargroup,false);
                        toolbargroup.addView(toolbarview);*/
                            fragment = new ChatFragment();
                            mposition = 1;
                        }
                        /*transaction.replace(R.id.frame, fragment, "new fragment");
                        transaction.addToBackStack(null);
                        transaction.commit();*/


                        break;
                    case 2:
                        if(mposition!=2) {
                            // tabsview=findViewById(R.id.header);
                            //tabsview.setVisibility(View.GONE);
                            toolbargroup.removeAllViews();
                            toolbarview = getLayoutInflater().inflate(R.layout.toolbar_myprofile, toolbargroup, false);
                            toolbargroup.addView(toolbarview);
                            fragment = new ProfileFragment();
                            mposition = 2;
                        }
                        /*transaction.replace(R.id.frame, fragment, "new fragment");
                        transaction.addToBackStack(null);
                        transaction.commit()*/;

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
                    //onDisconnectRef.cancel();
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
              //  System.err.println("Listener was cancelled");
            }
        });
        connectedRef.addValueEventListener(listener);

        super.onStart();
    }


}