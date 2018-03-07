package com.example.filip.unibook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);


    

        goToAdds();
        goToProfile();

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


    public void goToAdds(){
        Button addBtn = (Button) findViewById(R.id.btnAnnons);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedInActivity.this, MyAdsActivity.class);
                startActivity(intent);
            }
        });
    }



}
