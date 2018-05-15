package com.example.filip.unibook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChosenAdForSaleActivity extends AppCompatActivity {

    public static final String TAG = "message";
    TextView title, pris, info, program, kurs, seller, chosenAdId;
    ImageView pic, sellerpic;
    Button favoriteBtn, btnCallAd, btnReportAd, btnSendMessage;
    ProgressBar progressBar;
    Context context;
    String sellerId, sellerPhone, adId, id, sellerName, imageId, boktitel, createdChatId;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser loggenIn = mAuth.getCurrentUser();
    private int CALL_PERMISSION_CODE = 1;
    Bitmap img;
    String priceText;
    boolean chatCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_ad_for_sale);

        title = findViewById(R.id.chosenAdTitle);
        pris = findViewById(R.id.chosenAdPrice);
        info = findViewById(R.id.chosenAdDescription);
        program = findViewById(R.id.chosenAdProgramName);
        kurs = findViewById(R.id.chosenAdCourseName);
        pic = findViewById(R.id.chosenAdImg);
        seller = findViewById(R.id.chosenAdSellerName);
        favoriteBtn = findViewById(R.id.btnAddToFavorites);
        btnCallAd = findViewById(R.id.btnChosenAdCall);
        btnReportAd = findViewById(R.id.btnReportAd);
        progressBar = findViewById(R.id.progressBarChosenAd);
        btnSendMessage = findViewById(R.id.btnSendMsgAd);
        sellerpic = findViewById(R.id.ivsellerprofile);

        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        img = intent.getParcelableExtra("img");
        id = intent.getStringExtra("id");


        DocumentReference docRef = rootRef.collection("Ads").document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        priceText = document.getString("price");
                        title.setText(document.getString("title"));
                        pris.setText(document.getString("price") + " :-");
                        info.setText(document.getString("info"));
                        kurs.setText(document.getString("course"));
                        program.setText(document.getString("program"));
                        sellerId = document.getString("sellerId");
                        adId = document.getId();
                        imageId = document.getString("imageId");
                        boktitel = document.getString("title");
                        pic.setImageBitmap(img);

                        checkFavourites(adId);
                        getSeller(sellerId);
                        checkIfChatAlreadyExist();

                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference favouritesRef = rootRef.collection("Favourites");

                if(favoriteBtn.getText().toString().toLowerCase().equals("lägg till favorit")){

                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", loggenIn.getUid().toString());
                    map.put("adId", adId);
                    map.put("price", priceText);
                    map.put("title", title.getText().toString());
                    map.put("imageId", imageId);


                    favouritesRef.document()
                            .set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ChosenAdForSaleActivity.this, "Favorit tillagd",
                                            Toast.LENGTH_LONG).show();

                                    favoriteBtn.setText("Ta bort favorit");
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                }else{

                    removeFavourite(adId);
                }
            }
        });

        sellerpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChosenAdForSaleActivity.this, ProfilePageActivity.class);
                String id = sellerId;
                intent.putExtra("userid", id);
                intent.putExtra("adid", adId);
                startActivity(intent);
            }
        });

        btnReportAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChosenAdForSaleActivity.this, ReportAdActivity.class);
                intent.putExtra("id", adId);
                startActivity(intent);
            }
        });


        btnCallAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ChosenAdForSaleActivity.this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sellerPhone));
                    startActivity(callIntent);
                }

                else{
                    requestCallPermission();
                }
            }
        });

            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChosenAdForSaleActivity.this, MessengerActivity.class);
                    String id = sellerId;
                    intent.putExtra("userid", id);
                    intent.putExtra("sellerName", sellerName);
                    intent.putExtra("boktitel", boktitel);
                    if(chatCreated){
                        intent.putExtra("chatId", createdChatId);
                    }
                    startActivity(intent);
                }
            });
    }

    //Metod som fäster bilden till annonsen i en image view.
    public void setImage(String imageId){

        StorageReference storageRef = storage.getReferenceFromUrl(imageId);

        final long ONE_MEGABYTE = 1024 * 1024;

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                sellerpic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Error", exception.getMessage());
            }
        });
    }

    //Metod som hämtar säljaren till annonsen
    public void getSeller(String sellerId){

        DocumentReference userRef = rootRef.collection("Users").document(sellerId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        sellerPhone = document.getString("phone");
                        sellerName = document.getString("name") + " " + document.getString("surname");
                        setImage(document.getString("imageId"));
                        seller.setText(sellerName);

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

    //Metod som kollar om annonsen tillhör favoriter
    public void checkFavourites(final String adId){

        CollectionReference favouritesRef =  rootRef.collection("Favourites");
        Query query = favouritesRef.whereEqualTo("userId", loggenIn.getUid().toString());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> list = task.getResult().getDocuments();
                    for (DocumentSnapshot document : list) {

                        if (document.getString("adId").equals(adId)) {
                            favoriteBtn.setText("Ta bort favorit");
                        }
                    }
                }else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    //Metod som tar bort eller lägger till annons som favorit
    public void removeFavourite(final String adId){

        final CollectionReference favouritesRef =  rootRef.collection("Favourites");
        Query query = favouritesRef.whereEqualTo("userId", loggenIn.getUid().toString());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> list = task.getResult().getDocuments();
                    for (DocumentSnapshot document : list) {

                        if (document.getString("adId").equals(adId)) {
                            favouritesRef.document(document.getId()).delete();
                            Toast.makeText(ChosenAdForSaleActivity.this, "Favorit borttagen",
                                    Toast.LENGTH_LONG).show();
                            favoriteBtn.setText("Lägg till favorit");
                        }
                    }
                }else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    //Metod som kollar om man redan har startat en chat med säljaren
    //Vid klick på knapp så kommer man till chatten.
    public void checkIfChatAlreadyExist(){
        CollectionReference chatRef =  rootRef.collection("Chat");
        chatRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> list = task.getResult().getDocuments();
                    for (DocumentSnapshot document : list) {

                        if (document.getString("User1").equals(loggenIn.getUid().toString()) && document.getString("User2").equals(sellerId) && document.getString("BokTitel").equals(boktitel)) {
                            btnSendMessage.setText("En chat är redan startad");
                            chatCreated = true;
                            createdChatId = document.getId();
                        }
                        else if (document.getString("User2").equals(loggenIn.getUid().toString()) && document.getString("User1").equals(sellerId) && document.getString("BokTitel").equals(boktitel)) {
                            btnSendMessage.setText("En chat är redan startad");
                            chatCreated = true;
                            createdChatId = document.getId();
                        }
                    }
                }else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    //Metod för att be om att ringa annonsör
    private void requestCallPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to make calls from application")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ChosenAdForSaleActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
        }
    }

    //Resultatet som skickas tillbaka från requesten ovan
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CALL_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}