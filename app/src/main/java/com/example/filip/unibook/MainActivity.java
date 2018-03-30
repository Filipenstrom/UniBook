package com.example.filip.unibook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button loginBtn, registerBtn;
    EditText email, password;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registreraBtn);
        email = findViewById(R.id.editTxtUsername);
        password = findViewById(R.id.editTxtPassword);
        pbar = findViewById(R.id.pBarLogin);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    public void logIn(){
        pbar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("message", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent logInIntent = new Intent(MainActivity.this, LoggedInActivity.class);
                            pbar.setVisibility(View.INVISIBLE);
                            startActivity(logInIntent);
                            //updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w("message", "signInWithEmail:failure", task.getException());

                            if(task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                pbar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Fel lösenord",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                pbar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}