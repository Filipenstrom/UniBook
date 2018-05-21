package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MyMessagesActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser user;
    ImageView profilepic;
    String chatId, userTalkingToId, imageId, name;
    ListView listView;
    Context context = this;
    String[] id;
    String[] names;
    String[] userid;
    String[] boktitel;
    int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        user = mAuth.getCurrentUser();
        listView = findViewById(R.id.listViewMyMessages);
        profilepic = findViewById(R.id.ivMessages);

        getChat();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView chatId = (TextView) view.findViewById(R.id.txtChatId);
                TextView userTalkingTo = view.findViewById(R.id.txtUserTalkingTo);
                Intent data = new Intent(MyMessagesActivity.this, MessengerActivity.class);
                data.putExtra("chatId", chatId.getText().toString());
                data.putExtra("userTalkingTo", userTalkingTo.getText().toString());
                data.putExtra("userTalkingToId", userTalkingToId);
                startActivity(data);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
        startActivity(intent);
    }

    //Metod som hämtar alla chattar som den inloggade användaren är med i.
    public void getChat() {
        CollectionReference chatRef = rootRef.collection("Chat");
        chatRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<DocumentSnapshot> chatLista = task.getResult().getDocuments();
                            int size = task.getResult().size();
                            int arraysize = 0;

                            for (int i = 0;i<size;i++){
                                DocumentSnapshot doc = chatLista.get(i);
                                if(doc.getString("User1").equals(user.getUid().toString()) || doc.getString("User2").equals(user.getUid().toString())) {
                                    arraysize++;
                                }
                            }

                            id = new String[arraysize];
                            userid = new String[arraysize];
                            names = new String[arraysize];
                            boktitel = new String[arraysize];

                            for(int i = 0;i < size;i++){
                                DocumentSnapshot doc = chatLista.get(i);
                                if(doc.getString("User1").equals(user.getUid().toString()) || doc.getString("User2").equals(user.getUid().toString())) {
                                    String chatIdholder = doc.getId().toString();
                                    chatId = chatIdholder;
                                    id[counter] = doc.getId().toString();
                                    boktitel[counter] = doc.getString("BokTitel");

                                    if(!doc.getString("User1").equals(user.getUid())){
                                        names[counter] = doc.getString("User2Name");
                                        userTalkingToId = doc.getString("User1");
                                        userid[counter] = userTalkingToId;
                                        counter++;
                                    }
                                    else {
                                        names[counter] = doc.getString("User1Name");
                                        userTalkingToId = doc.getString("User2");
                                        userid[counter] = userTalkingToId;
                                        counter++;
                                    }
                                    MyMessagesAdapter adapter = new MyMessagesAdapter(context, names, id, userid, boktitel, null);
                                    listView.setAdapter(adapter);
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //Metod som sätter bilden på den användare man pratar med.
    public void setImage(String[] userId){
        for(int i = 0; i<userId.length;i++) {
            final DocumentReference docRef = rootRef.collection("Users").document(userId[i]);
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
    }
}