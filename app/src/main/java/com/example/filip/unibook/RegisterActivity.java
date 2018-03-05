package com.example.filip.unibook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText editName, editSurname, editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myDb = new DatabaseHelper(this);

        editName = (EditText) findViewById(R.id.editTxtName);
        editSurname = (EditText) findViewById(R.id.editTxtSurname);
        editEmail = (EditText) findViewById(R.id.editTxtMail);
        editPassword = (EditText) findViewById(R.id.editTxtPass);

        register();
    }

    public void register(){

        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String namn = editName.getText().toString();
                String surname = editSurname.getText().toString();
                String email = editEmail.getText().toString();
                String pass = editPassword.getText().toString();

                boolean isInserted = myDb.insertUser(namn, surname, email, pass);

                if(isInserted == true){
                    Toast.makeText(RegisterActivity.this,"Data Inserted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
