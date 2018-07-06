package com.example.krishna.bukie.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.krishna.bukie.AuthActivity;
import com.example.krishna.bukie.R;
import com.google.firebase.auth.FirebaseAuth;

public class MyWishlistFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v=inflater.inflate(R.layout.fragment_mywishlist, container,false);
        firebaseAuth=FirebaseAuth.getInstance();

        return v;
    }



    @Override
    public void onClick(View v) {

    }
}
