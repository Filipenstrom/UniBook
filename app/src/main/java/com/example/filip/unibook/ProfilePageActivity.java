package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
        User userInformation = db.getUser(username);
        SharedPreferences sharedPreferences = new SharedPreferences(this);

        TextView name = (TextView) findViewById(R.id.txtNamn);
        TextView surname = (TextView) findViewById(R.id.txtMail);
        ImageView imageView = findViewById(R.id.profil);

        name.setText(userInformation.getName() + " " + userInformation.getSurname());
        surname.setText(userInformation.getSurname());
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(userInformation.getPic(), 0, userInformation.getPic().length));
    }

    public void goToEditProfile(View view){
        Intent intent = new Intent(ProfilePageActivity.this, EditProfileActivity.class);
        startActivity(intent);

    }
}
