package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class ChosenAdForSale extends AppCompatActivity {

    Ad chosenAd;
    TextView title;
    TextView pris;
    TextView info;
    TextView program;
    TextView kurs;
    ImageView pic;
    TextView seller;
    User user;
    TextView chosenAdId;
    Button favoriteBtn;
    Context context;
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
        seller = findViewById(R.id.chosenAdSellerName);
        chosenAdId = findViewById(R.id.txtChosenAdForSale);
        favoriteBtn = findViewById(R.id.btnAddToFavorites);
        context = this;

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        chosenAd = db.getAd(id);

        user = db.getUserWithId(chosenAd.getUserId());

        fillAdInformation();

        Button btnReportAd = findViewById(R.id.btnReportAd);


        btnReportAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChosenAdForSale.this, ReportAd.class);
                TextView id = findViewById(R.id.txtChosenAdForSale);
                intent.putExtra("id", Integer.parseInt(id.getText().toString()));
                startActivity(intent);
            }
        });

        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = new SharedPreferences(context);
                User inloggadAnv = db.getUser(prefs.getusername());
                String knappText = favoriteBtn.getText().toString();
                // String userid = Integer.toString(id);
                String adId = chosenAd.getId();
                String anvandare = inloggadAnv.getId();

                if(knappText.equals("LÄGG TILL SOM FAVORIT"))
                {
                    db.addFavorite(adId, anvandare);
                    Toast.makeText(ChosenAdForSale.this,"Tillagd i favoriter!", Toast.LENGTH_LONG).show();
                    favoriteBtn.setText("TA BORT SOM FAVORIT");
                }

                else {
                    //db.deleteFavorite(adId,anvandare);
                    Toast.makeText(ChosenAdForSale.this,"Borttagen från favoriter!", Toast.LENGTH_LONG).show();
                    favoriteBtn.setText("LÄGG TILL SOM FAVORIT");
                }
            }
        });

        //canAddToFavorite();
    }

    /*public void canAddToFavorite(){
        SharedPreferences prefs = new SharedPreferences(context);
        User anvandare = db.getUser(prefs.getusername());
        List<Ad> annonser = db.getMyFavoriteAds(anvandare.getId());
        String adId = chosenAd.getId();
        String annonsAnvId = Integer.toString(chosenAd.getUserId());

        for (int i = 0; i < annonser.size(); i++) {
            Ad annons = annonser.get(i);
            if(annonsAnvId.equals(anvandare.getId())){
                favoriteBtn.setVisibility(View.INVISIBLE);
            }
            else if(annons.getId().equals(adId)){

                favoriteBtn.setText("TA BORT SOM FAVORIT");
                break;
            }
            else {
                favoriteBtn.setText("LÄGG TILL SOM FAVORIT");

            }
        }
    }*/

        //Hämtar data om den valda annonsen från listan.
    public void fillAdInformation(){
        String fullName = user.getName() + " " + user.getSurname();
        chosenAdId.setText(chosenAd.getId());
        seller.setText(fullName);
        title.setText(chosenAd.getTitle());
        pris.setText(chosenAd.getPrice() + ":-");
        info.setText(chosenAd.getInfo());
        program.setText(chosenAd.getProgram());
        kurs.setText(chosenAd.getCourse());
        pic.setImageBitmap(BitmapFactory.decodeByteArray(chosenAd.getPic(), 0, chosenAd.getPic().length));
    }
}

