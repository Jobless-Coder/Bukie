package com.example.krishna.bukie;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

public class FirebaseHelper {
private String adID;
private String seller;
private String buyer;
private String username,receiver,userfullname,profilepic;
private String refID;//this id is the node key for this entire chat section
private IncomingMessageListener listener;
private boolean isListening;
private DatabaseReference ddref;
private ChildEventListener childEventListener;
private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
private CollectionReference collectionReference;
private ListenerRegistration listenerRegistration;
private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
private MyChatsStatus myChatsStatus;
private String identity;


    public FirebaseHelper(String ad, String sel, String buy, String usernameofuser, String userfullname,IncomingMessageListener listener,String profilepic,String identity,MyChatsStatus myChatsStatus)
    {

        adID = ad;
        seller = sel;
        buyer = buy;
        username = usernameofuser;
        this.listener = listener;
        isListening = false;
        this.userfullname=userfullname;
        createRefID();
        this.profilepic=profilepic;
        this.myChatsStatus=myChatsStatus;
        this.identity=identity;

    }

    private void createRefID() {
        refID=adID;
        if(seller.equals(username))
            receiver=buyer;
            else
                receiver=seller;



    }


    public ArrayList<MessageItem> getPreviousTexts()
    {
        final ArrayList<MessageItem> chats = new ArrayList<>();

        firebaseFirestore.collection("allchats").document("chats").collection(refID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MessageItem item= document.toObject(MessageItem.class);
                                chats.add(item);

                            }
                        }
                    }
                });

        return chats;
    }

    public void sendMessage(final MessageItem message)//add to recyclerview then send
    {
        final LastMessage[] last_message = new LastMessage[1];

        firebaseFirestore.collection("allchats").document("chats").collection(refID)
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                       // Date d=new Date();
                        Long d=Long.parseLong(message.getTimestamp());
                        final DatabaseReference databaseReference=firebaseDatabase.getReference().child("chat_status").child(refID);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                MyChatsStatus myChatsStatus = dataSnapshot.getValue(MyChatsStatus.class);
                                if(myChatsStatus.getBuyerid_isactive().equals(myChatsStatus.getBuyerid()+"_false")) {
                                    databaseReference.child("buyerid_isactive").setValue(myChatsStatus.getBuyerid() + "_true");
                                    databaseReference.child("sellerid_isactive").setValue(myChatsStatus.getSellerid() + "_true");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        final Long[] time = new Long[1];
                        databaseReference.child("last_message").child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!=null) {
                                    time[0] =Long.parseLong(dataSnapshot.getValue().toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if(time[0]==null||time[0]!=null&&d> time[0]){

                            databaseReference.child("last_message").child("time").setValue(d);
                            databaseReference.child("last_message").child("sender").setValue(message.getUid());
                            databaseReference.child("last_message").child("type").setValue(message.getType());
                            databaseReference.child("last_message").child("status").setValue(message.getStatus());
                            databaseReference.child("last_message").child("message_body").setValue(message.getMessage_body());


                        }

                        databaseReference.child(receiver).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.getValue()==null||dataSnapshot.getValue().toString().equals("false")){
                                    ChatNotifs chatNotifs=new ChatNotifs(message.getMessage_body(),receiver,userfullname,profilepic,myChatsStatus,identity);
                                    firebaseDatabase.getReference().child("notifications").push().setValue(chatNotifs);
                                    firebaseDatabase.getReference().child("fake_notifications").push().setValue(chatNotifs);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            MessageItem chat=dc.getDocument().toObject(MessageItem.class);

                            listener.receiveIncomingMessage(chat, dc.getDocument().getId());
                            break;
                        case MODIFIED:
                            MessageItem ch = dc.getDocument().toObject(MessageItem.class);
                            listener.updateMessageStatus(ch);

                            break;
                        case REMOVED:
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
