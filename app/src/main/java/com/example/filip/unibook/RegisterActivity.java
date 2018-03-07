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
    EditText editName, editSurname, editEmail, editPassword;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView imageView;
    Button button;
    byte[] bytes;

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImg();
            }
        });

        register();
    }

    public void register() {

        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String namn = editName.getText().toString();
                String surname = editSurname.getText().toString();
                String email = editEmail.getText().toString();
                String pass = editPassword.getText().toString();

                boolean isInserted = myDb.insertUser(namn, surname, email, pass, bytes);

                if (isInserted == true) {
                    Toast.makeText(RegisterActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                  // Intent intent = new Intent(RegisterActivity.this, AddProfilePictureActivity.class);
                  // startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


        public void choseImg(){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(photoPickerIntent, PICK_IMAGE);
        }

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

