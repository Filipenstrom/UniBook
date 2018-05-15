package com.example.filip.unibook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class ReportAdActivity extends AppCompatActivity {

    public static final String TAG = "message";
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private TextView adName, adId;
    private EditText authorName, authorMail, message;
    private Button btnReportAd;
    private String adSellerId, id, loggedInUserName, loggedInUserMail, loggedInUser;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_ad);

        adName = findViewById(R.id.txtReportAdName);
        adId = findViewById(R.id.txtReportAdID);
        authorName = findViewById(R.id.txtReportAuthorName);
        authorMail = findViewById(R.id.txtReportAdAuthorMail);
        message = findViewById(R.id.txtReportAdMessage);
        btnReportAd = findViewById(R.id.btnReportSendReportAd);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        user = auth.getCurrentUser();
        loggedInUser = user.getUid().toString();

        DocumentReference userRef = rootRef.collection("Users").document(loggedInUser);

        userRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {

                                loggedInUserName = document.getString("name") + " " + document.getString("surname");
                                loggedInUserMail = document.getString("email");

                                fillInfo();

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

        btnReportAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference reportsRef = rootRef.collection("Reports");

                Map<String, Object> mapTwo = new HashMap<>();
                mapTwo.put("sellerId", adSellerId);
                mapTwo.put("reporterId", loggedInUser);
                mapTwo.put("adId", id);
                mapTwo.put("reporterMail", loggedInUserMail);
                mapTwo.put("reportMessage", message.getText().toString());
                mapTwo.put("reporterName", loggedInUserName);

                reportsRef.document()
                        .set(mapTwo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(ReportAdActivity.this, "Anmälan skapad",
                                        Toast.LENGTH_LONG).show();

                                finish();
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

    public void fillInfo(){

        DocumentReference docRef = rootRef.collection("Ads").document(id);

        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {

                                adSellerId = document.getString("sellerId");
                                adName.setText(document.getString("title"));
                                adId.setText(document.getId());
                                authorName.setText(loggedInUserName);
                                authorMail.setText(loggedInUserMail);

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }
}