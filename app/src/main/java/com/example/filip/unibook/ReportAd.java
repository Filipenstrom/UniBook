package com.example.filip.unibook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ReportAd extends AppCompatActivity {

    private TextView adTitle;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private DatabaseReference mChildReference = mRootReference.child("users");
    private DatabaseReference mittBarn = mRootReference.child("users").child("1").child("adress");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_ad);

        writeUserData();

        adTitle = findViewById(R.id.txtAdTitle);

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
        final String fullName = user.getName() + " " + user.getSurname();
        authorName.setText(fullName);
        authorMail.setText(user.getMail());
        Button reportBtn = findViewById(R.id.btnReportSendReportAd);

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isInserted = db.insertReport(ad.getTitle(), authorName.getText().toString(), authorMail.getText().toString(), message.getText().toString(), Integer.parseInt(user.getId()), Integer.parseInt(ad.getId()));

                if(isInserted == true){
                    Toast.makeText(ReportAd.this,"Anmälan skapad", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(ReportAd.this, "Något gick fel", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void writeUserData() {

        //Balle bajs = new Balle();
//
        //mChildReference.child("7").setValue(bajs);
        //mChildReference.push().setValue(bajs);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mittBarn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String message = "";

                message = dataSnapshot.getValue(String.class);

                adTitle.setText(message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
