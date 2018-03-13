package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdsActivity extends AppCompatActivity {

    Context context = this;
    DatabaseHelper myDb;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        myDb = new DatabaseHelper(this);
        listView = findViewById(R.id.listViewMyAds);

        goToCreateAd();
        getAllMyAds();

        //Gå till vald annons och skicka med item index.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent showDetailActivity = new Intent(getApplicationContext(), ChosenAdPageActivity.class);
                showDetailActivity.putExtra("com.example.filip.unibook.ITEM_INDEX", i);
                startActivity(showDetailActivity);
            }
        });

    }

    public void goToCreateAd(){
        Button button = (Button) findViewById(R.id.btnSkapaAnnons);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyAdsActivity.this, CreateNewAdActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getAllMyAds(){

    SharedPreferences prefs = new SharedPreferences(context);
    String[] mail = myDb.getUser(prefs.getusername());
    DatabaseHelper db = new DatabaseHelper(this);
    SharedPreferences sharedPreferences = new SharedPreferences(this);
    String[] user = db.getUser(sharedPreferences.getusername());
    ListView listView = findViewById(R.id.listViewMyAds);


    List<List<String>> annonser = myDb.getMyAds(mail[3]);
    int numberOfAds = annonser.size();
    String[] items = new String[numberOfAds];
    String[] prices = new String[numberOfAds];

    //Hämtar alla bilder tills annonserna
    List<byte[]> bytes = db.getAdsImg(user[0]);


    //Hämtar all data om annonserna, exkluderat tillhörande bilder.
    for(int i = 0;i<annonser.size();i++) {
        List<String> annons = annonser.get(i);

        for(int i2 = 0; i2<annons.size();i2++)
            items[i] = annons.get(1).toString();
        prices[i] = annons.get(2).toString();
    }

    ItemAdapter itemAdapter = new ItemAdapter(this, items, prices, bytes);
    listView.setAdapter(itemAdapter);
}
}
