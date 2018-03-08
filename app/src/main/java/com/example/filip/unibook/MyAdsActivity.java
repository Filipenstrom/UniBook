package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Arrays;

public class MyAdsActivity extends AppCompatActivity {

    Context context = this;
    DatabaseHelper myDb;
    ArrayAdapter<String > adapter;

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
        String [] id = myDb.getUser(prefs.getusername());
        ListView minaAnnonser = (ListView) findViewById(R.id.listViewMyAds);

        ArrayList annonser = myDb.getMyAds(id[0]);
        ArrayList myAds = new ArrayList(Arrays.asList(annonser));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,myAds);
        minaAnnonser.setAdapter(adapter);


    }
}
