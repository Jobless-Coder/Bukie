package com.example.krishna.bukie.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.krishna.bukie.R;

public class HomeFragment extends Fragment {



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater2, ViewGroup container,  Bundle savedInstanceState) {
        setHasOptionsMenu(true);



        return inflater2.inflate(R.layout.fragment_home, container,false);
    }



   @Override
    public void onCreateOptionsMenu(Menu menu2, MenuInflater inflater2) {


        menu2.clear();
       inflater2=getActivity().getMenuInflater();
        inflater2.inflate(R.menu.homemenu, menu2);
        //Toast.makeText(getContext(), ""+menu2.size(), Toast.LENGTH_SHORT).show();
        /*MenuItem searchItem = menu.findItem(R.id.share_location);
        searchItem = menu.findItem(R.id.delete_chat);
        searchItem = menu.findItem(R.id.block_user);

        SearchView searchView =
                (SearchView) searchItem.getActionView();*/

        super.onCreateOptionsMenu(menu2,inflater2);
    }



}
