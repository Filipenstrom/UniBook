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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = "Bra meddelande";
    EditText editName, editSurname, editEmail, editPassword, editAdress, editPhone, editSchool;
    ImageView imageView;
    Button button;
    Button changePic;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    byte[] bytes;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

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

        insertUserInformation();

        //setByteIfUserDontChangePic();

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

                        //pic.setImageBitmap(BitmapFactory.decodeByteArray(chosenAd.getPic(), 0, chosenAd.getPic().length));

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

    public void save(View view) {

        DocumentReference docRef = rootRef.collection("Users").document(user.getUid().toString());
        docRef.update("name", editName.getText().toString());
        docRef.update("surname", editSurname.getText().toString());
        docRef.update("email", editEmail.getText().toString());
        docRef.update("adress", editAdress.getText().toString());
        docRef.update("phone", editPhone.getText().toString());
        docRef.update("school", editSchool.getText().toString());

        Intent intent = new Intent(EditProfileActivity.this, ProfilePageActivity.class);
        startActivity(intent);

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
