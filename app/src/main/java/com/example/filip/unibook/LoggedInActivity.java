package com.example.filip.unibook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        String username = getIntent().getStringExtra("Welcome");
        TextView tv = (TextView) findViewById(R.id.txtUser);
        tv.setText(username);

    }

    public void goToProfile(View view){
        Intent intent = new Intent(LoggedInActivity.this, AddProfilePictureActivity.class);
        startActivity(intent);
    }
}
