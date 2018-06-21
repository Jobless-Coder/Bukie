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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PostnewadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PHOTO = 21;
    private int PICK_IMAGE_MULTIPLE = 1;
    private  String imageEncoded;
    private  List<String> imagesEncodedList;
    private EditText title,category,price;
    private View chooseimg,postad;
    //private ArrayList<Uri> mArrayUri;
    private FirebaseStorage firebaseStorage;
    private String path, coverurl="";
    private  StorageReference storageReference;
    private List<String> downloadurl;
    private List<String> imagesPathList;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private String mtitle,mcategory,mprice,mdate,madid,musername;
    private CollectionReference bookadscollection;
    private LinearLayout linearLayout;
    private HashSet<Uri> hset;
    private Uri coveruri;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postnewad);
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        musername=sharedPreferences.getString("username",null);
        progressDialog=new ProgressDialog(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

       linearLayout=findViewById(R.id.imgscroll);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater inflater = getLayoutInflater();;
        View rowView = inflater.inflate(R.layout.postnewad_bookimageview, linearLayout,false);
        rowView.findViewById(R.id.deleteimg).setVisibility(View.GONE);
        rowView.findViewById(R.id.bookpic).setVisibility(View.GONE);
        rowView.findViewById(R.id.addimg).setVisibility(View.VISIBLE);
        linearLayout.addView(rowView);



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



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //mArrayUri = new ArrayList<Uri>();
        hset = new HashSet<Uri>();


            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri uri=data.getData();
                    hset.add(uri);
                    LayoutInflater inflater = getLayoutInflater();;
                    View rowView = inflater.inflate(R.layout.postnewad_bookimageview, linearLayout,false);

                    ImageView v2=rowView.findViewById(R.id.bookpic);
                    v2.setImageURI(uri);
                    v2.setTag(uri.toString());
                    linearLayout.addView(rowView);
                    //mArrayUri.add(mImageUri);
                    //Toast.makeText(this, ""+mArrayUri.size(), Toast.LENGTH_SHORT).show();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            hset.add(uri);
                            //mArrayUri.add(uri);
                            LayoutInflater inflater = getLayoutInflater();;
                            View rowView = inflater.inflate(R.layout.postnewad_bookimageview, linearLayout,false);

                            ImageView v2=rowView.findViewById(R.id.bookpic);
                            v2.setImageURI(uri);
                            v2.setTag(uri.toString());
                            //v2.setImageResource(R.drawable.bookpic);


                            linearLayout.addView(rowView);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }

                    }
                }
            }
            else if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK
                    && null != data){
                //Toast.makeText(this, "kkll", Toast.LENGTH_SHORT).show();

                    try {
                        final Uri imageUri = data.getData();
                        coveruri=imageUri;

                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        this.findViewById(R.id.addcoverimage).setVisibility(View.GONE);
                        this.findViewById(R.id.coverpicrl).setVisibility(View.VISIBLE);
                        ImageView imageView=this.findViewById(R.id.coverpic);
                        //imageView.setVisibility(View.VISIBLE);
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





    public void addImageView(View v){Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

    }


    public void deleteImageView(View v){



        ViewGroup parent = ((ViewGroup) v.getParent().getParent());
        ImageView v22=parent.findViewById(R.id.bookpic);
        String path=v22.getTag().toString();
        Uri myUri = Uri.parse(path);
        hset.remove(myUri);
        //Toast.makeText(this, ""+myUri, Toast.LENGTH_SHORT).show();

        linearLayout.removeView(parent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.chooseimg:
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

                break;*/

            case R.id.fabpostad:
                //Toast.makeText(this, "Enter all fields to proceedkll", Toast.LENGTH_SHORT).show();
                downloadurl=new ArrayList<String>();

                mtitle=title.getText().toString();
                mcategory=category.getText().toString();
                mprice=price.getText().toString();
                Date d = new Date();

                SimpleDateFormat ft =
                        new SimpleDateFormat ("dd MMMM yyyy");
                mdate=ft.format(d);



                if(mprice.isEmpty()||mtitle.isEmpty()||mcategory.isEmpty()/*||mArrayUri!=null*/)
                    Toast.makeText(this, "Enter all fields to proceed", Toast.LENGTH_SHORT).show();
                else {
                    //String date, String booktitle, String price, String bookcategory, List<Uri> listbookpics
                    uploadFile(hset);



                }






                break;

                default:
                    break;
        }

    }

    private void uploadFile(final HashSet<Uri> hset) {
        //Toast.makeText(this, "lll"+hset, Toast.LENGTH_SHORT).show();
        progressDialog.setMessage("Posting ad ...");
        progressDialog.show();
        hset.add(coveruri);
        Iterator iterator = hset.iterator();

        // check values
        while (iterator.hasNext()) {
            Uri r= (Uri) iterator.next();
            //Toast.makeText(this, ""+r, Toast.LENGTH_SHORT).show();
       // for (Uri r : mArrayUri) {


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
                })/*.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                           Uri uri2 = task.getResult();

                        } else {

                        }
                    }
                }).*/.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                            downloadurl.add(uri + "");
                            Toast.makeText(PostnewadActivity.this, "images added ", Toast.LENGTH_SHORT).show();
                        /*}
                        else {
                            Toast.makeText(PostnewadActivity.this, ""+uri, Toast.LENGTH_SHORT).show();
                            coverurl = uri + "";
                        }*/
                        //Toast.makeText(PostnewadActivity.this, ""+uri, Toast.LENGTH_SHORT).show();
                        if(downloadurl.size()==hset.size())
                       {
                            //Log.e("hello","error");
                           madid=musername+"%"+UUID.randomUUID();
                            BookAds bookAds=new BookAds(mdate,mtitle,mprice,mcategory,musername,madid,downloadurl);
                           // firebaseFirestore.collection("bookads").document(madid).set(bookAds).addOnSuccessListener(onSu)
                           firebaseFirestore.collection("bookads").document(madid).set(bookAds)
                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           progressDialog.dismiss();


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
                           // bookadscollection.add(bookAds);
                            //Toast.makeText(getApplicationContext(), "ad posted", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }

        }


    }

    public void addcoverimage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PHOTO);


    }

    public void deleteCoverImageView(View view) {
        coveruri=null;
        ImageView imageView=this.findViewById(R.id.coverpic);
        imageView.setImageResource(0);
        this.findViewById(R.id.coverpicrl).setVisibility(View.GONE);
        this.findViewById(R.id.addcoverimage).setVisibility(View.VISIBLE);
    }
}
