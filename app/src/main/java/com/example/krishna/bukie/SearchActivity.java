package com.example.krishna.bukie;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean togglesearch=true;
    private View search,back,search_icon,clear_icon;
    private ProgressDialog progressDialog;
    private EditText searchbox;
    private List<String> bookadslistpath=new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private HomeBookAdsAdapter homeBookAdsAdapter;
    private RecyclerView recyclerView;
    private List<BookAds> bookAdsList=new ArrayList<>();
    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        Toolbar toolbar=findViewById(R.id.toolbar_chats);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        search=toolbar.findViewById(R.id.searchbtn);
        back=toolbar.findViewById(R.id.back);
        search.setOnClickListener(this);
        back.setOnClickListener(this);
        searchbox=toolbar.findViewById(R.id.searchbox);
        search_icon=toolbar.findViewById(R.id.searchicon);
        clear_icon=toolbar.findViewById(R.id.clearicon);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Searching wait ..");

       // Log.i("hellonibbas","ffg");
        Fade fade = new Fade();
        fade.excludeTarget(R.id.tt, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        firebaseFirestore=FirebaseFirestore.getInstance();
        context=getApplicationContext();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        homeBookAdsAdapter=new HomeBookAdsAdapter(bookAdsList,context);
        recyclerView.setAdapter(homeBookAdsAdapter);


       // handleIntent(getIntent());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchbtn:
                if(togglesearch==true){
                    bookAdsList.clear();
                    bookadslistpath.clear();
                    homeBookAdsAdapter.notifyDataSetChanged();



                    String query=searchbox.getText().toString().trim();
                    if(query!=""){
                        progressDialog.show();
                        getMyAdsPaths(query);
                        togglesearch=false;
                        search_icon.setVisibility(View.GONE);
                        clear_icon.setVisibility(View.VISIBLE);
                    }


                }
                else {
                    searchbox.setText("");
                    togglesearch=true;
                    search_icon.setVisibility(View.VISIBLE);
                    clear_icon.setVisibility(View.GONE);
                }
                break;
            case R.id.back:
                onBackPressed();
                break;
        }

    }

    private void getMyAdsPaths(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RESTapiinterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RESTapiinterface resTapiinterface=retrofit.create(RESTapiinterface.class);
        Call<List<String>> call = resTapiinterface.searchBook(query);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
               if(/*response.code()==200&&*/response.body()!=null) {
                    bookadslistpath=response.body();
                    getMyAds(bookadslistpath);
                    progressDialog.dismiss();
                    Toast.makeText(SearchActivity.this, bookadslistpath.get(0)+""+response.body().toString(), Toast.LENGTH_SHORT).show();

               }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    private void getMyAds(List<String> bookadslistpath) {

        for(String bookadspath:bookadslistpath){
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

                       /// Log.d(TAG, "Cached document data: " + document.getData());
                    } else {
                       // Log.d(TAG, "Cached get failed: ", task.getException());
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();

            super.onBackPressed();

    }


   /* @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow'
            Log.i("hellonibbas","ffg");
            Log.i("hellonibbas",query);
            Toast.makeText(this, ""+query, Toast.LENGTH_SHORT).show();
        }
    }*/

}
