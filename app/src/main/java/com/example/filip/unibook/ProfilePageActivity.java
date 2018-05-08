package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.Reference;
import java.sql.Ref;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ludvig on 2018-03-06.
 *
 */

public class ProfilePageActivity extends AppCompatActivity {

    public static final String TAG = "Bra meddelande";
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    TextView name, mail, adress, phone, school, numberOflikes;
    Button redigeraProfil;
    ImageView pic,likebtn, dislikebtn;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String adid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        name = findViewById(R.id.txtNamn);
        mail =  findViewById(R.id.txtMail);
        adress =  findViewById(R.id.txtAdress);
        phone =  findViewById(R.id.txtTele);
        school =  findViewById(R.id.txtSkola);
        pic = findViewById(R.id.ivsellerprofile);
        redigeraProfil = findViewById(R.id.btnedit);
        likebtn = findViewById(R.id.like);
        dislikebtn = findViewById(R.id.dislike);
        numberOflikes = findViewById(R.id.txtNumberOfLikes);

        Intent intent = getIntent();
        final String sellerId = intent.getStringExtra("userid");
        adid = intent.getStringExtra("adid");

        if(sellerId != null) {
            insertUserInformation(sellerId, sellerId);
            redigeraProfil.setVisibility(View.INVISIBLE);
        }
        else{
            insertUserInformation(user.getUid(), sellerId);
            likebtn.setVisibility(View.INVISIBLE);
            dislikebtn.setVisibility(View.INVISIBLE);
            numberOflikes.setVisibility(View.INVISIBLE);
        }

        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(sellerId, user.getUid(), true);
            }
        });

        dislikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(sellerId, user.getUid(), false);
            }
        });
    }

    public void goToEditProfile(View view){
        Intent intent = new Intent(ProfilePageActivity.this, EditProfileActivity.class);
        startActivity(intent);

    }

    public void insertUserInformation(String userid, final String sellerid) {
        DocumentReference docRef = rootRef.collection("Users").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        name.setText(document.getString("name") + " " + document.getString("surname"));
                        mail.setText(document.getString("email"));
                        adress.setText(document.getString("adress"));
                        phone.setText(document.getString("phone"));
                        school.setText(document.getString("school"));
                        String imageId = document.getString("imageId");

                        setImage(imageId);
                        if(sellerid != null) {
                            checkLike(sellerid, user.getUid());
                        }

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

    public void checkLike(String sellerid, final String userid) {
        CollectionReference docRef = rootRef.collection("Likes").document(sellerid).collection("UsersThatLiked");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> listSnapshot = task.getResult().getDocuments();
                    if (listSnapshot != null) {
                        int likecounter = 0;
                        for(int i = 0;i<listSnapshot.size();i++) {
                            DocumentSnapshot docSnapshot = listSnapshot.get(i);
                            if(docSnapshot.getString("Liked").equals("Liked")){
                                likecounter++;
                            }
                            else{
                                likecounter--;
                            }
                        }

                        if(likecounter <= -5){
                            numberOflikes.setTextColor(Color.RED);
                            numberOflikes.setText("Detta är inte en pålitlig användare");
                        }
                        else if(likecounter >= 5){
                            numberOflikes.setTextColor(Color.GREEN);
                            numberOflikes.setText("Detta är en pålitlig användare");
                        }
                        else{
                            numberOflikes.setText("Denna användare har inget betyg");
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void like(final String sellerid, final String userid, boolean like) {
        CollectionReference colRef = rootRef.collection("Likes").document(sellerid).collection("UsersThatLiked");
        Map<String, Object> mapOne = new HashMap<>();

        if(like) {
            mapOne.put("Liked", "Liked");
            Toast.makeText(this, "Du gillar nu denna användare", Toast.LENGTH_SHORT).show();
        }
        else{
            mapOne.put("Liked", "Disliked");
            Toast.makeText(this, "Du gillar inte denna användare", Toast.LENGTH_SHORT).show();
        }

        colRef.document(userid)
                .set(mapOne)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        checkLike(sellerid, user.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    public void setImage(String imageId){

        StorageReference storageRef = storage.getReferenceFromUrl(imageId);

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
}