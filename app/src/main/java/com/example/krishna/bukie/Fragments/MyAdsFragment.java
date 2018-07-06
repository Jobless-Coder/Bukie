package com.example.krishna.bukie.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.krishna.bukie.AuthActivity;
import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.Myadswishadapter;
import com.example.krishna.bukie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyAdsFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseFirestore firebaseFirestore;
    private List<BookAds> myadslist=new ArrayList<BookAds>();
    private List<String> myadspathlist=new ArrayList<String>();
    public static final String ARG_PAGE = "page";
    private int mPageNumber;

    public MyAdsFragment() {
    }
   /* public static MyAdsFragment create(int pageNumber) {
        MyAdsFragment fragment = new MyAdsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public int getPageNumber() {
        return mPageNumber;
    }*/

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mPageNumber = getArguments().getInt(ARG_PAGE);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v=inflater.inflate(R.layout.fragment_myads, null);
        //Toast.makeText(getContext(), "bba", Toast.LENGTH_SHORT).show();
        //myadslist=;
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        firebaseFirestore=FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String username=sharedPreferences.getString("username",null);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(username).child("myads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                    myadspathlist.add(dataSnapshot1.getValue().toString());
                    myadspathlist.add(dataSnapshot1.getValue().toString());
                    myadspathlist.add(dataSnapshot1.getValue().toString());
                    myadspathlist.add(dataSnapshot1.getValue().toString());
                   // Log.i("hello2",dataSnapshot1.getValue().toString());
                }
                getAds(myadspathlist);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Toast.makeText(getContext(), "hello"+myadslist.size(), Toast.LENGTH_SHORT).show();

      /* Myadswishadapter adapter=new Myadswishadapter(myadslist,getContext());
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);*/
       /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    floatingActionButton.hide();
                else if (dy < 0)
                    floatingActionButton.show();
            }
        });*/

        return v;
    }

    public void getAds(final List<String> myadspathlist) {
        for (String path:myadspathlist){
            DocumentReference book=firebaseFirestore.collection("bookads").document(path);
            book.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task< DocumentSnapshot > task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        BookAds bookAds=snapshot.toObject(BookAds.class);
                        myadslist.add(bookAds);
                        //Toast.makeText(getContext(), ""+myadslist.size(), Toast.LENGTH_SHORT).show();
                        if (myadspathlist.size()==myadslist.size())
                        {
                            Myadswishadapter adapter=new Myadswishadapter(myadslist,getContext());
                            recyclerView.setAdapter(adapter);
                            GridLayoutManager manager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(manager);
                        }
                    }
                }
            });
        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    @Override
    public void onClick(View v) {


    }
}
