package com.example.krishna.bukie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishna.bukie.home.HomePageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.rd.PageIndicatorView;
import com.rd.draw.controller.DrawController;
import com.victor.loading.book.BookLoading;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private boolean editad,isHome;
    private List<String> booksUrl;
    private BookLoading bookloader;
    private boolean wasReferredBylink = false;
    private View back;
    private String datedetails,pricedetail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ad_two);
        bookloader = findViewById(R.id.bookloader);
        bookloader.start();
        if(!checkForDynamicLinks())
        {
            Bundle bundle = getIntent().getExtras();
            editad=bundle.getBoolean("editad",false);
            bookAds = bundle.getParcelable("bookads");
            initEverything();
        }

    }

    private void initEverything() {
        pricedetail="₹ " + bookAds.getPrice();
        Date date2=new Date(bookAds.getDate());
        SimpleDateFormat timeformat=new SimpleDateFormat("dd MMMM yyyy");
        datedetails=timeformat.format(date2);
        progressBar=findViewById(R.id.progress_bar);
        firebaseFirestore=FirebaseFirestore.getInstance();

        viewcounter=findViewById(R.id.viewcounter);
        price= findViewById(R.id.price);
        price.setText(pricedetail);
        title=findViewById(R.id.title);
        desc=findViewById(R.id.desc);
        author=findViewById(R.id.author);
        publisher=findViewById(R.id.publisher);
        category=findViewById(R.id.category);
        date=findViewById(R.id.date);
        fullname=findViewById(R.id.fullname);
        if(bookAds.getAuthor()!=null)
            author.setText("written by "+bookAds.getAuthor());
        if(bookAds.getPublisher()!=null)
            publisher.setText("published by "+bookAds.getPublisher());
        title.setText(bookAds.getTitle());
        date.setText("uploaded on "+datedetails);
        category.setText(bookAds.getCategory());
        desc.setText(bookAds.getDesc());
        fullname.setText("by "+bookAds.getSellerfullname());
        back=findViewById(R.id.back);
        back.setOnClickListener(this);

        viewPager=findViewById(R.id.viewPager);
        gotoleft=findViewById(R.id.gotoleft);
        gotoright=findViewById(R.id.gotoright);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
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


        booksUrl=new ArrayList<>();
        booksUrl.add(bookAds.getCoverpic());
        booksUrl.addAll(bookAds.getBookpicslist());
        if(booksUrl.size()==1){
            gotoleft.setVisibility(View.GONE);
            gotoright.setVisibility(View.GONE);
        }
        viewPagerAdapter=new ViewPagerAdapter(DisplayAdActivity.this,this,booksUrl);

        viewPager.setAdapter(viewPagerAdapter);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(bookAds.getBookpicslist().size()); // specify total count of indicators

        pageIndicatorView.setClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position>0)
                    gotoleft.setVisibility(View.VISIBLE);
                else if(position==0)
                    gotoleft.setVisibility(View.GONE);
                 if(position==booksUrl.size()-1)
                    gotoright.setVisibility(View.GONE);
                else if(position<booksUrl.size()-1)
                    gotoright.setVisibility(View.VISIBLE);
                pageIndicatorView.setSelection(position);/*empty*/
            }

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });
        bookloader.stop();
        bookloader.setVisibility(View.GONE);
    }


    private boolean checkForDynamicLinks() {

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
                        bookAds = b;
                        wasReferredBylink = true;
                        initEverything();

                    }
                });
                return true;
            }
            return false;
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
                            Toast.makeText(DisplayAdActivity.this, "Deep link found!"+deepLink.toString(), Toast.LENGTH_SHORT).show();

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
        return false;
    }

    private void getViewCounter() {
        firebaseFirestore.collection("bookads").document(bookAds.getAdid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BookAds bookAds1=documentSnapshot.toObject(BookAds.class);
                counter=bookAds1.getViewcounter();
                viewcounter.setText(counter+"");


            }
        });
    }

    private void increaseViewCounter() {

        //added by Krishna Bose, 22nd August, 2018
        //I know, what you are going to say, this method is for increasing the view counter
        //But This method is called when anyone except ad owner opens the app
        //so this suits my purpose for logging the "ad opened by.." record here as well

        //this code uploads a beacon that user has opened this ad at a particular time
        DatabaseReference dref = firebaseDatabase.getReference().child("collectibles").push();
        // I'm pushing into RTDb
        // root-> collectibles-> {{AutoID}}
        //                                  --user_id
        //                                  --timestamp
        //                                  --adid
        //                                  --from: (can be one of [homepage,ref_link])
        dref.child("user_id").setValue(uid);
        dref.child("timestamp").setValue(new Date().getTime());
        dref.child("adid").setValue(bookAds.getAdid());
        if(wasReferredBylink)
            dref.child("from").setValue("ref");
        else
        {
            try
            {
                String coming_from = getIntent().getExtras().getString("from","home");
                dref.child("from").setValue(coming_from);
            }
            catch (Exception e)
            {
                dref.child("from").setValue("home");
            }


        }

        //this code increases the counter value by 1
        firebaseFirestore.collection("bookads").document(bookAds.getAdid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BookAds bookAds1=documentSnapshot.toObject(BookAds.class);
                counter=bookAds1.getViewcounter();
                viewcounter.setText(counter+"");
                firebaseFirestore.collection("bookads").document(bookAds.getAdid()).update("viewcounter",counter+1);

            }
        });

    }

    @Override
    public void onBackPressed()
    {
        if(wasReferredBylink)
            startActivity(new Intent(this, HomePageActivity.class));
        finish();
    }
    private void setFavouriteButton() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("users/"+uid+"/mywishlist").child(bookAds.getAdid());

        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

        String deepLink = "https://booksapp-e588d.firebaseapp.com/ads/"+bookAds.getAdid();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
        intent.putExtra(Intent.EXTRA_TEXT,deepLink);

        startActivity(intent);
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
        int pos=viewPager.getCurrentItem();;
        switch (v.getId()){
            case R.id.gotoleft:
                if(pos!=0)
                {
                    viewPager.setCurrentItem(pos-1,true);
                }


                break;
            case  R.id.gotoright:
                if(pos!=booksUrl.size()-1)
                {
                    viewPager.setCurrentItem(pos+1,true);
                }


                break;
            case R.id.back:
                onBackPressed();
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
        else if(!editad&&!bookAds.getSellerid().equals(uid)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.displayad_menu2, menu);
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
            case R.id.report:
                Intent intent=new Intent(DisplayAdActivity.this,ReportActivity.class);
                intent.putExtra("adid",bookAds.getAdid());
                startActivity(intent);
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

                            myChats=new MyChats(bookAds.getSellerid(),uid,bookAds.getAdid(),bookAds.getCoverpic(),chatid,bookAds.getSellerpic(),userprofilepic,bookAds.getSellerfullname(),userfullname,false);
                            myChatsStatus=new MyChatsStatus(bookAds.getSellerid(),uid,bookAds.getAdid(),bookAds.getCoverpic(),chatid,bookAds.getSellerpic(),userprofilepic,bookAds.getSellerfullname(),userfullname);

                            DocumentSnapshot snapshot = task.getResult();

                            if (snapshot.exists()) {

                                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                intent.putExtra("mychats", myChatsStatus);
                                intent.putExtra("identity", "buyer");
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
