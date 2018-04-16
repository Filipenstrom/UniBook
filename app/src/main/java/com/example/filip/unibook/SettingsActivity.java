package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity {
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        logOut();
        goToNotiser();
        goToMeddelande();
    }

    public void logOut(){
        Button btnlogout =  findViewById(R.id.btnlogOut);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                SharedPreferences sharedPreferences = new SharedPreferences(context);
                sharedPreferences.setUserID("");
                startActivity(intent);
            }
        });
    }

    public void goToNotiser(){
        Button btnnotiser =  findViewById(R.id.btnnotiser);
        btnnotiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void goToMeddelande(){
        Button btnmessages =  findViewById(R.id.btnMyMessages);
        btnmessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MyMessagesActivity.class);
                startActivity(intent);
            }
        });
    }
}
