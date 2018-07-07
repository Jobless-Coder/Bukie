package com.example.krishna.bukie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.rd.PageIndicatorView;
import com.rd.draw.controller.DrawController;

import java.util.ArrayList;
import java.util.List;

public class DisplayAdActivity extends AppCompatActivity implements DrawController.ClickListener, View.OnClickListener {
    private ViewPager viewPager;
    private LinearLayout gotoleft,gotoright;
    private ViewPagerAdapter viewPagerAdapter;
    private PageIndicatorView pageIndicatorView;
    Toolbar toolbar;
    ActionBar actionBar;
    private FloatingActionButton floatingActionButton;
    private BookAds bookAds;
    private LikeButton likeButton;
    private FirebaseFirestore firebaseFirestore;
    private String chatid,username,userprofilepic;
    private MyChats myChats;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ad);
        Bundle bundle = getIntent().getExtras();
        bookAds = bundle.getParcelable("bookads");
       // Log.i("bookads",bookAds.getSeller()+"hello");
        floatingActionButton=findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nsv);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
            }
        });
        firebaseFirestore=FirebaseFirestore.getInstance();
        TextView price,title,category,date;
        price= findViewById(R.id.price);
        price.setText(bookAds.getPrice());
        title=findViewById(R.id.title);
        category=findViewById(R.id.category);
        date=findViewById(R.id.date);
        title.setText(bookAds.getBooktitle());
        date.setText(bookAds.getDate());
        category.setText(bookAds.getBookcategory());
        floatingActionButton.setOnClickListener(this);

        viewPager=findViewById(R.id.viewPager);
        gotoleft=findViewById(R.id.gotoleft);
        gotoright=findViewById(R.id.gotoright);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        gotoright.setOnClickListener(this);
        gotoleft.setOnClickListener(this);
        likeButton=toolbar.findViewById(R.id.favourites);

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {


            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });
        viewPagerAdapter=new ViewPagerAdapter(this,bookAds);

        viewPager.setAdapter(viewPagerAdapter);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(bookAds.getBookpicslist().size()); // specify total count of indicators

        pageIndicatorView.setClickListener(this);
        //viewPager.

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageIndicatorView.setSelection(position);/*empty*/}

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });

    }

    @Override
    public void onIndicatorClicked(int position) {


    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(this, "jk", Toast.LENGTH_SHORT).show();
        int pos=viewPager.getCurrentItem();;
        switch (v.getId()){
            case R.id.gotoleft:
                if(pos==0)
                {
                    viewPager.setCurrentItem(bookAds.getBookpicslist().size()-1,true);

                }

                else{
                    viewPager.setCurrentItem(pos-1,true);

                }

                break;
            case  R.id.gotoright:
                if(pos==bookAds.getBookpicslist().size()-1)
                    viewPager.setCurrentItem(0,true);
                else
                    viewPager.setCurrentItem(pos+1,true);
                break;
            case R.id.floatingActionButton:
                SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
                username=sharedPreferences.getString("username",null);
                userprofilepic=sharedPreferences.getString("profilepic",null);
                final String userfullname=sharedPreferences.getString("fullname",null);
                chatid=username+"%"+bookAds.getAdid();
                firebaseFirestore=FirebaseFirestore.getInstance();
                DocumentReference mychatdoc=firebaseFirestore.collection("users").document(username).collection("mychats").document(chatid);
                if(username!=null) {
                    //Log.i("kll",bookAds.getSeller()+"hello");
                    if(username.compareTo(bookAds.getSeller())!=0) {

                        mychatdoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {

                                    myChats=new MyChats(bookAds.getSeller(),username,bookAds.getAdid(),bookAds.getBookpicslist().get(bookAds.getBookpicslist().size()-1),chatid,bookAds.getSellerpic(),userprofilepic,bookAds.getSellerfullname(),userfullname);
                                    DocumentSnapshot snapshot = task.getResult();

                                    if (snapshot.exists()) {

                                        //Toast.makeText(DisplayAdActivity.this, "Chat already exists", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                        intent.putExtra("mychats", myChats);
                                        intent.putExtra("identity", "buyer");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);

                                    } else {
                                        //chatid=username+bookAds.getAdid();
                                        //Toast.makeText(DisplayAdActivity.this, "new chat to be created", Toast.LENGTH_SHORT).show();
                                        createNewChat();
                                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                        intent.putExtra("mychats", myChats);
                                        intent.putExtra("identity", "buyer");
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);
                                    }


                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(this, "Why so lonely?,you can't chat with yourself", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
                default:
                    break;
        }
    }

    private void createNewChat() {

        firebaseFirestore.collection("users").document(username).collection("mychats").document(chatid)
                .set(myChats)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //progressDialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        firebaseFirestore.collection("users").document(bookAds.getSeller()).collection("mychats").document(chatid)
                .set(myChats)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }
}
