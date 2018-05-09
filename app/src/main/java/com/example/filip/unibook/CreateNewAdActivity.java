package com.example.filip.unibook;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.ref.Reference;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CreateNewAdActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    public static final int PICK_IMAGE_REQUEST = 71;
    Context context = this;
    EditText titel, pris, info, isdn;
    TextView kurs, program;
    Button button, listProgramBtn, listCourseBtn;
    ImageView imageView;
    private Uri filePath;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageRandomNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_ad);

        titel = findViewById(R.id.editTxtTitel);
        pris = findViewById(R.id.editTxtPris);
        info = findViewById(R.id.editTxtInfo);
        isdn = findViewById(R.id.editTxtISDN);
        program = findViewById(R.id.txtViewProgram);
        kurs = findViewById(R.id.textViewCourses);
        button = findViewById(R.id.btnBildKnapp);
        listProgramBtn = findViewById(R.id.btnGoToProgram);
        listCourseBtn = findViewById(R.id.btnKurs);
        imageView = findViewById(R.id.imgViewBokbild);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseImg();
            }
        });

        listProgramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewAdActivity.this, ListAllProgramsActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        listCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewAdActivity.this, ListAllCoursesFromProgramActivity.class);
                String[] extras = new String[2];
                extras[0] = "2";
                extras[1] = program.getText().toString();
                intent.putExtra("extras", extras);
                startActivityForResult(intent, 2);
            }
        });

        Intent intent = getIntent();
        String programNamn = intent.getStringExtra("programInfoIntent");
        TextView txtProgram =  findViewById(R.id.txtViewProgram);
        txtProgram.setText(programNamn);
        txtProgram.setVisibility(View.VISIBLE);
    }

    public boolean validate(){

        boolean valid = true;
        if(titel.length() > 50 || titel.getText().toString().trim().equals("")){
            titel.setError("Fältet får inte vara tomt eller ha mer än 50 tecken.");
            //Toast.makeText(CreateNewAdActivity.this, "Titel får inte vara tom eller ha mer än 50 tecken", Toast.LENGTH_SHORT).show();
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
        if(isdn.length() > 30 || isdn.getText().toString().trim().equals("")){
            isdn.setError("Fältet får inte vara tomt eller ha mer än 30 tecken.");
            valid = false;
        }

        return valid;
    }

    public void createAd(View view) {
        FirebaseUser user = mAuth.getCurrentUser();

        CollectionReference userRef = rootRef.collection("Ads");
        DocumentReference userRefLatest = rootRef.collection("Ads").document("latest");
        //CollectionReference userRef = rootRef.collection("User").document();

        String bokTitel = titel.getText().toString();
        String bokPris = pris.getText().toString();
        String bokInfo = info.getText().toString();
        String bokISDN = isdn.getText().toString();
        TextView program = findViewById(R.id.txtViewProgram);
        String bokTillhorProgram = program.getText().toString();
        String bokTillhorKurs = kurs.getText().toString();

        Map<String, Object> mapOne = new HashMap<>();
        mapOne.put("title", bokTitel);
        mapOne.put("price", bokPris);
        mapOne.put("info", bokInfo);
        mapOne.put("ISDN", bokISDN);
        mapOne.put("program", bokTillhorProgram);
        mapOne.put("course", bokTillhorKurs);
        mapOne.put("sellerId", user.getUid().toString());


        //if(!bokTitel.trim().equals("") || !bokPris.trim().equals("") || !bokInfo.trim().equals("") || !bokISDN.trim().equals("") ||  !bokTillhorKurs.trim().equals("")) {
          if(validate()){

            uploadImage();
            mapOne.put("imageId", "gs://unibook-41e0f.appspot.com/images/" + imageRandomNumber);
            userRef.document()
                    .set(mapOne)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateNewAdActivity.this, "Annons skapad", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Intent intent = new Intent(CreateNewAdActivity.this, MyAdsActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

              userRefLatest
                      .set(mapOne)
                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              Toast.makeText(CreateNewAdActivity.this, "Annons skapad", Toast.LENGTH_SHORT).show();
                              Log.d(TAG, "DocumentSnapshot successfully written!");
                              Intent intent = new Intent(CreateNewAdActivity.this, MyAdsActivity.class);
                              startActivity(intent);
                          }
                      })
                      .addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Log.w(TAG, "Error writing document", e);
                          }
                      });

        }


       /* else{
            Toast.makeText(CreateNewAdActivity.this, "Något gick fel", Toast.LENGTH_LONG).show();
        }*/
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

                            //Toast.makeText(CreateNewAdActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(CreateNewAdActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    public void choseImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Metod som fäster ens valda bild i en imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
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
        if(resultCode == 1){
            String programnamn = data.getStringExtra("programInfoIntent");
            program.setText(programnamn);
            program.setVisibility(View.VISIBLE);
        }
        if(resultCode == 2){
            String[] kursnamn = data.getStringArrayExtra("kursInfoIntent");
            kurs.setText(kursnamn[1]);
            kurs.setVisibility(View.VISIBLE);
        }
    }
}