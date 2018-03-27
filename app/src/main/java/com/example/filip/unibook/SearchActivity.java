package com.example.filip.unibook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import android.widget.*;

public class SearchActivity extends AppCompatActivity {

    DatabaseHelper myDb = new DatabaseHelper(this);
    private Button programSearchBtn;
    private Button courseSearchBtn;
    private SearchView searchView;
    private String chosenProgram = "";
    private String chosenCourse = "";
    private String input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        fillUpAds(input, chosenProgram, chosenCourse);

        courseSearchBtn = findViewById(R.id.btnSearchGoToCourse);

        programSearchBtn = findViewById(R.id.btnSearchGoToProgram);

        searchView = findViewById(R.id.searchViewBooks);

        programSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, ListAllProgramsActivity.class);
                intent.putExtra("activityCode", 1);
                startActivityForResult(intent, 1);
            }
        });

        courseSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, ListAllCoursesFromProgramActivity.class);
                String programNamn = programSearchBtn.getText().toString();
                if(programNamn.equals("")){

                    Toast.makeText(SearchActivity.this, "Du måste först välja ett program innan du väljer kurs", Toast.LENGTH_SHORT).show();
                }else {

                    String[] myExtras = new String[]{"1", programNamn};
                    intent.putExtra("extras", myExtras);
                    startActivityForResult(intent, 1);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                input = searchView.getQuery().toString();
                fillUpAds(input, chosenProgram, chosenCourse);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                input = searchView.getQuery().toString();
                fillUpAds(input, chosenProgram, chosenCourse);
                return false;
            }
        });

        ListView listView = findViewById(R.id.listViewAllAds);

        //Gå till vald annons och skicka med id på vald annons från ett osynligt textfält som ligger i my_listview_ad.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent showDetailActivity = new Intent(getApplicationContext(), ChosenAdForSale.class);
                TextView id = view.findViewById(R.id.txtAdID);
                showDetailActivity.putExtra("id", Integer.parseInt(id.getText().toString()));
                startActivity(showDetailActivity);
            }
        });
    }

    //Fyller upp listan med annonser baserat på användarens input
    public void fillUpAds(String query, String chosenProgram, String chosenCourse){
        ListView listView = findViewById(R.id.listViewAllAds);

        List<Ad> annonser = myDb.getAllAds(query, chosenProgram, chosenCourse );
        int numberOfAds = annonser.size();
        String[] titles = new String[numberOfAds];
        String[] prices = new String[numberOfAds];
        List<byte[]> bytes = new ArrayList<>();
        String[] ids = new String[numberOfAds];

        for(int i = 0;i < annonser.size();i++){
            titles[i] = annonser.get(i).getTitle();
            prices[i] = annonser.get(i).getPrice();
            bytes.add(annonser.get(i).getPic());
            ids[i] = annonser.get(i).getId();
         }
        ItemAdapter itemAdapter = new ItemAdapter(this, titles, prices, bytes, ids);
        listView.setAdapter(itemAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1){

            String programNamn = data.getStringExtra("programNamn");
            programSearchBtn.setText(programNamn);
            courseSearchBtn.setText("");
            chosenCourse = "";
            chosenProgram = programNamn;
            fillUpAds(input, chosenProgram, chosenCourse);
        }
        if(resultCode == 2){

            String kursNamn = data.getStringExtra("kursNamn");
            courseSearchBtn.setText(kursNamn);
            chosenCourse = kursNamn;
            fillUpAds(input, chosenProgram, chosenCourse);
        }
    }
}
