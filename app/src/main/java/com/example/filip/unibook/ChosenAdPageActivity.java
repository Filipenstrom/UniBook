package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.lang.ref.Reference;
import java.util.List;

public class ChosenAdPageActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    String id;
    TextView title;
    TextView pris;
    TextView ISDN;
    TextView info;
    TextView program;
    TextView kurs;
    ImageView pic;
    Context context = this;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;

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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



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


                            ISDN.setText(doc.getString("ISDN"));
                            kurs.setText(doc.getString("course"));
                            info.setText(doc.getString("info"));
                            pris.setText(doc.getString("price"));
                            program.setText(doc.getString("program"));
                            title.setText(doc.getString("title"));
                            String imageId = doc.getString("imageId");

                            setImage(imageId);

                        }
                        else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    public void setImage(String imageId){

        StorageReference storageRef = storage.getReferenceFromUrl(imageId);

        /*
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/152a1281-2366-4f3a-a50e-7d7c1e7019b4");
        */

        final long ONE_MEGABYTE = 1024 * 1024;

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                pic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
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

