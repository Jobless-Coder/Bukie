package com.example.krishna.bukie.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.Myadswishadapter;
import com.example.krishna.bukie.R;
//import com.example.krishna.bukie.mFragmentPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private TabLayout tabLayout;
    //private ViewPager viewPager;
   // mFragmentPagerAdapter adapter;
    //private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseFirestore firebaseFirestore;
    private List<BookAds> myadslist;
    private List<String> myadspathlist;
    private View v,myads,mywishlist;
    private boolean myadsfrag=true;
    private String username,ppic,fullname;
    private Myadswishadapter adapter;

    public ProfileFragment() {
    }

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        String msg = getString(R.string.msg_token_fmt, token);
        Log.e("token", msg);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username",null);

        if(username!=null)
        {
            FirebaseDatabase.getInstance().getReference().child("user").child(username).child("token").setValue(token);
        }
        else {
            Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
        }
        setHasOptionsMenu(true);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame, fragment);

        transaction.commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v=inflater.inflate(R.layout.fragment_profile, container,false);
        myads=v.findViewById(R.id.myads);
        mywishlist=v.findViewById(R.id.mywishlist);
        myads.setOnClickListener(this);
        mywishlist.setOnClickListener(this);
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);

        firebaseFirestore=FirebaseFirestore.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();


       // viewPager = (ViewPager) v.findViewById(R.id.viewpager);
       /* tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        //mFragmentPagerAdapter adapter = new mFragmentPagerAdapter(getChildFragmentManager());
        //viewPager.setAdapter(adapter);
        //tabLayout.setupWithViewPager(viewPager);
        //new setAdapterTask().execute();
        //viewPager.setAdapter(adapter);
        tabLayout.addTab(tabLayout.newTab().setText("My ads"));
        tabLayout.addTab(tabLayout.newTab().setText("My wishlist"));
        //tabLayout.addTab(tabLayout.newTab().setText("Games"));
        replaceFragment(new MyAdsFragment());*/

        myads.setSelected(true);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        fullname=sharedPreferences.getString("fullname",null);
        ppic=sharedPreferences.getString("profilepic",null);
        username=sharedPreferences.getString("username",null);
        ImageView imageView=v.findViewById(R.id.profilepic);
        TextView textView=v.findViewById(R.id.fullname);
        textView.setBackground(null);
        textView.setText(fullname);
        //TextView textView=view.findViewById(R.id.textview);
        Glide.with(getActivity())
                .load(ppic)
                .into(imageView);
        setupRecyclerViewContent("myads");





        return v;
    }

    private void setupRecyclerViewContent(String s) {
        recyclerView.setHasFixedSize(true);
       myadslist=new ArrayList<BookAds>();
      // myadspathlist=new ArrayList<String>();
        adapter=new Myadswishadapter(myadslist,getContext());
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        mDatabase.child("user").child(username).child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    //String path=dataSnapshot1.getValue().toString();
                    String adid=dataSnapshot1.getValue().toString();
                    getAds(adid);
                   // getAds(adid);
                   // getAds(adid);
                    //getAds(adid);

                   /*myadspathlist.add(dataSnapshot1.getValue().toString());

                   myadspathlist.add(dataSnapshot1.getValue().toString());
                   myadspathlist.add(dataSnapshot1.getValue().toString());
                   myadspathlist.add(dataSnapshot1.getValue().toString());*/
                    // Log.i("hello2",dataSnapshot1.getValue().toString());
                    //getAds(myadspathlist);
                    /*getAds(path);
                    getAds(path);*/
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getAds(/*final List<String> myadspathlist*/String path) {
        /*adapter=new Myadswishadapter(myadslist,getContext());
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);*/
       // for (String path:myadspathlist){
            DocumentReference book=firebaseFirestore.collection("bookads").document(path);
            book.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task< DocumentSnapshot > task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        BookAds bookAds=snapshot.toObject(BookAds.class);
                        myadslist.add(bookAds);
                        adapter.notifyDataSetChanged();
                        //Toast.makeText(getContext(), ""+myadslist.size(), Toast.LENGTH_SHORT).show();
                        /*if (myadspathlist.size()==myadslist.size())
                        {
                            Myadswishadapter adapter=new Myadswishadapter(myadslist,getContext());
                            recyclerView.setAdapter(adapter);
                            GridLayoutManager manager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(manager);
                        }*/
                    }
                }
            });
       // }


    }

    /* private class setAdapterTask extends AsyncTask<Void,Void,Void> {
         protected Void doInBackground(Void... params) {
             return null;
         }

         @Override
         protected void onPostExecute(Void result) {

         }
     }*/
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
        switch (v.getId()){
            case R.id.myads:

                if(myadsfrag==false){
                    myadsfrag=true;
                    myads.setSelected(true);
                    mywishlist.setSelected(false);
                    myadslist.clear();
                    adapter.notifyDataSetChanged();
                    setupRecyclerViewContent("myads");

                }

                break;
            case R.id.mywishlist:
                if(myadsfrag==true){
                    myads.setSelected(false);
                    mywishlist.setSelected(true);
                    myadsfrag=false;
                    myadslist.clear();
                    adapter.notifyDataSetChanged();
                    setupRecyclerViewContent("mywishlist");
                }


                break;
                default:
                    break;
        }


    }
}
