package com.example.krishna.bukie;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
private CollectionReference collectionReference;
    ListenerRegistration listenerRegistration;


    public FirebaseHelper(String ad, String sel, String buy, String usernameofuser, IncomingMessageListener listener)
    {

        adID = ad;
        seller = sel;
        buyer = buy;
        username = usernameofuser;
        this.listener = listener;
        isListening = false;
        createRefID();
    }

    private void createRefID() {
        refID=adID;

    }


    public ArrayList<MessageItem> getPreviousTexts()
    {
        //Log.d("MyApp","I am here");
        final ArrayList<MessageItem> chats = new ArrayList<>();
       /* FirebaseDatabase database = FirebaseDatabase.getInstance();

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
        });*/
        firebaseFirestore.collection("allchats").document("chats").collection(refID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MessageItem item= document.toObject(MessageItem.class);
                                chats.add(item);
                                Log.i("ghjkl",item.getMessage_body());
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                           // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return chats;
    }

    public void sendMessage(MessageItem message)//add to recyclerview then send
    {

        firebaseFirestore.collection("allchats").document("chats").collection(refID)
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        //documentReference.getId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void startListening() {
        if (isListening) return;
      Query query=firebaseFirestore.collection("allchats").document("chats").collection(refID).orderBy("timestamp");
      listenerRegistration=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Log.w(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            MessageItem chat=dc.getDocument().toObject(MessageItem.class);
                            //Log.i("MessageChati",chat.getMessage_body());
                            listener.receiveIncomingMessage(chat, dc.getDocument().getId());
                            // Log.d(TAG, "New city: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            MessageItem ch = dc.getDocument().toObject(MessageItem.class);
                            listener.updateMessageStatus(ch);

                            // Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            //Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                            break;
                    }

                }
            }
        });

        isListening = true;
    }

    public void stopListening()
    {
        if(!isListening) return;

        listenerRegistration.remove();

       isListening = false;
    }
}
