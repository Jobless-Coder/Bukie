package com.example.krishna.bukie;

import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.krishna.bukie.Fragments.ChatFragment;
import com.example.krishna.bukie.Fragments.HomeFragment;
import com.example.krishna.bukie.Fragments.MyAdsFragment;

public class HomePageActivity extends AppCompatActivity {
    AHBottomNavigation bottomNavigation;
    AHBottomNavigationItem item1,item2,item3;
    FrameLayout frameLayout;
    View toolbarview;
    Toolbar toolbar;
    ViewGroup toolbargroup;
    private DrawerLayout mDrawerLayout;
    ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        frameLayout =findViewById(R.id.frame);
        toolbar=findViewById(R.id.toolbar);

        toolbargroup=findViewById(R.id.toolbar_layout);


        setSupportActionBar(toolbar);
         actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();


                        return true;
                    }
                });


        loadFragment(new HomeFragment());
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        initializeViews();


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
                Fragment fragment = null;

                switch (position){

                    case 0:
                        /*toolbargroup.removeAllViews();
                        toolbarview=getLayoutInflater().inflate(R.layout.toolbar_homepage,toolbargroup,false);
                        toolbargroup.addView(toolbarview);*/
                        fragment = new HomeFragment();



                        break;
                    case 1:
                       toolbargroup.removeAllViews();
                        toolbarview=getLayoutInflater().inflate(R.layout.toolbar_chats,toolbargroup,false);
                        toolbargroup.addView(toolbarview);
                        fragment = new ChatFragment();

                        break;
                    case 2:
                        toolbargroup.removeAllViews();
                       toolbarview=getLayoutInflater().inflate(R.layout.toolbar_myads,toolbargroup,false);
                        toolbargroup.addView(toolbarview);
                        fragment = new MyAdsFragment();

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
}