package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.Reference;
import java.sql.Ref;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MessengerActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    ImageView sendbtn, profilepic;
    EditText messageTxt;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView userTalkingTo;
    String sellerId, loggedinusername, imageId, chatId, iChatId, boktitel;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser user;
    Context context = this;
    ListView listView;
    String[] adapterids;
    Date[] adapterDate;
    String[] userids;
    int idcounter = 0;
    int size;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        messageTxt = findViewById(R.id.etMessage);
        listView = findViewById(R.id.listViewMessages);
        sendbtn = findViewById(R.id.sendBtn);
        userTalkingTo = findViewById(R.id.txtUserTalkingTo);
        profilepic = findViewById(R.id.ivUserTalkingTo);

        final Intent intent = getIntent();
        sellerId = intent.getStringExtra("userid");
        //Sätter texten i toolbaren till den personen man chattar med.
        if(intent.getStringExtra("userTalkingTo") != null) {
            userTalkingTo.setText(intent.getStringExtra("userTalkingTo"));
        }
        else {
            userTalkingTo.setText(intent.getStringExtra("sellerName"));
        }
        user = mAuth.getCurrentUser();

        Intent intentchatId = getIntent();
        iChatId = intentchatId.getStringExtra("chatId");
        boktitel = intentchatId.getStringExtra("boktitel");

        displayChatMessage();

        //Om iChatId är null betyder det att användaren vill skapa en ny chat med en annan användare.
        //Createchat() körs då och en ny chat skapas.
        if (iChatId == null) {
            createChat();
            chatId = sellerId;
        }
        //Annars hämtas en chat som redan är skapad mellan två användare.
        else {
            chatId = iChatId;
            showMessages(iChatId);
        }

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatId);
            }
        });

        //Sätter profilbilden på användaren man pratar med.
        if(intent.getStringExtra("userTalkingTo") != null) {
            setImage(intent.getStringExtra("userTalkingToId"));
        } else{
            setImage(intent.getStringExtra("userid"));
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        Intent intent = new Intent(getApplicationContext(), MyMessagesActivity.class);
        startActivity(intent);
    }

    //Metod som kollar om ett nytt meddelande skickats. Om det är fallet så uppdaterar den listan så att
    //det nya meddelandet skickas.
    public void displayChatMessage() {
        try {
            //Uppdatera meddelandelistan när ett nytt meddelande lagts till i databasen.
            final DocumentReference docRef = rootRef.collection("Chat").document(chatId).collection("Messages").document("latest");
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    showMessages(chatId);
                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    //Metod som hämtar profilbilden på användaren man pratar med.
    public void setImage(String userId){
        final DocumentReference docRef = rootRef.collection("Users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    imageId = doc.getString("imageId");

                    //Hämta profilbild.
                    StorageReference storageRef = storage.getReferenceFromUrl(imageId);

                    final long ONE_MEGABYTE = 1024 * 1024;

                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            profilepic.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }
        });
    }

    //Skapa en chatt till en annons mellan användaren som lagt upp annonsen och den som är inloggad. Körs enbart när man går in på en annons som är till salu och trycker på skicka meddelande.
    public void createChat() {
        DocumentReference loggedInRef = rootRef.collection("Users").document(user.getUid().toString());
        loggedInRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    loggedinusername = documentSnapshot.getString("name") + " " + documentSnapshot.getString("surname");
                    String user1 = user.getUid().toString();
                    String user2 = sellerId;
                    Intent intent = getIntent();

                    CollectionReference userRef = rootRef.collection("Chat");

                    Map<String, Object> mapOne = new HashMap<>();
                    mapOne.put("User1", user1);
                    mapOne.put("User2", user2);
                    mapOne.put("User1Name", intent.getStringExtra("sellerName"));
                    mapOne.put("User2Name", loggedinusername);
                    mapOne.put("BokTitel", intent.getStringExtra("boktitel"));

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
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
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
                                if (doc.getString("User1").equals(user.getUid().toString()) && doc.getString("User2").equals(sellerId) && boktitel.equals(doc.getString("BokTitel"))) {
                                    String chatIdholder = doc.getId().toString();
                                    chatId = chatIdholder;
                                    showMessages(chatId);
                                    Toast.makeText(MessengerActivity.this, chatId, Toast.LENGTH_SHORT).show();
                                }
                                else if (doc.getString("User1").equals(sellerId) && doc.getString("User2").equals(user.getUid().toString()) && boktitel.equals(doc.getString("BokTitel"))) {
                                    String chatIdholder = doc.getId().toString();
                                    chatId = chatIdholder;
                                    showMessages(chatId);
                                    Toast.makeText(MessengerActivity.this, chatId, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //Skickar ett meddelande till en användare och sparar det i databasen.
    public void sendMessage(final String chatId) {
        DocumentReference userRef = rootRef.collection("Users").document(user.getUid().toString());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                String name = doc.getString("name") + " " + doc.getString("surname");
                String message = messageTxt.getText().toString();

                TimeZone timeZone = TimeZone.getTimeZone("Europe/Stockholm");
                TimeZone.setDefault(timeZone);
                Date date = new Date();

                Map<String, Object> mapOne = new HashMap<>();
                mapOne.put("Message", message);
                mapOne.put("UserId", user.getUid().toString());
                mapOne.put("Name", name);
                mapOne.put("Date", date);

                Map<String, Object> mapTwo = new HashMap<>();
                mapTwo.put("Message", message);
                mapTwo.put("UserId", user.getUid().toString());
                mapTwo.put("Name", name);
                mapTwo.put("Date", date);
                mapTwo.put("NotiSent", "Not Sent");

                DocumentReference chatRefLatest = rootRef.collection("Chat").document(chatId).collection("Messages").document("latest");
                CollectionReference chatRef = rootRef.collection("Chat").document(chatId).collection("Messages");

                chatRefLatest.set(mapTwo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //showMessages(chatId);
                                displayChatMessage();
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

    //Hämtar och visar alla meddelande tillhörande en chat i meddelandelistan.
    public void showMessages(String chatId) {
        idcounter = 0;
        CollectionReference chatRef = rootRef.collection("Chat").document(chatId).collection("Messages");
        Query query = chatRef.orderBy("Date", Query.Direction.ASCENDING);
        query.get()
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
                            adapterDate = new Date[size];

                            int counter = 0;

                            List<DocumentSnapshot> messagesLista = task.getResult().getDocuments();

                            //Loopa igenom alla meddelande för att separera bort "latest" då det inte ska visas.
                            for (int i = 0; i < task.getResult().size(); i++) {
                                DocumentSnapshot doc = messagesLista.get(i);
                                if (!doc.getId().equals("latest")) {
                                    userids[counter] = doc.getString("UserId");
                                    messages[counter] = doc.getString("Message");
                                    adapterids[counter] = doc.getString("Name");
                                    adapterDate[counter] = doc.getDate("Date");
                                    counter++;
                                }
                            }

                            MessageAdapter adapter = new MessageAdapter(context, messages, adapterids, adapterDate, userids);
                            listView.setAdapter(adapter);
                            //Ser till att listvyn inte åker tillbaka längst upp efter att ett meddelande har skickats.
                            listView.setSelection(adapter.getCount() - 1);
                            messageTxt.setText("");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }
}