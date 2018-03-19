package com.example.filip.unibook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

public class ReportAd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_ad);

        final DatabaseHelper db = new DatabaseHelper(this);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        final Ad ad = db.getAd(id);

        TextView adTitle = findViewById(R.id.txtReportAdName);
        TextView adID = findViewById(R.id.txtReportAdID);
        final EditText authorName = findViewById(R.id.txtReportAuthorName);
        final EditText authorMail = findViewById(R.id.txtReportAdAuthorMail);
        final EditText message = findViewById(R.id.txtReportAdMessage);

        SharedPreferences sp = new SharedPreferences(this);
        final User user = db.getUser(sp.getusername());


        adTitle.setText(ad.getTitle());
        adID.setText(ad.getId());
        String fullName = user.getName() + " " + user.getSurname();
        authorName.setText(fullName);
        authorMail.setText(user.getMail());

        Button reportBtn = findViewById(R.id.btnReportAd);

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.
            }
        });
    }
}
