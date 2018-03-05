package com.example.filip.unibook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        String username = getIntent().getStringExtra("Welcome");
        TextView tv = (TextView) findViewById(R.id.editTxtUsername);
        tv.setText(username);
    }
}
