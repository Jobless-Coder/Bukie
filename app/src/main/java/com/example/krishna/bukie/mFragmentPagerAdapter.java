/*package com.example.krishna.bukie;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.krishna.bukie.Fragments.MyAdsFragment;
import com.example.krishna.bukie.Fragments.MyWishlistFragment;

public class mFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public mFragmentPagerAdapter( FragmentManager fm) {
        super(fm);
        //mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        return MyAdsFragment.create(position);
    }

   /* @Override
    public int getCount() {
        return 2;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new MyAdsFragment();
        }
        else {
            return new MyWishlistFragment();
        }
    }*/

    // This determines the number of tabs
   /* @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "My ads";
            case 1:
                return "My wishlist";

            default:
                return null;
        }
    }

}*/