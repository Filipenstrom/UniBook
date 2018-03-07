package com.example.filip.unibook;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.prefs.Preferences;


public class CreateNewAdActivity extends AppCompatActivity {
    Context context = this;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_ad);
        myDb = new DatabaseHelper(this);

        Button btnSpara = (Button) findViewById(R.id.btnSpara);

         btnSpara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText titel = (EditText) findViewById(R.id.editTxtTitel);
                EditText pris = (EditText) findViewById(R.id.editTxtPris);
                EditText info = (EditText) findViewById(R.id.editTxtInfo);
                EditText isdn = (EditText) findViewById(R.id.editTxtISDN);
                EditText program = (EditText) findViewById(R.id.editTxtProgram);
                EditText kurs = (EditText) findViewById(R.id.editTxtKurs);

                String boktitel = titel.getText().toString();
                String bokPris = pris.getText().toString();
                String bokInfo = info.getText().toString();
                String bokISDN = isdn.getText().toString();
                String bokTillhorProgram = program.getText().toString();
                String bokTillhorKurs = kurs.getText().toString();


                SharedPreferences prefs = new SharedPreferences(context);
                String [] id = myDb.getUser(prefs.getusername());

                boolean isInserted = myDb.insertAd(boktitel, bokPris, bokInfo, bokISDN, bokTillhorProgram, bokTillhorKurs, id[0]);


                if(isInserted == true){
                    Toast.makeText(CreateNewAdActivity.this,"Data Inserted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(CreateNewAdActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
                }
            }
        });

    }




}
