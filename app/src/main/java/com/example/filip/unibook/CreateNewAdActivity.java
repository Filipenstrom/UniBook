package com.example.filip.unibook;

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
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class CreateNewAdActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    Context context = this;
    DatabaseHelper myDb;
    EditText titel, pris, info, isdn;
    TextView kurs, program;
    Button button, listProgramBtn, listCourseBtn;
    private static final int PICK_IMAGE = 100;
    ImageView imageView;
    Uri imageUri;
    ListView listView;
    byte[] bytes = null;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_ad);

         myDb = new DatabaseHelper(this);
         titel = (EditText) findViewById(R.id.editTxtTitel);
         pris = (EditText) findViewById(R.id.editTxtPris);
         info = (EditText) findViewById(R.id.editTxtInfo);
         isdn = (EditText) findViewById(R.id.editTxtISDN);
         program = findViewById(R.id.txtViewProgram);
         kurs = findViewById(R.id.textViewCourses);
         button = (Button) findViewById(R.id.btnBildKnapp);
         listProgramBtn = (Button) findViewById(R.id.btnGoToProgram);
         listCourseBtn = (Button) findViewById(R.id.btnKurs);
         imageView = (ImageView) findViewById(R.id.imgViewBokbild);


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
                startActivity(intent);
            }
        });

         listCourseBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(CreateNewAdActivity.this, ListAllCoursesFromProgramActivity.class);
                 String[] extras = new String[2];
                 extras[0] = "2";
                 intent.putExtra("extras", extras);
                 intent.putExtra("programNamn", program.getText().toString());
                 startActivityForResult(intent, 2);
             }
         });

        Intent intent = getIntent();
        String programNamn = intent.getStringExtra("programInfoIntent");
        TextView txtProgram = (TextView) findViewById(R.id.txtViewProgram);
        txtProgram.setText(programNamn);
        txtProgram.setVisibility(View.VISIBLE);

        Intent kursIntent = getIntent();
        String kursNamn = kursIntent.getStringExtra("kursInfoIntent");
        TextView txtKurs = (TextView) findViewById(R.id.textViewCourses) ;
        txtKurs.setText(kursNamn);
        txtKurs.setVisibility(View.VISIBLE);

    }

    //Metod som skapar en ny annons för den inloggade
    public void createAdbajs(){

        Button bildKnapp = (Button) findViewById(R.id.btnSpara);
        bildKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String boktitel = titel.getText().toString();
                String bokPris = pris.getText().toString();
                String bokInfo = info.getText().toString();
                String bokISDN = isdn.getText().toString();
                TextView program = (TextView) findViewById(R.id.txtViewProgram);
                String bokTillhorProgram = program.getText().toString();
                String bokTillhorKurs = kurs.getText().toString();

                SharedPreferences prefs = new SharedPreferences(context);
                User id = myDb.getUser(prefs.getusername());

                if(boktitel.trim().equals("") || bokPris.trim().equals("") || bokInfo.trim().equals("") || bokISDN.trim().equals("") ||  bokTillhorKurs.trim().equals("") || bytes == null) {
                    Toast.makeText(CreateNewAdActivity.this,"Alla fält måste vara ifyllda", Toast.LENGTH_LONG).show();
                }
                else{
                    boolean isInserted = myDb.insertAd(boktitel, bokPris, bokInfo, bokISDN, bokTillhorProgram, bokTillhorKurs, id.getId(), bytes);

                    if(isInserted == true){
                        Toast.makeText(CreateNewAdActivity.this,"Annons skapad", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateNewAdActivity.this, MyAdsActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(CreateNewAdActivity.this, "Något gick fel", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void createAd(View view) {
        FirebaseUser user = mAuth.getCurrentUser();

        CollectionReference userRef = rootRef.collection("Ads");
        //CollectionReference userRef = rootRef.collection("User").document();

        String bokTitel = titel.getText().toString();
        String bokPris = pris.getText().toString();
        String bokInfo = info.getText().toString();
        String bokISDN = isdn.getText().toString();
        TextView program = (TextView) findViewById(R.id.txtViewProgram);
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

        if(!bokTitel.trim().equals("") || !bokPris.trim().equals("") || !bokInfo.trim().equals("") || !bokISDN.trim().equals("") ||  !bokTillhorKurs.trim().equals("")) {
            userRef.document()
                    .set(mapOne)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
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
        else{
                Toast.makeText(CreateNewAdActivity.this, "Något gick fel", Toast.LENGTH_LONG).show();
        }
    }

    public void choseImg(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, PICK_IMAGE);
    }

    //Metod som fäster ens valda bild i en imageView
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

        if(resultCode == 2){
            kurs.setText(data.getStringExtra("kursInfoIntent"));
        }
    }
}
