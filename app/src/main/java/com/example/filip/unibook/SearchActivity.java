package com.example.filip.unibook;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    DatabaseHelper myDb = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        final ArrayList<String> programList = myDb.getAllPrograms();

        myDb.insertExampleProgram();


        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, programList);

        ListView programs = findViewById(R.id.programsListView);

        // DataBind ListView with items from ArrayAdapter
        programs.setAdapter(arrayAdapter);

        arrayAdapter.notifyDataSetChanged();
    }

    public void fillUpAds() {
        DatabaseHelper db = new DatabaseHelper(this);
        ListView listView = findViewById(R.id.listViewMyAds);
        List<List<String>> annonser = myDb.getMyAds(id[3]);
        int numberOfAds = annonser.size();
        //Hämtar alla bilder tills annonserna
        List<byte[]> bytes = db.getAdsImg(user[0]);


        //Hämtar all data om annonserna, exkluderat tillhörande bilder.
        for(int i = 0;i<annonser.size();i++) {
            List<String> annons = annonser.get(i);
            for(int i2 = 0; i2<annons.size();i2++)
                items[i] = annons.get(1).toString();
            prices[i] = annons.get(2).toString();
        }
        ItemAdapter itemAdapter = new ItemAdapter(this, items, prices, bytes);
        listView.setAdapter(itemAdapter);
    }
}
