package com.example.filip.unibook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editSurname, editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = (EditText) findViewById(R.id.editTxtName);
        editSurname = (EditText) findViewById(R.id.editTxtSurname);
        editEmail = (EditText) findViewById(R.id.editTxtMail);
        editPassword = (EditText) findViewById(R.id.editTxtPass);
       // imageView = (ImageView) findViewById(R.id.ivProfile);
    }
}
