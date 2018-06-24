package com.example.krishna.bukie.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.krishna.bukie.BookAds;
//import com.example.krishna.bukie.BookAdsAdapter;
import com.example.krishna.bukie.DisplayAdActivity;
import com.example.krishna.bukie.PostnewadActivity;
import com.example.krishna.bukie.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<BookAds> bookAdsList;
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



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setHasOptionsMenu(true);
        firebaseFirestore=FirebaseFirestore.getInstance();
        context=getContext();



    }

    private void getadvertisements() {
        Query query = firebaseFirestore.collection("bookads");
        FirestoreRecyclerOptions<BookAds> response = new FirestoreRecyclerOptions.Builder<BookAds>()
                .setQuery(query, BookAds.class)
                .build();
        firestoreRecyclerAdapter=new FirestoreRecyclerAdapter<BookAds, BookHolder>(response) {
            @Override
            public void onBindViewHolder(final BookHolder holder, int position, BookAds model)
            {

                final BookAds bookAds=model;

                if(holder.bookprice.getBackground()!=null) {

                    holder.shimmerFrameLayout.startShimmerAnimation();
                    Glide.with(context)
                            .load(bookAds.getBookpicslist().get(bookAds.getBookpicslist().size()-1))
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    holder.shimmerFrameLayout.stopShimmerAnimation();
                                    holder.bookcategory.setBackground(null);
                                    holder.bookpic.setBackground(null);
                                    holder.bookdate.setBackground(null);
                                    holder.booktitle.setBackground(null);
                                    holder.bookprice.setBackground(null);
                                    holder.booktitle.setText(bookAds.getBooktitle());
                                    holder.bookprice.setText(bookAds.getPrice());
                                    holder.bookdate.setText(bookAds.getDate());
                                    holder.bookcategory.setText(bookAds.getBookcategory());
                                    return false;
                                }
                            })
                            .into(holder.bookpic);

                }
                else{

                    Glide.with(context)
                            .load(bookAds.getBookpicslist().get(bookAds.getBookpicslist().size()-1))
                            .into(holder.bookpic);
                    holder.booktitle.setText(bookAds.getBooktitle());
                    holder.bookprice.setText(bookAds.getPrice());
                    holder.bookdate.setText(bookAds.getDate());
                    holder.bookcategory.setText(bookAds.getBookcategory());

                }
                holder.selectad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        Intent intent = new Intent(context, DisplayAdActivity.class);
                        intent.putExtra("bookads", bookAds);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @NonNull
            @Override
            public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(getContext())
                        .inflate(R.layout.bookadview,parent,false);
                final BookHolder bookHolder=new BookHolder(v);

                return bookHolder;
            }
        };
        firestoreRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firestoreRecyclerAdapter);

    }
    public class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView bookpic;
        public TextView bookprice,bookdate,bookcategory,booktitle;
        public ShimmerFrameLayout shimmerFrameLayout;
        public View selectad;


        public BookHolder(View itemView) {
            super(itemView);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerlayout);
            bookpic=(ImageView)itemView.findViewById(R.id.bookpic);
            bookcategory=itemView.findViewById(R.id.bookcategory);
            bookdate=itemView.findViewById(R.id.bookdate);
            bookprice=itemView.findViewById(R.id.bookprice);
            booktitle=itemView.findViewById(R.id.booktitle);
            selectad=itemView.findViewById(R.id.selectad);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookItemClickListener.onItemClick(v, getAdapterPosition());

                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }


    @Override
    public View onCreateView(LayoutInflater inflater2, ViewGroup container,  Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        v=inflater2.inflate(R.layout.fragment_home, container,false);
         floatingActionButton=v.findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        toolbargroup=getActivity().findViewById(R.id.toolbar_layout);
        toolbargroup.removeAllViews();
        toolbarview= getActivity().getLayoutInflater().inflate(R.layout.toolbar_homepage,toolbargroup,false);
        toolbargroup.addView(toolbarview);
        //mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        floatingActionButton.setOnClickListener(this);
        getadvertisements();



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
                Toast.makeText(getContext(), "search selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.filter:
                Toast.makeText(getContext(), "filter selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case android.R.id.home:

                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;


            default:
                break;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        floatingActionButton.hide();
        super.onDestroyView();


    }


    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(), PostnewadActivity.class);
        startActivity(intent);

    }
}
