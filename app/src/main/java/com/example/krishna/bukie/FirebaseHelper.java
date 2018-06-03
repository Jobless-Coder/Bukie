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

public class FirebaseHelper {
private String adID;
private String seller;
private String buyer;
private String refID;//this id is the node key for this entire chat section
private IncomingMessageListener listener;
private boolean isListening;
private DatabaseReference ddref;
private ChildEventListener childEventListener;

    public FirebaseHelper(String ad, String sel, String buy, IncomingMessageListener listener)
    {
        adID = ad;
        seller = sel;
        buyer = buy;
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


    public ArrayList<Chat> getPreviousTexts()
    {
        final ArrayList<Chat> chats = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference dref = database.getReference().child("chats/"+refID);
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    chats.add(data.getValue(Chat.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return chats;
    }

    public void pushMessage(String message)
    {
        Chat chat = new Chat(message);
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("chats/"+refID);
        dref.push().setValue(chat);
    }

    public void startListening()
    {
        if(isListening) return;
        ddref = FirebaseDatabase.getInstance().getReference().child("chats/"+refID);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
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
