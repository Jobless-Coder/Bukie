package com.example.krishna.bukie.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.krishna.bukie.home.HomePageActivity;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.User;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegistrationActivity";

    private static final int SELECT_PHOTO = 1;

    private static String TEMP_PIC_FILENAME = "temp_pic.png";
    private static String DEFAULT_PROFILE_PIC_FILENAME = "profile_pic_default.png";

    private EditText mFullNameEditText;
    private View mRegisterBtn, mPickImageBtn;
    private ProgressDialog mProgressDialog;
    private ImageView mProfilePicImg;
    private User mUser;
    private Uri mNewProfilePicUri;
    private StorageReference mStorageReference;
    private FirebaseFirestore mFirebaseFirestore;
    private InterestAdapter mInterestAdapter;
    // private TextView mRegisterText;
    // private boolean mToggleProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Bundle bundle = getIntent().getExtras();
        String signinmethod = bundle.getString("signinmethod");
        // mToggleProfile = bundle.getBoolean("isProfile");

        mProfilePicImg = findViewById(R.id.profile_pic);
        mPickImageBtn = findViewById(R.id.ppview);
        mFullNameEditText = findViewById(R.id.fullname);
        mRegisterBtn = findViewById(R.id.register);
        // mRegisterText = findViewById(R.id.registertxt);

        mPickImageBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        // TODO: Support edit profile
        /*
        if (mToggleProfile) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
            mProfilePicUrl = sharedPreferences.getString("profilepic",null);
            mFullName = sharedPreferences.getString("fullname",null);
            mUid = sharedPreferences.getString("uid",null);

            Glide.with(getApplicationContext()).load(mProfilePicUrl).into(mProfilePicImg);
            mFullNameEditText.setText(mFullName);
            mRegisterText.setText("UPDATE");
            userRef = FirebaseFirestore.getInstance().collection("users").document(mUid);
        }
        */

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        String fullname = firebaseUser.getDisplayName();
        mUser = new User(uid, fullname, "", signinmethod);
        if (firebaseUser.getPhotoUrl() != null) {
            Uri uri = firebaseUser.getPhotoUrl();
            mUser.setProfilepic(uri.toString());
            Glide.with(getApplicationContext()).load(uri).into(mProfilePicImg);
        } else {
            String path = "profilepicuser/" + DEFAULT_PROFILE_PIC_FILENAME;
            StorageReference imageRef = mStorageReference.child(path);
            final File tmpPic = new File(getCacheDir(), TEMP_PIC_FILENAME);
            imageRef.getFile(tmpPic)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            mNewProfilePicUri = Uri.fromFile(tmpPic);
                            Glide.with(getApplicationContext()).load(mNewProfilePicUri).into(mProfilePicImg);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to fetch default profile pic: " + e);
                        }
                    });
        }
        Log.d(TAG, "Current user info: " + mUser);

        mFullNameEditText.setText(mUser.getFullname());

        DatabaseReference interestsRef = FirebaseDatabase.getInstance().getReference().child("interests");
        interestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                displayInterests(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to fetch interests with error: " + databaseError);
            }
        });
    }

    /**
     * Displays available interests in a FlexboxLayout.
     *
     * @param dataSnapshot the data snapshot to extract the available interests from
     */
    private void displayInterests(DataSnapshot dataSnapshot) {
        ArrayList<String> list = new ArrayList<>();
        for (DataSnapshot s : dataSnapshot.getChildren()) {
            list.add(s.getValue().toString());
        }
        FlexboxLayout layout = findViewById(R.id.flexlayout);
        mInterestAdapter = new InterestAdapter(this, list, layout);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                /*
                if (mToggleProfile) {
                    mProgressDialog.setMessage("Updating user... ");
                    mProgressDialog.show();
                    // editProfile();
                } else {
                */
                registerNewUser();
                // }
                break;
            case R.id.ppview:
                changeProfilePic();
                break;
        }
    }

    /**
     * Launches an intent allowing the user to pick a picture from local storage.
     */
    private void changeProfilePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_img)), SELECT_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri srcImageUri = data.getData();
                    File tmpPic = new File(getCacheDir(), TEMP_PIC_FILENAME);
                    Uri destImageUri = Uri.fromFile(tmpPic);
                    UCrop uCrop = UCrop.of(srcImageUri, destImageUri).withAspectRatio(1, 1);
                    uCrop.start(RegistrationActivity.this);
                } else {
                    Toast.makeText(this, R.string.no_img_selected, Toast.LENGTH_SHORT).show();
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    mNewProfilePicUri = UCrop.getOutput(data);
                    // Skip cache because user can change profile pic again but destImageUrl remains same.
                    Glide.with(getApplicationContext())
                            .load(mNewProfilePicUri)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(mProfilePicImg);
                } else {
                    Throwable cropError = UCrop.getError(data);
                    Log.d(TAG, "Error in cropping: " + cropError);
                    Toast.makeText(RegistrationActivity.this, R.string.error_selecting_img_retry, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.d(TAG, "Unknown request code: " + requestCode);
        }
    }

    /**
     * Begins registration process for the new user.
     */
    private void registerNewUser() {
        mUser.setFullname(mFullNameEditText.getText().toString());
        if (TextUtils.isEmpty(mUser.getFullname())) {
            Toast.makeText(RegistrationActivity.this, R.string.name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.registering));
        mProgressDialog.show();

        if (mNewProfilePicUri != null) {
            uploadProfilePicThenUploadUserDetails();
        } else {
            uploadUserDetails();
        }
    }

    /**
     * Uploads the profile pic selected by the user to cloud storage, then calls
     * {@link #uploadUserDetails} to upload the user details.
     */
    private void uploadProfilePicThenUploadUserDetails() {
        String path = "profilepicuser/" + UUID.randomUUID() + ".png";
        Log.d(TAG, "Uploading profile pic to: " + path);

        final StorageReference imageRef = mStorageReference.child(path);
        imageRef.putFile(mNewProfilePicUri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            mUser.setProfilepic(uri.toString());
                            uploadUserDetails();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, R.string.error_uploading_img_retry, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Profile pic upload failed: " + task.getException());
                        }
                    }
                });
    }

    /**
     * Uploads user details to Firestore.
     */
    private void uploadUserDetails() {
        Log.d(TAG, "Uploading user details");
        mFirebaseFirestore.collection("users")
                .document(mUser.getUid())
                .set(mUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                            editor.putString("uid", mUser.getUid());
                            editor.putString("profilepic", mUser.getProfilepic());
                            editor.putString("fullname", mUser.getFullname());
                            editor.commit();

                            Log.d(TAG, "User details uploaded successfully");
                            Toast.makeText(RegistrationActivity.this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                            finish();
                        } else {
                            Log.d(TAG, "User details upload failed: " + task.getException());
                            Toast.makeText(RegistrationActivity.this, R.string.registration_failed_retry, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
    private void editProfile() {
        String tmp=mFullNameEditText.getText()+"";
        tmp=tmp.trim();
        if(!mFullName.equals(tmp)&&tmp.length()>0)
        userRef.update("fullname",tmp);
           // profileMap.put("fullname",mFullNameEditText.getText()+"");

            if(!imageUri.equals(mProfilePicUrl)){
                path = "profilepicuser/" + mUid ;
                final StorageReference riversRef = mStorageReference.child(path);

                UploadTask uploadTask = riversRef.putFile(imageUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            mProgressDialog.dismiss();                        //throw task.getException();
                            Toast.makeText(RegistrationActivity.this, "Error uploading photo,checck internet connection", Toast.LENGTH_SHORT).show();
                        }


                        return riversRef.getDownloadUrl();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mProgressDialog.dismiss();
                        userRef.update("profilepic",uri+"");
                //profileMap.put("profilepic",uri+"");


                    }
                });


            }
            else {
                mProgressDialog.dismiss();
            }

    }
    */
}
