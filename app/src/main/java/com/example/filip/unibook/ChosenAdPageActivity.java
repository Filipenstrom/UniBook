package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ChosenAdPageActivity extends AppCompatActivity {

    Ad chosenAd;
    byte[] bytepic;
    TextView title;
    TextView pris;
    TextView ISDN;
    TextView info;
    TextView program;
    TextView kurs;
    ImageView pic;
    Context context = this;
    final DatabaseHelper db = new DatabaseHelper(this);

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
        int id = intent.getIntExtra("id", -1);
        Log.d("Index", "Index är " + id);

        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = new SharedPreferences(this);
        User user = db.getUser(sharedPreferences.getusername());

        List<Ad> ads = db.getMyAds(user.getMail());

        //Sök igenom alla annonser tills en matchning sker på index = id på annons.
        for(int i = 0;i<ads.size();i++){
            Ad annons = ads.get(i);

                     int adid = Integer.parseInt(annons.getId());
                     if(id == (adid)){
                         chosenAd = annons;
                         bytepic = annons.getPic();
                         fillAdInformation();
                 }
            }

            deleteAd();
        }

        //Hämtar data om den valda annonsen från listan.
        public void fillAdInformation(){
            title.setText(chosenAd.getTitle());
            pris.setText(chosenAd.getPrice());
            ISDN.setText(chosenAd.getISDN());
            info.setText(chosenAd.getInfo());
            program.setText(chosenAd.getProgram());
            kurs.setText(chosenAd.getCourse());
            pic.setImageBitmap(BitmapFactory.decodeByteArray(chosenAd.getPic(), 0, chosenAd.getPic().length));
        }

        //Metod som uppdaterar ens valda annons
        public void updateData(View view){
            DatabaseHelper db = new DatabaseHelper(this);
            SharedPreferences sharedPreferences = new SharedPreferences(this);
            User user = db.getUser(sharedPreferences.getusername());
            int id = Integer.parseInt(chosenAd.getId());
            int prisInt = Integer.parseInt(pris.getText().toString());
            int userid = Integer.parseInt(user.getId());

            db.updateAd(id, title.getText().toString(), prisInt, ISDN.getText().toString(), info.getText().toString(), program.getText().toString(), kurs.getText().toString(), bytepic, userid);
            Toast.makeText(ChosenAdPageActivity.this,"Update successful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChosenAdPageActivity.this, MyAdsActivity.class);
            startActivity(intent);
        }

        //Metod som tar bort den valda annonsen och skickar en tillbaka till listan

        public void deleteAd(){
            Button deleteBtn = (Button) findViewById(R.id.btnDelete);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = new SharedPreferences(context);
                    User user = db.getUser(sharedPreferences.getusername());
                    int adId = Integer.parseInt(chosenAd.getId());

                    db.deleteAd(adId);
                    Intent intent = new Intent(ChosenAdPageActivity.this, MyAdsActivity.class);
                    startActivity(intent);
                }
            });
        }
}

