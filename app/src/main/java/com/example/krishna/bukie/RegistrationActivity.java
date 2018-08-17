package com.example.krishna.bukie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="dfg" ;
    private static final int SELECT_PHOTO = 1;
    private EditText username,fullname;
    private View register,ppview;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private ImageView profilepic;
    private Uri imageUri;
    private String profilepicurl,path,signinmethod,uid,fullnameid;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference usercollection;
    private SharedPreferences pref ;
    private FirebaseUser firebaseUser;
    private InterestAdapter interestAdapter;
    private ArrayList<String> interests;
    private TextView registerText;
    private boolean toogleProfile=false;
    private Map<String, Object> profileMap;
    private DocumentReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ppview=findViewById(R.id.ppview);
        ppview.setOnClickListener(this);
        profilepic =findViewById(R.id.profile_pic);
        pref=getSharedPreferences("UserInfo",MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        signinmethod=bundle.getString("signinmethod");
        toogleProfile=bundle.getBoolean("isProfile");
        fullname=(EditText) findViewById(R.id.fullname);
        register = (View) findViewById(R.id.register);
        register.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseUser=firebaseAuth.getCurrentUser();
        interests= new ArrayList<>();
        registerText=findViewById(R.id.registertxt);

        if(toogleProfile==true){

            SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
            profilepicurl=sharedPreferences.getString("profilepic",null);
            Glide.with(getApplicationContext()).load(profilepicurl).into(profilepic);
            fullnameid=sharedPreferences.getString("fullname",null);
            uid=sharedPreferences.getString("uid",null);
            fullname.setText(fullnameid);
            registerText.setText("UPDATE");
            userRef=FirebaseFirestore.getInstance().collection("users").document(uid);
            /*SharedPreferences.Editor editor = pref.edit();
            editor.putString("uid",uid);
            editor.putString("profilepic",profilepicurl);
            editor.putString("fullname",fullnameid);
            editor.commit();*/
        }

        if(firebaseUser != null) {
            //Toast.makeText(this, "kkk", Toast.LENGTH_SHORT).show();
            if (firebaseUser.getPhotoUrl() != null)
                profilepicurl = firebaseUser.getPhotoUrl().toString();
            else
                profilepicurl = "";
            uid = firebaseUser.getUid();
            fullnameid = firebaseUser.getDisplayName();
            Glide.with(getApplicationContext()).load(profilepicurl).into(profilepic);
            fullname.setText(fullnameid);
        }

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("interests");
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList t = new ArrayList();
                for(DataSnapshot s: dataSnapshot.getChildren())
                {
                    t.add(s.getValue().toString());
                }
                displayInterests(t);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void displayInterests(ArrayList<String> t) {
        interests = t;
        FlexboxLayout layout = findViewById(R.id.flexlayout);
        interestAdapter = new InterestAdapter(this, interests, layout);
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.register:
                    if(toogleProfile==false) {
                        createNewUser();
                        progressDialog.setMessage("Registering user ...");
                        progressDialog.show();
                    }
                    else {
                        progressDialog.setMessage("Updating user ...");
                        progressDialog.show();
                        editProfile();
                    }

                break;
            case R.id.ppview:
                changeProfilepic();
                break;
            
            default:
                break;
        }

    }

    private void editProfile() {
       /* String tmp=fullname.getText()+"";
        tmp=tmp.trim();
        if(!fullnameid.equals(tmp)&&tmp.length()>0)
        userRef.update("fullname",tmp);
           // profileMap.put("fullname",fullname.getText()+"");

            if(!imageUri.equals(profilepicurl)){
                path = "profilepicuser/" + uid ;
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
                        progressDialog.dismiss();
                        userRef.update("profilepic",uri+"");
                //profileMap.put("profilepic",uri+"");


                    }
                });


            }
            else {
                progressDialog.dismiss();
            }
            */



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



         if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK
                && null != data){


                imageUri = data.getData();
                String destinationFileName = "temp_profile_picture.png";

                UCrop uCrop = UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), destinationFileName)))
                        .withAspectRatio(1,1);


                uCrop.start(RegistrationActivity.this);


        }
        else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null)
         {

             try {
                imageUri = UCrop.getOutput(data);

                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profilepic.setImageBitmap(selectedImage);

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

    /*private void registerUser() {

        createNewUser();
    }*/

    private void createNewUser() {

        if (imageUri != null) {
            path = "profilepicuser/" + uid + ".png";
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
                    User user=new User(profilepicurl,signinmethod,uid,fullnameid);
                    firebaseFirestore.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("uid",uid);
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

            User user=new User(profilepicurl,signinmethod,uid,fullnameid);
            //User user=new User(usernameid,profilepicurl);
            firebaseFirestore.collection("users").document(uid)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("uid",uid);
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
