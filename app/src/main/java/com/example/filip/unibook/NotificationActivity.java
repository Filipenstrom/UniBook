package com.example.filip.unibook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    TextView addedNotis;
    TextView newNotis;
    TextView newNotisCourse;
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

        usersNotices();

        Button btnaddNotis =  findViewById(R.id.btnaddNotis);
        Button btnaddNotisCourse =  findViewById(R.id.btnAddNotisCourse);

        btnaddNotis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotisProg();
            }
        });

        btnaddNotisCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotisCourse();
            }
        });
    }

    //Spara ner en ny notis. Om det inte finns ett program som heter så som användaren skrivit in ska det inte sparas.
    public void saveNotisProg(){
        List<Program> allprogram = db.getPrograms();
        List<Ad> allads = db.getAllAds("");

        if(allads != null){

        for(int i = 0;i<allprogram.size();i++) {
            if (allprogram.get(i).getName().equals(newNotis.getText().toString().trim())) {
                db.addNotis(newNotis.getText().toString(), user.getId().toString(), allads.size());
                usersNotices();
                Toast.makeText(NotificationActivity.this, "En ny notis har sparats.", Toast.LENGTH_LONG).show();
                break;
            } else if (allprogram.size() == (i + 1)) {
                Toast.makeText(NotificationActivity.this, "Det finns inget program som heter så.", Toast.LENGTH_LONG).show();
            }
          }
        }
    }

    //Spara ner en ny notis. Om det inte finns en kurs som heter så som användaren skrivit in ska det inte sparas.
    public void saveNotisCourse(){
        List<Course> allcourses = db.getCourses("");
        List<Ad> allads = db.getAllAds("");



        for(int i = 0;i<allcourses.size();i++) {
            if (allcourses.get(i).getName().equals(newNotis.getText().toString().trim())) {
                db.addNotis(newNotis.getText().toString(), user.getId().toString(), allads.size());
                usersNotices();
                Toast.makeText(NotificationActivity.this, "En ny notis har sparats.", Toast.LENGTH_LONG).show();
                break;
            } else if (allcourses.size() == (i + 1)) {
                Toast.makeText(NotificationActivity.this, "Det finns ingen kurs som heter så.", Toast.LENGTH_LONG).show();
            }
          }

    }

    //Lista redan sparade notiser som användaren har gjort.
    public void usersNotices(){
        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sp = new SharedPreferences(this);
        User user = db.getUser(sp.getusername());
        List<String> noti = db.getNotis(user.getId());
        String allnoti = "";

        if(noti.size() > 0) {
            for (int i = 0; i < noti.size(); i++) {
                allnoti += noti.get(i) + ", ";
            }
        } else{
            allnoti = "Du har inga sparade notiser.";
        }
        addedNotis.setText(allnoti);
    }
}
