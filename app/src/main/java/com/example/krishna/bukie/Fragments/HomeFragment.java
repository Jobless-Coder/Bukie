package com.example.krishna.bukie.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.Filter;
import com.example.krishna.bukie.HomeBookAdsAdapter;
import com.example.krishna.bukie.PostnewadActivity;
import com.example.krishna.bukie.Query;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.RESTapiinterface;
import com.example.krishna.bukie.SearchActivity;
import com.example.krishna.bukie.Sort;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<BookAds> bookAdsList=new ArrayList<>();
    private Context context;
    private View v;
    public FloatingActionButton floatingActionButton;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ViewGroup toolbargroup;
    private View toolbarview;
    private BookItemClickListener bookItemClickListener;
    private DrawerLayout mDrawerLayout;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private HomeBookAdsAdapter homeBookAdsAdapter;
    private ListenerRegistration listenerRegistration;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CollectionReference db;
    private DiscreteSeekBar price,location;
    private TextView price1,location1;
    private List<String> searchBookadsList=new ArrayList<>();
    private View sort,filter,search,back;
    private boolean toggleSort,toggleFilter;
    private RadioButton option1,option2,option3,option4,option5;
    private RadioGroup radioGroup,radioGroup2;
    private int orderbyLocation=-2,orderbyPrice=-2;
    private List<String> filterSortBookadsListPath=new ArrayList<>();
    private List<BookAds> tempBookadsList=new ArrayList<>();
    private ProgressDialog progressDialog;
    private BottomSheetDialog dialog;
    private boolean isSearch=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setHasOptionsMenu(true);
        context=getContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater2, ViewGroup container,  Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        v=inflater2.inflate(R.layout.fragment_home, container,false);
        getActivity().findViewById(R.id.header).setVisibility(View.GONE);
        View tabsview=getActivity().findViewById(R.id.header2);
        tabsview.setVisibility(View.VISIBLE);
        sort=tabsview.findViewById(R.id.sort);
        sort.setOnClickListener(this);
        filter=tabsview.findViewById(R.id.filter);
        filter.setOnClickListener(this);
        sort.setSelected(false);
        filter.setSelected(false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.simpleSwipeRefreshLayout);
        floatingActionButton=v.findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        toolbargroup=getActivity().findViewById(R.id.toolbar_layout);
        toolbargroup.removeAllViews();
        toolbarview= getActivity().getLayoutInflater().inflate(R.layout.toolbar_homepage,toolbargroup,false);
        toolbargroup.addView(toolbarview);
        progressDialog=new ProgressDialog(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        homeBookAdsAdapter=new HomeBookAdsAdapter(bookAdsList,context);
        recyclerView.setAdapter(homeBookAdsAdapter);
        floatingActionButton.setOnClickListener(this);
        back=getActivity().findViewById(R.id.back);
        back.setOnClickListener(this);
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
        search=getActivity().findViewById(R.id.search);
        search.setOnClickListener(this);



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

  /* @Override
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

                showSearch();
                break;
                /*View toolbarsearch=getActivity().findViewById(R.id.transitiontoolbar);
                View search=getActivity().findViewById(R.id.search);
                Pair<View, String> p1 = Pair.create(toolbarsearch, "search");
               // Pair<View, String> p2 = Pair.create(search, "searchicon");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(getActivity(),p1);
                Intent intent=new Intent(getActivity(), SearchActivity.class);
                //intent.putExtra(SyncStateContract.Constants.KEY_ANIM_TYPE,)
                ///Toast.makeText(getContext(), "search selected", Toast.LENGTH_SHORT)
               //         .show();
                startActivity(intent,activityOptions.toBundle());*/

            /*case R.id.Filter:
                showBottomSheetDialog();
               /* Intent intent1=new Intent(getActivity(), FilterActivity.class);
                startActivity(intent1);



                break;
            default:
                break;
        }
        return true;
    }*/

    private void showSearch() {
        final View view = getActivity().findViewById(R.id.header3);


        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (search.getX() + (search.getWidth()/2));
        int cy = (int) (search.getY())+ search.getHeight()/2;


        if(!isSearch){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(300);
            revealAnimator.start();
            isSearch=true;

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //dialog.dismiss();
                    view.setVisibility(View.GONE);
                    isSearch=false;

                }
            });
            anim.setDuration(300);
            anim.start();
        }
    }
    /*public void filterorSortAds(Query query){
        progressDialog.setMessage("wait..");
        progressDialog.show();
        tempBookadsList.addAll(bookAdsList);

        bookAdsList.clear();
       // Toast.makeText(context, bookAdsList.size()+"ddddd"+tempBookadsList.size(), Toast.LENGTH_SHORT).show();
        homeBookAdsAdapter.notifyDataSetChanged();

       // Query query=new Query();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RESTapiinterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RESTapiinterface resTapiinterface=retrofit.create(RESTapiinterface.class);
        Call<List<String>> call = resTapiinterface.filterSortBook(query);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.code()==200&&response.body()!=null) {
                    filterSortBookadsListPath.clear();
                    filterSortBookadsListPath=response.body();
                   // Log.i("adspath", (filterSortBookadsListPath).toString());
                    //getFilterOrSortAdsList(filterSortBookadsListPath);

                }
                else {
                    progressDialog.dismiss();
                }



            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                //progressDialog.dismiss();
            }
        });
    }*/

    /*private void getFilterOrSortAdsList(List<String> filterSortBookadsListPath) {

        Log.i("newads",filterSortBookadsListPath.toString());
        for(final String bookadspath:filterSortBookadsListPath){
            DocumentReference docRef = firebaseFirestore.collection("bookads").document(bookadspath);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        // Document found in the offline cache
                        DocumentSnapshot document = task.getResult();
                        BookAds bookAds=document.toObject(BookAds.class);
                        bookAdsList.add(bookAds);
                       // k[0]=k[0]+" "+bookAds.getAdid();
                        Log.i("newads",bookAds.getAdid()+" "+bookadspath);
                        homeBookAdsAdapter.notifyDataSetChanged();

                        /// Log.d(TAG, "Cached document data: " + document.getData());
                    } else {
                        // Log.d(TAG, "Cached get failed: ", task.getException());
                    }
                }
            });
        }
       // Log.i("newads",k[0]);

    }*/

    public void showFilterBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_bottomsheet_filter, null);

         dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view);
        dialog.show();
        View apply=view.findViewById(R.id.apply);
        price=view.findViewById(R.id.price);
        location=view.findViewById(R.id.location);
        price1=view.findViewById(R.id.pricetxt);
        location1=view.findViewById(R.id.locationtxt);
        location1.setText(0+"");
        price1.setText(0+"");


        price.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
               price1.setText(value+"");
               // Toast.makeText(context, ""+value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        location.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                location1.setText(value+"");
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int location,price;
                location=Integer.parseInt(location1.getText().toString());
                price=Integer.parseInt(price1.getText().toString());
                if(location>1||price>1) {
                    filter.setSelected(true);
                    toggleFilter = true;

                    swipeRefreshLayout.setEnabled(false);
                    tempBookadsList.clear();
                    tempBookadsList.addAll(bookAdsList);
                    bookAdsList.clear();
                    homeBookAdsAdapter.notifyDataSetChanged();
                    filterBookads(location,price);

                }



                dialog.dismiss();


            }
        });


    }

    private void filterBookads(int location, int price) {
        if(price==1000){
            //Toast.makeText(context, "kkk", Toast.LENGTH_SHORT).show();
            bookAdsList.addAll(tempBookadsList);
            homeBookAdsAdapter.notifyDataSetChanged();
            dialog.dismiss();

        }
        else {

            for (BookAds bookAds : tempBookadsList) {
                int k = Integer.parseInt(bookAds.getPrice().replace(" ", "").replace("₹", ""));

            if (k <= price) {
              //  Log.e("kkr",k+" "+bookAdsList.size());
               // Toast.makeText(context, "kkkkk"+k+"lll"+bookAdsList.size(), Toast.LENGTH_SHORT).show();
                bookAdsList.add(bookAds);
                    homeBookAdsAdapter.notifyDataSetChanged();
                    if(dialog.isShowing())
                        dialog.dismiss();

                }

            }
        }

    }

    public void showSortBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_bottomsheet_sort, null);

        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view);
        dialog.show();
        View apply=view.findViewById(R.id.apply);
        option1=view.findViewById(R.id.option1);
        option1.setChecked(true);
        option2=view.findViewById(R.id.option2);
        option3=view.findViewById(R.id.option3);
        option4=view.findViewById(R.id.option4);
        option5=view.findViewById(R.id.option5);
        radioGroup=view.findViewById(R.id.rg);
        radioGroup2=view.findViewById(R.id.rg2);
        option1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


               if(isChecked){
                   orderbyPrice=-2;
                   orderbyLocation=-2;
                   radioGroup.clearCheck();
                   radioGroup2.clearCheck();
                   option1.setChecked(true);
               }



                //relevance

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    /*case R.id.option1:

                        break;*/
                    case R.id.option2:
                        orderbyPrice=1;
                       // option3.setChecked(false);
                        option1.setChecked(false);
                        //price low to high
                        break;
                    case R.id.option3:
                        orderbyPrice=-1;
                        option1.setChecked(false);

                        //price high to low
                        break;


                }
            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.option4:
                        orderbyLocation=1;
                        option1.setChecked(false);
                        break;
                    case R.id.option5:
                        orderbyLocation=-1;
                        option1.setChecked(false);
                        break;
                }

            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setEnabled(false);

                sort.setSelected(true);
                toggleSort=true;
                tempBookadsList.clear();
                tempBookadsList.addAll(bookAdsList);


                if(orderbyLocation==-2||orderbyPrice==-2) {

                    Collections.sort(bookAdsList, new Comparator<BookAds>() {
                        @Override
                        public int compare(BookAds o1, BookAds o2) {
                            if (orderbyLocation == -2 && orderbyPrice != -2) {
                                if (o1.getPrice().replace(" ", "").replace("₹", "").equals("") || o2.getPrice().replace(" ", "").replace("₹", "").equals(""))
                                    return 0;
                                Log.e("kkkk", "ll" + o1.getPrice() + "ll");
                                return orderbyPrice * (Integer.parseInt(o1.getPrice().replace(" ", "").replace("₹", "")) - Integer.parseInt(o2.getPrice().replace(" ", "").replace("₹", "")));
                            }


                            return 0;
                        }
                    });
                    homeBookAdsAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();



            }
        });

    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton:
                Intent intent = new Intent(getContext(), PostnewadActivity.class);
                intent.putExtra("isHome", 1);

                startActivity(intent);

                break;
            case R.id.filter:
                if (toggleFilter == false)
                {

                    //Filter.setSelected(true);
                    showFilterBottomSheetDialog();

        }
                else {
                    swipeRefreshLayout.setEnabled(true);
                    bookAdsList.clear();
                    homeBookAdsAdapter.notifyDataSetChanged();
                    bookAdsList.addAll(tempBookadsList);
                  //  Toast.makeText(context, ""+bookAdsList.size(), Toast.LENGTH_SHORT).show();
                    homeBookAdsAdapter.notifyDataSetChanged();
                    tempBookadsList.clear();
                    filter.setSelected(false);
                    toggleFilter=false;
                    //tempBookadsList.clear();
                }



                break;
            case R.id.sort:
                if(toggleSort==false) {
                    showSortBottomSheetDialog();

                }
                else {
                    swipeRefreshLayout.setEnabled(true);
                    bookAdsList.clear();
                    homeBookAdsAdapter.notifyDataSetChanged();
                    bookAdsList.addAll(tempBookadsList);
                   // Toast.makeText(context, ""+bookAdsList.size(), Toast.LENGTH_SHORT).show();
                    homeBookAdsAdapter.notifyDataSetChanged();
                    tempBookadsList.clear();
                    sort.setSelected(false);
                    toggleSort=false;
                }
                break;
            case R.id.search:
                showSearch();
                break;
            case R.id.back:
                showSearch();
                break;
        }




    }

    @Override
    public void onResume() {

        //homeBookAdsAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
