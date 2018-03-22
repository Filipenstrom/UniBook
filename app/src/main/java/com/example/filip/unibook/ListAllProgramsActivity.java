package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

        Intent intent = getIntent();

        final int activityCode = intent.getIntExtra("activityCode", -1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txtProgram = (TextView) view.findViewById(R.id.txtProgram);
                TextView txtProgramId = view.findViewById(R.id.txtProgramId);
                if(activityCode ==  1) {
                    Intent data = new Intent();
                    String programNamn = txtProgram.getText().toString();
                    data.putExtra("programNamn", programNamn);
                    setResult(1, data);
                    finish();
                }else {
                    Intent intent = new Intent(ListAllProgramsActivity.this, CreateNewAdActivity.class);
                    String programId = txtProgramId.getText().toString();
                    String program = txtProgram.getText().toString();
                    String[] myExtras = new String[]{programId, program};
                    intent.putExtra("programInfoIntent", myExtras);

                    startActivity(intent);
                }
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
