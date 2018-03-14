package com.example.filip.unibook;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


public class CreateNewAdActivity extends AppCompatActivity {
    Context context = this;
    DatabaseHelper myDb;
    EditText titel, pris, info, isdn, program, kurs;
    Button button, listProgramBtn, listCourseBtn;
    private static final int PICK_IMAGE = 100;
    ImageView imageView;
    Uri imageUri;
<<<<<<< HEAD

    ListView listView;

=======
    ListView listView;
>>>>>>> 07fdf7e3afe8f0ae94c5c83c49d90a8d35fe85c4
    byte[] bytes = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_ad);

        myDb = new DatabaseHelper(this);
         titel = (EditText) findViewById(R.id.editTxtTitel);
         pris = (EditText) findViewById(R.id.editTxtPris);
         info = (EditText) findViewById(R.id.editTxtInfo);
         isdn = (EditText) findViewById(R.id.editTxtISDN);
<<<<<<< HEAD

=======
        //program = (EditText) findViewById(R.id.editTxtProgram);
>>>>>>> 07fdf7e3afe8f0ae94c5c83c49d90a8d35fe85c4
         kurs = (EditText) findViewById(R.id.editTxtKurs);
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

        Intent intent = getIntent();
        String programNamn = intent.getStringExtra("programNamn");
        TextView txtProgram = (TextView) findViewById(R.id.txtViewProgram);
        txtProgram.setText(programNamn);
        txtProgram.setVisibility(View.VISIBLE);



        createAd();


    }

    //Metod som skapar en ny annons för den inloggade
    public void createAd(){

        Button bildKnapp = (Button) findViewById(R.id.btnSpara);
        bildKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String boktitel = titel.getText().toString();
                String bokPris = pris.getText().toString();
                String bokInfo = info.getText().toString();
                String bokISDN = isdn.getText().toString();
                TextView program = (TextView) findViewById(R.id.txtProgram);
                String bokTillhorProgram = program.toString();
                TextView kurs = (TextView) findViewById(R.id.editTxtKurs);
                String bokTillhorKurs = kurs.toString();






                SharedPreferences prefs = new SharedPreferences(context);
                User id = myDb.getUser(prefs.getusername());

                if(boktitel.trim().equals("") || bokPris.trim().equals("") || bokInfo.trim().equals("") || bokISDN.trim().equals("") || bokTillhorProgram.trim().equals("") || bokTillhorKurs.trim().equals("") || bytes == null) {
                    Toast.makeText(CreateNewAdActivity.this,"Alla fält måste vara ifyllda", Toast.LENGTH_LONG).show();
                }
                else{
                    boolean isInserted = myDb.insertAd(boktitel, bokPris, bokInfo, bokISDN, bokTillhorProgram, bokTillhorKurs, id.getId(), bytes);

                    if(isInserted == true){
                        Toast.makeText(CreateNewAdActivity.this,"Annons skapad", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(CreateNewAdActivity.this, "Något gick fel", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
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
    }
}
