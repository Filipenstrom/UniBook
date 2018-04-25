package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
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

/**
 * Created by Ludvig on 2018-03-06.
 *
 */

public class ProfilePageActivity extends AppCompatActivity {

    public static final String TAG = "Bra meddelande";
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    TextView name, mail, adress, phone, school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        name = findViewById(R.id.txtNamn);
        mail =  findViewById(R.id.txtMail);
        adress =  findViewById(R.id.txtAdress);
        phone =  findViewById(R.id.txtTele);
        school =  findViewById(R.id.txtSkola);
        ImageView imageView = findViewById(R.id.profil);

        insertUserInformation();
    }

    public void goToEditProfile(View view){
        Intent intent = new Intent(ProfilePageActivity.this, EditProfileActivity.class);
        startActivity(intent);

    }


    public void insertUserInformation() {
        DocumentReference docRef = rootRef.collection("Users").document(user.getUid().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        name.setText(document.getString("name") + " " + document.getString("surname"));
                        mail.setText(document.getString("email"));
                        adress.setText(document.getString("adress"));
                        phone.setText(document.getString("phone"));
                        school.setText(document.getString("school"));

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
}
