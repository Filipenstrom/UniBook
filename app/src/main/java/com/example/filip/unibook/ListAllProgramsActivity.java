package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Ref;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAllProgramsActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    Context context = this;
    DatabaseHelper myDb;
    ListView listView;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_programs);

        listView = (ListView) findViewById(R.id.programListView);
        myDb = new DatabaseHelper(this);

        Intent intent = getIntent();

        final int activityCode = intent.getIntExtra("activityCode", -1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txtProgram = (TextView) view.findViewById(R.id.txtProgram);
                TextView txtProgramId = view.findViewById(R.id.txtProgramId);
                if (activityCode == 1) {
                    Intent data = new Intent();
                    String programNamn = txtProgram.getText().toString();
                    data.putExtra("programNamn", programNamn);
                    setResult(1, data);
                    finish();
                } else {
                    Intent intent = new Intent(ListAllProgramsActivity.this, CreateNewAdActivity.class);
                    //String programId = txtProgramId.getText().toString();
                    String program = txtProgram.getText().toString();
                    String myExtras = program;
                    intent.putExtra("programInfoIntent", myExtras);

                    startActivity(intent);
                }
            }
        });

        getAllPrograms();
    }


    public void getAllPrograms() {

        CollectionReference progRef = rootRef.collection("Program");
        progRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int size = task.getResult().size();

                            String[] progName = new String[size];
                            String[] ids = new String[size];

                            List<DocumentSnapshot> progLista = task.getResult().getDocuments();

                            for(int i = 0;i < size;i++){
                                DocumentSnapshot doc = progLista.get(i);
                                ids[i] = doc.getId().toString();
                                progName[i] = doc.getString("Name");
                            }
                            ProgramAdapter adapter = new ProgramAdapter(context, progName, ids);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
