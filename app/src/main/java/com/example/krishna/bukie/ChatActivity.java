package com.example.krishna.bukie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
/*import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;*/

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="helloo" ;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 21;
    private static final int RESULT_PICK_CONTACT = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 5;
    private static final int PICK_IMAGE_MULTIPLE = 14;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<MessageItem> messageItemList;
    private  FirebaseHelper fh;
    private Context context;
    private EmojiconEditText chatbox;
    private int count;
    private String fullname, ppic,tmpuser,identity,username,userfullname,sendtouid;
    private TextView username2,status;
    private MyChats myChats;
    private View camera,attach,send,rootview,keyboard,sendbtn,camerabtn,online;
   // EmojiPopup emojiPopup;
    private boolean emojikeyboard=true;
    private final Handler handler = new Handler();
    private boolean isNetworkLocation, isGPSLocation;
    private boolean togglesend=false;
    private String msg,date,name,phone;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath,latitude,longitude,locality;
    private ImageView mImageView,emoji;
    private List<String> imagepaths=new ArrayList<>();
    private List<String> imagefilenamelist=new ArrayList<>();
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private Geopoint geopoint;
    private  String imageEncoded;
    private  List<String> imagesEncodedList;
    private List<Uri> pathsurilist=new ArrayList<>();
    private Point point;
    private PopupWindow popup=new PopupWindow();
    private EmojIconActions emojIcon;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_chat);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        username = sharedPreferences.getString("uid", null);
        userfullname=sharedPreferences.getString("fullname",null);
        Bundle bundle = getIntent().getExtras();
        String isMap = bundle.getString("isMap");

        myChats = bundle.getParcelable("mychats");
        identity = bundle.getString("identity");
        if (identity.compareTo("buyer") == 0) {
            tmpuser = myChats.getSellerid();
            ppic = myChats.getSellerpic();
            fullname = myChats.getSellerfullname();
            sendtouid=myChats.getSellerid();

        } else {
            tmpuser = myChats.getBuyerid();
            ppic = myChats.getBuyerpic();
            fullname = myChats.getBuyerfullname();
            sendtouid=myChats.getBuyerid();

        }
        firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(identity).setValue(userfullname);


        chatbox = findViewById(R.id.chatbox);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        TextView username2 = (TextView) findViewById(R.id.username);
        ImageView pp = (ImageView) findViewById(R.id.profile_pic);
        username2.setText(fullname);
        Glide.with(getApplicationContext()).load(ppic).into(pp);
        status=findViewById(R.id.status);
        online=findViewById(R.id.online);



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();
        firebaseDatabase.getReference().child("users").child(sendtouid).child("last_seen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    String h = dataSnapshot.getValue().toString();
                    if (h.equals("online")) {
                        status.setText("online");
                        online.setVisibility(View.VISIBLE);
                    } else {
                        status.setText("offline");
                        online.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        recyclerView = (RecyclerView) findViewById(R.id.reyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // recyclerView.getLayoutManager().set

        //recyclerView.setR
        messageItemList = new ArrayList<>();
        adapter = new MessageAdapter(messageItemList, context, new MessageItemClickListener() {
            @Override
            public void onSaveContact(View view, int position) {
                //Toast.makeText(context, "new", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, messageItemList.get(position).getContact().getPhoneno());
                intent.putExtra(ContactsContract.Intents.Insert.NAME, messageItemList.get(position).getContact().getName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra(ContactsContract.Intents.Insert.EMAIL, bean.getEmailID());
                getApplicationContext().startActivity(intent);

            }

            @Override
            public void onLocation(View view, int position) {
                Geopoint geoPoint = messageItemList.get(position).getGeopoint();
                //TODO:Dont fuckin delete this
                    /* String url="geo:"+geoPoint.getLatitude()+","+geoPoint.getLongitude();
                      //Log.e("geopoint",url);
                      Uri gmmIntentUri = Uri.parse(url);
                      // Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                      Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                      mapIntent.setPackage("com.google.android.apps.maps");
                      mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      getApplicationContext().startActivity(mapIntent);
                      /*String urlAddress = "http://maps.google.com/maps?q="+ geoPoint.getLatitude()  +"," + geoPoint.getLongitude() +"("+ geoPoint.getLocality() + ")&iwloc=A&hl=es";
                      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress));
                      startActivity(intent);*/
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + geoPoint.getLatitude() + ">,<" + geoPoint.getLongitude() + ">?q=<" + geoPoint.getLatitude() + ">,<" + geoPoint.getLongitude() + ">(" + geoPoint.getLocality() + ")"));
                startActivity(intent);

            }

            @Override
            public void onCameraImage(View view, int position) {

            }
        });
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        //recyclerView.getItemAnimator().setChangeDuration(0);
       // recyclerView.scrollToPosition(messageItemList.size() - 1);
        //camerabtn=findViewById(R.id.camerabtn);
        sendbtn = (View) findViewById(R.id.sendbtn);
        camera = findViewById(R.id.camera);
        attach = findViewById(R.id.attach);
        send = (View) findViewById(R.id.send);
        emoji = (ImageView)findViewById(R.id.emoji);
        rootview = findViewById(R.id.root);
        keyboard = findViewById(R.id.keyboard);
        keyboard.setOnClickListener(this);
       // emoji.setOnClickListener(this);
        //camerabtn.setOnClickListener(this);
        attach.setOnClickListener(this);
        sendbtn.setOnClickListener(this);
        //emojiPopup = EmojiPopup.Builder.fromRootView(rootview).build((EmojiEditText) chatbox);
        //Emoji
        emojIcon=new EmojIconActions(this,rootview,chatbox,emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.keyboard, R.drawable.emoji);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                recyclerView.scrollToPosition(messageItemList.size() - 1);
                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                //recyclerView.scrollToPosition(messageItemList.size() - 1);
                Log.e(TAG, "Keyboard closed");
            }
        });


        fh = new FirebaseHelper(myChats.getChatid(), myChats.getSellerid(), myChats.getBuyerid(), username, new IncomingMessageListener() {
            public void receiveIncomingMessage(final MessageItem ch, String id) {
                if (!ch.getUid().equals(username))
                {
                    FirebaseFirestore.getInstance()
                            .collection("allchats")
                            .document("chats")
                            .collection(myChats.getChatid())
                            .document(id)
                            .update("status","seen");
                    ch.setStatus("seen");
                    final String[] time = new String[1];
                    final String[] sender = new String[1];

                    Log.i("Chat_status",ch.getMessage_body()+" "+ch.getUid()+" "+ch.getStatus());
                    final DatabaseReference databaseReference=firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child("last_message");
                    databaseReference.child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        time[0] =dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    databaseReference.child("sender").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            sender[0] =dataSnapshot.getValue().toString();
                            if(sender[0].equals(ch.getUid())&&time[0].equals(ch.getTimestamp())){
                                databaseReference.child("status").setValue("seen");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


                messageItemList.add(ch);

                adapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(messageItemList.size() - 1);
            }

            public void updateMessageStatus(MessageItem ch)
            {
                for(int i = messageItemList.size()-1;i>=0;i--){
                    MessageItem ms = messageItemList.get(i);
                    if(ms.getTimestamp().equals(ch.getTimestamp()))
                    {
                        ms.setStatus("seen");
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

        });
        //fh.getPreviousTexts();
        fh.startListening();
        //TODO :this too
        /*recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //if(messageItemList!=null)
               // recyclerView.scrollToPosition(messageItemList.size() - 1);
                //Toast.makeText(ChatActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });*/
        if (isMap.compareTo("1") == 0) {
            geopoint = bundle.getParcelable("geopoint");

            sendMessage("location");
        }
        chatbox.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (before == 0 && s.length() > 0) {
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
                            togglesend = true;
                        }
                    });


                    anim.start();

                }
                if (before > 0 && s.length() == 0) {
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
                            togglesend = false;
                        }
                    });


                    anim.start();

                }

            }
        });
        chatbox.setImeOptions(EditorInfo.IME_ACTION_SEND);
        chatbox.setRawInputType(InputType.TYPE_CLASS_TEXT);



        chatbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    msg = chatbox.getText().toString().trim();
                    if (TextUtils.isEmpty(msg) == false) {
                        sendMessage("message");

                    }
                    handled = true;
                }
                return handled;
            }
        });


      /*  if (popup.isShowing()) {
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {



                }
            });
        }*/
        rootview.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    //@SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {
                        if(popup.isShowing()){
                            popup.dismiss();
                        }


                    }
                });
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatactivitymenu, menu);





        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                break;
        }
        return true;
    }

    private void shareGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
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
        pathsurilist=new ArrayList<>();
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
                case REQUEST_IMAGE_CAPTURE:

                    uploadImage(mCurrentPhotoPath);

                    break;
                case PICK_IMAGE_MULTIPLE:

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    imagesEncodedList = new ArrayList<String>();
                    if(data.getData()!=null){

                        Uri uri=data.getData();
                        pathsurilist.add(uri);

                        Cursor cursor = getContentResolver().query(uri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded  = cursor.getString(columnIndex);
                        cursor.close();
                        uploadImages(pathsurilist);

                    } else {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();

                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                pathsurilist.add(uri);

                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded  = cursor.getString(columnIndex);
                                imagesEncodedList.add(imageEncoded);
                                cursor.close();

                            }
                            uploadImages(pathsurilist);

                        }

                    }
                    break;
            }
        }

    }
    private void uploadImages(final List<Uri> photoPaths) {
        imagepaths=new ArrayList<>();
        for(Uri photo:photoPaths) {
            String path = "chatimages/" + myChats.getChatid() + "/" + UUID.randomUUID() + ".png";
            //Log.e("nigga",path);
            final StorageReference riversRef = storageReference.child(path);

            photo = ImageCompressor.compressFromUri(this, photo);
            UploadTask uploadTask = riversRef.putFile(photo);
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
                    imagepaths.add(uri + "");
                    //mImageView.setImageBitmap(mImageBitmap);
                    if(photoPaths.size()==imagepaths.size()&&photoPaths.size()!=1)
                    sendMessage("gallery");
                    if(photoPaths.size()==imagepaths.size()&&photoPaths.size()==1)
                        sendMessage("camera");


                }
            });
        }
    }

    private void uploadImage(final String mCurrentPhotoPath) {
       String path = "chatimages/" + myChats.getChatid()+"/"+ UUID.randomUUID() + ".png";
       //Log.e("nigga",path);
        final StorageReference riversRef = storageReference.child(path);
        imagepaths=new ArrayList<>();

        Uri imageToUpload  = Uri.parse(mCurrentPhotoPath);
        imageToUpload = ImageCompressor.compressFromUri(this, imageToUpload);
        UploadTask uploadTask = riversRef.putFile(imageToUpload);
        //UploadTask uploadTask = riversRef.putFile(Uri.parse(mCurrentPhotoPath));
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
        if(type.compareTo("gallery")==0){

            MessageItem m = new MessageItem(date, username, d.getTime() + "", type,imagepaths);
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
            //MessageItem m2 = new MessageItem(contact, date, uid, d.getTime() + "","contact");
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
                if(togglesend) {

                    msg = chatbox.getText().toString().trim();

                    if (!TextUtils.isEmpty(msg)) {
                        sendMessage("message");

                    }
                }
                if(!togglesend){
                   // Toast.makeText(context, "hkl", Toast.LENGTH_SHORT).show();
                    dispatchTakePictureIntent();

                }

                break;
            case R.id.attach:
                onWindowFocusChanged();
                showPopup( point,v);
                break;
            case R.id.emoji:
                /*emojiPopup.toggle(); // Toggles visibility of the Popup.
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
                    emojikeyboard=true;*/


                break;
            case R.id.keyboard:
                /*if(emojiPopup.isShowing()){
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

                }*/

                break;
            case R.id.popupcamera:
                popup.dismiss();
                dispatchTakePictureIntent();
                break;
            case R.id.popupcontact:
                popup.dismiss();
                shareContact();

                break;
            case R.id.popupgallery:
                popup.dismiss();
                shareGallery();
                break;
            case R.id.popuplocation:
                popup.dismiss();
                shareLocation();
                break;

            default:
                break;
        }
    }

    public void onWindowFocusChanged() {

        int[] location = new int[2];
        View attach = (View) findViewById(R.id.attach);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        attach.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        point = new Point();
        point.x = location[0];
        point.y = location[1];
    }



    private void showPopup(Point point, View v) {
        //popupstate=true;
        Resources r=getResources();
        int popupWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, r.getDisplayMetrics());
        int popupHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, r.getDisplayMetrics());
        Context context=getApplicationContext();
        LinearLayout viewGroup = (LinearLayout)findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_popup, viewGroup);
        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);


        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, r.getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
        int OFFSET_X = width;
        int OFFSET_Y = 1500;
        OFFSET_Y=point.y-height;


        //Toast.makeText(context, ""+point.y+"kl"+height, Toast.LENGTH_SHORT).show();
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.showAtLocation(v, Gravity.TOP|Gravity.LEFT, OFFSET_X,OFFSET_Y);
        View popupcamera,popupgallery,popupcontact,popuplocation;
        popupcamera=layout.findViewById(R.id.popupcamera);
        popupcontact=layout.findViewById(R.id.popupcontact);
        popupgallery=layout.findViewById(R.id.popupgallery);
        popuplocation=layout.findViewById(R.id.popuplocation);
        if(togglesend==false)
           layout.findViewById(R.id.popupcameraview).setVisibility(View.GONE);
        popupcamera.setOnClickListener(this);
        popupcontact.setOnClickListener(this);
        popupgallery.setOnClickListener(this);
        popuplocation.setOnClickListener(this);


    }
    @Override
    public void onBackPressed()
    {
        if(popup.isShowing())
            popup.dismiss();
        else
        super.onBackPressed();
    }
    @Override
    public boolean onSupportNavigateUp(){

       finish();
        return true;
    }

    @Override
    protected void onStart() {

        firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(username).setValue(true);

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(username).setValue(false);

        fh.stopListening();
        super.onDestroy();
    }
   /* @Override
    protected void onPause() {

        firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(username).setValue(false);

        fh.stopListening();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(username).setValue(true);
        super.onPostResume();
    }*/

}


