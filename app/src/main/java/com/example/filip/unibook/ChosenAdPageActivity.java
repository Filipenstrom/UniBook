package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.lang.ref.Reference;
import java.util.List;

public class ChosenAdPageActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    String id;
    Ad chosenAd;
    byte[] bytepic;
    TextView title;
    TextView pris;
    TextView ISDN;
    TextView info;
    TextView program;
    TextView kurs;
    ImageView pic;
    Context context = this;
    final DatabaseHelper db = new DatabaseHelper(this);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_ad_page);

        title = findViewById(R.id.etchosenAdTitle);
        pris = findViewById(R.id.etchosenAdPris);
        ISDN = findViewById(R.id.etchosenAdISDN);
        info = findViewById(R.id.etchosenAdInfo);
        program = findViewById(R.id.etchosenAdProgram);
        kurs = findViewById(R.id.etchosenAdCourse);
        pic = findViewById(R.id.ivchosenAdImage);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        getAd();
        deleteAd();
        }

    public void getAd(){
        final DocumentReference adsRef = rootRef.collection("Ads").document(id);
        adsRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot doc = task.getResult();


                            if (doc.getString("sellerId").equals(user.getUid().toString())) {
                                ISDN.setText(doc.getString("ISDN"));
                                kurs.setText(doc.getString("course"));
                                info.setText(doc.getString("info"));
                                pris.setText(doc.getString("price"));
                                program.setText(doc.getString("program"));
                                String seller = doc.getString("seller");
                                String sellerId = doc.getString("sellerId");
                                title.setText(doc.getString("title"));
                            }
                        }
                        else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
}

    public void updateData(View view) {
        DocumentReference docRef = rootRef.collection("Ads").document(id);
        docRef.update("title", title.getText().toString());
        docRef.update("ISDN", ISDN.getText().toString());
        docRef.update("course", kurs.getText().toString());
        docRef.update("info", info.getText().toString());
        docRef.update("price", pris.getText().toString());
        docRef.update("program", program.getText().toString());

        Intent intent = new Intent(ChosenAdPageActivity.this, MyAdsActivity.class);
        startActivity(intent);
    }

        //Metod som tar bort den valda annonsen och skickar en tillbaka till listan
        public void deleteAd(){
            Button deleteBtn = findViewById(R.id.btnDelete);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootRef.collection("Ads").document(id).delete();

                    Intent intent = new Intent(ChosenAdPageActivity.this, MyAdsActivity.class);
                    startActivity(intent);
                }
            });
        }
}

