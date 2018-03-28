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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String fakeInput = "";
        
        fillUpAds(fakeInput);

        SearchView search = findViewById(R.id.searchViewBooks);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchView search = findViewById(R.id.searchViewBooks);
                String input = search.getQuery().toString();
                fillUpAds(input);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchView search = findViewById(R.id.searchViewBooks);
                String input = search.getQuery().toString();
                fillUpAds(input);
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
    public void fillUpAds(String input){
        ListView listView = findViewById(R.id.listViewAllAds);

        List<Ad> annonser = myDb.getAllAds(input);
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
}
