package com.example.krishna.bukie;

import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class HomePageActivity extends AppCompatActivity {
    AHBottomNavigation bottomNavigation;
    AHBottomNavigationItem item1,item2,item3;
    FrameLayout frameLayout;
    View toolbarview;
    Toolbar toolbar;
    ViewGroup toolbargroup;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        frameLayout =findViewById(R.id.frame);
        toolbar=findViewById(R.id.toolbar);

        toolbargroup=findViewById(R.id.toolbar);;

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
        //fabhide
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fabAddNew.hide();
                else if (dy < 0)
                    fabAddNew.show();
            }
        });*/


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*BottomNavigationViewEx bnve = (BottomNavigationViewEx) findViewById(R.id.bnve);
        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);*/
        //bnve.enableItemShiftingMode(false);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        initializeViews();


    }


    private void initializeViews() {
        item1 = new AHBottomNavigationItem("Home", R.drawable.home, R.color.colorAccent);
        item2 = new AHBottomNavigationItem("Chat", R.drawable.chat, R.color.violet);
        item3 = new AHBottomNavigationItem("My ads", R.drawable.myads, R.color.colorAccent);
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
                switch (position){
                    case 0:
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.black));
                        //LayoutInflater layoutInflater=getLayoutInflater();
                       // toolbar.removeAllViews();
                        toolbargroup.removeAllViews();
                        toolbarview=getLayoutInflater().inflate(R.layout.toolbar,toolbargroup,false);
                        toolbargroup.addView(toolbarview);
                        //CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
                        //layoutParams.setBehavior(new BottomNavigationViewBehavior());

                        break;
                    case 1:
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        toolbargroup.removeAllViews();
                        toolbarview=getLayoutInflater().inflate(R.layout.toolbar_conversation,toolbargroup,false);
                        toolbargroup.addView(toolbarview);
                        break;
                    case 2:
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.light_violet));
                        toolbargroup.removeAllViews();
                        toolbarview=getLayoutInflater().inflate(R.layout.toolbar,toolbargroup,false);
                        toolbargroup.addView(toolbarview);
                        break;
                        default:
                            break;

                }
                return true;


            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
}