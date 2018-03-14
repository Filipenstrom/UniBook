package com.example.filip.unibook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    TextView addedNotis;
    TextView newNotis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        addedNotis = findViewById(R.id.txtnotisText);
        newNotis = findViewById(R.id.etnotisAd);
        setNotis();
    }

    public void setNotis(){
        Button btnaddNotis =  findViewById(R.id.btnaddNotis);
        btnaddNotis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedNotis.setText(newNotis.getText().toString());
            }
        });
    }
}
