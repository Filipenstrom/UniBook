package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyFavorites extends AppCompatActivity {

    DatabaseHelper myDb;
    ListView listView;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);

        myDb = new DatabaseHelper(this);
        listView = findViewById(R.id.listViewMyFavorites);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showDetailActivity = new Intent(MyFavorites.this, ChosenAdForSale.class);
                TextView adId = view.findViewById(R.id.txtAdID);
                showDetailActivity.putExtra("id", Integer.parseInt(adId.getText().toString()));
                startActivity(showDetailActivity);
            }
        });

       // getMyFavoriteAds();

    }

   /* public void getMyFavoriteAds(){
        SharedPreferences prefs = new SharedPreferences(context);
        User user = myDb.getUser(prefs.getusername());

        ListView listView = findViewById(R.id.listViewMyFavorites);

        List<Ad> annonser = myDb.getMyFavoriteAds(user.getId());
        int numberOfAds = annonser.size();
        String[] items = new String[numberOfAds];
        String[] prices = new String[numberOfAds];
        String[] ids = new String[numberOfAds];
        List<byte[]> bytes = new ArrayList<>();

        for (int i = 0; i < annonser.size(); i++) {
            Ad annons = annonser.get(i);
            ids[i] = annons.getId();
            items[i] = annons.getTitle();
            prices[i] = annons.getPrice();
            bytes.add(annons.getPic());
        }

        ItemAdapter itemAdapter = new ItemAdapter(this, items, prices, bytes, ids);
        listView.setAdapter(itemAdapter);
    }*/
}
