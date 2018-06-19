package com.example.krishna.bukie;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private BookAds bookAds;

   // private String [] desc={"Makaut organizer","das pal","makaut organizer","das pal","makaut organizer"};
   /* private String [] images={"https://firebasestorage.googleapis.com/v0/b/booksapp-e588d.appspot.com/o/adimages%2F3c1bf95e-0033-4f1c-9327-1185e80942c0.png?alt=media&token=177fd3e0-8b64-41fe-a1de-ba38a05b9f00",
            "https://firebasestorage.googleapis.com/v0/b/booksapp-e588d.appspot.com/o/adimages%2F78e781f0-d088-43d4-9448-c666390cd479.png?alt=media&token=7d507578-7e68-426b-a9dd-650595fc2671",
    "https://firebasestorage.googleapis.com/v0/b/booksapp-e588d.appspot.com/o/adimages%2F04f3d3c0-920b-4511-93f3-71871148017d.png?alt=media&token=2d2704c5-18cc-479c-a329-26daa9559aaf",
    "https://firebasestorage.googleapis.com/v0/b/booksapp-e588d.appspot.com/o/adimages%2Ff5c76931-dbce-4e92-98e0-60ae23c450b5.png?alt=media&token=5e35e3d7-1d84-4a47-aa64-04418d69ad05",
    "https://firebasestorage.googleapis.com/v0/b/booksapp-e588d.appspot.com/o/adimages%2F71f22c71-0060-4e4d-946a-f84ba04477b7.png?alt=media&token=05ee90f8-b482-4a7c-b6e6-f7264acedf26"};*/
    private List<String> images=new ArrayList<String>();

    //{R.drawable.bookpic,R.drawable.bookpic,R.drawable.bookpic,R.drawable.bookpic,R.drawable.bookpic};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ad);
        Bundle bundle = getIntent().getExtras();
        bookAds = bundle.getParcelable("bookads");
       // images=bookAds.getBookpicslist();
        TextView price,title,category,date;
        price= findViewById(R.id.price);
        price.setText(bookAds.getPrice());
        title=findViewById(R.id.title);
        category=findViewById(R.id.category);
        date=findViewById(R.id.date);
        title.setText(bookAds.getBooktitle());
        date.setText(bookAds.getDate());
        category.setText(bookAds.getBookcategory());
        //Toast.makeText(this, "hello"+bookAds.getCoverpic(), Toast.LENGTH_SHORT).show();
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
        viewPagerAdapter=new ViewPagerAdapter(this,bookAds);

        viewPager.setAdapter(viewPagerAdapter);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(bookAds.getBookpicslist().size()); // specify total count of indicators
        //pageIndicatorView.setSelection(0);
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
                    //viewPager.setScrollBarFadeDuration(10000);
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
}
