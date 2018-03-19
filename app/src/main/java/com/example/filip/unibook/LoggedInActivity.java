package com.example.filip.unibook;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        goToAdds();
        goToProfile();
        goToSettings();
        goToSearch();
        checkForNotis();
    }

    public void goToProfile() {
        LinearLayout profileBtn = findViewById(R.id.profileLinearLayout);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePage = new Intent(LoggedInActivity.this, ProfilePageActivity.class);
                startActivity(profilePage);
            }
        });
    }

    public void goToSearch(){
        LinearLayout searchBtn = findViewById(R.id.searchLinearLayout);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchPage = new Intent(LoggedInActivity.this, SearchActivity.class);
                startActivity(searchPage);
            }
        });
    }

    public void goToAdds(){
        LinearLayout addBtn =  findViewById(R.id.adsLinearLayout);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedInActivity.this, MyAdsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void goToSettings(){
        LinearLayout settingsBtn =  findViewById(R.id.settingsLinearLayout);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedInActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void checkForNotis() {
        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sp = new SharedPreferences(this);
        User user = db.getUser(sp.getusername());
        List<String> notis = db.getNotis(Integer.parseInt(user.getId()));
        List<Ad> ads = db.getAllAds("");

        try {
            //Loopa igenom alla ads.
            for (int i = 0; i < ads.size(); i++) {
                //Loopa igenom alla notiser på en ad.
                for (int i2 = 0; i2 < notis.size(); i2++) {
                    Ad ad;
                    if (ads.size() == 1) {
                        ad = ads.get(i);
                    } else {
                        //Gör bara kollen på de senast tillagda böckerna
                        ad = ads.get(i + db.getNotisCounter(notis.get(i)));
                    }
                    //Kolla om det lagts till nya böcker sen senaste notisen visades, om inte visa ingen ny notis.
                    if (ads.size() > db.getNotisCounter(ad.getProgram())) {
                        String program = ad.getProgram();
                        //Om notisen matchar ett program på en ny bok, visa notis.
                        if (notis.get(i2).equals(program)) {
                            Notification notification = new Notification(this);
                            notification.notificationManagerCompat.notify(2, notification.mBuilder.build());
                            db.setNotisCounter(ad.getProgram(), ads.size());
                        }
                    }
                }
            }
        }
        catch(Exception e){
            Log.d("Broken", "Index out of bounds");
        }
    }
}
