package com.example.krishna.bukie;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="dfg" ;
    private static final int SELECT_PHOTO = 1;

    private EditText username,fullname;
    private View register,ppview;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private ImageView imageView;
    private Uri imageUri;
    private String usernameid,profilepicurl,path,signinmethod,uid,fullnameid;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference usercollection;
    private SharedPreferences pref ;
    private FirebaseUser firebaseUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        pref=getSharedPreferences("UserInfo",MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        signinmethod=bundle.getString("signinmethod");
        fullname=(EditText) findViewById(R.id.fullname);
        username = (EditText) findViewById(R.id.username);
        register = (View) findViewById(R.id.register);
        imageView=findViewById(R.id.profile_pic);
        ppview=findViewById(R.id.ppview);
        register.setOnClickListener(this);
        ppview.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser.getPhotoUrl()!=null)
        profilepicurl=firebaseUser.getPhotoUrl().toString();
        else
            profilepicurl="";
        uid=firebaseUser.getUid();
        fullnameid=firebaseUser.getDisplayName();
        Glide.with(getApplicationContext()).load(profilepicurl).into(imageView);
        fullname.setText(fullnameid);




    }






    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.register:
                usernameid=username.getText().toString();
                if(usernameid.isEmpty()==false){
                    registerUser();
                    progressDialog.setMessage("Registering user ...");
                    progressDialog.show();
                }

                else
                    Toast.makeText(this, "Input valid username", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ppview:
                changeProfilepic();
                break;
            
            default:
                break;
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



         if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK
                && null != data){

            try {
                imageUri = data.getData();

                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(this, "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeProfilepic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PHOTO);
    }

    private void registerUser() {
        username.setText("");
        //Toast
        DocumentReference user=firebaseFirestore.collection("users").document(usernameid);
        user.get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot> () {
            @Override
            public void onComplete(@NonNull Task < DocumentSnapshot > task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistrationActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();

                    } else {
                        createNewUser();
                    }


                }
            }
        });




    }

    private void createNewUser() {

        if (imageUri != null) {
            path = "profilepicuser/" + usernameid + ".png";
            final StorageReference riversRef = storageReference.child(path);

            UploadTask uploadTask = riversRef.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();                        //throw task.getException();
                        Toast.makeText(RegistrationActivity.this, "Error uploading photo,checck internet connection", Toast.LENGTH_SHORT).show();
                    }


                    return riversRef.getDownloadUrl();
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    profilepicurl=uri+"";
                    User user=new User(usernameid,profilepicurl,signinmethod,uid,fullnameid);
                    firebaseFirestore.collection("users").document(usernameid)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("username",usernameid);
                                    editor.putString("profilepic",profilepicurl);
                                    editor.putString("fullname",fullnameid);
                                    editor.commit();

                                    Toast.makeText(RegistrationActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegistrationActivity.this, "Error registering,pls try again later", Toast.LENGTH_SHORT).show();

                                }
                            });


                }
            });

        }
        else {

            User user=new User(usernameid,profilepicurl,signinmethod,uid,fullnameid);
            //User user=new User(usernameid,profilepicurl);
            firebaseFirestore.collection("users").document(usernameid)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("username",usernameid);
                            editor.putString("profilepic",profilepicurl);
                            editor.putString("fullname",fullnameid);
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                            finish();



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Error registering,pls try again later", Toast.LENGTH_SHORT).show();

                        }
                    });


        }
    }


}
