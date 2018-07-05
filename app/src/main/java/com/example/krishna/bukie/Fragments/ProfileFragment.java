package com.example.krishna.bukie.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.krishna.bukie.AuthActivity;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.mFragmentPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v=inflater.inflate(R.layout.fragment_profile, container,false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        mFragmentPagerAdapter adapter = new mFragmentPagerAdapter(getActivity(), getFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String fullname=sharedPreferences.getString("fullname",null);
        String ppic=sharedPreferences.getString("profilepic",null);
        ImageView imageView=v.findViewById(R.id.profilepic);
        TextView textView=v.findViewById(R.id.fullname);
        textView.setBackground(null);
        textView.setText(fullname);
        //TextView textView=view.findViewById(R.id.textview);
        Glide.with(getActivity())
                .load(ppic)
                .into(imageView);

        firebaseAuth=FirebaseAuth.getInstance();

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu2, MenuInflater inflater2) {


        menu2.clear();
        inflater2=getActivity().getMenuInflater();
        inflater2.inflate(R.menu.myprofilemenu, menu2);


        super.onCreateOptionsMenu(menu2,inflater2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                SharedPreferences settings = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Toast.makeText(getActivity(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), AuthActivity.class);
                getContext().startActivity(intent);
                getActivity().finish();
               /* Toast.makeText(getContext(), "logout selected", Toast.LENGTH_SHORT)
                        .show();*/

                break;
            case R.id.settings:
                Toast.makeText(getContext(), "settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            /*case android.R.id.home:

                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;*/


            default:
                break;
        }
        return true;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();


    }

    @Override
    public void onClick(View v) {


    }
}
