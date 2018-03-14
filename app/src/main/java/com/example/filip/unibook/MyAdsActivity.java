package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationManagerCompat;
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

        //Gå till vald annons och skicka med id på vald annons från ett osynligt textfält som ligger i my_listview_ad.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent showDetailActivity = new Intent(getApplicationContext(), ChosenAdPageActivity.class);
                TextView id = view.findViewById(R.id.txtAdID);
                showDetailActivity.putExtra("id", Integer.parseInt(id.getText().toString()));
                startActivity(showDetailActivity);
            }
        });
    }

    public void goToCreateAd() {
        Button button = (Button) findViewById(R.id.btnSkapaAnnons);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAdsActivity.this, CreateNewAdActivity.class);
                startActivity(intent);
            }
        });
    }

    //Hämta data om alla annonser som en användare lagt upp och skicka de till ItemAdapter.
    public void getAllMyAds() {
        SharedPreferences prefs = new SharedPreferences(context);
        User mail = myDb.getUser(prefs.getusername());
        ListView listView = findViewById(R.id.listViewMyAds);

        List<Ad> annonser = myDb.getMyAds(mail.getMail());
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
    }
}
