package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Ref;
import java.util.List;

public class ListAllCoursesFromProgramActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    Context context = this;
    DatabaseHelper myDb;
    ListView listView;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_courses_from_program);

        listView = (ListView) findViewById(R.id.coursesListView);
        myDb = new DatabaseHelper(this);
        SharedPreferences prefs = new SharedPreferences(context);

        Intent intent = getIntent();

        final String[] extras = intent.getStringArrayExtra("extras");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txtCourse = (TextView) view.findViewById(R.id.textViewCourses);
                TextView txtCourseId = view.findViewById(R.id.txtViewCoursesId);
                if(extras[0].equals("1")) {
                    Intent data = new Intent();
                    data.putExtra("kursNamn", txtCourse.getText().toString());
                    setResult(2, data);
                    finish();
                } else {
                    Intent intent = new Intent(ListAllCoursesFromProgramActivity.this, CreateNewAdActivity.class);
                    //String kursId = txtCourseId.getText().toString();
                    String kursNamn = txtCourse.getText().toString();
                    //String myExtras = kursNamn;
                    intent.putExtra("kursInfoIntent", kursNamn);
                    setResult(2, intent);
                    finish();
                }
            }
        });

        getAllCourses();
    }

    public void getAllCoursesBajs(){

        Intent intent = getIntent();
        String[] programNamn = intent.getStringArrayExtra("extras");
        String programName = programNamn[1];
        List<Course> kursLista = myDb.getCourses(programName);
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

    public void getAllCourses() {

        CollectionReference courseRef = rootRef.collection("Courses");
        courseRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int size = task.getResult().size();

                            String[] courseName = new String[size];
                            String[] ids = new String[size];
                            String[] courseCode = new String[size];
                            String[] program = new String[size];

                            List<DocumentSnapshot> progLista = task.getResult().getDocuments();

                            for(int i = 0;i < size;i++){
                                DocumentSnapshot doc = progLista.get(i);
                                ids[i] = doc.getId().toString();
                                courseName[i] = doc.getString("name");
                                courseCode[i] = doc.getString("coursecode");
                                program[i] = doc.getString("program");
                            }
                            CourseAdapter adapter = new CourseAdapter(context, courseName, ids, courseCode);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
