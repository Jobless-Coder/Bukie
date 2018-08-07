package com.example.krishna.bukie;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private Toolbar toolbar;
    private ActionBar actionBar;
    private BookAds bookAds;
    private LikeButton likeButton;
    private FirebaseFirestore firebaseFirestore;
    private String chatid,uid,userprofilepic;
    private MyChats myChats;
    private MyChatsStatus myChatsStatus;
    private ProgressBar progressBar;
    private  FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private int counter;
    private TextView price,title,category,date,desc,fullname,author,publisher,viewcounter;
    boolean editad,isHome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ad_two);

        Bundle bundle = getIntent().getExtras();

        editad=bundle.getBoolean("editad",false);
        bookAds = bundle.getParcelable("bookads");

        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nsv);

        progressBar=findViewById(R.id.progress_bar);
        firebaseFirestore=FirebaseFirestore.getInstance();

        viewcounter=findViewById(R.id.viewcounter);
        viewcounter.setText(bookAds.getViewcounter()+"");
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
            getViewCounter();
        }
        else {
            increaseViewCounter();
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

    private void getViewCounter() {
        firebaseFirestore.collection("bookads").document(bookAds.getAdid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BookAds bookAds1=documentSnapshot.toObject(BookAds.class);
                counter=bookAds1.getViewcounter();
                viewcounter.setText(counter+"");
               // firebaseFirestore.collection("bookads").document(bookAds.getAdid()).update("viewcounter",counter+1);

            }
        });
    }

    private void increaseViewCounter() {
       // if(bookAds.getViewcounter()==null)
       // Toast.makeText(this, ""+bookAds.getViewcounter(), Toast.LENGTH_SHORT).show();
        firebaseFirestore.collection("bookads").document(bookAds.getAdid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BookAds bookAds1=documentSnapshot.toObject(BookAds.class);
                counter=bookAds1.getViewcounter();
                viewcounter.setText(counter+"");
                firebaseFirestore.collection("bookads").document(bookAds.getAdid()).update("viewcounter",counter+1);

            }
        });
       // Log.i("helloll",bookAds.getViewcounter()+"");

    }


    /*@Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }*/

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
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            likeButton.setVisibility(View.VISIBLE);

                            likeButton.setLiked(true);

                        }
                    },300);

                }
                else {
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           // progressBar.setIndeterminate(false);
                            progressBar.setVisibility(View.GONE);
                            likeButton.setVisibility(View.VISIBLE);
                        }
                    },300);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DisplayAdActivity.this, "Cannot read data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromWishList() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("users/"+uid+"/mywishlist");

       dref.child(bookAds.getAdid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {

           }
       });
    }



    public void shareAd(View view) {

        String deepLink = "https://books.jc/ads/"+bookAds.getAdid();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
        intent.putExtra(Intent.EXTRA_TEXT,deepLink);

        startActivity(intent);
    }

    /*class Pair
    {
        String key, value;
        Pair(String k, String v){
            key = k;
            value = v;
        }
    }*/
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

                default:
                    break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(editad==true) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_editad, menu);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.editad:
                Intent intent1=new Intent(this, PostnewadActivity.class);
                intent1.putExtra("isHome", 0);
                intent1.putExtra("bookads",bookAds);
                startActivity(intent1);


                break;
            case R.id.deletead:
                firebaseFirestore.collection("bookads").document(bookAds.getAdid()).update("isactive",false);
                onBackPressed();

                break;
            case android.R.id.home:
                onBackPressed();
                break;





            default:
                break;
        }
        return true;
    }

    public void goToChat(View view) {

       SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
        userprofilepic=sharedPreferences.getString("profilepic",null);
        final String userfullname=sharedPreferences.getString("fullname",null);
        chatid=uid+"%"+bookAds.getAdid();
        firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference mychatdoc=firebaseFirestore.collection("users").document(uid).collection("mychats").document(chatid);
        if(uid!=null) {
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




}
