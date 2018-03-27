package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button login = (Button) findViewById(R.id.loginBtn);
    EditText email = (EditText) findViewById(R.id.editTxtUsername);
    EditText password = (EditText) findViewById(R.id.editTxtPassword);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);


        //EditText username = (EditText) findViewById(R.id.editTxtUsername);
        //EditText password = (EditText) findViewById(R.id.editTxtPassword);

        Button registerBtn = (Button) findViewById(R.id.registreraBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });



        //login.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        EditText username = (EditText) findViewById(R.id.editTxtUsername);
        //        EditText password = (EditText) findViewById(R.id.editTxtPassword);
        //        String user = username.getText().toString();
        //        String losenord = password.getText().toString();
//
        //        String pass = myDb.searchPass(user);
        //        if(losenord.equals(pass)){
//
        //            Intent loggedIn = new Intent(MainActivity.this, LoggedInActivity.class);
        //
        //            //Kör metoden för att spara ner username.
        //            saveUserInformation();
        //            startActivity(loggedIn);
        //        }
        //        else {
        //            Toast.makeText(MainActivity.this,"Username och password don't match!", Toast.LENGTH_LONG).show();
        //        }
        //    }
        //});
    }

    public void logIn(){

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);

                            Intent regIntent = new Intent(MainActivity.this,LoggedInActivity.class);
                            startActivity(regIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
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
