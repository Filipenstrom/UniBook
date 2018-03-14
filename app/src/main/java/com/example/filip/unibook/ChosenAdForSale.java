package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ChosenAdForSale extends AppCompatActivity {

    Ad chosenAd;
    byte[] bytepic;
    TextView title;
    TextView pris;
    TextView info;
    TextView program;
    TextView kurs;
    ImageView pic;
    final DatabaseHelper db = new DatabaseHelper(this);

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


        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        String fakeInput = "";

        List<Ad> ads = db.getAllAds(fakeInput);

        //Sök igenom alla annonser tills en matchning sker på index = id på annons.
        for (int i = 0; i < ads.size(); i++) {
            Ad annons = ads.get(i);

            int adid = Integer.parseInt(annons.getId());
            if (id == (adid)) {
                chosenAd = annons;
                bytepic = annons.getPic();
                fillAdInformation();
            }
        }
    }

        //Hämtar data om den valda annonsen från listan.
    public void fillAdInformation(){
        title.setText(chosenAd.getTitle());
        pris.setText(chosenAd.getPrice());
        info.setText(chosenAd.getInfo());
        program.setText(chosenAd.getProgram());
        kurs.setText(chosenAd.getCourse());
        pic.setImageBitmap(BitmapFactory.decodeByteArray(chosenAd.getPic(), 0, chosenAd.getPic().length));
    }
}

