package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.Reference;
import java.sql.Array;
import java.sql.Ref;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessengerActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    Button sendbtn;
    EditText messageTxt;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String sellerId;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseUser user;
    String chatId;
    Context context = this;
    ListView listView;
    String iChatId;
    String[] adaptermessages;
    String[] adapterids;
    int idcounter = 0;
    int size;
    String[] userids;
    int usernamecounter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        sendbtn = findViewById(R.id.sendBtn);
        messageTxt = findViewById(R.id.etMessage);
        listView = findViewById(R.id.listViewMessages);

        Intent intent = getIntent();
        sellerId = intent.getStringExtra("userid");
        user = mAuth.getCurrentUser();

        Intent intentchatId = getIntent();
        iChatId = intentchatId.getStringExtra("chatId");

        displayChatMessage();
        //createChat();
        if (iChatId == null) {
            getChat();
        } else {
            chatId = iChatId;
            showMessages(iChatId);
        }

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatId);
            }
        });

        final DocumentReference docRef = rootRef.collection("Chat").document(chatId).collection("Messages").document("latest");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    showMessages(chatId);
                    return;
                }
                showMessages(chatId);
            }
        });
    }

    public void displayChatMessage() {

    }

    public void createChat() {
        CollectionReference userRef = rootRef.collection("Chat");

        String user1 = user.getUid().toString();
        String user2 = sellerId;


        Map<String, Object> mapOne = new HashMap<>();
        mapOne.put("User1", user1);
        mapOne.put("User2", user2);

        userRef.document()
                .set(mapOne)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void getChat() {
        CollectionReference chatRef = rootRef.collection("Chat");
        chatRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<DocumentSnapshot> chatLista = task.getResult().getDocuments();
                            int size = task.getResult().size();

                            for (int i = 0; i < size; i++) {
                                DocumentSnapshot doc = chatLista.get(i);
                                if (doc.getString("User1").equals(user.getUid().toString()) && doc.getString("User2").equals(sellerId)) {
                                    String chatIdholder = doc.getId().toString();
                                    chatId = chatIdholder;
                                    showMessages(chatId);
                                    Toast.makeText(MessengerActivity.this, chatId,
                                            Toast.LENGTH_SHORT).show();
                                } else if (doc.getString("User1").equals(sellerId) && doc.getString("User2").equals(user.getUid().toString())) {
                                    String chatIdholder = doc.getId().toString();
                                    chatId = chatIdholder;
                                    showMessages(chatId);
                                    Toast.makeText(MessengerActivity.this, chatId,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            //CourseAdapter adapter = new CourseAdapter(context, courseName, ids, courseCode);
                            //ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, courseName);
                            //listView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void sendMessage(final String chatId) {
        DocumentReference chatRef = rootRef.collection("Chat").document(chatId).collection("Messages").document("latest");
        CollectionReference chatRefOld = rootRef.collection("Chat").document(chatId).collection("Messages");
        //CollectionReference userRef = rootRef.collection("User").document();

        String message = messageTxt.getText().toString();

        Map<String, Object> mapOne = new HashMap<>();
        mapOne.put("Message", message);
        mapOne.put("UserId", user.getUid().toString());


        chatRef.set(mapOne)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //showMessages(chatId);
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        chatRefOld.document()
                .set(mapOne)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //showMessages(chatId);
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void showMessages(String chatId) {
        idcounter = 0;
        CollectionReference chatRef = rootRef.collection("Chat").document(chatId).collection("Messages");
        chatRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            size = task.getResult().size();

                            String[] messages = new String[size];
                            String[] ids = new String[size];
                            userids = new String[size];
                            adaptermessages = new String[size - 1];
                            adapterids = new String[size - 1];

                            int counter = 0;

                            List<DocumentSnapshot> messagesLista = task.getResult().getDocuments();

                            for (int i = 0; i < size; i++) {
                                DocumentSnapshot doc = messagesLista.get(i);
                                if (!doc.getId().equals("latest")) {
                                    messages[i] = doc.getString("Message");

                                    ids[i] = doc.getId().toString();
                                }
                            }
                            for (int i = 0; i < messages.length; i++) {
                                if (messages[i] != null) {
                                    DocumentSnapshot doc = messagesLista.get(i);
                                    adaptermessages[counter] = messages[i];
                                    userids[i] = doc.getString("UserId");
                                    counter++;
                                    if(i == messages.length-1){
                                        getUsername(userids);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }


    public void getUsernameOld(String[] userid) {
        for (int i = 0; i < userid.length; i++) {
            final DocumentReference docRef = rootRef.collection("Users").document(userid[i]);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (idcounter < adapterids.length) {
                            adapterids[idcounter] = document.getString("name") + " " + document.getString("surname");
                        }
                        if (idcounter == size - 1) {
                            MessageAdapter adapter = new MessageAdapter(context, adaptermessages, adapterids);
                            listView.setAdapter(adapter);
                        }
                        idcounter++;

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }

            });
        }
    }

    public void getUsernameOold(final String[] userids) {
        usernamecounter = 0;
        for (int i = 0; i < userids.length; i++) {
            CollectionReference chatRef = rootRef.collection("Users");
            chatRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> usersLista = task.getResult().getDocuments();

                                for (int i = 0; i < userids.length; i++) {
                                    for(int i2 = 0;i2<usersLista.size();i2++) {
                                        DocumentSnapshot doc = usersLista.get(i2);
                                        String id = userids[usernamecounter];
                                        if(doc.getId().toString().equals(id)) {
                                            adapterids[usernamecounter] = doc.getString("name") + " " + doc.getString("surname");
                                            if (usernamecounter == userids.length) {
                                                break;
                                            }
                                            usernamecounter++;
                                        }
                                    }
                                }

                                    MessageAdapter adapter = new MessageAdapter(context, adaptermessages, adapterids);
                                    listView.setAdapter(adapter);

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }

                    });
        }
    }

    public void getUsername(final String[] userids) {
        usernamecounter = 0;
        CollectionReference favouritesRef = rootRef.collection("Users");
        for (int i = 0; i < userids.length; i++) {
            Query query = favouritesRef.whereEqualTo("userId", userids[i]);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                        for (DocumentSnapshot document : list) {
                                adapterids[usernamecounter] = document.getString("name") + " " + document.getString("surname");
                                if (usernamecounter == userids.length) {
                                    break;
                                }
                                usernamecounter++;
                        }
                        MessageAdapter adapter = new MessageAdapter(context, adaptermessages, adapterids);
                        listView.setAdapter(adapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

}
