package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText editName, editSurname, editEmail, editPassword, editAdress, editPhone, editSchool;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView imageView;
    Button button;
    byte[] bytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editTxtName);
        editSurname = findViewById(R.id.editTxtSurname);
        editEmail = findViewById(R.id.editTxtMail);
        editPassword = findViewById(R.id.editTxtPass);
        button = findViewById(R.id.btnImage);
        imageView = findViewById(R.id.ivProfile);
        editAdress = findViewById(R.id.edittxtAdress);
        editPhone = findViewById(R.id.edittxtPhone);
        editSchool = findViewById(R.id.edittxtSchool);
        mAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImg();
            }
        });
        register();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void register(){
        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
    }

    public void addUser(){
        final String namn = editName.getText().toString();
        final String surname = editSurname.getText().toString();
        final String email = editEmail.getText().toString();
        final String password = editPassword.getText().toString();
        final String adress = editAdress.getText().toString();
        final String phone = editPhone.getText().toString();
        final String school = editSchool.getText().toString();

        if(namn.trim().equals("") || surname.trim().equals("") || email.trim().equals("") || password.trim().equals("") || adress.trim().equals("") || phone.trim().equals("") || school.trim().equals("")) {

            Toast.makeText(RegisterActivity.this,"Alla fält måste vara ifyllda", Toast.LENGTH_LONG).show();
        }else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("message", "createUserWithEmail:success");

                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseHelper helper = new FirebaseHelper();

                                helper.insertUser(user.getUid().toString(), namn, surname, email, adress, phone, school );

                                Toast.makeText(RegisterActivity.this, "Registrering lyckades",
                                        Toast.LENGTH_SHORT).show();

                                Intent logInIntent = new Intent(RegisterActivity.this, LoggedInActivity.class);
                                startActivity(logInIntent);
                            } else {

                                Log.w("Exception", "createUserWithEmail:failure", task.getException());

                                if(task.getException().getMessage().equals("The email address is badly formatted.")) {

                                    //Om emailen redan existerar i databasem, visa meddelande för användaren.
                                    Toast.makeText(RegisterActivity.this, "Det måste vara en giltig e-mail adress",
                                            Toast.LENGTH_LONG).show();
                                }else if(task.getException().getMessage().equals("The email address is already in use by another account.")) {

                                    //Om emailen redan existerar i databasem, visa meddelande för användaren.
                                    Toast.makeText(RegisterActivity.this, "Denna e-mail finns redan registrerad",
                                            Toast.LENGTH_LONG).show();
                                }else if(task.getException().getMessage().equals("The given password is invalid. [ Password should be at least 6 characters ]")) {

                                    //Om inloggning misslyckas av andra skäl, visa meddelande för användaren.
                                    Toast.makeText(RegisterActivity.this, "Lösenordet måste vara minst 6 tecken.",
                                            Toast.LENGTH_SHORT).show();
                                }else {

                                    //Om inloggning misslyckas av andra skäl, visa meddelande för användaren.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }

    //Metod för att välja profilbild
    public void choseImg(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, PICK_IMAGE);
    }

    //Metod som fäster den valda bilden i en imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();
            bytes = imageInByte;
        }
    }
}