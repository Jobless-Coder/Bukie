package com.example.krishna.bukie;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class FirebaseHelper {
private String adID;
private String seller;
private String buyer;
private String username;
private String refID;//this id is the node key for this entire chat section
private IncomingMessageListener listener;
private boolean isListening;
private DatabaseReference ddref;
private ChildEventListener childEventListener;

/*
* When you enter one-to-one chat page, first create an object of FirebaseHelper(...)
* Parameter description
*   ad - refers to a unique ID assigned to the ad in question
*   sel - username of the seller for the particular product in question
*   buy - username of the buyer for the product
*   listener - incoming message listener interface object (described below in details)
*
*   After you create the object, call the getPreviousMessages() to retrieve a list of previous texts in chronological order
*   this function returns an ArrayList<Chat> type object, make sure you follow its data fields
*   PS: this function shouldn't be called multiple times, just once you enter the page (preferable on the onCreate() method of your activity/fragment)
*
*   To continuously listen to new texts throughout the active time of the current page call startListening()
*   this method listens for new texts and calls receiveIncomingMessage(Chat) method on the attached listener
*   make sure you call the stopListening() during exiting from the activity, this is mandatory!!
*
*   This is a demo usage for the FirebaseHelper class.
*
*   public void onCreate()
*   {
*       ...
*       FirebaseHelper fh = new FirebaseHelper(adId, usernameseller, usernamebuyer, usernameofuser, new IncomingMessageListener(){
*
*           public void receiveIncomingMessage(MessageItem ch)
*           {
*               ...
*               //add this chatText to any scrollview/listview as the text to display
*           }
*
*       });
*       fh.startListening();
*       //after executing this line, the above method receiveIncomingMessage(MessageItem) gets called for any new text from other user
*       ...
*
*   }
*
*   //its similarly important to stop listening to incoming texts, insert the code to onDestroy() and onPause()
*
*   public void onDestroy()  // or onPause()
*   {
*       ...
*       fh.stopListening();
*       ...
*   }
*
*   //to send texts written by this user, call sendMessage(String message)
*
*   ..
*   fh.sendMessage(text);
*   ..
*
*   PS: consider reading the documentation for MessageItem class once
*
*/
    public FirebaseHelper(String ad, String sel, String buy, String usernameofuser, IncomingMessageListener listener)
    {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        adID = ad;
        seller = sel;
        buyer = buy;
        username = usernameofuser;
        this.listener = listener;
        isListening = false;
        createRefID();
    }

    private void createRefID() {
        seller = seller.toLowerCase();
        buyer = buyer.toLowerCase();
        String concat = "";
        if(seller.compareTo(buyer)<0)
            concat = seller+"%"+buyer;
        else
            concat = buyer+"%"+seller;
        concat = adID+"%"+concat;
        refID = concat;
    }


    public ArrayList<MessageItem> getPreviousTexts()
    {
        final ArrayList<MessageItem> chats = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference dref = database.getReference().child("chats/"+refID);
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    chats.add(data.getValue(MessageItem.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return chats;
    }

    public void sendMessage(MessageItem message)//add to recyclerview then send
    {
        //MessageItem chat = new MessageItem(message, new Date().toString(),username);
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("chats/"+refID);
        dref.push().setValue(message);
    }

    public void startListening()
    {
        if(isListening) return;
        ddref = FirebaseDatabase.getInstance().getReference().child("chats/"+refID);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageItem chat = dataSnapshot.getValue(MessageItem.class);
                listener.receiveIncomingMessage(chat);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ddref.addChildEventListener(childEventListener);
        isListening = true;
    }

    public void stopListening()
    {
        if(!isListening) return;
        ddref.removeEventListener(childEventListener);
        isListening = false;
    }
}
