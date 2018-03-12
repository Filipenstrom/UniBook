package com.example.filip.unibook;

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
    List<String> items = new ArrayList<>();
    List<String> prices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        myDb = new DatabaseHelper(this);
        goToCreateAd();
        getAllMyAds();

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
        String[] id = myDb.getUser(prefs.getusername());
        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = new SharedPreferences(this);

        ListView listView = findViewById(R.id.listViewMyAds);


        List<List<String>> annonser = myDb.getMyAds(id[3]);
        //ArrayList myAds = new ArrayList(Arrays.asList(annonser));
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,myAds);
        //minaAnnonser.setAdapter(adapter);

        for(int i = 0;i<annonser.size();i++) {
            List<String> annons = annonser.get(i);
            for(int i2 = 0; i2<annons.size();i2++)
            //textView.setText(annons.get(0).toString() + " " + annons.get(1).toString() + " " + annons.get(2).toString() + " " + annons.get(3).toString() + " " + annons.get(4).toString());
            //imageView.setImageBitmap(BitmapFactory.decodeByteArray(db.getAdsImg(user[0]), 0, db.getAdsImg(user[0]).length));
            items.add(annons.get(0).toString());
            prices.add(annons.get(1).toString());
        }

        ItemAdapter itemAdapter = new ItemAdapter(this, items, prices);
        listView.setAdapter(itemAdapter);

     //   myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
     //       @Override
     //       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
     //           Intent showDetailActivity = new Intent(getApplicationContext(), DetailActivity.class);
     //           showDetailActivity.putExtra("com.example.ludvig.listapp.ITEM_INDEX", i);
     //           startActivity(showDetailActivity);
     //       }
     //   });
    }
}
