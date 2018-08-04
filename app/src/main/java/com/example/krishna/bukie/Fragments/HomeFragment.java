package com.example.krishna.bukie.Fragments;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.FilterActivity;
import com.example.krishna.bukie.HomeBookAdsAdapter;
import com.example.krishna.bukie.PostnewadActivity;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.SearchActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<BookAds> bookAdsList=new ArrayList<>();
    private Context context;
    private View v;
    public FloatingActionButton floatingActionButton;
    FirebaseStorage storage;
    StorageReference storageReference;
    ViewGroup toolbargroup;
    View toolbarview;
    BookItemClickListener bookItemClickListener;
    DrawerLayout mDrawerLayout;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private HomeBookAdsAdapter homeBookAdsAdapter;
    private ListenerRegistration listenerRegistration;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CollectionReference db;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setHasOptionsMenu(true);
        firebaseFirestore=FirebaseFirestore.getInstance();
        context=getContext();




    }
    @Override
    public View onCreateView(LayoutInflater inflater2, ViewGroup container,  Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        v=inflater2.inflate(R.layout.fragment_home, container,false);
        View tabsview=getActivity().findViewById(R.id.header);
        tabsview.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.simpleSwipeRefreshLayout);
        floatingActionButton=v.findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        toolbargroup=getActivity().findViewById(R.id.toolbar_layout);
        toolbargroup.removeAllViews();
        toolbarview= getActivity().getLayoutInflater().inflate(R.layout.toolbar_homepage,toolbargroup,false);
        toolbargroup.addView(toolbarview);

        //mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        homeBookAdsAdapter=new HomeBookAdsAdapter(bookAdsList,context);
        recyclerView.setAdapter(homeBookAdsAdapter);
        floatingActionButton.setOnClickListener(this);
        getadvertisements();
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.yellow,
                R.color.deep_red);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

               bookAdsList.clear();

                getadvertisements();
            }
        });



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    floatingActionButton.hide();
                else if (dy < 0)
                    floatingActionButton.show();
            }
        });


        return v;
    }

    private void getadvertisements() {

        db = firebaseFirestore.collection("bookads");

        db.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BookAds bookAds = document.toObject(BookAds.class);
                                bookAdsList.add(bookAds);
                                homeBookAdsAdapter.notifyDataSetChanged();


                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            //
                        }
                    }
                });
    }










   @Override
    public void onCreateOptionsMenu(Menu menu2, MenuInflater inflater2) {


        menu2.clear();
       inflater2=getActivity().getMenuInflater();
        inflater2.inflate(R.menu.homemenu, menu2);




       super.onCreateOptionsMenu(menu2,inflater2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                View toolbarsearch=getActivity().findViewById(R.id.transitiontoolbar);
                View search=getActivity().findViewById(R.id.search);
                Pair<View, String> p1 = Pair.create(toolbarsearch, "search");
                Pair<View, String> p2 = Pair.create(search, "searchicon");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(getActivity(),p1,p2);
                Intent intent=new Intent(getActivity(), SearchActivity.class);
                //intent.putExtra(SyncStateContract.Constants.KEY_ANIM_TYPE,)
                ///Toast.makeText(getContext(), "search selected", Toast.LENGTH_SHORT)
               //         .show();
                startActivity(intent,activityOptions.toBundle());
                break;
            case R.id.filter:
                Intent intent1=new Intent(getActivity(), FilterActivity.class);
                startActivity(intent1);

                break;





            default:
                break;
        }
        return true;
    }




    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(), PostnewadActivity.class);
        intent.putExtra("isHome", true);

        startActivity(intent);


    }

    @Override
    public void onResume() {

        //homeBookAdsAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
