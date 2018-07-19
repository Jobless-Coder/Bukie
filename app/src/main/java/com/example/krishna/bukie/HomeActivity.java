package com.example.krishna.bukie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*final Toolbar toolbar_mychats = (Toolbar) findViewById(R.id.toolbar_mychats);
        setSupportActionBar(toolbar_mychats);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_mychats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Toolbar","Clicked");
            }
        });*/
    }

    public void goToChat(View view) {
        startActivity(new Intent(this,ChatActivity.class));

    }



    public void goToFeedback(View view) {
        startActivity(new Intent(this, FeedbackActivity.class));
        //startActivity(new Intent(this, FeedbackActivity.class));
    }

    public void goToHomepage(View view) {
        startActivity(new Intent(this,HomePageActivity.class));
    }

    public void goToAuth(View view) {
        startActivity(new Intent(this,AuthActivity.class));
    }

    public void goToDisplayAd(View view) {
        startActivity(new Intent(this,RegistrationActivity.class));
    }
}
