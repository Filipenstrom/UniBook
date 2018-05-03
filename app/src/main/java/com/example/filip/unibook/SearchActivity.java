package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.*;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

public class SearchActivity extends AppCompatActivity {

    private Button programSearchBtn, courseSearchBtn;
    private SearchView searchView;
    public static final String TAG = "message";
    private ListView listView;
    private ProgressBar progressbar;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private Context context;
    private String[] items, ids, prices, programs, courses;
    List<byte[]> pics;
    private String queryString;
    Client client;
    Index index;
    JSONArray hits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        CollectionReference adsRef = rootRef.collection("Ads");

        listView = findViewById(R.id.listViewAllAds);

        searchView = findViewById(R.id.searchViewBooks);

        programSearchBtn = findViewById(R.id.btnSearchGoToProgram);

        progressbar = findViewById(R.id.searchProgressBar);

        courseSearchBtn = findViewById(R.id.btnSearchGoToCourse);

        context = getApplicationContext();

        queryString = "";

        client = new Client("K1KZR7ER1O", "6dea0396ee42fb485fc94cd182f21423");
        index = client.getIndex("ads");

        progressbar.setVisibility(View.VISIBLE);

        adsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            final int size = task.getResult().size();

                            items = new String[size];
                            ids = new String[size];
                            prices = new String[size];
                            programs = new String[size];
                            courses = new String[size];
                            pics = new ArrayList<>();

                            List<DocumentSnapshot> minLista = task.getResult().getDocuments();

                            for(int i = 0;i < size;i++){

                                DocumentSnapshot doc = minLista.get(i);
                                ids[i] = doc.getId().toString();
                                items[i] = doc.getString("title");
                                prices[i] = doc.getString("price");
                                programs[i] = doc.getString("program");
                                courses[i] = doc.getString("course");
                            }
                            index.clearIndexAsync(new CompletionHandler() {
                                @Override
                                public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {

                                    List<JSONObject> adsList = new ArrayList<>();

                                    for (int i = 0; i < size; i++) {

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("title", items[i]);
                                        map.put("price", prices[i]);
                                        map.put("id", ids[i]);
                                        map.put("program", programs[i]);
                                        map.put("course", courses[i]);

                                        adsList.add(new JSONObject(map));
                                    }
                                    index.addObjectsAsync(new JSONArray(adsList), null);
                                }
                            });
                            ItemAdapter itemAdapter = new ItemAdapter(context, items, prices, ids);
                            listView.setAdapter(itemAdapter);

                            progressbar.setVisibility(View.INVISIBLE);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent chosenAdForSale = new Intent(context, ChosenAdForSale.class);
                TextView id = view.findViewById(R.id.txtAdID);
                chosenAdForSale.putExtra("id", id.getText().toString());
                startActivity(chosenAdForSale);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchQuery(newText);
                return false;
            }
        });

        programSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSearchProgram = new Intent(context, ListAllProgramsActivity.class);
                goToSearchProgram.putExtra("activityCode", 1);
                startActivityForResult(goToSearchProgram, 1);
                courseSearchBtn.setText("");
            }
        });

        courseSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(programSearchBtn.getText().toString().equals("")){
                    Toast.makeText(SearchActivity.this, "Du måste välja program först",
                            Toast.LENGTH_LONG).show();
                }else{

                    Intent goToSearchCourse = new Intent(context, ListAllCoursesFromProgramActivity.class);

                    String chosenProgram = programSearchBtn.getText().toString();

                    String[] myExtras = new String[]{"1", chosenProgram};
                    goToSearchCourse.putExtra("extras", myExtras);
                    startActivityForResult(goToSearchCourse, 2);
                }
            }
        });
    }

    public void searchQuery(String text){

        Query query1 = new Query(text)
                .setAttributesToRetrieve("title", "id", "price")
                .setHitsPerPage(50);
        index.searchAsync(query1.setFacetFilters(new JSONArray().put("program: " + programSearchBtn.getText().toString()).put("course: " + courseSearchBtn.getText().toString())), new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    hits = content.getJSONArray("hits");

                    int size = hits.length();

                    String[] onChangeItems = new String[size];
                    String[] onChangeIds = new String[size];
                    String[] onChangePrices = new String[size];

                    for(int i = 0; i < size; i++){
                        JSONObject jsonObject = hits.getJSONObject(i);
                        onChangeItems[i] = jsonObject.getString("title");
                        onChangeIds[i] = jsonObject.getString("id");
                        onChangePrices[i] = jsonObject.getString("price");
                    }
                    ItemAdapter itemAdapter = new ItemAdapter(context, onChangeItems, onChangePrices, onChangeIds);
                    listView.setAdapter(itemAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void searchProgram(String programFacet){

        Query query1 = new Query()
                .setAttributesToRetrieve("title", "id", "price")
                .setHitsPerPage(50);
        index.searchAsync(query1.setFacetFilters(new JSONArray().put("program: " + programFacet)), new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    hits = content.getJSONArray("hits");

                    int size = hits.length();

                    String[] onChangeItems = new String[size];
                    String[] onChangeIds = new String[size];
                    String[] onChangePrices = new String[size];

                    for(int i = 0; i < size; i++){
                        JSONObject jsonObject = hits.getJSONObject(i);
                        onChangeItems[i] = jsonObject.getString("title");
                        onChangeIds[i] = jsonObject.getString("id");
                        onChangePrices[i] = jsonObject.getString("price");
                    }
                    ItemAdapter itemAdapter = new ItemAdapter(context, onChangeItems, onChangePrices, onChangeIds);
                    listView.setAdapter(itemAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void searchCourse(String programFacet, String courseFacet){

        Query query1 = new Query()
                .setAttributesToRetrieve("title", "id", "price")
                .setHitsPerPage(50);
        index.searchAsync(query1.setFacetFilters(new JSONArray().put("program: " + programFacet).put("course: " + courseFacet)), new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    hits = content.getJSONArray("hits");

                    int size = hits.length();

                    String[] onChangeItems = new String[size];
                    String[] onChangeIds = new String[size];
                    String[] onChangePrices = new String[size];

                    for(int i = 0; i < size; i++){
                        JSONObject jsonObject = hits.getJSONObject(i);
                        onChangeItems[i] = jsonObject.getString("title");
                        onChangeIds[i] = jsonObject.getString("id");
                        onChangePrices[i] = jsonObject.getString("price");
                    }
                    ItemAdapter itemAdapter = new ItemAdapter(context, onChangeItems, onChangePrices, onChangeIds);
                    listView.setAdapter(itemAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String programName = "";
        String courseName = "";
        if(resultCode == 1){

            programName = data.getStringExtra("programNamn");

            programSearchBtn.setText(programName);

            searchProgram(programName);

        }
        if(resultCode == 2){

            courseName = data.getStringExtra("kursNamn");

            courseSearchBtn.setText(courseName);

            searchCourse(programName, courseName);

        }
    }
}