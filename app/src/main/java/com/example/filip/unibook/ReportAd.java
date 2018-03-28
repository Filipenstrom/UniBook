package com.example.filip.unibook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ReportAd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_ad);

        DatabaseHelper db = new DatabaseHelper(this);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        Ad ad = db.getAd(id);

        TextView adTitle = findViewById(R.id.txtReportAdName);

        adTitle.setText(ad.getTitle());
    }
}
