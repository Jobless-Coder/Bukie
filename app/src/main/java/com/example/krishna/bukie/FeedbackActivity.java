package com.example.krishna.bukie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 123;
    boolean imageAdded = false;
String review;
String imgurl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

    }

    public void submitReview(View view) {
        review = ((EditText)findViewById(R.id.usercomment)).getText().toString().trim();
        ((EditText)findViewById(R.id.usercomment)).setText("");
        if(review.equalsIgnoreCase("")){
            Toast.makeText(this, "Please enter something to send to the developers", Toast.LENGTH_SHORT).show();
            return;
        }

        if(imageAdded)
            uploadImage();
        else
            upload();
    }

    private void upload() {

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("reviews");
        Feedback fb = new Feedback(getUserId(), review, imgurl);
        dref.push().setValue(fb);
        Toast.makeText(this, "Thanks for your feedback", Toast.LENGTH_SHORT).show();
    }

    private String getUserId() {
        //pick up current user from sharedPreferences
        //return null;
        return "admin";
    }

    private void uploadImage() {
        //add firebase storage integration and upload it, onsuccess get the download url and call upload()
        upload();
    }

    public void addImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            //TODO: action
        }
    }
}
