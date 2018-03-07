package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);


        //EditText username = (EditText) findViewById(R.id.editTxtUsername);
        //EditText password = (EditText) findViewById(R.id.editTxtPassword);
        Button login = (Button) findViewById(R.id.loginBtn);
        Button registerBtn = (Button) findViewById(R.id.registreraBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.editTxtUsername);
                EditText password = (EditText) findViewById(R.id.editTxtPassword);
                String user = username.getText().toString();
                String losenord = password.getText().toString();

                String pass = myDb.searchPass(user);
                if(losenord.equals(pass)){

                    String namn = myDb.getName(user);
                    Intent inloggadInt = new Intent(MainActivity.this, LoggedInActivity.class);
                    inloggadInt.putExtra("Welcome", namn);
                    //Kör metoden för att spara ner username.
                    saveUserInformation();
                    startActivity(inloggadInt);
                }
                else {
                    Toast.makeText(MainActivity.this,"Username och password don't match!", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    //Kod för att spara ner username så man kan nå det i alla aktiviteter.
    public void saveUserInformation(){
        EditText username = (EditText) findViewById(R.id.editTxtUsername);
        SharedPreferences sp = new SharedPreferences(this);
        sp.setusername(username.getText().toString());


    }

}
