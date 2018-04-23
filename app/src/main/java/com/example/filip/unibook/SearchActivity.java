package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.*;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {

    private Button programSearchBtn, courseSearchBtn;
    private SearchView searchView;
    public static final String TAG = "message";
    private ListView listView;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private Context context;
    private List<DocumentSnapshot> programResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        CollectionReference adsRef = rootRef.collection("Ads");

        listView = findViewById(R.id.listViewAllAds);

        searchView = findViewById(R.id.searchViewBooks);

        programSearchBtn = findViewById(R.id.btnSearchGoToProgram);

        courseSearchBtn = findViewById(R.id.btnSearchGoToCourse);

        context = getApplicationContext();

        //Map<String, Object> mapOne = new HashMap<>();
        //mapOne.put("ISDN", "567-98-00-11");
        //mapOne.put("course", "Psykologi A");
        //mapOne.put("info", "Den här boken var inte så bra som jag trodde");
        //mapOne.put("price", "600");
        //mapOne.put("program", "Psykologiprogrammet");
        //mapOne.put("title", "Socialpsykologi med experiment");
        //mapOne.put("userid", "UMDVQfaNAVxZ5Srzqzg1");

        //Map<String, Object> mapTwo = new HashMap<>();
        //mapTwo.put("ISDN", "567-98-00-11");
        //mapTwo.put("course", "Kravhantering");
        //mapTwo.put("info", "Den här boken kommer hjälpa dig att bli den bästa kravhanteraren");
        //mapTwo.put("price", "850");
        //mapTwo.put("program", "Systemvetenskapliga Programmet");
        //mapTwo.put("title", "Kravhantering deluxe för en noob");
        //mapTwo.put("sellerId", "UMDVQfaNAVxZ5Srzqzg1");

        //adsRef.document()
        //        .set(mapOne)
        //        .addOnSuccessListener(new OnSuccessListener<Void>() {
        //            @Override
        //            public void onSuccess(Void aVoid) {
        //                Log.d(TAG, "DocumentSnapshot successfully written!");
        //            }
        //        })
        //        .addOnFailureListener(new OnFailureListener() {
        //            @Override
        //            public void onFailure(@NonNull Exception e) {
        //                Log.w(TAG, "Error writing document", e);
        //            }
        //        });

        //adsRef.document()
        //        .set(mapTwo)
        //        .addOnSuccessListener(new OnSuccessListener<Void>() {
        //            @Override
        //            public void onSuccess(Void aVoid) {
        //                Log.d(TAG, "DocumentSnapshot successfully written!");
        //            }
        //        })
        //        .addOnFailureListener(new OnFailureListener() {
        //            @Override
        //            public void onFailure(@NonNull Exception e) {
        //                Log.w(TAG, "Error writing document", e);
        //            }
        //        });

        //Client client = new Client("K1KZR7ER1O", "6dea0396ee42fb485fc94cd182f21423");
        //final Index index = client.getIndex("ads");

        //Map<String, Object> mapTwo = new HashMap<>();
        //mapTwo.put("title", "Teknik");
        //mapTwo.put("price", "500");
        //mapTwo.put("id", "ftYAM9xLRIRH9A74myYx");
        //Map<String, Object> mapThree = new HashMap<>();
        //mapThree.put("title", "Socialpsykologi");
        //mapThree.put("price", "600");
        //mapThree.put("id", "n3dtvFC13y9LFZlgWFLd");
        //List<JSONObject> adsList = new ArrayList<>();
        //adsList.add(new JSONObject(mapTwo));
        //adsList.add(new JSONObject(mapThree));

        //index.addObjectsAsync(new JSONArray(adsList), null);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent chosenAdForSale = new Intent(context, ChosenAdForSale.class);
                TextView id = view.findViewById(R.id.txtAdID);
                chosenAdForSale.putExtra("id", id.getText().toString());
                startActivity(chosenAdForSale);
            }
        });

        adsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int size = task.getResult().size();

                            String[] items = new String[size];
                            String[] ids = new String[size];
                            String[] prices = new String[size];

                            List<DocumentSnapshot> minLista = task.getResult().getDocuments();

                            for(int i = 0;i < size;i++){

                                DocumentSnapshot doc = minLista.get(i);
                                ids[i] = doc.getId().toString();
                                items[i] = doc.getString("title");
                                prices[i] = doc.getString("price");

                            }
                            ItemAdapter adapter = new ItemAdapter(context, items, prices, null, ids);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Query query1 = new Query(query)
                        .setAttributesToRetrieve("title")
                        .setHitsPerPage(50);
                index.searchAsync(query1, new CompletionHandler() {
                    @Override
                    public void requestCompleted(JSONObject content, AlgoliaException error) {
                        try {
                            JSONArray hits = content.getJSONArray("hits");
                            List<String> list = new ArrayList<>();
                            for(int i = 0; i < hits.length(); i++){
                                JSONObject jsonObject = hits.getJSONObject(i);
                                String productName = jsonObject.getString("title");
                                list.add(productName);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
                            listView.setAdapter(arrayAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Query query = new Query(newText)
                        .setAttributesToRetrieve("title", "id", "price")
                        .setHitsPerPage(50);
                index.searchAsync(query, new CompletionHandler() {
                    @Override
                    public void requestCompleted(JSONObject content, AlgoliaException error) {
                        try {
                            JSONArray hits = content.getJSONArray("hits");

                            int size = hits.length();
                            String[] items = new String[size];
                            String[] ids = new String[size];
                            String[] prices = new String[size];

                            for(int i = 0; i < hits.length(); i++){
                                JSONObject jsonObject = hits.getJSONObject(i);
                                items[i] = jsonObject.getString("title");
                                ids[i] = jsonObject.getString("id");
                                prices[i] = jsonObject.getString("price");
                            }
                            ItemAdapter itemAdapter = new ItemAdapter(context, items, prices, null, ids);
                            listView.setAdapter(itemAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
        });*/

        programSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToSearchProgram = new Intent(context, ListAllProgramsActivity.class);
                goToSearchProgram.putExtra("activityCode", 1);
                startActivityForResult(goToSearchProgram, 1);
            }
        });

        courseSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSearchCourse = new Intent(context, ListAllCoursesFromProgramActivity.class);

                String chosenProgram = programSearchBtn.getText().toString();

                String[] myExtras = new String[]{"1", chosenProgram};
                goToSearchCourse.putExtra("extras", myExtras);
                startActivityForResult(goToSearchCourse, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){

            String programName = data.getStringExtra("programNamn");

            programSearchBtn.setText(programName);

            CollectionReference favouritesRef =  rootRef.collection("Ads");

            com.google.firebase.firestore.Query query = favouritesRef.whereEqualTo("program", programName);
            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                int size = task.getResult().size();

                                String[] items = new String[size];
                                String[] ids = new String[size];
                                String[] prices = new String[size];

                                programResult = task.getResult().getDocuments();

                                for(int i = 0;i < size;i++){

                                    DocumentSnapshot doc = programResult.get(i);
                                    ids[i] = doc.getId().toString();
                                    items[i] = doc.getString("title");
                                    prices[i] = doc.getString("price");

                                }
                                ItemAdapter adapter = new ItemAdapter(context, items, prices, null, ids);
                                listView.setAdapter(adapter);
                            }else{
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        if(resultCode == 2){

            String courseName = data.getStringExtra("kursNamn");

            courseSearchBtn.setText(courseName);


        }
    }
}