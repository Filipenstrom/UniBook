package com.example.filip.unibook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ProfilePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        DatabaseHelper db = new DatabaseHelper(this);

    }
}
