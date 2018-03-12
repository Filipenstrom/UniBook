package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ChosenAdPageActivity extends AppCompatActivity {

    List<String> chosenAd;
    byte[] bytepic;
    TextView title;
    TextView pris;
    TextView ISDN;
    TextView info;
    TextView program;
    TextView kurs;
    ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_ad_page);
        title = findViewById(R.id.etchosenAdTitle);
        pris = findViewById(R.id.etchosenAdPris);
        ISDN = findViewById(R.id.etchosenAdISDN);
        info = findViewById(R.id.etchosenAdInfo);
        program = findViewById(R.id.etchosenAdProgram);
        kurs = findViewById(R.id.etchosenAdCourse);
        pic = findViewById(R.id.ivchosenAdImage);
        Intent intent = getIntent();
        int index = intent.getIntExtra("com.example.filip.unibook.ITEM_INDEX", -1);
        Log.d("Index", "Index är " + index);

        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = new SharedPreferences(this);
        String[] user = db.getUser(sharedPreferences.getusername());
        List<byte[]> bytes = db.getAdsImg(user[0]);

        List<List<String>> ads = db.getMyAds(user[3]);

        for(int i = 0;i<ads.size();i++){
            List<String> annons = ads.get(i);
                 for(int i2 = 0; i2<annons.size();i2++) {
                     int id = Integer.parseInt(annons.get(0));
                     if(index == (id-1)){
                         chosenAd = annons;
                         bytepic = bytes.get(i);
                         fillAdInformation();
                         Log.d("Balle", "Det fungerade");
                     }
                 }
            }

        }

        public void fillAdInformation(){
            title.setText(chosenAd.get(1));
            pris.setText(chosenAd.get(2));
            ISDN.setText(chosenAd.get(3));
            info.setText(chosenAd.get(4));
            program.setText(chosenAd.get(5));
            kurs.setText(chosenAd.get(6));
            pic.setImageBitmap(BitmapFactory.decodeByteArray(bytepic, 0, bytepic.length));
        }

        public void updateData(View view){
            DatabaseHelper db = new DatabaseHelper(this);
            SharedPreferences sharedPreferences = new SharedPreferences(this);
            String[] user = db.getUser(sharedPreferences.getusername());
            int id = Integer.parseInt(chosenAd.get(0));
            int prisInt = Integer.parseInt(pris.getText().toString());
            int userid = Integer.parseInt(user[0]);

            db.updateAd(id, title.getText().toString(), prisInt, ISDN.getText().toString(), info.getText().toString(), program.getText().toString(), kurs.getText().toString(), bytepic, userid);
            Toast.makeText(ChosenAdPageActivity.this,"Update successful, gå tillbaka till huvudmeny för att refresha!", Toast.LENGTH_LONG).show();
        }

}

