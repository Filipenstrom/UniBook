package com.example.filip.unibook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Ref;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "Bra meddelande";
    private Uri filePath;
    public static final int PICK_IMAGE_REQUEST = 71;
    private FirebaseAuth mAuth;
    EditText editName, editSurname, editEmail, editPassword, editAdress, editPhone, editSchool;
    TextView button;
    ImageView imageView;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageRandomNumber;
    String regexEmail = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editTxtName);
        editSurname = findViewById(R.id.editTxtSurname);
        editEmail = findViewById(R.id.editTxtMail);
        editPassword = findViewById(R.id.editTxtPass);
        button = findViewById(R.id.txtVäljProfilbild);
        imageView = findViewById(R.id.ivProfile);
        editAdress = findViewById(R.id.edittxtAdress);
        editPhone = findViewById(R.id.edittxtPhone);
        editSchool = findViewById(R.id.edittxtSchool);
        mAuth = FirebaseAuth.getInstance();


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImg();
            }
        });
        register();
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

    public boolean validate(){

        boolean valid = true;

        if(editName.length() > 15 || editName.getText().toString().trim().equals("")){
            editName.setError("Fältet får inte vara tomt eller ha mer än 15 tecken.");
            valid = false;
        }
        if(editSurname.length() > 20 || editSurname.getText().toString().trim().equals("")){
            editSurname.setError("Fältet får inte vara tomt, får inte innehålla mer än 20 tecken.");
            valid = false;
        }
        if(editEmail.length() > 50 || editEmail.getText().toString().trim().equals("")){
            editEmail.setError("Fältet får inte vara tomt eller ha mer än 50 tecken.");
            valid = false;
        }
        if(!editEmail.getText().toString().trim().matches(regexEmail)){
            editEmail.setError("Du måste skriva en giltig emailadress.");
            valid = false;
        }
        if(editPassword.length() > 15 || editPassword.getText().toString().trim().equals("")){
            editPassword.setError("Fältet får inte vara tomt eller ha mer än 15 tecken.");
            valid = false;
        }
        if(editAdress.length() > 25 || editAdress.getText().toString().trim().equals("")){
            editPassword.setError("Fältet får inte vara tomt eller ha mer än 25 tecken.");
            valid = false;
        }
        if(editPhone.length() > 25 || editPassword.getText().toString().trim().equals("") || !TextUtils.isDigitsOnly(editPhone.getText().toString())){
            editPassword.setError("Fältet får inte vara tomt, inte ha mer än 25 tecken och får endast innehålla siffror.");
            valid = false;
        }
        if(editSchool.length() > 50 || editAdress.getText().toString().trim().equals("")){
            editPassword.setError("Fältet får inte vara tomt eller ha mer än 50 tecken.");
            valid = false;
        }

        return valid;
    }


    public void addUser(){
        final String namn = editName.getText().toString();
        final String surname = editSurname.getText().toString();
        final String email = editEmail.getText().toString();
        final String password = editPassword.getText().toString();
        final String adress = editAdress.getText().toString();
        final String phone = editPhone.getText().toString();
        final String school = editSchool.getText().toString();

        /*if(namn.trim().equals("") || surname.trim().equals("") || email.trim().equals("") || password.trim().equals("") || adress.trim().equals("") || phone.trim().equals("") || school.trim().equals("")) {

            Toast.makeText(RegisterActivity.this,"Alla fält måste vara ifyllda", Toast.LENGTH_LONG).show();
        }else {*/

        if(validate()){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("message", "createUserWithEmail:success");

                                uploadImage();

                                createUser(namn, surname, email, adress, phone, school, password);
                            } else {

                                Log.w("Exception", "createUserWithEmail:failure", task.getException());

                                if(task.getException().getMessage().equals("The email address is badly formatted.")) {

                                    //Om emailen redan existerar i databasen, visa meddelande för användaren.
                                    Toast.makeText(RegisterActivity.this, "Det måste vara en giltig e-mail adress",
                                            Toast.LENGTH_LONG).show();
                                }else if(task.getException().getMessage().equals("The email address is already in use by another account.")) {

                                    //Om emailen redan existerar i databasen, visa meddelande för användaren.
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


    //Metod som lägger in data i Firestore om den skapade användaren.
    public void createUser(String namn, String surname, String email, String adress, String phone, String school, String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        CollectionReference userRef = rootRef.collection("Users");

        Map<String, Object> mapOne = new HashMap<>();
        mapOne.put("name", namn);
        mapOne.put("surname", surname);
        mapOne.put("email", email);
        mapOne.put("adress", adress);
        mapOne.put("phone", phone);
        mapOne.put("school", school);
        mapOne.put("password", password);
        mapOne.put("imageId", "gs://unibook-41e0f.appspot.com/images/" + imageRandomNumber);

        userRef.document(user.getUid().toString())
                .set(mapOne)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this, "Registrering lyckades", Toast.LENGTH_SHORT).show();
                        Intent logInIntent = new Intent(RegisterActivity.this, LoggedInActivity.class);
                        startActivity(logInIntent);
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    //Metod för att välja profilbild
    public void choseImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {

        if(filePath != null)
        {
            imageRandomNumber = UUID.randomUUID().toString();

            StorageReference ref = storageReference.child("images/"+ imageRandomNumber);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(RegisterActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(RegisterActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //Metod som fäster den valda bilden i en imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}