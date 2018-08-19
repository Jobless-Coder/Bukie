package com.example.krishna.bukie;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ImageViewActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> ImageUrl=new ArrayList<>();
    private int position;
    private ViewPager viewPager;
    private View back,gotoleft,gotoright;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_view);
       // postponeEnterTransition();
        viewPager=findViewById(R.id.viewPager);
        back=findViewById(R.id.back);
        back.setOnClickListener(this);
        gotoleft=findViewById(R.id.gotoleft);
        gotoright=findViewById(R.id.gotoright);
        gotoright.setOnClickListener(this);
        gotoleft.setOnClickListener(this);
        Bundle bundle=getIntent().getExtras();
        gotoleft.setVisibility(View.GONE);
        ImageUrl=bundle.getStringArrayList("url");
        if(ImageUrl.size()==1){
            gotoleft.setVisibility(View.GONE);
            gotoright.setVisibility(View.GONE);
        }
        position=bundle.getInt("position");
        ImageViewPagerAdapter imageViewPagerAdapter=new ImageViewPagerAdapter(this,ImageUrl);
        //viewPager.addOnPageChangeListener(ViewPager.OnPageChangeListener);
        viewPager.setAdapter(imageViewPagerAdapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position>0)
                    gotoleft.setVisibility(View.VISIBLE);
                else if(position==0)
                    gotoleft.setVisibility(View.GONE);
                if(position==ImageUrl.size()-1)
                    gotoright.setVisibility(View.GONE);
                else if(position<ImageUrl.size()-1)
                    gotoright.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        int pos=viewPager.getCurrentItem();
        switch (v.getId()){
            case R.id.back:
                onBackPressed();
                break;
            case R.id.gotoleft:
                if(pos!=0)
                {
                    //gotoleft.setVisibility(View.GONE);
                    viewPager.setCurrentItem(pos-1,true);
                    //viewPager.setCurrentItem(ImageUrl.size()-1,true);

                }

               /* else{
                    if(pos==ImageUrl.size()-1)
                        gotoright.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(pos-1,true);

                }*/

                break;
            case R.id.gotoright:
                if(pos!=ImageUrl.size()-1){
               // gotoright.setVisibility(View.GONE);
                    viewPager.setCurrentItem(pos + 1, true);
                }
                   // viewPager.setCurrentItem(0,true);
                /*else {
                    if(pos==0)
                        gotoleft.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(pos + 1, true);
                }*/

                break;
        }
    }

    @Override
    public void onBackPressed() {
        //supportFinishAfterTransition();
        super.onBackPressed();
    }
}
