package com.example.filip.unibook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
/**
 * Created by Ludvig on 2018-03-06.
 *
 */

public class ProfilePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sp = new SharedPreferences(this);
        insertUserInformation(sp.getusername());


    }

    //Fyller profilen med information om den som är inloggad.
    public void insertUserInformation(String username){
        DatabaseHelper db = new DatabaseHelper(this);
        String[] userInformation = db.getUser(username);

        TextView name = (TextView) findViewById(R.id.txtNamn);
        TextView surname = (TextView) findViewById(R.id.txtMail);

        name.setText(userInformation[0] + " " + userInformation[1]);
        surname.setText(userInformation[2]);

    }
}
