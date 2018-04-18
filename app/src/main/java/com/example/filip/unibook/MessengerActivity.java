package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
    String[] adapterids;
    String[] adapterDate;
    int idcounter = 0;
    int size;
    String[] userids;



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

        if (iChatId == null) {
            createChat();
            chatId = sellerId;
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

        //Uppdatera meddelandelistan när ett nytt meddelande lagts till i databasen.
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

    //Skapa en chatt till en annons. Körs enbart när man går in på en annons som är till salu och trycker på skicka meddelande.
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
                        getChat();
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

    //Hämtar chatten efter att den skapats. Körs enbart första gången när man går in på en annons som är till salu och trycker på skicka meddelande.
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

    //Skickar ett meddelande till en användare och sparar det i databasen.
    public void sendMessage(final String chatId) {
        DocumentReference userRef = rootRef.collection("Users").document(user.getUid().toString());
        //CollectionReference userRef = rootRef.collection("User").document();

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              DocumentSnapshot doc = task.getResult();
              String name = doc.getString("name") + " " + doc.getString("surname");
                String message = messageTxt.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
                dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
                Date date = new Date();
                System.out.println(dateFormat.format(date));

                Map<String, Object> mapOne = new HashMap<>();
                mapOne.put("Message", message);
                mapOne.put("UserId", user.getUid().toString());
                mapOne.put("Name", name);
                mapOne.put("Date", "Skickat: " + dateFormat.format(date));

                DocumentReference chatRefLatest = rootRef.collection("Chat").document(chatId).collection("Messages").document("latest");
                CollectionReference chatRef = rootRef.collection("Chat").document(chatId).collection("Messages");

                chatRefLatest.set(mapOne)
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

                chatRef.document()
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
        });
    }

    //Visar alla meddelande i medelandelistan.
    public void showMessages(String chatId) {
        idcounter = 0;
        CollectionReference chatRef = rootRef.collection("Chat").document(chatId).collection("Messages");
        chatRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if(task.getResult().size() != 0) {
                                size = task.getResult().size() - 1;
                            }
                            else{
                                size = 0;
                            }

                            String[] messages = new String[size];
                            userids = new String[size];
                            adapterids = new String[size];
                            adapterDate = new String[size];

                            int counter = 0;

                            List<DocumentSnapshot> messagesLista = task.getResult().getDocuments();

                            //Loopa igenom alla meddelande för att separera bort "latest" då det inte ska visas.
                            for (int i = 0; i < task.getResult().size(); i++) {
                                DocumentSnapshot doc = messagesLista.get(i);
                                if (!doc.getId().equals("latest")) {
                                    messages[counter] = doc.getString("Message");
                                    adapterids[counter] = doc.getString("Name");
                                    adapterDate[counter] = doc.getString("Date");
                                    counter++;
                                }
                            }
                            MessageAdapter adapter = new MessageAdapter(context, messages, adapterids, adapterDate);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }
}
