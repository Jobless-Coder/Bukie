package com.example.krishna.bukie;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.krishna.bukie.Fragments.MyAdsFragment;
import com.example.krishna.bukie.Fragments.MyWishlistFragment;

public class mFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public mFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
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
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Myads";
            case 1:
                return "Mywish";

            default:
                return null;
        }
    }

}