package com.example.krishna.bukie.Fragments;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.example.krishna.bukie.BookAds;
import com.example.krishna.bukie.FullscreenScannerActivity;
import com.example.krishna.bukie.HomeBookAdsAdapter;
import com.example.krishna.bukie.PostnewadActivity;
import com.example.krishna.bukie.Query;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.RESTapiinterface;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 212;
    private static final int SCAN = 122;
    private RecyclerView recyclerView;
    private List<BookAds> bookAdsList=new ArrayList<>();
    private Context context;
    private View v,searchbtn,search_icon,clear_icon;
    private EditText searchbox;
    public FloatingActionButton floatingActionButton;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ViewGroup toolbargroup;
    private View toolbarview;
    private BookItemClickListener bookItemClickListener;
   // private DrawerLayout mDrawerLayout;
    //private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private HomeBookAdsAdapter homeBookAdsAdapter;
    private ListenerRegistration listenerRegistration;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CollectionReference db;
    private DiscreteSeekBar price,location;
    private TextView price1,location1;
    private List<String> searchPathList =new ArrayList<>();
    private View sort,filter,search,back;
    private boolean toggleSort,toggleFilter;
    private RadioButton option1,option2,option3,option4,option5;
    private RadioGroup radioGroup,radioGroup2;
    private int orderbyLocation=-2,orderbyPrice=-2;
    private List<BookAds> tempBookadsList=new ArrayList<>();
    private ProgressDialog progressDialog;
    private BottomSheetDialog dialog;
    private boolean isSearch=false,togglesearch=false;
   // private List<String> bookadslistPath=new ArrayList<>();
    private Map<String,Integer> adidMap=new HashMap<>();
    private String searchType,uid;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    //private ValueEventListener sellerListener,buyerListener;
    private AHBottomNavigation bottomNavigation;
   // private com.google.firebase.database.Query buyerQuery,sellerQuery;
    private List<BookAds> sortAdsList=new ArrayList<>();
    private List<BookAds> filterAdsList=new ArrayList<>();
    private List<BookAds> searchAdsList=new ArrayList<>();
    private int count=0;
    private LinearLayout linearLayout;
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
        linearLayout=v.findViewById(R.id.error);
        searchbox=getActivity().findViewById(R.id.searchbox);
        searchbtn=getActivity().findViewById(R.id.searchbtn);
        searchbtn.setOnClickListener(this);
        search_icon=getActivity().findViewById(R.id.searchicon);
        clear_icon=getActivity().findViewById(R.id.clearicon);
        firebaseFirestore=FirebaseFirestore.getInstance();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.simpleSwipeRefreshLayout);
        floatingActionButton=v.findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        toolbargroup=getActivity().findViewById(R.id.toolbar_layout);
        toolbargroup.removeAllViews();
        toolbarview= getActivity().getLayoutInflater().inflate(R.layout.toolbar_homepage,toolbargroup,false);
        toolbargroup.addView(toolbarview);
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Searching...");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        homeBookAdsAdapter=new HomeBookAdsAdapter(bookAdsList,context);
        recyclerView.setAdapter(homeBookAdsAdapter);
        floatingActionButton.setOnClickListener(this);
        back=getActivity().findViewById(R.id.back);
        back.setOnClickListener(this);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
        getActivity().findViewById(R.id.searchscanicon).setOnClickListener(this);
        bottomNavigation = (AHBottomNavigation) getActivity().findViewById(R.id.bottom_navigation);

        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));
        getadvertisements();
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.yellow,
                R.color.light_blue);
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
        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ( (s.length() <before||s.length()>before)&&togglesearch==false) {
                    togglesearch=true;
                    search_icon.setVisibility(View.VISIBLE);
                    clear_icon.setVisibility(View.GONE);

                }
                if (before > 0 && s.length() == 0) {
                    togglesearch=true;
                    search_icon.setVisibility(View.VISIBLE);
                    clear_icon.setVisibility(View.GONE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchAds();

                    return true;
                }
                return false;
            }
        });

        return v;
    }
    public void searchAds(){
        InputMethodManager in = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchbox.getWindowToken(), 0);
        searchAdsList.clear();
        searchAdsList.addAll(bookAdsList);
        bookAdsList.clear();
        searchPathList.clear();
        homeBookAdsAdapter.notifyDataSetChanged();

        String query=searchbox.getText().toString().trim();
        if(query.length()>0){
            linearLayout.setVisibility(View.GONE);
            // Toast.makeText(context, ""+query, Toast.LENGTH_SHORT).show();
            progressDialog.show();
            getMyAdsPathsSearch(query,searchType);
            togglesearch=false;
            search_icon.setVisibility(View.GONE);
            clear_icon.setVisibility(View.VISIBLE);
        }
        else {
            searchbox.setText("");
        }

    }
    private void getMyAdsPathsSearch(String query,String searchType) {
        Query query1=new Query(query,searchType);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RESTapiinterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RESTapiinterface resTapiinterface=retrofit.create(RESTapiinterface.class);
        Call<List<String>> call = resTapiinterface.searchBook(query1);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.code()==200&&response.body()!=null) {
                    searchPathList =response.body();
                    if(searchPathList.size()>0) {
                        //swipeRefreshLayout.setEnabled(false);
                        getMyAdsSearch(searchPathList);
                    }
                    else {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Server error!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getMyAdsSearch(List<String> searchBookadsList) {
        bookAdsList.clear();
        homeBookAdsAdapter.notifyDataSetChanged();
        for (String s:searchBookadsList){
            if(adidMap.containsKey(s)){
                bookAdsList.add(searchAdsList.get(adidMap.get(s)));
                homeBookAdsAdapter.notifyDataSetChanged();
            }
        }

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
                                adidMap.put(bookAds.getAdid(),bookAdsList.size());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SCAN)
        {
            if(resultCode == RESULT_OK)
            {
                searchbox.setText(data.getExtras().getString("isbn"));
                searchType="isbn";
                searchAds();
            }
                 else
                Toast.makeText(getContext(), "No isbn found", Toast.LENGTH_SHORT).show();
        }
    }



    private void showSearch() {
        final View view = getActivity().findViewById(R.id.header3);
        int w = view.getWidth();
        int h = view.getHeight();
        int endRadius = (int) Math.hypot(w, h);
        int cx = (int) (search.getX() + (search.getWidth()/2));
        int cy = (int) (search.getY())+ search.getHeight()/2;
        if(!isSearch){
            swipeRefreshLayout.setEnabled(false);
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(300);
            revealAnimator.start();
            isSearch=true;

        } else {
            swipeRefreshLayout.setEnabled(true);
            searchbox.setText("");
            if(searchAdsList.size()>0) {
                bookAdsList.clear();
                homeBookAdsAdapter.notifyDataSetChanged();
                bookAdsList.addAll(searchAdsList);
                searchAdsList.clear();
                homeBookAdsAdapter.notifyDataSetChanged();
            }

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                    isSearch=false;
                }
            });
            anim.setDuration(300);
            anim.start();
            searchbox.requestFocus();
        }
    }
    ///Initially written for filter/sort through cloud function/rest api
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

                        filterAdsList.clear();
                        filterAdsList.addAll(bookAdsList);
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

            bookAdsList.addAll(filterAdsList);
            homeBookAdsAdapter.notifyDataSetChanged();
            dialog.dismiss();

        }
        else {

            for (BookAds bookAds : filterAdsList) {
                int k = Integer.parseInt(bookAds.getPrice().replace(" ", "").replace("₹", ""));

            if (k <= price) {
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
                    case R.id.option2:
                        orderbyPrice=1;
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
                sortAdsList.clear();
                sortAdsList.addAll(bookAdsList);
                sortAds(bookAdsList);



                dialog.dismiss();



            }
        });

    }
    public void sortAds(List<BookAds> bookAdsList1){
        if(orderbyLocation==-2||orderbyPrice==-2) {


            Collections.sort(bookAdsList1, new Comparator<BookAds>() {
                @Override
                public int compare(BookAds o1, BookAds o2) {
                    if (orderbyLocation == -2 && orderbyPrice != -2) {
                        if (o1.getPrice().replace(" ", "").replace("₹", "").equals("") || o2.getPrice().replace(" ", "").replace("₹", "").equals(""))
                            return 0;
                        return orderbyPrice * (Integer.parseInt(o1.getPrice().replace(" ", "").replace("₹", "")) - Integer.parseInt(o2.getPrice().replace(" ", "").replace("₹", "")));
                    }


                    return 0;
                }
            });
            homeBookAdsAdapter.notifyDataSetChanged();
        }

    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchbtn:
                if(togglesearch){
                    searchType="text";
                    searchAds();

                }
                else {
                    searchbox.setText("");
                    togglesearch=true;
                    search_icon.setVisibility(View.VISIBLE);
                    clear_icon.setVisibility(View.GONE);
                }
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(getContext(), PostnewadActivity.class);
                intent.putExtra("isHome", 1);

                startActivity(intent);


                break;
            case R.id.filter:
                if (toggleFilter == false)
                {

                    showFilterBottomSheetDialog();

        }
                else {
                    if(!isSearch&&!toggleSort) {
                      //  Toast.makeText(context, ""+toggleSort+""+isSearch, Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setEnabled(true);
                    }
                    bookAdsList.clear();
                    homeBookAdsAdapter.notifyDataSetChanged();
                    bookAdsList.addAll(filterAdsList);
                    homeBookAdsAdapter.notifyDataSetChanged();
                    filterAdsList.clear();
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
                    if(!isSearch&&!toggleFilter)
                    swipeRefreshLayout.setEnabled(true);
                    bookAdsList.clear();
                    homeBookAdsAdapter.notifyDataSetChanged();


                        bookAdsList.addAll(sortAdsList);

                    homeBookAdsAdapter.notifyDataSetChanged();
                    sortAdsList.clear();
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
            case R.id.searchscanicon:
                if(!getCameraPermissions())
                    return;
                startActivityForResult(new Intent(getContext(), FullscreenScannerActivity.class),SCAN);
                break;
        }
    }
    private boolean getCameraPermissions()
    {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }

            return false;
        }
        return true;

    }


}
