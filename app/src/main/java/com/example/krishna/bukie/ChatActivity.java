package com.example.krishna.bukie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="helloo" ;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 21;
    private static final int RESULT_PICK_CONTACT = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 5;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<MessageItem> messageItemList;
    private  FirebaseHelper fh;
    private Context context;
    private EditText chatbox;
    private int count;
    private String fullname, ppic,tmpuser,identity,username;
    private TextView username2;
    private MyChats myChats;
    private View camera,attach,send,emoji,rootview,keyboard,sendbtn,camerabtn;
    EmojiPopup emojiPopup;
    private boolean emojikeyboard=true;
    private final Handler handler = new Handler();
    private boolean isNetworkLocation, isGPSLocation;
    private boolean togglesend=false;
    private String msg,date,name,phone;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath,latitude,longitude,locality;
    private ImageView mImageView;
    private List<String> imagepaths=new ArrayList<>();
    private List<String> imagefilenamelist=new ArrayList<>();
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private Geopoint geopoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_chat);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        username=sharedPreferences.getString("username",null);
        Bundle bundle = getIntent().getExtras();
        String isMap=bundle.getString("isMap");

        myChats = bundle.getParcelable("mychats");
        identity=bundle.getString("identity");
        if(identity.compareTo("buyer")==0){
            tmpuser=myChats.getSeller();
            ppic=myChats.getSellerpic();
            fullname=myChats.getSellerfullname();
        }

        else{
            tmpuser=myChats.getBuyer();
            ppic=myChats.getBuyerpic();
            fullname=myChats.getBuyerfullname();

        }

      //  imagefilenamelist=new ArrayList<>();
       // Toast.makeText(getApplicationContext(), "buyer"+identity, Toast.LENGTH_SHORT).show();

        chatbox=(EditText)findViewById(R.id.chatbox);

       //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        TextView username2=(TextView)findViewById(R.id.username);
        ImageView pp=(ImageView)findViewById(R.id.profile_pic);
        username2.setText(fullname);
        Glide.with(getApplicationContext()).load(ppic).into(pp);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=getApplicationContext();


        recyclerView=(RecyclerView)findViewById(R.id.reyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageItemList=new ArrayList<>();
        //camerabtn=findViewById(R.id.camerabtn);
        sendbtn=(View)findViewById(R.id.sendbtn);
        camera=findViewById(R.id.camera);
        attach=findViewById(R.id.attach);
        send=(View)findViewById(R.id.send);
        emoji=findViewById(R.id.emoji);
        rootview=findViewById(R.id.root);
        keyboard=findViewById(R.id.keyboard);
        keyboard.setOnClickListener(this);
        emoji.setOnClickListener(this);
        //camerabtn.setOnClickListener(this);
        attach.setOnClickListener(this);
        sendbtn.setOnClickListener(this);
        emojiPopup = EmojiPopup.Builder.fromRootView(rootview).build((EmojiEditText) chatbox);



        fh = new FirebaseHelper(myChats.getChatid(), myChats.getSeller(), myChats.getBuyer(), username, new IncomingMessageListener(){
                 public void receiveIncomingMessage(MessageItem ch)
          {
              messageItemList.add(ch);
              adapter=new MessageAdapter(messageItemList,context);
              recyclerView.setAdapter(adapter);
              recyclerView.scrollToPosition(messageItemList.size()-1);
          }

              });
        //fh.getPreviousTexts();
        fh.startListening();
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                //if(messageItemList!=null)
                recyclerView.scrollToPosition(messageItemList.size()-1);
                //Toast.makeText(ChatActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });
        if(isMap.compareTo("1")==0) {
            geopoint=bundle.getParcelable("geopoint");
            /*latitude = bundle.getString("latitude");
            longitude = bundle.getString("longitude");
            locality=bundle.getString("locality");
            Log.i("latitude",latitude);
            Log.i("latitude",longitude);
            Log.i("latitude",locality);*/
            sendMessage("location");
        }
        chatbox.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(before ==0 && s.length()>0){
                    int cx = camera.getWidth() / 2;
                    int cy = camera.getHeight() / 2;
                    float initialRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(camera, cx, cy, initialRadius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            camera.setVisibility(View.INVISIBLE);
                            send.setVisibility(View.VISIBLE);
                            togglesend=true;
                        }
                    });


                    anim.start();

                }
                if(before >0 && s.length()==0){
                    int cx = send.getWidth() / 2;
                    int cy = send.getHeight() / 2;
                    float initialRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(send, cx, cy, initialRadius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            send.setVisibility(View.INVISIBLE);
                            camera.setVisibility(View.VISIBLE);
                            togglesend=false;
                        }
                    });


                    anim.start();

                }

            }
        });
        chatbox.setImeOptions(EditorInfo.IME_ACTION_SEND);
        chatbox.setRawInputType(InputType.TYPE_CLASS_TEXT);

        chatbox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();

                chatbox.getWindowVisibleDisplayFrame(r);

                int heightDiff = chatbox.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    if(emojikeyboard==true&&keyboard.getVisibility()==View.VISIBLE)
                    {
                        //Toast.makeText(context, "ghk", Toast.LENGTH_SHORT).show();
                        //emojiPopup.dismiss();
                        int cx2 = keyboard.getWidth() / 2;
                        int cy2 = keyboard.getHeight() / 2;
                        float initialRadius2 = (float) Math.hypot(cx2, cy2);
                        Animator anim2 = ViewAnimationUtils.createCircularReveal(keyboard, cx2, cy2, initialRadius2, 0);
                        anim2.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                keyboard.setVisibility(View.INVISIBLE);
                                emoji.setVisibility(View.VISIBLE);
                            }
                        });


                        anim2.start();
                        emojikeyboard=true;
                    }

                    //enter your code here
                }else{
                    //enter code for hid
                }
            }
        });

        chatbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    msg = chatbox.getText().toString().trim();
                    if (TextUtils.isEmpty(msg) == false) {
                        sendMessage("message");
                        /*Date d = new Date();

                        SimpleDateFormat ft =
                                new SimpleDateFormat("hh:mm a");
                        date = ft.format(d);
                        MessageItem m = new MessageItem(msg, date, username, d.getTime() + "","message");
                        fh.sendMessage(m);

                        chatbox.setText("");*/
                    }
                    handled = true;
                }
                return handled;
            }
        });




    }

    public void onDestroy() {
        super.onDestroy();
        fh.stopListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatactivitymenu, menu);


        MenuItem searchItem = menu.findItem(R.id.share_location);
        searchItem = menu.findItem(R.id.delete_chat);
        searchItem = menu.findItem(R.id.block_user);

        SearchView searchView =
                (SearchView) searchItem.getActionView();



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_location:
                shareLocation();
                /*Intent intent=new Intent(ChatActivity.this,MapActivity.class);
                startActivity(intent);*/
               /* LocationManager mListener = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(mListener != null){
                    isGPSLocation = mListener.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    isNetworkLocation = mListener.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    Log.e("gps, network", String.valueOf(isGPSLocation + "," + isNetworkLocation));
                }
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        if(isGPSLocation){
                            Intent intent = new Intent(ChatActivity.this, MapActivity.class);
                            intent.putExtra("provider", LocationManager.GPS_PROVIDER);
                            intent.putExtra("mychats", myChats);
                            intent.putExtra("identity", identity);
                            startActivity(intent);
                            finish();
                        }else if(isNetworkLocation){
                            Intent intent = new Intent(ChatActivity.this, MapActivity.class);
                            intent.putExtra("provider", LocationManager.NETWORK_PROVIDER);
                            intent.putExtra("mychats", myChats);
                            intent.putExtra("identity", identity);
                            startActivity(intent);
                            finish();
                        }else{
                            //Device location is not set
                            PermissionUtils.LocationSettingDialog.newInstance().show(getSupportFragmentManager(), "Setting");
                        }
                    }
                }, 1500);*/
                break;
            case R.id.contact:
                shareContact();

                break;

            default:
                break;
        }
        return true;
    }

    private void shareContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    private void shareLocation() {
        LocationManager mListener = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(mListener != null){
            isGPSLocation = mListener.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkLocation = mListener.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.e("gps, network", String.valueOf(isGPSLocation + "," + isNetworkLocation));
        }
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                if(isGPSLocation){
                    Intent intent = new Intent(ChatActivity.this, MapActivity.class);
                    intent.putExtra("provider", LocationManager.GPS_PROVIDER);
                    intent.putExtra("mychats", myChats);
                    intent.putExtra("identity", identity);
                    startActivity(intent);
                    finish();
                }else if(isNetworkLocation){
                    Intent intent = new Intent(ChatActivity.this, MapActivity.class);
                    intent.putExtra("provider", LocationManager.NETWORK_PROVIDER);
                    intent.putExtra("mychats", myChats);
                    intent.putExtra("identity", identity);
                    startActivity(intent);
                    finish();
                }else{
                    //Device location is not set
                    PermissionUtils.LocationSettingDialog.newInstance().show(getSupportFragmentManager(), "Setting");
                }
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                   /* Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mImageView.setImageBitmap(imageBitmap);*/
                    uploadImage(mCurrentPhotoPath);

                    break;
            }
        }

    }

    private void uploadImage(final String mCurrentPhotoPath) {
       String path = "chatimages/" + myChats.getChatid()+"/"+imagefilenamelist.get(0) + ".png";
       Log.e("nigga",path);
        final StorageReference riversRef = storageReference.child(path);

        UploadTask uploadTask = riversRef.putFile(Uri.parse(mCurrentPhotoPath));
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                   // progressDialog.dismiss();                        //throw task.getException();
                    Toast.makeText(ChatActivity.this, "Error uploading photo,checck internet connection", Toast.LENGTH_SHORT).show();
                }


                return riversRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // mImageBitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                imagepaths.add(uri+"");
                //mImageView.setImageBitmap(mImageBitmap);
                sendMessage("camera");



            }
        });
    }

    private void sendMessage(String type) {
        //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        //imagepaths.add(mCurrentPhotoPath);
        Date d = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm a");
        date = ft.format(d);
        if(type.compareTo("camera")==0){

            MessageItem m = new MessageItem(date, username, d.getTime() + "", type,imagepaths);
            fh.sendMessage(m);
        }
        if(type.compareTo("message")==0){
            MessageItem m = new MessageItem(msg, date, username, d.getTime() + "","message");
            fh.sendMessage(m);
            chatbox.setText("");
        }
        if(type.compareTo("location")==0){
           //Geopoint geopoint=new Geopoint(latitude,longitude);
            MessageItem m=new MessageItem(date,username,d.getTime()+"",geopoint,type);
            fh.sendMessage(m);
        }

    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int photouti=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            String photouri=cursor.getString(photouti);
            Toast.makeText(context, ""+photouri, Toast.LENGTH_SHORT).show();
            Contact contact=new Contact(name,phoneNo);
            Date d = new Date();

            SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm a");
            date = ft.format(d);
            //MessageItem m2 = new MessageItem(contact, date, username, d.getTime() + "","contact");
            MessageItem m=new MessageItem(date,username,d.getTime() + "",contact,"contact");
            fh.sendMessage(m);

            // Set the value to the textviews


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatchTakePictureIntent() {

        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() +".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = username+timeStamp;
        imagefilenamelist.add(imageFileName);
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:"+image.getAbsolutePath();
        Log.e("location",mCurrentPhotoPath);
        return image;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendbtn:
                if(togglesend==true) {

                    msg = chatbox.getText().toString().trim();

                    if (TextUtils.isEmpty(msg) == false) {
                        sendMessage("message");
                        /*Date d = new Date();

                        SimpleDateFormat ft =
                                new SimpleDateFormat("hh:mm a");
                        date = ft.format(d);
                        MessageItem m = new MessageItem(msg, date, username, d.getTime() + "","message");
                        fh.sendMessage(m);

                        chatbox.setText("");*/
                    }
                }
                if(togglesend==false){
                   // Toast.makeText(context, "hkl", Toast.LENGTH_SHORT).show();
                    dispatchTakePictureIntent();

                }

                break;
            case R.id.attach:
                break;
            case R.id.emoji:
                emojiPopup.toggle(); // Toggles visibility of the Popup.
                    int cx = emoji.getWidth() / 2;
                    int cy = emoji.getHeight() / 2;
                    float initialRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(emoji, cx, cy, initialRadius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            emoji.setVisibility(View.INVISIBLE);
                            keyboard.setVisibility(View.VISIBLE);
                        }
                    });


                    anim.start();
                    emojikeyboard=true;


                break;
            case R.id.keyboard:
                if(emojiPopup.isShowing()){
                    emojiPopup.dismiss();
                    int cx2 = keyboard.getWidth() / 2;
                    int cy2 = keyboard.getHeight() / 2;
                    float initialRadius2 = (float) Math.hypot(cx2, cy2);
                    Animator anim2 = ViewAnimationUtils.createCircularReveal(keyboard, cx2, cy2, initialRadius2, 0);
                    anim2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            keyboard.setVisibility(View.INVISIBLE);
                            emoji.setVisibility(View.VISIBLE);
                        }
                    });


                    anim2.start();
                    emojikeyboard=true;

                }

                break;

            default:
                break;
        }
    }
}

