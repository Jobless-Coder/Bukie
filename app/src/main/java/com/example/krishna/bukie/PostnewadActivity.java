package com.example.krishna.bukie;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PostnewadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PHOTO = 21;
    private static final int EXTRA_PHOTO = 32;
    private static final int PROFILE_IMAGE = 11;
    private int PICK_IMAGE_MULTIPLE = 1;
    private  String imageEncoded;
    private  List<String> imagesEncodedList;
    private EditText title,category,price,author,publisher,desc;
    private View chooseimg,postad;
    //private ArrayList<Uri> mArrayUri;
    private FirebaseStorage firebaseStorage;
    private String path, coverurl;
    private  StorageReference storageReference;
    private List<String> downloadurl;
    private List<String> imagesPathList;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private String mtitle,mcategory,mprice,mdate,madid, muid,mprofilepic,mfullname,mpublisher,mauthor,mdesc;
    private CollectionReference bookadscollection;
    private LinearLayout linearLayout;
    private HashSet<Uri> hset;
    private Uri coveruri;
    private int random = 0;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog progressDialog;
    private DisplayMetrics metrics;
    private ArrayList<SquareImageView> extraImages;
    private FlexboxLayout flex;
    private Uri coverImageUri;
    private boolean coverimage=false;
    private int count;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postnewad);
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        muid =sharedPreferences.getString("uid",null);
        mprofilepic=sharedPreferences.getString("profilepic",null);
        mfullname=sharedPreferences.getString("fullname",null);
        progressDialog=new ProgressDialog(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);




        count=0;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        setSizeOfSquareImageViews();
        extraImages = new ArrayList<>();
        flex = findViewById(R.id.flexlayout);
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
       storageReference=firebaseStorage.getReference();
      // documentReference=firebaseFirestore.document("bookads/"+UUID.randomUUID());
        bookadscollection = firebaseFirestore.collection("bookads");

        //chooseimg=findViewById(R.id.chooseimg);
        floatingActionButton=findViewById(R.id.fabpostad);
        title=findViewById(R.id.title);
        category=findViewById(R.id.category);
        price=findViewById(R.id.price);
        publisher=findViewById(R.id.publisher);
        author=findViewById(R.id.author);
        desc=findViewById(R.id.desc);
        //chooseimg.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nsv);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
            }
        });
        desc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    floatingActionButton.hide();
                   // Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
                } else {
                    floatingActionButton.show();
                    //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    public void addToMyAds(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(muid).child("myads").setValue(madid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                    }
                });

    }

    private void setSizeOfSquareImageViews() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


             if (requestCode == EXTRA_PHOTO && resultCode == RESULT_OK
                && null != data) {
                sendToUCrop(data.getData(), UCrop.REQUEST_CROP);
            }
            else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null)
            {
                try {
                    Uri imageUri = UCrop.getOutput(data);
                    imageUri = ImageCompressor.compressFromUri(this,imageUri);
                    SquareImageView sq = new SquareImageView(this, imageUri, metrics);
                    extraImages.add(sq);
                    flex.addView(sq,1);
                    refreshFlex();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "null....", Toast.LENGTH_SHORT).show();
                }
            }

            else if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK
                    && null != data){
                sendToUCrop(data.getData(), PROFILE_IMAGE);
            }

            else if(requestCode == PROFILE_IMAGE && resultCode == RESULT_OK && data != null)
            {
                try {
                    coverImageUri = UCrop.getOutput(data);
                    coverImageUri = ImageCompressor.compressFromUri(this,coverImageUri);
                    Glide.with(this).load(coverImageUri).into((ImageView)findViewById(R.id.coverimage));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "null....", Toast.LENGTH_SHORT).show();
                }

            }
                else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void sendToUCrop(Uri uri, int tag)
    {
        try
        {
            String destinationFileName = "temp_"+(new Date().getTime())+".png";
            UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)))
                    .withAspectRatio(1,1);
            uCrop.start(this, tag);

        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error cropping the image!", Toast.LENGTH_SHORT).show();
            Log.e("UCrop error", e.getLocalizedMessage());
        }
    }

    public void refreshFlex(){
        if(flex.getChildCount()>5)
        {
            findViewById(R.id.adnewimagebutton).setVisibility(View.GONE);
        }
        else
        {
            findViewById(R.id.adnewimagebutton).setVisibility(View.VISIBLE);
        }
    }
    public void addImageView(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        trimCache(this);
        finish();
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        Log.e("file found",dir.getName());
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fabpostad:

                downloadurl=new ArrayList<String>();
                coverurl=null;
                mtitle=title.getText().toString();
                mcategory=category.getText().toString();
                mprice="₹ "+price.getText().toString();
                mauthor=author.getText().toString();
                mdesc=desc.getText().toString();
                mpublisher=publisher.getText().toString();

                Date d = new Date();

                SimpleDateFormat ft =
                        new SimpleDateFormat ("dd MMMM yyyy");
                mdate=ft.format(d);



                if(mprice.isEmpty()||mtitle.isEmpty()||mcategory.isEmpty()/*||mArrayUri!=null*/)
                    Toast.makeText(this, "Enter all fields to proceed", Toast.LENGTH_SHORT).show();
                else {
                    progressDialog.setMessage("Posting ad ...");
                    progressDialog.show();
                    uploadImage(coverImageUri,true);
                    for(SquareImageView squareImageView:extraImages){
                        uploadImage(squareImageView.getImageLink(), false);
                        count++;
                    }




                }
                break;

                default: break;
        }

    }

    private void uploadImage(Uri r, final boolean b) {



            if (r != null) {
                path = "adimages/" + UUID.randomUUID() + ".png";
                final StorageReference riversRef = storageReference.child(path);

                UploadTask uploadTask = riversRef.putFile(r);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }


                        return riversRef.getDownloadUrl();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                            if (b==false)
                            downloadurl.add(uri + "");
                            else
                                coverurl=uri+"";
                        if(downloadurl.size()==extraImages.size()&&coverurl!=null)

                       {
                            postAd();


                        }

                    }
                });

            }


    }
    public void  postAd(){
        title.setText("");
        price.setText("");
        publisher.setText("");
        author.setText("");
        desc.setText("");
        category.setText("");

        madid= muid +"%"+UUID.randomUUID();
        BookAds bookAds=new BookAds(mdate,mtitle,mprice,mcategory,coverurl,mpublisher,mauthor,mdesc, muid,madid,mprofilepic,mfullname,downloadurl);
       // BookAds bookAds=new BookAds(mdate,mtitle,mprice,mcategory,muid,madid,mprofilepic,mfullname,downloadurl);
        // firebaseFirestore.collection("bookads").document(madid).set(bookAds).addOnSuccessListener(onSu)
        firebaseFirestore.collection("bookads").document(madid).set(bookAds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        addToMyAds();

                        Toast.makeText(PostnewadActivity.this, "Ad posted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(PostnewadActivity.this, "Error posting ad,pls try again later", Toast.LENGTH_SHORT).show();

                    }
                });

    }



    public void addNewSquareImage(View view) {
        coverimage=false;
        //TODO: Add imagepicker cropper and all necessary shit here
        selectImage(EXTRA_PHOTO);
    }


    /*public void goToImageActivity(View view) {
        startActivity(new Intent(this, ImageCompressorTestActivity.class));
    }*/


    private void selectImage(final int tag) {//1
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select an image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                //TODO: add permissions
                //boolean result= Utility.checkPermission(this);

                if (items[item].equals("Take Photo")) {
                    //userChoosenTask ="Take Photo";
                    //if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    //userChoosenTask ="Choose from Library";
                    //if(result)
                        galleryIntent(tag);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void galleryIntent(int tag) {//2

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), tag);
    }

    private void cameraIntent() {

    }

    public void selectCoverImage(View view) {
        coverimage=true;
        selectImage(SELECT_PHOTO);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;
    }
}
