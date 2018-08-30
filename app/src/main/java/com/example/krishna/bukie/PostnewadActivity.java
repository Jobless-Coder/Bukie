package com.example.krishna.bukie;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.krishna.bukie.home.HomePageActivity;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class PostnewadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PHOTO = 21;
    private static final int EXTRA_PHOTO = 32;
    private static final int PROFILE_IMAGE = 11;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 101;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 102;
    private static final int SCANNER = 120;
    private static final int PICKTAGS = 216;
    private int PICK_IMAGE_MULTIPLE = 1;
    private EditText title,category,price,author,publisher,desc;
    private long mprice;
    private TextView toolbar_title;
    private FirebaseStorage firebaseStorage;
    private String path, coverurl;
    private  StorageReference storageReference;
    private List<String> downloadurl;
    private Uri photoURI;
    private FirebaseFirestore firebaseFirestore;
    private String mtitle,mcategory,madid, muid,mprofilepic,mfullname,mpublisher,mauthor,mdesc;
    private long mdate;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog progressDialog;
    private DisplayMetrics metrics;
    private ArrayList<SquareImageView> extraImages;
    private FlexboxLayout flex;
    private Uri coverImageUri;
    private String username;
    private DatabaseReference mDatabase;
    private BookAds bookAds;
    private int isHome;
    private Map<String, Object> bookadsmap;
    private DocumentReference bookref;
    private String isbn;
    private SquareImageAdapter adapter;
    private View tagpicker;
    private List<Tuple> chipList=new ArrayList<>();
    private RecyclerView recyclerView;
    private TagPickerAdapter tagPickerAdapter;
    private ChipAdapter chipAdapter;
    private List<String> tagList=new ArrayList<>();
    private FrameLayout frame;
    private ZXingScannerView scannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postnewad);;
        Bundle bundle= getIntent().getExtras();
        isHome=bundle.getInt("isHome");


        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        muid =sharedPreferences.getString("uid",null);
        mprofilepic=sharedPreferences.getString("profilepic",null);
        mfullname=sharedPreferences.getString("fullname",null);
        progressDialog=new ProgressDialog(this);
        madid=muid +"%"+UUID.randomUUID();
        final Toolbar toolbar = findViewById(R.id.toolbar);

        username = sharedPreferences.getString("uid", null);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.recyclerview);
        extraImages = new ArrayList<>();
        flex = findViewById(R.id.flexlayout);
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
       storageReference=firebaseStorage.getReference();

        floatingActionButton=findViewById(R.id.fabpostad);
        title=findViewById(R.id.title);
        category=findViewById(R.id.category);
        toolbar_title=findViewById(R.id.toolbar_title);
        price=findViewById(R.id.price);
        publisher=findViewById(R.id.publisher);
        author=findViewById(R.id.author);
        desc=findViewById(R.id.desc);
        tagpicker=findViewById(R.id.tagpicker);
        tagpicker.setOnClickListener(this);

        setSizeOfSquareImageViews();
        if(isHome==0) {
            bookAds = bundle.getParcelable("bookads");
            editMyAds(bookAds);
        }

        floatingActionButton.setOnClickListener(this);
        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nsv);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY)
                {
                    floatingActionButton.hide();
                }
                else
                    {
                    floatingActionButton.show();
                    }
            }
        });
        desc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    floatingActionButton.hide();

                } else {
                    floatingActionButton.show();
                }
            }
        });

    }

    private void addTags() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);

        recyclerView.setLayoutManager(layoutManager);
        chipAdapter=new ChipAdapter(this, chipList, new TagItemListener() {
            @Override
            public void onRemoveClick(String text, int position) {
                chipList.remove(new Tuple(text,position));
                chipAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(chipAdapter);


    }

    public void editMyAds(BookAds bookAds) {

        toolbar_title.setText("Edit ad");
        Glide.with(getApplicationContext()).load(bookAds.getCoverpic()).into((ImageView)findViewById(R.id.coverimage));

        title.setText(bookAds.getTitle());
        category.setText(bookAds.getCategory());
        author.setText(bookAds.getAuthor());
        publisher.setText(bookAds.getPublisher());
        price.setText(bookAds.getPrice()+"");
        desc.setText(bookAds.getDesc());
        for(String path:bookAds.getBookpicslist()) {
            Toast.makeText(this, ""+metrics.toString(), Toast.LENGTH_SHORT).show();
            adapter.addImage(Uri.parse(path));

            refreshFlex();
        }

    }
    public void editAndPostMyads(){
        refreshFlex();
        bookref = firebaseFirestore.collection("bookads").document(bookAds.getAdid());
        progressDialog.setMessage("Editing ad ...");
        progressDialog.show();
        Map<String, Object> bookadsmap = new HashMap<>();
        downloadurl = new ArrayList<String>();
        coverurl = null;
        mtitle = title.getText().toString();
        if(mtitle.equals(bookAds.getTitle())==false)
           bookadsmap.put("booktitle",mtitle);
        mcategory = category.getText().toString();
        if(mcategory.equals(bookAds.getCategory())==false)
            bookadsmap.put("bookcategory",mcategory);
        mprice =  Long.parseLong(price.getText().toString());
        if(mprice==(bookAds.getPrice()))
            bookadsmap.put("price",mprice);
        mauthor = author.getText().toString();
        if(mauthor.equals(bookAds.getAuthor())==false)
            bookadsmap.put("bookauthor",mauthor);
        mdesc = desc.getText().toString();
        if(mdesc.equals(bookAds.getDesc())==false)
            bookadsmap.put("bookdesc",mdesc);
        mpublisher = publisher.getText().toString();
        if(mpublisher.equals(bookAds.getPublisher())==false)
            bookadsmap.put("bookpublisher",mpublisher);

        Date d = new Date();

        //SimpleDateFormat ft = new SimpleDateFormat("dd MMMM yyyy");
        mdate = d.getTime();
        if(mdate!=(bookAds.getTimestamp()))
            bookadsmap.put("date",mdate);


        if (mprice>0 || mtitle.isEmpty() || mcategory.isEmpty()/*||mArrayUri!=null*/)
            Toast.makeText(this, "Enter all fields to proceed", Toast.LENGTH_SHORT).show();
        else {


            if(coverImageUri!=null&&coverImageUri.equals(bookAds.getCoverpic())==false)
            uploadImage(coverImageUri, true);
            else
                coverurl=bookAds.getCoverpic();
            for (int i = 0; i < flex.getChildCount() - 1; i++) {
                SquareImageView squareImageView = (SquareImageView) flex.getChildAt(i);
                if (squareImageView != null) {

                    for(String path:bookAds.getBookpicslist()){
                        if(path.compareTo(squareImageView.getImageLink()+"")!=0){
                            uploadImage(squareImageView.getImageLink(), false);
                        }
                        else {
                            downloadurl.add(path);
                        }
                    }

                }
                else
                    Toast.makeText(this, "Null image during upload!", Toast.LENGTH_SHORT).show();
            }

        }

        Toast.makeText(this, ""+extraImages.size()+" ", Toast.LENGTH_SHORT).show();

        if(downloadurl.size()==extraImages.size()&&coverurl!=null)

        {

                bookadsmap.put("bookpicslist",bookAds.getBookpicslist());
                bookref.update(bookadsmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.dismiss();
                        }
                    }
                });




        }


    }

    public void addToMyAds(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(muid).child("myads").child(madid).setValue(madid)
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
        adapter = new SquareImageAdapter(this, flex, metrics);
        flex.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                refreshFlex();
            }
        });

    }


    private void dispatchTakePictureIntent(int tag) {

        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                Log.e("picture camera","here inside createImage try");
                photoFile = createImageFile();
            } catch (Exception ex) {
                // Error occurred while creating the File
                Log.e("picture camera","here: "+ex.getLocalizedMessage());
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() +".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        tag);
            }
            else
            {
                Log.e("picture camera","photofile is null");
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = username+timeStamp;
        //imagefilenamelist.add(imageFileName);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:"+image.getAbsolutePath();
        //Log.e("location",mCurrentPhotoPath);
        return image;
    }
    private void getStoragePermissions()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Toast.makeText(context, "kll2", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);

            } else {
                // Toast.makeText(context, "kll", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }

        }

    }

    private boolean getCameraPermissions()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Toast.makeText(context, "kll2", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

            } else {
                // Toast.makeText(context, "kll", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }

            return false;
        }
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PICKTAGS && resultCode==RESULT_OK){
            Tuple chips=data.getParcelableExtra("chips");
            chipList=chips.getChipList();
            //Toast.makeText(this, "size "+chipList.size(), Toast.LENGTH_SHORT).show();
            addTags();
        }

        if(requestCode == SCANNER && resultCode == RESULT_OK)
        {
            isbn = data.getExtras().getString("isbn");
            ((TextView)findViewById(R.id.barcodetext)).setText("ISBN:"+isbn);
            return;
        }
             else if (requestCode == EXTRA_PHOTO && resultCode == RESULT_OK) {
                 if(data != null)
                sendToUCrop(data.getData(), UCrop.REQUEST_CROP);
                 else if(photoURI != null)
                     sendToUCrop(photoURI, UCrop.REQUEST_CROP);
            }
            else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && (data != null || photoURI != null))
            {
                Uri imageUri;
                if(data != null)
                    imageUri = UCrop.getOutput(data);
                else
                    imageUri = photoURI;
                try {
                    imageUri = ImageCompressor.compressFromUri(this,imageUri);
                    adapter.addImage(imageUri);
                    refreshFlex();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "null....", Toast.LENGTH_SHORT).show();
                }
                photoURI = null;
            }
            else if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK){
                if(data != null)
                    sendToUCrop(data.getData(), PROFILE_IMAGE);
                else
                    sendToUCrop(photoURI, PROFILE_IMAGE);
            }

            //experimental

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
                photoURI = null;

            }
            else if(requestCode == PROFILE_IMAGE && resultCode == RESULT_OK)
             {
                 Log.e("photos","Inside this block");
                 if(photoURI!=null)
                 {
                     Log.e("photos","photouri has something");
                     Toast.makeText(this, "photouri not null", Toast.LENGTH_SHORT).show();
                 }
                 else
                 {
                     Log.e("photos","photouri has nothing");
                 }
                 photoURI = null;
             }
                else {
                    Log.e("photoerror", "Request code: "+requestCode+" Result Code: "+resultCode);
                   // Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                    photoURI = null;
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
                if(isHome==1) {

                    downloadurl = new ArrayList<String>();
                    coverurl = null;
                    mtitle = title.getText().toString();
                    mcategory = category.getText().toString();
                    if(!price.getText().toString().equals(""))
                    mprice =  Long.parseLong(price.getText()+"");
                    mauthor = author.getText().toString();
                    mdesc = desc.getText().toString();
                    mpublisher = publisher.getText().toString();

                    Date d = new Date();


                    mdate = d.getTime();


                    if (coverImageUri==null)
                        Toast.makeText(this, "You should add a cover picture for your book", Toast.LENGTH_SHORT).show();
                    else if (mprice==0)
                        Toast.makeText(this, "Please add a required price for your book", Toast.LENGTH_SHORT).show();

                    else if (mtitle.isEmpty())
                        Toast.makeText(this, "Enter the book title to continue", Toast.LENGTH_SHORT).show();
                    else if (mcategory.isEmpty())
                        Toast.makeText(this, "Enter a category where this book belongs", Toast.LENGTH_SHORT).show();
                    else if (mpublisher.isEmpty())
                        Toast.makeText(this, "Enter the publisher for this book", Toast.LENGTH_SHORT).show();

                    else if (mdesc.isEmpty())
                        Toast.makeText(this, "Enter a short description for this book", Toast.LENGTH_SHORT).show();
                    ///*||mArrayUri!=null*/)
                        //Toast.makeText(this, "Enter all fields to proceed", Toast.LENGTH_SHORT).show();
                    else if (adapter.getImages().size()<2)
                    {
                        Toast.makeText(this, "Please add atleast 2 additional images", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog.setMessage("Posting ad ...");
                        progressDialog.show();
                        uploadImage(coverImageUri, true);

                        for(Uri link: adapter.getImages())
                        {
                            uploadImage(link, false);
                        }
                    }
                    if(chipList.size()<3){
                        Toast.makeText(this, "Enter atleast 3 tags", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        for (Tuple tuple:chipList){
                            tagList.add(tuple.getText());
                        }
                    }
                }
                else {
                editAndPostMyads();
                }
                break;
            case R.id.tagpicker:
                Intent intent=new Intent(this,TagPickerActivity.class);
                startActivityForResult(intent,PICKTAGS);
                break;

                default: break;
        }

    }

    private void uploadImage(Uri r, final boolean b) {

            if (r != null) {
                path = "adimages/" + madid+"/"+UUID.randomUUID() + ".png";
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
                        if(downloadurl.size()==adapter.getImages().size()&&coverurl!=null)

                       {
                           if(isHome==1)
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


        BookAds bookAds=new BookAds(mdate,mtitle,mprice,mcategory,coverurl,mpublisher,mauthor,mdesc, muid,madid,mprofilepic,mfullname,downloadurl, isbn,tagList,true,false);

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
        //TODO: Add imagepicker cropper and all necessary shit here
        selectImage(EXTRA_PHOTO);
    }




    private void selectImage(final int tag) {//1
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //builder.setTitle("Select an image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                //TODO: add permissions
                //boolean result= Utility.checkPermission(this);

                if (items[item].equals("Take Photo")) {
                    //userChoosenTask ="Take Photo";
                    //if(result)
                        cameraIntent(tag);
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

    private void cameraIntent(int tag) {
        //TODO: add camera permission
        getStoragePermissions();
        getCameraPermissions();
        dispatchTakePictureIntent(tag);
    }

    public void selectCoverImage(View view) {
        selectImage(SELECT_PHOTO);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void scanCode(View view) {

        if(!getCameraPermissions())
            return;
        startActivityForResult(new Intent(this, FullscreenScannerActivity.class),SCANNER);
    }


}
