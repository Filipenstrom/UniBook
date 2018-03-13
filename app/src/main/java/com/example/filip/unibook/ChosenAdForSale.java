package com.example.filip.unibook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ChosenAdForSale extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_ad_for_sale);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        Log.d("Index", "Index är " + id);
    }
}
