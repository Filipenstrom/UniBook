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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;


public class CreateNewAdActivity extends AppCompatActivity {
    Context context = this;
    DatabaseHelper myDb;
    EditText titel, pris, info, isdn, program, kurs;
    Button button;
    private static final int PICK_IMAGE = 100;
    ImageView imageView;
    Uri imageUri;
<<<<<<< HEAD
    ListView listView;
    byte[] bytes = null;

=======


    ListView listView;

    byte[] bytes = null;
>>>>>>> 96dd7cad2e699e7ce6ee25adb4768e145b4437ea

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_ad);

        myDb = new DatabaseHelper(this);
         titel = (EditText) findViewById(R.id.editTxtTitel);
         pris = (EditText) findViewById(R.id.editTxtPris);
         info = (EditText) findViewById(R.id.editTxtInfo);
         isdn = (EditText) findViewById(R.id.editTxtISDN);
        program = (EditText) findViewById(R.id.editTxtProgram);
         kurs = (EditText) findViewById(R.id.editTxtKurs);
         button = (Button) findViewById(R.id.btnBildKnapp);
         imageView = (ImageView) findViewById(R.id.imgViewBokbild);
         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 choseImg();
             }
         });
         listView = (ListView) findViewById(R.id.programListView);


        createAd();
        getAllPrograms();

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



                String bokTillhorProgram = program.getText().toString();
                String bokTillhorKurs = kurs.getText().toString();

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

    public void getAllPrograms() {
        List<Program> programLista = myDb.getPrograms();
        int numberOfPrograms = programLista.size();
        String[] items = new String[numberOfPrograms];
        String[] ids = new String[numberOfPrograms];
        String[] codes = new String[numberOfPrograms];

        for (int i = 0; i < programLista.size(); i++) {
            Program program = programLista.get(i);
            ids[i] = program.getId();
            items[i] = program.getName();
            codes[i] = program.getProgramCode();

        }

        ProgramAdapter programAdapter = new ProgramAdapter(this, items, ids);
        listView.setAdapter(programAdapter);
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
