package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    TextView addedNotis, newNotis;
    Context context = this;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        addedNotis = findViewById(R.id.txtnotisText);
        newNotis = findViewById(R.id.etnotisAd);
        addedNotis.setText(" ");

        listNotifications();

        Button btnaddNotis =  findViewById(R.id.btnaddNotis);
        Button btnaddNotisCourse =  findViewById(R.id.btnAddNotisCourse);
        Button deleteBtn = findViewById(R.id.btnDeleteNotification);

        btnaddNotis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProgram();
            }
        });
        btnaddNotisCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCourse();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearNotifications();
            }
        });
    }

    //Kollar så att det programmet man vill ha notifikationer för faktiskt finns.
    public void validateProgram(){
        CollectionReference programRef = rootRef.collection("Program");

        programRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> lista = task.getResult().getDocuments();

                    for(int i = 0; i < lista.size(); i++){
                        DocumentSnapshot documentSnapshot = lista.get(i);
                        if(documentSnapshot.getString("Name").equals(newNotis.getText().toString()) && checkProgramAndCourse()){
                            saveNotis();
                            break;
                        }
                        else if(!checkProgramAndCourse()){
                            newNotis.setError("Du har redan anmält notifikationer för detta program.");
                        }
                        else if(i == lista.size()-1){
                            newNotis.setError("Det finns inget program med detta namn. Vänligen testa med ett annat namn.");
                        }
                    }
                }
            }
        });
    }

    //Kollar så att den kursen man vill ha notifikationer för faktiskt finns.
    public void validateCourse(){
        CollectionReference courseRef = rootRef.collection("Courses");

        courseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> lista = task.getResult().getDocuments();
                    for(int i = 0; i < lista.size(); i++){
                        DocumentSnapshot documentSnapshot = lista.get(i);
                            if(documentSnapshot.getString("name").equals(newNotis.getText().toString()) && checkProgramAndCourse()){
                                saveNotis();
                                break;
                            }
                            else if(!checkProgramAndCourse()){
                                newNotis.setError("Du har redan anmält notifikationer för denna kurs.");
                            }
                            else if(i == lista.size()-1){
                                newNotis.setError("Det finns ingen kurs med detta namn. Vänligen testa med ett annat namn.");
                            }
                        }
                    }
                }

        });
    }

    //Kollar så att man inte försöker lägga till en notifikation som man redan har.
    private boolean checkProgramAndCourse(){
        String[] notifications = addedNotis.getText().toString().trim().split(",");
        int counter = 0;
        boolean valid = false;
        for(int i = 0; i < notifications.length; i++) {
            if(!newNotis.getText().toString().equals(notifications[i])){
                counter++;
            }
        }
        if(counter == notifications.length){
            valid = true;
        }

        return valid;
    }


    //Spara ner en ny notis. Om det inte finns ett program som heter så som användaren skrivit in ska det inte sparas.
    public void saveNotis(){
        CollectionReference usersRef = rootRef.collection("Users").document(user.getUid().toString()).collection("Notifications");
        String notisTitel = newNotis.getText().toString();


        Map<String, Object> mapOne = new HashMap<>();
        mapOne.put("Notification", notisTitel);
        mapOne.put("AdId", "");


        usersRef.document().set(mapOne)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Notifikation skapad.", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "DocumentSnapshot successfully written!");
                            listNotifications();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error writing document", e);
                        }
                    });

    }

    //Tar bort alla notifikationer en användare har.
    public void clearNotifications(){
        CollectionReference usersRef = rootRef.collection("Users").document(user.getUid().toString()).collection("Notifications");
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> lista = task.getResult().getDocuments();
                for(int i = 0; i < lista.size(); i++){
                    DocumentSnapshot documentSnapshot = lista.get(i);
                    rootRef.collection("Users").document(user.getUid().toString()).collection("Notifications").document(documentSnapshot.getId()).delete();
                    listNotifications();
                }
                addedNotis.setText("");
            }
        });
    }

    //Hämtar och visar upp alla kurser och program en användare valt att få notifikationer om.
    public void listNotifications(){
        CollectionReference usersRef = rootRef.collection("Users").document(user.getUid().toString()).collection("Notifications");
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> lista = task.getResult().getDocuments();
                for(int i = 0; i < lista.size(); i++){
                    DocumentSnapshot documentSnapshot = lista.get(i);
                    if(i == 0){
                        addedNotis.setText(documentSnapshot.getString("Notification"));
                    }
                    else {
                        String text = addedNotis.getText().toString() + ", " + documentSnapshot.getString("Notification");
                        addedNotis.setText(text);
                    }
                }
            }
        });
    }
}
