package com.example.filip.unibook;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
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
        goToFavorites();
        goToMeddelande();
        //checkForNotis();


            if (!isMyServiceRunning()) {
                Intent serviceIntent = new Intent(LoggedInActivity.this, MyService.class);
                startService(serviceIntent);
            }
    }

    //Metod för att gå till profilsida
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

    //Metod för att gå till sök-vyn
    public void goToSearch() {
        LinearLayout searchBtn = findViewById(R.id.searchLinearLayout);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchPage = new Intent(LoggedInActivity.this, SearchActivity.class);
                startActivity(searchPage);
            }
        });
    }

    //Metod för att gå till användarens annonser
    public void goToAdds() {
        LinearLayout addBtn = findViewById(R.id.adsLinearLayout);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedInActivity.this, MyAdsActivity.class);
                startActivity(intent);
            }
        });
    }

    //Metod för att gå till inställningar
    public void goToSettings() {
        LinearLayout settingsBtn = findViewById(R.id.settingsLinearLayout);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedInActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    //Metod för att gå till favoriter
    public void goToFavorites() {
        LinearLayout favoritesBtn = findViewById(R.id.favouritesLinearLayout);
        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedInActivity.this, MyFavorites.class);
                startActivity(intent);
            }
        });
    }

    //Metod för att gå till chatt
    public void goToMeddelande(){
        LinearLayout btnmessages =  findViewById(R.id.messagesLinearLayout);
        btnmessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedInActivity.this, MyMessagesActivity.class);
                startActivity(intent);
            }
        });
    }

    //Starta background service om den inte redan är igång. Detta föra att skicka notifikationer.
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
