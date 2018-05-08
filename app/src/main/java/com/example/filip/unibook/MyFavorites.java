package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class MyFavorites extends AppCompatActivity {

    ListView listView;
    Context context = this;
    public static final String TAG = "TAG";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);

        listView = findViewById(R.id.listViewMyFavorites);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showDetailActivity = new Intent(MyFavorites.this, ChosenAdForSale.class);
                TextView adId = view.findViewById(R.id.txtAdID);
                showDetailActivity.putExtra("id", adId.getText().toString());
                startActivity(showDetailActivity);
            }
        });

        getMyFavoriteAds();
    }

    public void getMyFavoriteAds(){
        CollectionReference favouritesRef =  rootRef.collection("Favourites");

        Query query = favouritesRef.whereEqualTo("userId", user.getUid().toString());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int size = task.getResult().size();

                    String[] items = new String[size];
                    String[] ids = new String[size];
                    String[] prices = new String[size];

                    List<DocumentSnapshot> list = task.getResult().getDocuments();
                    for (int i = 0; i < task.getResult().size(); i++) {
                        DocumentSnapshot doc = list.get(i);
                        ids[i] = doc.getString("adId");
                        items[i] = doc.getString("title");
                        prices[i] = doc.getString("price");
                    }

                    ItemAdapter adapter = new ItemAdapter(context, items, prices, ids);
                    listView.setAdapter(adapter);
                }

                else{

                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
