package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editSurname, editEmail, editPassword, editAdress, editPhone, editSchool;
    ImageView imageView;
    Button button;
    Button changePic;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.etFirstname);
        editSurname =  findViewById(R.id.etSurname);
        editEmail =  findViewById(R.id.etMail);
        editPhone =  findViewById(R.id.etphonenumber);
        editSchool = findViewById(R.id.etSchool);
        editAdress =  findViewById(R.id.etAdress);
        button = findViewById(R.id.btnSpara);
        changePic = findViewById(R.id.btnChangePic);

        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sp = new SharedPreferences(this);
        User user = db.getUser(sp.getusername());
        insertUserInformation(user.getMail());

        setByteIfUserDontChangePic();

        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImg();
            }
        });
    }

    public void insertUserInformation(String username){
        DatabaseHelper db = new DatabaseHelper(this);
        User userInformation = db.getUser(username);
        SharedPreferences sharedPreferences = new SharedPreferences(this);

        imageView = findViewById(R.id.ivProfile);

        editName.setText(userInformation.getName());
        editSurname.setText(userInformation.getSurname());
        editEmail.setText(userInformation.getMail());
        editAdress.setText(userInformation.getAdress());
        editSchool.setText(userInformation.getSchool());
        editPhone.setText(Integer.toString(userInformation.getPhone()));
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(userInformation.getPic(), 0, userInformation.getPic().length));
    }

    public void save(View view) {
        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sp = new SharedPreferences(this);
        User id = db.getUser(sp.getusername());

        //Ifall användaren byter mail måste sharedpreferences variabeln för username ändras.
        Intent intent = new Intent(EditProfileActivity.this, ProfilePageActivity.class);

        if(editEmail.getText().toString().equals(id.getMail())) {

            db.updateUser(id.getId(), editName.getText().toString(), editSurname.getText().toString(), editEmail.getText().toString(), bytes);

            startActivity(intent);
        }
        else{
            saveUserInformation();
            db.updateUser(id.getMail(), editName.getText().toString(), editSurname.getText().toString(), editEmail.getText().toString(), bytes);
            startActivity(intent);
        }
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

    public void saveUserInformation(){
        SharedPreferences sp = new SharedPreferences(this);
        sp.setusername(editEmail.getText().toString());
    }

    //Så det inte krachar om anvndaren inte byter profilbild.
    public void setByteIfUserDontChangePic(){
        imageView.setImageURI(imageUri);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        bytes = imageInByte;
    }

}