package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MyMessagesActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String sellerId;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseUser user;
    String chatId;
    ListView listView;
    Context context = this;
    String name;
    String[] id;
    String[] names;
    int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        user = mAuth.getCurrentUser();
        listView = findViewById(R.id.listViewMyMessages);

        getChat();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView chatId = (TextView) view.findViewById(R.id.txtChatId);
                TextView userTalkingTo = view.findViewById(R.id.txtUserTalkingTo);
                //TextView txtCourseId = view.findViewById(R.id.txtViewCoursesId);
                    Intent data = new Intent(MyMessagesActivity.this, MessengerActivity.class);
                    data.putExtra("chatId", chatId.getText().toString());
                    data.putExtra("userTalkingTo", userTalkingTo.getText().toString());
                    startActivity(data);
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
                            id = new String[size];
                            names = new String[size];

                            for(int i = 0;i < size;i++){
                                DocumentSnapshot doc = chatLista.get(i);
                                if(doc.getString("User1").equals(user.getUid().toString()) || doc.getString("User2").equals(user.getUid().toString())) {
                                    String chatIdholder = doc.getId().toString();
                                    chatId = chatIdholder;
                                    id[i] = doc.getId().toString();
                                    if(!doc.getString("User1").equals(user.getUid())){
                                        counter = i;
                                        names[counter] = doc.getString("User2Name");
                                    }
                                    else {
                                        counter = i;
                                        names[counter] = doc.getString("User1Name");
                                    }

                                    MyMessagesAdapter adapter = new MyMessagesAdapter(context, names, id,null);
                                    listView.setAdapter(adapter);
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
