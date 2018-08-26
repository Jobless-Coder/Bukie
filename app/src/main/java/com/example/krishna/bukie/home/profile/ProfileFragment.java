package com.example.krishna.bukie.home.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.krishna.bukie.auth.AuthActivity;
import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.FeedbackActivity;
import com.example.krishna.bukie.Myadswishadapter;
import com.example.krishna.bukie.R;
//import com.example.krishna.bukie.mFragmentPagerAdapter;
import com.example.krishna.bukie.registration.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseFirestore firebaseFirestore;
    private List<BookAds> myadslist;
    private List<String> myadspathlist;
    private View v,myads,mywishlist;
    private boolean myadsfrag=true;
    private String uid,ppic,fullname;
    private Myadswishadapter adapter;
    private ListenerRegistration listenerRegistration;
    private View editprofilebtn;
    boolean pauseState=false;
    private View menu;

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
        uid=sharedPreferences.getString("uid",null);

        if(uid!=null)
        {
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("token").setValue(token);
        }
        else {
            Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
        }
    }
    /*private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame, fragment);

        transaction.commit();
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("My Profile");

        // Restore bottom nav if it is removed in settings screen.
        View bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.VISIBLE);

        View v=inflater.inflate(R.layout.fragment_profile, container,false);

        View tabsview=getActivity().findViewById(R.id.header);
        tabsview.setVisibility(View.GONE);
        getActivity().findViewById(R.id.header2).setVisibility(View.GONE);
        myads=v.findViewById(R.id.myads);
        mywishlist=v.findViewById(R.id.mywishlist);
        myads.setOnClickListener(this);
        mywishlist.setOnClickListener(this);
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
        editprofilebtn=v.findViewById(R.id.editprofilebutton);
        editprofilebtn.setOnClickListener(this);
        firebaseFirestore=FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView.setNestedScrollingEnabled(false);

        myads.setSelected(true);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        fullname=sharedPreferences.getString("fullname",null);
        ppic=sharedPreferences.getString("profilepic",null);
        uid=sharedPreferences.getString("uid",null);
        ImageView imageView=v.findViewById(R.id.profilepic);
        TextView textView=v.findViewById(R.id.fullname);
        textView.setBackground(null);
        textView.setText(fullname);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("fullname").setValue(fullname);
        //TextView textView=view.findViewById(R.id.textview);
        Glide.with(getActivity())
                .load(ppic)
                .into(imageView);
        myadsfrag=true;
        setupRecyclerViewContent("myads");
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.myprofilemenu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    private void setupRecyclerViewContent(final String s) {
        recyclerView.setHasFixedSize(true);
       myadslist=new ArrayList<BookAds>();
      // myadspathlist=new ArrayList<String>();
        adapter=new Myadswishadapter(myadslist,getContext());
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        mDatabase.child("users").child(uid).child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    String adid=dataSnapshot1.getValue().toString();
                    getAds(adid,s);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getAds(final String path, final String s) {

            DocumentReference book=firebaseFirestore.collection("bookads").document(path);
            book.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task< DocumentSnapshot > task) {

                    if (task.isSuccessful()) {
                        if(s.equals("myads")&&myadsfrag==true||s.equals("mywishlist")&&myadsfrag==false) {
                            DocumentSnapshot snapshot = task.getResult();
                            BookAds bookAds = snapshot.toObject(BookAds.class);
                            myadslist.add(bookAds);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            });




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                disableFCM();
                firebaseAuth.signOut();

               // FirebaseMessagingService.
                SharedPreferences settings = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Toast.makeText(getActivity(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), AuthActivity.class);
                getContext().startActivity(intent);
                getActivity().finish();




                break;
            case R.id.item_settings:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.item_feedback:
                startActivity(new Intent(getContext(), FeedbackActivity.class));
                break;
        }
        return true;
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
            case R.id.editprofilebutton:
                Intent intent=new Intent(getContext(),RegistrationActivity.class);
                intent.putExtra("isProfile",true);
                getContext().startActivity(intent);
                break;
        }
    }

    @Override
    public void onPause() {
        pauseState=true;
        super.onPause();
    }

    @Override
    public void onResume() {
        if(pauseState==true) {
            pauseState=false;
            if (myadsfrag == true) {
                myads.setSelected(true);
                mywishlist.setSelected(false);
                myadslist.clear();
                adapter.notifyDataSetChanged();
                setupRecyclerViewContent("myads");

            }
            if (myadsfrag == false) {
                myads.setSelected(false);
                mywishlist.setSelected(true);
                myadslist.clear();
                adapter.notifyDataSetChanged();
                setupRecyclerViewContent("mywishlist");
            }
        }

        super.onResume();
    }
    @SuppressLint("StaticFieldLeak")
    public void disableFCM(){
       // FirebaseMessaging.getInstance().setAutoInitEnabled(false);
       /* new Thread(() -> {
            try {
                // Remove InstanceID initiate to unsubscribe all topic
                // TODO: May be a better way to use FirebaseMessaging.getInstance().unsubscribeFromTopic()
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();*/
      /* Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("kkk","lll");
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    Log.i("kkk","lll");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("kkk","lll2");
                }
            }
        });
       thread.start();*/
        /*new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {


                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {

            }
        }.execute();*/
    }
}
