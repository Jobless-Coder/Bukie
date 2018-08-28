package com.example.krishna.bukie;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.function.ToDoubleBiFunction;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconMultiAutoCompleteTextView;

import static java.security.AccessController.getContext;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="helloo" ;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 21;
    private static final int RESULT_PICK_CONTACT = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 5;
    private static final int PICK_IMAGE_MULTIPLE = 14;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE =111 ;
    private static final int SHARE_LOCATION = 199;
    private static final int ERROR_DIALOG = 97 ;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<MessageItem> messageItemList;
    private  FirebaseHelper fh;
    private Context context;
    private EmojiconMultiAutoCompleteTextView chatbox;
    private int count;
    private String fullname, ppic,tmpuser,identity,username,userfullname,sendtouid;
    private TextView username2,status;
    private View camera,attach,send,rootview,keyboard,sendbtn,camerabtn,back;
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
    private CardView online;
    private MyChatsStatus myChats;
    private DatabaseReference connectedRef;
    private ValueEventListener listener;
    private String contactnumber,contactname,contactpic,profilepicuser;
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
        profilepicuser=sharedPreferences.getString("profilepic",null);
        Bundle bundle = getIntent().getExtras();
        //String isMap = bundle.getString("isMap");
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
        back=findViewById(R.id.back);
        back.setOnClickListener(this);
        chatbox = findViewById(R.id.chatbox);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        TextView username2 = (TextView) findViewById(R.id.username);
        ImageView pp = (ImageView) findViewById(R.id.profile_pic);
        username2.setText(fullname);
        Glide.with(getApplicationContext()).load(ppic).into(pp);
        status=findViewById(R.id.status);
        online=findViewById(R.id.online);
        status.setText("offline");
        online.setCardBackgroundColor(getResources().getColor(R.color.grey));




        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();
        firebaseDatabase.getReference().child("users").child(sendtouid).child("last_seen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    String h = dataSnapshot.getValue().toString();
                    if (h.equals("online")) {
                        status.setText("online");
                        online.setCardBackgroundColor(getResources().getColor(R.color.green));
                    } else {
                        status.setText("offline");
                        online.setCardBackgroundColor(getResources().getColor(R.color.grey));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                status.setText("offline");
                online.setCardBackgroundColor(getResources().getColor(R.color.grey));

            }
        });



        recyclerView = (RecyclerView) findViewById(R.id.reyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageItemList = new ArrayList<>();
        adapter = new MessageAdapter(messageItemList, context, new MessageItemClickListener() {
            @Override
            public void onSaveContact(View view, int position) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, messageItemList.get(position).getContact().getPhoneno());
                intent.putExtra(ContactsContract.Intents.Insert.NAME, messageItemList.get(position).getContact().getName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }

            @Override
            public void onLocation(View view, int position) {
                Geopoint geoPoint = messageItemList.get(position).getGeopoint();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + geoPoint.getLatitude() + ">,<" + geoPoint.getLongitude() + ">?q=<" + geoPoint.getLatitude() + ">,<" + geoPoint.getLongitude() + ">(" + geoPoint.getLocality() + ")"));
                startActivity(intent);

            }

            @Override
            public void onCameraImage(View view, int position) {
                ArrayList<String> imagelist= (ArrayList<String>) messageItemList.get(position).getImageurl();
                Intent intent=new Intent(context,ImageViewActivity.class);
                intent.putExtra("position",0);
                intent.putStringArrayListExtra("url", (ArrayList<String>) imagelist);
                context.startActivity(intent/*,activityOptions.toBundle()*/);

            }

        });
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        Toast.makeText(context, ""+adapter.getLastMessagePosition(), Toast.LENGTH_SHORT).show();
        if(adapter.getLastMessagePosition()!=-1)
        recyclerView.scrollToPosition(adapter.getLastMessagePosition());
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
        //emoji keyboard
        //emojiPopup = EmojiPopup.Builder.fromRootView(rootview).build((EmojiEditText) chatbox);
        //Emoji
        /*
        emojIcon=new EmojIconActions(this,rootview,chatbox,emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.keyboard, R.drawable.emoji);*/
      /*  emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
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
        });*/


        fh = new FirebaseHelper(myChats.getChatid(), myChats.getSellerid(), myChats.getBuyerid(), username, userfullname,new IncomingMessageListener() {
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

                   // Log.i("Chat_status",ch.getMessage_body()+" "+ch.getUid()+" "+ch.getStatus());
                    final DatabaseReference databaseReference=firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child("last_message");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            LastMessage lastMessage= dataSnapshot.getValue(LastMessage.class);
                            time[0]=lastMessage.getTime().toString();
                            sender[0]=lastMessage.getSender();
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
                //recyclerView.scrollToPosition(messageItemList.size() - 1);
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

        },profilepicuser,identity,myChats);
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
        //TODO:

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
                    camera.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);
                    togglesend = true;
                }
                if (before > 0 && s.length() == 0) {
                    send.setVisibility(View.INVISIBLE);
                    camera.setVisibility(View.VISIBLE);
                    togglesend = false;
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
        MenuItem item = menu.findItem(R.id.mark_as_sold);
        if(identity.equals("buyer"))
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_chat:
                
                break;
            case R.id.block_user:
                break;
            case R.id.mark_as_sold:
                if(identity.equals("seller")) {//check whether it is already marked as sold
                    final android.support.v7.app.AlertDialog.Builder alert=new android.support.v7.app.AlertDialog.Builder(this);
                    alert.setMessage("Are you sure you want to mark this ad as sold? You're action cannot be undone.");
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    alert.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            FirebaseFirestore.getInstance().collection("bookads").document(myChats.getAdid()).update("issold",true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(context, "Your book was mrked as sold.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                    else {

                                        dialog.dismiss();
                                    }
                                }


                            });
                        }
                    });
                    android.support.v7.app.AlertDialog alert11 = alert.create();
                    alert11.show();

                }

                break;
            case R.id.gotoad:
                FirebaseFirestore.getInstance().collection("bookads").document(myChats.getAdid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            BookAds bookAds =task.getResult().toObject(BookAds.class);
                                    Intent intent = new Intent(context, DisplayAdActivity.class);
                            intent.putExtra("bookads", bookAds);
                            intent.putExtra("from", "chat");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });

                break;

            default:
                break;
        }
        return true;
    }
    private static final int MY_PERMISSIONS_REQUEST_CAMERA =999 ;
    private void getCameraPermissions()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }

        }

    }
    private void getStoragePermissions()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }

        }

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
       /* LocationManager mListener = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

                    startActivityForResult(intent,SHARE_LOCATION);

                }else if(isNetworkLocation){
                    Intent intent = new Intent(ChatActivity.this, MapActivity.class);
                    intent.putExtra("provider", LocationManager.NETWORK_PROVIDER);
                    startActivityForResult(intent,SHARE_LOCATION);



                }else{
                    //Device location is not set
                    PermissionUtils.LocationSettingDialog.newInstance().show(getSupportFragmentManager(), "Setting");
                }
            }
        }, 1000);*/
        if(isServicesOk()) {
            Intent intent = new Intent(ChatActivity.this, MapActivity2.class);
            startActivityForResult(intent, SHARE_LOCATION);
        }
    }
    public boolean isServicesOk(){
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ChatActivity.this);
        if(available== ConnectionResult.SUCCESS)
        {
            return true;

        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(ChatActivity.this,available,ERROR_DIALOG);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Error in play services,can't load map!", Toast.LENGTH_SHORT).show();
        }
        return false;
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

                    uploadImage(mCurrentPhotoPath,"camera");

                    break;
                case SHARE_LOCATION:
                    geopoint = data.getParcelableExtra("geopoint");

                    sendMessage("location");


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
                        uploadImages(pathsurilist,"gallery");

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
                            uploadImages(pathsurilist,"camera");

                        }

                    }
                    break;
            }
        }

    }
    private void uploadImages(final List<Uri> photoPaths, final String type) {
        imagepaths=new ArrayList<>();
        for(Uri photo:photoPaths) {
            String path = "chatimages/" + myChats.getChatid() + "/" + UUID.randomUUID() + ".png";
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
                    imagepaths.add(uri + "");
                    if(photoPaths.size()==imagepaths.size()&&photoPaths.size()!=1&&type.equals("gallery"))
                    sendMessage("gallery");
                    if(photoPaths.size()==imagepaths.size()&&photoPaths.size()==1&&type.equals("camera"))
                        sendMessage("camera");


                }
            });
        }
    }

    private void uploadImage(final String mCurrentPhotoPath, final String type) {
       String path = "chatimages/" + myChats.getChatid()+"/"+ UUID.randomUUID() + ".png";
        final StorageReference riversRef = storageReference.child(path);
        imagepaths=new ArrayList<>();
        Uri imageToUpload  = Uri.parse(mCurrentPhotoPath);
        imageToUpload = ImageCompressor.compressFromUri(this, imageToUpload);
        UploadTask uploadTask = riversRef.putFile(imageToUpload);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "Error uploading photo,checck internet connection", Toast.LENGTH_SHORT).show();
                }
                return riversRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imagepaths.add(uri+"");
                if(type.equals("camera"))
                sendMessage("camera");
                else if(type.equals("contact")) {
                    contactpic=uri+"";
                    sendMessage("contact");
                }
            }
        });
    }

    private void sendMessage(String type) {
        Date d = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm a");
        date = ft.format(d);
        if(type.compareTo("camera")==0){

            MessageItem m = new MessageItem(date, username, d.getTime() + "", type,imagepaths,"Sent a photo");
            fh.sendMessage(m);
        }
        else if(type.compareTo("message")==0){
            MessageItem m = new MessageItem(msg, date, username, d.getTime() + "","message");
            fh.sendMessage(m);
            chatbox.setText("");
        }
        else if(type.compareTo("location")==0){
           //Geopoint geopoint=new Geopoint(latitude,longitude);
            MessageItem m=new MessageItem(date,username,d.getTime()+"",geopoint,type,"Sent a location");
            fh.sendMessage(m);
        }
        else if(type.compareTo("gallery")==0){

            MessageItem m = new MessageItem(date, username, d.getTime() + "", type,imagepaths,"Sent photos");
            fh.sendMessage(m);
        }
        else if(type.equals("contact")){

            if(contactpic==null) {
                Contact contact = new Contact(contactname, contactnumber);
                MessageItem m = new MessageItem(date, username, d.getTime() + "", contact, "contact", "Sent a contact");
                fh.sendMessage(m);
            }
            else {
                Contact contact = new Contact(contactname, contactnumber,contactpic);
                MessageItem m = new MessageItem(date, username, d.getTime() + "", contact, "contact", "Sent a contact");
                fh.sendMessage(m);
            }


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
            contactnumber = cursor.getString(phoneIndex);
            contactname = cursor.getString(nameIndex);
            contactpic =cursor.getString(photouti);

           Toast.makeText(context, ""+contactpic, Toast.LENGTH_SHORT).show();
            if(contactpic!=null)
            uploadImage(contactpic,"contact");
            else
                sendMessage("contact");





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
                else {
                    getStoragePermissions();

                   //Toast.makeText(context, "hkl", Toast.LENGTH_SHORT).show();
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
            case R.id.back:
                onBackPressed();
                break;
            case R.id.popupcamera:
                popup.dismiss();
                //getCameraPermissions();
                getStoragePermissions();
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
        attach.getLocationOnScreen(location);
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
    protected void onResume() {
        fh.startListening();

        firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(username).setValue(true);
        DatabaseReference presenceRef = firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(username);
        final OnDisconnect onDisconnectRef = presenceRef.onDisconnect();
        //onDisconnectRef.cancel();
        presenceRef.onDisconnect().setValue("false");

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        listener=(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(username).setValue(true);

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        connectedRef.addValueEventListener(listener);

        super.onResume();
    }

    @Override
    protected void onStop() {
        firebaseDatabase.getReference().child("chat_status").child(myChats.getChatid()).child(username).setValue(false);
        connectedRef.removeEventListener(listener);

        fh.stopListening();
        super.onStop();
    }


}


