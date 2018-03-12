package com.example.filip.unibook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ChosenAdPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_ad_page);
        Intent intent = getIntent();
        int index = intent.getIntExtra("com.example.filip.unibook.ITEM_INDEX", -1);

        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = new SharedPreferences(this);
        String[] user = db.getUser(sharedPreferences.getusername());

        List<List<String>> ads = db.getMyAds(user[3]);
        List<String> chosenAd;

        for(int i = 0;i<ads.size();i++){
            List<String> annons = ads.get(i);

                 for(int i2 = 0; i2<annons.size();i2++) {
                     int id = Integer.parseInt(annons.get(0));
                     if(index == id){
                         chosenAd = annons;
                     }
                 }
            }

        }

}

