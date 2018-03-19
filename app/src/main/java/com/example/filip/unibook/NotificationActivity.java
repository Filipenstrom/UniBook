package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    TextView addedNotis;
    TextView newNotis;
    DatabaseHelper db;
    SharedPreferences sp;
    User user;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        addedNotis = findViewById(R.id.txtnotisText);
        newNotis = findViewById(R.id.etnotisAd);
        db = new DatabaseHelper(this);
        sp = new SharedPreferences(this);
        user = db.getUser(sp.getusername());
        setNotis();
    }

    public void setNotis(){
        Button btnaddNotis =  findViewById(R.id.btnaddNotis);
        btnaddNotis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addNotis(newNotis.getText().toString(), user.getId().toString(), 0);
                List<String> notisText = db.getNotis(Integer.parseInt(user.getId()));
                addedNotis.setText(notisText.get(0));
            }
        });
    }
}
