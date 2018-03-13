package com.example.filip.unibook;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import android.widget.*;

public class SearchActivity extends AppCompatActivity {

    DatabaseHelper myDb = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        fillUpAds();

        Button btn = findViewById(R.id.buttonSearch);
        
    }

    public void fillUpAds(){
        ListView listView = findViewById(R.id.listViewAllAds);

        List<Ad> annonser = myDb.getAllAds();
        int numberOfAds = annonser.size();
        String[] titles = new String[numberOfAds];
        String[] prices = new String[numberOfAds];
        List<byte[]> bytes = new ArrayList<>();

        for(int i = 0;i < annonser.size();i++){
            titles[i] = annonser.get(i).getTitle();
            prices[i] = annonser.get(i).getPrice();
            bytes.add(annonser.get(i).getPic());
         }

        ItemAdapter itemAdapter = new ItemAdapter(this, titles, prices, bytes);
        listView.setAdapter(itemAdapter);
    }
}
