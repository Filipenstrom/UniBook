package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ListAllCoursesFromProgramActivity extends AppCompatActivity {

    Context context = this;
    DatabaseHelper myDb;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_courses_from_program);

        listView = (ListView) findViewById(R.id.coursesListView);
        myDb = new DatabaseHelper(this);
        SharedPreferences prefs = new SharedPreferences(context);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = listView.getItemAtPosition(position).toString();
                TextView txtCourse = (TextView) findViewById(R.id.textViewCourses);
                Intent intent = new Intent(ListAllCoursesFromProgramActivity.this, CreateNewAdActivity.class);
                intent.putExtra("kursNamn", txtCourse.getText().toString());
                startActivity(intent);

            }
        });

        getAllCourses();
    }

    public void getAllCourses(){
        TextView programNamn = (TextView) findViewById(R.id.txtViewProgram);
        String namn = programNamn.getText().toString();

        List<Course> kursLista = myDb.showCourses();
        int numberOfCourses = kursLista.size();
        String[] items = new String[numberOfCourses];
        String[] ids = new String[numberOfCourses];
        String[] codes = new String[numberOfCourses];


        for (int i = 0; i < kursLista.size(); i++) {
            Course course = kursLista.get(i);
            ids[i] = course.getId();
            items[i] = course.getName();
            codes[i] = course.getCode();

        }
        CourseAdapter courseAdapter = new CourseAdapter(this, items, ids, codes);
        listView.setAdapter(courseAdapter);
    }
}
