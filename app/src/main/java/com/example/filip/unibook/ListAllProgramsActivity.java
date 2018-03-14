package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ListAllProgramsActivity extends AppCompatActivity {

    Context context = this;
    DatabaseHelper myDb;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_programs);

        listView = (ListView) findViewById(R.id.programListView);
        myDb = new DatabaseHelper(this);
        SharedPreferences prefs = new SharedPreferences(context);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = listView.getItemAtPosition(position).toString();
                TextView txtProgram = (TextView) findViewById(R.id.txtProgram);
                Intent intent = new Intent(ListAllProgramsActivity.this, CreateNewAdActivity.class);
                intent.putExtra("programNamn", txtProgram.getText().toString());
                startActivity(intent);
            }
        });

        getAllPrograms();
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
}
