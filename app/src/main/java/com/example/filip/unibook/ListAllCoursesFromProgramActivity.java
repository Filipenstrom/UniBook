package com.example.filip.unibook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ListAllCoursesFromProgramActivity extends AppCompatActivity {

    public static final String TAG = "message";
    Context context = this;
    ListView listView;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private String[] extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_courses_from_program);
        listView = findViewById(R.id.coursesListView);
        Intent intent = getIntent();
        extras = intent.getStringArrayExtra("extras");

        //Vid klick väljs en kurs från ett program, på valt index i vyn och skickar data till CreateNewAdActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txtCourse = view.findViewById(R.id.textViewCourses);
                TextView txtCourseId = view.findViewById(R.id.txtViewCoursesId);
                if(extras[0].equals("1")) {
                    Intent data = new Intent();
                    data.putExtra("kursNamn", txtCourse.getText().toString());
                    setResult(2, data);
                    finish();

                } else {

                    Intent intent = new Intent(ListAllCoursesFromProgramActivity.this, CreateNewAdActivity.class);
                    String kursId = txtCourseId.getText().toString();
                    String kursNamn = txtCourse.getText().toString();
                    String[] myExtras = new String[]{kursId, kursNamn};
                    intent.putExtra("kursInfoIntent", myExtras);
                    setResult(2, intent);
                    finish();
                }
            }
        });

        getAllCourses();
    }

    //Metod som hämtar alla kurser från ett valt program
    public void getAllCourses() {

        String programName = extras[1];

        CollectionReference courseRef = rootRef.collection("Courses");
        Query query = courseRef.whereEqualTo("program", programName);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    int size = task.getResult().size();

                    String[] ids = new String[size];
                    String[] courseName = new String[size];
                    String[] courseCode = new String[size];

                    List<DocumentSnapshot> list = task.getResult().getDocuments();

                    for (int i = 0; i < size; i++) {

                        DocumentSnapshot doc = list.get(i);

                        ids[i] = doc.getId().toString();
                        courseName[i] = doc.getString("name");
                        courseCode[i] = doc.getString("coursecode");
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
