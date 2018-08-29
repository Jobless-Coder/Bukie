package com.example.krishna.bukie.home;


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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.krishna.bukie.PostnewadActivity;
import com.example.krishna.bukie.Query;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.RESTapiinterface;
import com.example.krishna.bukie.helper.SearchData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Date;
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

    private static final String TAG = "HomeFragment";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 212;
    private static final int SCAN = 122;
    private RecyclerView recyclerView;
    private List<BookAds> bookAdsList=new ArrayList<>();
    private Context context;
    private View v,searchbtn;

    private View mSearchBar, mSearchIcon, mClearIcon, mSearchBack, mScanIcon;
    private int mSearchItemPosX = -1, mSearchItemPosY;
    private EditText mSearchBox;

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
    private View sort,filter;
    private boolean toggleSort,toggleFilter;
    private RadioButton option1,option2,option3,option4,option5;
    private RadioGroup radioGroup,radioGroup2;
    private int orderbyLocation=-2,orderbyPrice=-2;
    private List<BookAds> tempBookadsList=new ArrayList<>();
    private ProgressDialog progressDialog;
    private BottomSheetDialog dialog;
    private boolean mIsSearchBarOpen =false;
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
    private LinearLayout erroLinearLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        context=getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater2, ViewGroup container,  Bundle savedInstanceState) {
        v=inflater2.inflate(R.layout.fragment_home, container,false);

        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("BookMart");

        getActivity().findViewById(R.id.header).setVisibility(View.GONE);
        View tabsview=getActivity().findViewById(R.id.header2);
        tabsview.setVisibility(View.VISIBLE);
        sort=tabsview.findViewById(R.id.sort);
        sort.setOnClickListener(this);
        filter=tabsview.findViewById(R.id.filter);
        filter.setOnClickListener(this);
        sort.setSelected(false);
        filter.setSelected(false);
        erroLinearLayout =v.findViewById(R.id.error);

        firebaseFirestore=FirebaseFirestore.getInstance();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.simpleSwipeRefreshLayout);
        floatingActionButton=v.findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Searching...");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        homeBookAdsAdapter=new HomeBookAdsAdapter(bookAdsList,context,!mIsSearchBarOpen);
        recyclerView.setAdapter(homeBookAdsAdapter);
        floatingActionButton.setOnClickListener(this);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    floatingActionButton.hide();
                else if (dy < 0)
                    floatingActionButton.show();
            }
        });

        initSearchBar();

        return v;
    }

    private void initSearchBar() {
        mSearchBar = getActivity().findViewById(R.id.search_bar_layout);
        mSearchBack = getActivity().findViewById(R.id.search_bar_back);
        mSearchIcon = getActivity().findViewById(R.id.search_bar_search_icon);
        mClearIcon = getActivity().findViewById(R.id.search_bar_clear_icon);
        mScanIcon = getActivity().findViewById(R.id.search_bar_scan_icon);
        mSearchBox = getActivity().findViewById(R.id.search_bar_edittext);
        mSearchBack.setOnClickListener(this);
        mSearchIcon.setOnClickListener(this);
        mClearIcon.setOnClickListener(this);
        mScanIcon.setOnClickListener(this);

        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSearchIcon.setVisibility(View.VISIBLE);
                    mClearIcon.setVisibility(View.GONE);
                } else {
                    mSearchIcon.setVisibility(View.GONE);
                    mClearIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchAds();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.homemenu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:
                if (mSearchItemPosX == -1) {
                    View searchItem = getActivity().findViewById(R.id.item_search);
                    int loc[] = new int[2];
                    searchItem.getLocationOnScreen(loc);
                    mSearchItemPosX = loc[0];
                    mSearchItemPosY = loc[1];
                    Log.d(TAG, "Search item location: (" + mSearchItemPosX + "," + mSearchItemPosY + ")");
                }
                showHideSearchBar();
                break;
        }
        return true;
    }

    public void searchAds(){
        InputMethodManager in = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
        searchAdsList.clear();
        searchAdsList.addAll(bookAdsList);
        bookAdsList.clear();
        searchPathList.clear();
        homeBookAdsAdapter.notifyDataSetChanged();

        String query= mSearchBox.getText().toString().trim();
        if(query.length()>0){
            erroLinearLayout.setVisibility(View.GONE);
            // Toast.makeText(context, ""+query, Toast.LENGTH_SHORT).show();
            progressDialog.show();
            getMyAdsPathsSearch(query,searchType);
            mSearchIcon.setVisibility(View.GONE);
            mClearIcon.setVisibility(View.VISIBLE);
        }
        else {
            mSearchBox.setText("");
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
                        erroLinearLayout.setVisibility(View.VISIBLE);
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

        //Added by Krishna Bose, 22nd August
        //this code is used to log the search data in RTDb for further reading
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPreferences= getContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
        dref = dref.child("users/"+uid).child("mysearches").push();
        SearchData data;
        if(searchType!=null)
            data = new SearchData(query, new Date().getTime(),!searchType.equals("isbn"));
        else
            data = new SearchData(query, new Date().getTime(),true);
        dref.setValue(data);

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
                mSearchBox.setText(data.getExtras().getString("isbn"));
                searchType="isbn";
                searchAds();
            }
                 else
                Toast.makeText(getContext(), "No isbn found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHideSearchBar() {
        int w = mSearchBar.getWidth();
        int h = mSearchBar.getHeight();
        int endRadius = (int) Math.hypot(w, h);

        int cx = mSearchItemPosX;
        int cy = mSearchItemPosY;
        if (!mIsSearchBarOpen) {
            swipeRefreshLayout.setEnabled(false);
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(mSearchBar, cx,cy, 0, endRadius);
            mSearchBar.setVisibility(View.VISIBLE);
            revealAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsSearchBarOpen = true;
                    mSearchBox.requestFocus();
                }
            });
            revealAnimator.setDuration(300);
            revealAnimator.start();
        } else {
            swipeRefreshLayout.setEnabled(true);
            mSearchBox.setText("");
            if (searchAdsList.size() > 0) {
                bookAdsList.clear();
                homeBookAdsAdapter.notifyDataSetChanged();
                homeBookAdsAdapter = new HomeBookAdsAdapter(bookAdsList,context,!mIsSearchBarOpen);//update mIsSearchBarOpen
                bookAdsList.addAll(searchAdsList);
                searchAdsList.clear();
                homeBookAdsAdapter.notifyDataSetChanged();
            }

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(mSearchBar, cx, cy, endRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mSearchBar.setVisibility(View.GONE);
                    mIsSearchBarOpen = false;
                }
            });
            anim.setDuration(300);
            anim.start();
        }
    }


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
                long k = bookAds.getPrice();

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
                        if (o1.getPrice()==0 || o2.getPrice()==0)
                            return 0;
                        return (int) (orderbyPrice * (o1.getPrice() - o2.getPrice()));
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
            case R.id.search_bar_search_icon:
                searchType="text";
                searchAds();
                break;
            case R.id.search_bar_clear_icon:
                mSearchBox.setText("");
                mSearchIcon.setVisibility(View.VISIBLE);
                mClearIcon.setVisibility(View.GONE);
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
                    if(!mIsSearchBarOpen &&!toggleSort) {
                      //  Toast.makeText(context, ""+toggleSort+""+mIsSearchBarOpen, Toast.LENGTH_SHORT).show();
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
                    if(!mIsSearchBarOpen &&!toggleFilter)
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
            case R.id.search_bar_back:
                showHideSearchBar();
                break;
            case R.id.search_bar_scan_icon:
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
