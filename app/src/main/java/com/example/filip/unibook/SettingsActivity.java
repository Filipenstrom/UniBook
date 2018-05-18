package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {
    Context context = this;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Button btnlogout =  findViewById(R.id.btnlogOut);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();

            }
        });

        Button btnnotiser =  findViewById(R.id.btnnotiser);
        btnnotiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotiser();
            }
        });

    }



    //Metod för att logga ut
    public void logOut(){
                mAuth.signOut();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

    }

    //Metod för att gå till notiser
    public void goToNotiser(){
                Intent intent = new Intent(SettingsActivity.this, NotificationActivity.class);
                startActivity(intent);

    }
}
