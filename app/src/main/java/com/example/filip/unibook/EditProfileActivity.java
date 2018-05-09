package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = "Bra meddelande";
    EditText editName, editSurname, editEmail, editAdress, editPhone, editSchool;
    TextView changePic;
    ImageView imageView;
    Button button;
    private Uri filePath;
    public static final int PICK_IMAGE_REQUEST = 71;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageRandomNumber, imageId;
    String regexEmail = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})";

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
        button = findViewById(R.id.btnSave);
        changePic = findViewById(R.id.txtChangePic);
        imageView = findViewById(R.id.ivProfile);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        insertUserInformation();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImg();
            }
        });
    }

    public void insertUserInformation() {
        DocumentReference docRef = rootRef.collection("Users").document(user.getUid().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        editName.setText(document.getString("name"));
                        editSurname.setText(document.getString("surname"));
                        editEmail.setText(document.getString("email"));
                        editAdress.setText(document.getString("adress"));
                        editPhone.setText(document.getString("phone"));
                        editSchool.setText(document.getString("school"));
                        imageId = document.getString("imageId");

                        setImage(imageId);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void save() {

        if(validate()){

            DocumentReference docRef = rootRef.collection("Users").document(user.getUid().toString());
            docRef.update("name", editName.getText().toString());
            docRef.update("surname", editSurname.getText().toString());
            docRef.update("email", editEmail.getText().toString());
            docRef.update("adress", editAdress.getText().toString());
            docRef.update("phone", editPhone.getText().toString());
            docRef.update("school", editSchool.getText().toString());

            uploadImage(docRef);

            Toast.makeText(EditProfileActivity.this, "Profilen uppdaterad",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(EditProfileActivity.this, ProfilePageActivity.class);
            startActivity(intent);
        }
    }

    public boolean validate(){

        boolean valid = true;

        if(editName.length() > 15 || editName.getText().toString().trim().equals("")) {
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

        if(editAdress.length() > 25 || editAdress.getText().toString().trim().equals("")){
            editAdress.setError("Fältet får inte vara tomt eller ha mer än 25 tecken.");
            valid = false;
        }
        if(editPhone.length() > 25 || editPhone.getText().toString().trim().equals("") || !TextUtils.isDigitsOnly(editPhone.getText().toString())){
            editPhone.setError("Fältet får inte vara tomt, inte ha mer än 25 tecken och får endast innehålla siffror.");
            valid = false;
        }
        if(editSchool.length() > 50 || editAdress.getText().toString().trim().equals("")){
            editSchool.setError("Fältet får inte vara tomt eller ha mer än 50 tecken.");
            valid = false;
        }

        return valid;
    }

    public void setImage(String imageId){

        StorageReference storageRef = storage.getReferenceFromUrl(imageId);

        final long ONE_MEGABYTE = 1024 * 1024;

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
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

    private void uploadImage(final DocumentReference docRef) {

        if(filePath != null)
        {
            StorageReference storageRef = storage.getReferenceFromUrl(imageId);

            imageRandomNumber = UUID.randomUUID().toString();

            storageRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            StorageReference ref = storageReference.child("images/"+ imageRandomNumber);
                            ref.putFile(filePath)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            docRef.update("imageId", "gs://unibook-41e0f.appspot.com/images/" + imageRandomNumber);
                                        }
                                    });
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