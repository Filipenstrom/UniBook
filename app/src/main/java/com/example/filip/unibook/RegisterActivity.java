package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    DatabaseHelper myDb;
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

        myDb = new DatabaseHelper(this);

        editName = (EditText) findViewById(R.id.editTxtName);
        editSurname = (EditText) findViewById(R.id.editTxtSurname);
        editEmail = (EditText) findViewById(R.id.editTxtMail);
        editPassword = (EditText) findViewById(R.id.editTxtPass);
        button = (Button) findViewById(R.id.btnImage);
        imageView = (ImageView) findViewById(R.id.ivProfile);
        editAdress = findViewById(R.id.edittxtAdress);
        editPhone = findViewById(R.id.edittxtPhone);
        editSchool = findViewById(R.id.edittxtSchool);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImg();
            }
        });

        register();
    }

    //Metod för att registrera sig som användare
    public void register() {

        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String namn = editName.getText().toString();
                String surname = editSurname.getText().toString();
                String email = editEmail.getText().toString();
                String pass = editPassword.getText().toString();
                String adress = editAdress.getText().toString();
                String phone = editPhone.getText().toString();
                String school = editSchool.getText().toString();


                if(namn.trim().equals("") || surname.trim().equals("") || email.trim().equals("") || pass.trim().equals("") || bytes == null || adress.trim().equals("") || phone.trim().equals("") || school.trim().equals("")) {
                    Toast.makeText(RegisterActivity.this,"Alla fält måste vara ifyllda", Toast.LENGTH_LONG).show();
                }
                else{
                    boolean isInserted = myDb.insertUser(namn, surname, email, pass, bytes, adress, Integer.parseInt(phone), school);

                    if (isInserted == true) {
                        Intent intent = new Intent(RegisterActivity.this, LoggedInActivity.class);
                        saveUserInformation();
                        startActivity(intent);
                        myDb.createProgram();
                        myDb.createCourse();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Denna e-mail finns redan registrerad", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
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

    //Kod för att spara ner username så man kan nå det i alla aktiviteter.
    public void saveUserInformation(){
        EditText username = findViewById(R.id.editTxtMail);
        SharedPreferences sp = new SharedPreferences(this);
        sp.setusername(username.getText().toString());
    }

}

