package com.example.krishna.bukie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FeedbackActivity extends AppCompatActivity {

    boolean underprogress=false;
    private Uri imageUri;
    private static final int PICK_IMAGE = 123;
    boolean imageAdded = false;
String review;
String imgurl = "";
String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);



    }

    public void submitReview(View view) {
        if(underprogress) return;
        underprogress= true;
        review = ((EditText)findViewById(R.id.usercomment)).getText().toString().trim();
        ((EditText)findViewById(R.id.usercomment)).setText("");
        if(review.equalsIgnoreCase("")){
            Toast.makeText(this, "Please enter something to send to the developers", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        if(imageAdded)
            uploadImage();
        else
            upload();
    }

    private void upload() {

        if(imageAdded)
        {
            imageAdded = false;
            findViewById(R.id.userselectedimage).setVisibility(View.GONE);

            findViewById(R.id.imageaddingbutton).setVisibility(View.VISIBLE);
        }
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("reviews");
        Feedback fb = new Feedback(getUserId(), review, imgurl);
        dref.push().setValue(fb);

        findViewById(R.id.progress).setVisibility(View.GONE);
        Toast.makeText(this, "Thanks for your feedback", Toast.LENGTH_SHORT).show();
        underprogress = false;
    }

    private String getUserId() {
        //pick up current user from sharedPreferences
        //return null;
        //TODO: get original user id in upcoming iterations
        return uid;
    }

    private void uploadImage() {
        //add firebase storage integration and upload it, onsuccess get the download url and call upload()
        final StorageReference ref = FirebaseStorage.getInstance().getReference("child/"+imageUri.getLastPathSegment());
        ref.putFile(imageUri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        // Forward any exceptions
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        Log.d("Firebase Storage", "uploadFromUri: upload success");

                        // Request the public download URL
                        return ref.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgurl = uri.toString();
                        upload();
                    }
                });
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
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageAdded = true;
            imageUri = data.getData();
            findViewById(R.id.userselectedimage).setVisibility(View.VISIBLE);
            ImageView userImage = findViewById(R.id.userselectedimage);
            userImage.setImageURI(imageUri);
            findViewById(R.id.imageaddingbutton).setVisibility(View.GONE);
        }
    }
}
