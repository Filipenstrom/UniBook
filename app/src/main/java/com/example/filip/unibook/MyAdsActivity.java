package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdsActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    Context context = this;
    ListView listView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        listView = findViewById(R.id.listViewMyAds);

        goToCreateAd();
        getAllMyAds();

        //Gå till vald annons och skicka med id på vald annons från ett osynligt textfält som ligger i my_listview_ad.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent showDetailActivity = new Intent(context, ChosenAdPageActivity.class);
                ImageView adPic = view.findViewById(R.id.ivAdsListPicture);
                TextView id = view.findViewById(R.id.txtAdID);

                try{

                    Bitmap bitmap = ((BitmapDrawable)adPic.getDrawable()).getBitmap();
                    showDetailActivity.putExtra("img", bitmap);
                    showDetailActivity.putExtra("id", id.getText().toString());
                    startActivity(showDetailActivity);

                }catch(Exception e){

                    Log.d("Error", e.getMessage().toString());

                }
            }
        });
    }

    public void goToCreateAd() {
        Button button = findViewById(R.id.btnSkapaAnnons);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAdsActivity.this, CreateNewAdActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getAllMyAds(){

        CollectionReference favouritesRef =  rootRef.collection("Ads");
        Query query = favouritesRef.whereEqualTo("sellerId", user.getUid().toString());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    int size = task.getResult().size();

                    String[] items = new String[size];
                    String[] ids = new String[size];
                    String[] prices = new String[size];

                    List<DocumentSnapshot> minLista = task.getResult().getDocuments();

                    for(int i = 0;i < size;i++){

                        DocumentSnapshot doc = minLista.get(i);
                        ids[i] = doc.getId().toString();
                        items[i] = doc.getString("title");
                        prices[i] = doc.getString("price");
                    }
                    ItemAdapter adapter = new ItemAdapter(context, items, prices, ids);
                    listView.setAdapter(adapter);
                }else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}