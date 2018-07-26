package com.example.krishna.bukie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.rd.PageIndicatorView;
import com.rd.draw.controller.DrawController;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DisplayAdActivity extends AppCompatActivity implements DrawController.ClickListener, View.OnClickListener {
    private ViewPager viewPager;
    private LinearLayout gotoleft,gotoright;
    private ViewPagerAdapter viewPagerAdapter;
    private PageIndicatorView pageIndicatorView;
    Toolbar toolbar;
    ActionBar actionBar;
    private BookAds bookAds;
    private LikeButton likeButton;
    private FirebaseFirestore firebaseFirestore;
    private String chatid,uid,userprofilepic;
    private MyChats myChats;
    private MyChatsStatus myChatsStatus;
    private  FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ad_two);

        Bundle bundle = getIntent().getExtras();
        bookAds = bundle.getParcelable("bookads");
       // Log.i("bookads",bookAds.getSellerid()+"hello");
        //floatingActionButton=findViewById(R.id.floatingActionButton);
        //floatingActionButton.setOnClickListener(this);
        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nsv);
        /*
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
            }
        });*/

        firebaseFirestore=FirebaseFirestore.getInstance();
        TextView price,title,category,date,desc,fullname,author,publisher;
        price= findViewById(R.id.price);
        price.setText(bookAds.getPrice());
        title=findViewById(R.id.title);
        desc=findViewById(R.id.desc);
        author=findViewById(R.id.author);
        publisher=findViewById(R.id.publisher);
        category=findViewById(R.id.category);
        date=findViewById(R.id.date);
        fullname=findViewById(R.id.fullname);
        if(bookAds.getBookauthor()!=null)
        author.setText("written by "+bookAds.getBookauthor());
        if(bookAds.getBookpublisher()!=null)
        publisher.setText("published by "+bookAds.getBookpublisher());
        title.setText(bookAds.getBooktitle());
        date.setText("uploaded on "+bookAds.getDate());
        category.setText(bookAds.getBookcategory());
        desc.setText(bookAds.getBookdesc());
        fullname.setText("by "+bookAds.getSellerfullname());


        //floatingActionButton.setOnClickListener(this);

        if(getActionBar()!=null)
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
        likeButton=findViewById(R.id.favourites);

        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);

        if(uid.equals(bookAds.getSellerid()))
        {
            findViewById(R.id.nigga).setVisibility(View.GONE);//this is the Heart-fab button, not the viewPagerCard as the id suggests
            findViewById(R.id.chatbutton).setVisibility(View.GONE);

        }

        setFavouriteButton();

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addToWishList();
            }

            @Override
            public void unLiked(LikeButton likeButton) {

                removeFromWishList();
            }
        });
        List<String> booksUrl=new ArrayList<>();
        booksUrl.add(bookAds.getBookcoverpic());
        booksUrl.addAll(bookAds.getBookpicslist());
        viewPagerAdapter=new ViewPagerAdapter(DisplayAdActivity.this,this,booksUrl);

        viewPager.setAdapter(viewPagerAdapter);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(bookAds.getBookpicslist().size()); // specify total count of indicators

        pageIndicatorView.setClickListener(this);

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
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        //startActivity(new Intent(this, HomePageActivity.class));
        finish();
    }
    private void setFavouriteButton() {
       // Log.e("Favourite", "inside fab setting function");
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("users/"+uid+"/mywishlist").child(bookAds.getAdid());

        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    Log.e("Comparing", data.getValue().toString()+" And "+bookAds.getAdid());
                    if(data.getValue().toString().equals(bookAds.getAdid()))
                    {
                        likeButton.setLiked(true);
                        //Toast.makeText(DisplayAdActivity.this, bookAds.getAdid(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(DisplayAdActivity.this, "Liked before!", Toast.LENGTH_SHORT).show();
                    }
                }*/
                if(dataSnapshot.exists()){
                    likeButton.setLiked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DisplayAdActivity.this, "Cannot read data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromWishList() {
       // final ArrayList<Pair> wishList = new ArrayList<>();
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("users/"+uid+"/mywishlist");
       /* dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    wishList.add(new Pair(data.getKey(), data.getValue().toString()));
                }
                removeIfAvailable(wishList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
       dref.child(bookAds.getAdid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {

           }
       });
    }

    /*private void removeIfAvailable(ArrayList<Pair> wishList) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("users/"+uid+"/mywishlist");
        for(Pair adObject: wishList)
        {
            if(adObject.value.equals(bookAds.adid))
            {
                dref.child(adObject.key).removeValue();
            }
        }
    }*/

    public void shareAd(View view) {
        Toast.makeText(this, "Call your friend and talk about this ad", Toast.LENGTH_SHORT).show();
    }

    class Pair
    {
        String key, value;
        Pair(String k, String v){
            key = k;
            value = v;
        }
    }
    private void addToWishList() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("users/"+uid+"/mywishlist");
        dref.child(bookAds.getAdid()).setValue(bookAds.getAdid());
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
                /*
                case R.id.floatingActionButton:
                goToChat();
                break;
                */
                default:
                    break;
        }
    }

    public void goToChat(View view) {
       // Toast.makeText(this, "niiga", Toast.LENGTH_SHORT).show();

       SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
        userprofilepic=sharedPreferences.getString("profilepic",null);
        final String userfullname=sharedPreferences.getString("fullname",null);
        chatid=uid+"%"+bookAds.getAdid();
        firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference mychatdoc=firebaseFirestore.collection("users").document(uid).collection("mychats").document(chatid);
        if(uid!=null) {
            //Log.i("kll",bookAds.getSellerid()+"hello");
            if(uid.compareTo(bookAds.getSellerid())!=0) {

                mychatdoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            myChats=new MyChats(bookAds.getSellerid(),uid,bookAds.getAdid(),bookAds.getBookcoverpic(),chatid,bookAds.getSellerpic(),userprofilepic,bookAds.getSellerfullname(),userfullname,false);
                            myChatsStatus=new MyChatsStatus(bookAds.getSellerid(),uid,bookAds.getAdid(),bookAds.getBookcoverpic(),chatid,bookAds.getSellerpic(),userprofilepic,bookAds.getSellerfullname(),userfullname);

                            DocumentSnapshot snapshot = task.getResult();

                            if (snapshot.exists()) {

                                //Toast.makeText(DisplayAdActivity.this, "Chat already exists", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                intent.putExtra("mychats", myChatsStatus);
                                intent.putExtra("identity", "buyer");
                                intent.putExtra("isMap","0");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                            } else {
                                //chatid=uid+bookAds.getAdid();
                                //Toast.makeText(DisplayAdActivity.this, "new chat to be created", Toast.LENGTH_SHORT).show();
                                createNewChat();

                            }


                        }
                    }
                });
            }
            else {
                //Toast.makeText(this, "Why so lonely?,you can't chat with yourself", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void createNewChat() {


        firebaseFirestore.collection("users").document(uid).collection("mychats").document(chatid)
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
        firebaseFirestore.collection("users").document(bookAds.getSellerid()).collection("mychats").document(chatid)
                .set(myChats)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseDatabase.getReference().child("chat_status").child(myChatsStatus.getChatid()).setValue(myChatsStatus);
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("mychats", myChatsStatus);
                        intent.putExtra("identity", "buyer");
                        intent.putExtra("isMap","0");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    /*public void goToChat(View view) {

    }*/
}
