package com.example.krishna.bukie.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.krishna.bukie.AuthActivity;
import com.example.krishna.bukie.ChatActivity;
import com.example.krishna.bukie.R;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;

public class MyAdsFragment extends Fragment implements View.OnClickListener {
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
        View v=inflater.inflate(R.layout.fragment_myads, null);
        Button button=v.findViewById(R.id.logout);
        button.setOnClickListener(this);
        firebaseAuth=FirebaseAuth.getInstance();
        return v;//inflater.inflate(R.layout.fragment_myads, null);
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
//        Toast.makeText(getContext(), "hello"+firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
       // if(firebaseAuth.getCurrentUser()!=null) {

            firebaseAuth.signOut();
            SharedPreferences settings = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            settings.edit().clear().commit();
            //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Toast.makeText(getActivity(), "Successfully logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), AuthActivity.class);
            getContext().startActivity(intent);
       // }

    }
}
