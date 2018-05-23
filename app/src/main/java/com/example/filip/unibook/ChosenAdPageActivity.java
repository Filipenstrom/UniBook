package com.example.filip.unibook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.List;
import java.util.UUID;

public class ChosenAdPageActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    String id, imageRandomNumber, imageId;
    TextView title;
    TextView pris;
    TextView ISDN;
    TextView info;
    TextView program;
    public static final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    TextView kurs;
    ImageView pic, delete;
    Context context = this;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;
    Button changeImg, listCourseBtn, listProgramBtn;
    ConstraintLayout bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_ad_page);

        title = findViewById(R.id.etchosenAdTitle);
        pris = findViewById(R.id.etchosenAdPris);
        ISDN = findViewById(R.id.etchosenAdISDN);
        info = findViewById(R.id.etchosenAdInfo);
        program = findViewById(R.id.txtProgram);
        kurs = findViewById(R.id.txtCourse);
        pic = findViewById(R.id.ivchosenAdImage);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        changeImg = findViewById(R.id.btnChangeImgChosenAd);
        bottomLayout = findViewById(R.id.bottomlayout);
        delete = findViewById(R.id.ivdelete);
        listProgramBtn = findViewById(R.id.listProgramBtn);
        listCourseBtn = findViewById(R.id.listCourseBtn);

        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseImg();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        getAd();

        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        listProgramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChosenAdPageActivity.this, ListAllProgramsActivity.class);
                intent.putExtra("activityCode", 2);
                startActivityForResult(intent, 1);
            }
        });

        listCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChosenAdPageActivity.this, ListAllCoursesFromProgramActivity.class);
                String[] extras = new String[2];
                extras[0] = "3";
                extras[1] = program.getText().toString();
                intent.putExtra("extras", extras);
                startActivityForResult(intent, 2);
            }
        });

        deleteAd();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        Intent intent = new Intent(getApplicationContext(), MyAdsActivity.class);
        startActivity(intent);
    }


    //Metod för att hämta den valda annonsen
    public void getAd(){
        final DocumentReference adsRef = rootRef.collection("Ads").document(id);
        adsRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();

                            ISDN.setText(doc.getString("ISDN"));
                            kurs.setText(doc.getString("course"));
                            info.setText(doc.getString("info"));
                            pris.setText(doc.getString("price"));
                            program.setText(doc.getString("program"));
                            title.setText(doc.getString("title"));
                            imageId = doc.getString("imageId");

                            setImage(doc.getString("imageId"), pic);



                        }
                        else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //Metod för att ta bort vald annons
    public void deleteAd(){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.collection("Ads").document(id).delete();
                CollectionReference colRef = rootRef.collection("Favourites");
                storage.getReferenceFromUrl(imageId).delete();

                colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                        for(int i = 0; i< list.size();i++){
                            DocumentSnapshot doc = list.get(i);
                            if(doc.getString("adId").equals(id)){
                                rootRef.collection("Favourites").document(doc.getId()).delete();
                            }
                        }

                        Intent intent = new Intent(ChosenAdPageActivity.this, MyAdsActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    //Metod för validering av textfält
    public boolean validate(){

        boolean valid = true;
        if(title.length() > 50 || title.getText().toString().trim().equals("")){
            title.setError("Fältet får inte vara tomt eller ha mer än 50 tecken.");
            valid = false;
        }
        if(pris.length() > 50 || pris.getText().toString().trim().equals("") || !TextUtils.isDigitsOnly(pris.getText().toString())){
            pris.setError("Fältet får inte vara tomt, får inte innehålla mer än 50 tecken och måste vara siffror.");
            valid = false;
        }
        if(info.length() > 100 || info.getText().toString().trim().equals("")){
            info.setError("Fältet får inte vara tomt eller ha mer än 100 tecken.");
            valid = false;
        }
        if(ISDN.length() > 30 || ISDN.getText().toString().trim().equals("")){
            ISDN.setError("Fältet får inte vara tomt eller ha mer än 30 tecken.");
            valid = false;
        }

        return valid;
    }

    //Metod som fäster bilden till annonsen i en image view.
    public void setImage(String imageId, final ImageView view){

        StorageReference storageRef = storage.getReferenceFromUrl(imageId);

        final long ONE_MEGABYTE = 1024 * 1024;

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                view.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Error", exception.getMessage());
            }
        });
    }

    //Metod för att uppdatera den valda annonsen
    public void updateData() {

        if (validate()) {
            DocumentReference docRef = rootRef.collection("Ads").document(id);
            docRef.update("title", title.getText().toString());
            docRef.update("ISDN", ISDN.getText().toString());
            docRef.update("course", kurs.getText().toString());
            docRef.update("info", info.getText().toString());
            docRef.update("price", pris.getText().toString());
            docRef.update("program", program.getText().toString());
            uploadImage(docRef);
            Intent intent = new Intent(ChosenAdPageActivity.this, MyAdsActivity.class);
            startActivity(intent);
        }
    }

    //Metod för att välja profilbild
    public void choseImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Metod som laddar upp den valda bilden till databasen
    private void uploadImage(final DocumentReference docRef) {

        if(filePath != null)
        {
            StorageReference storageRef = storage.getReferenceFromUrl(imageId);

            imageRandomNumber = UUID.randomUUID().toString();

            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    StorageReference ref = storageReference.child("images/"+ imageRandomNumber);
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    docRef.update("imageId", "gs://unibook-41e0f.appspot.com/images/" + imageRandomNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ChosenAdPageActivity.this, "Annonsen uppdaterad",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChosenAdPageActivity.this, MyAdsActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChosenAdPageActivity.this, "Något gick fel. Vänligen försök igen.", Toast.LENGTH_SHORT).show();
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
                pic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if(resultCode == 1) {
            String programName = data.getStringExtra("programInfoIntent");
            program.setText(programName);
            kurs.setText("");
        }

        if(resultCode == 2) {
            String[] kursName = data.getStringArrayExtra("kursInfoIntent");
            kurs.setText(kursName[1]);
        }
    }
}