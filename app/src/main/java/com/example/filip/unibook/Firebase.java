package com.example.filip.unibook;

import android.util.Log;

/*import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Ludvig on 2018-03-26.
 */

/*public class Firebase {

    FirebaseDatabase database;
    DatabaseReference myRef;

    public Firebase(){
        instance();
        read();
        writeNewUser("hej", "hej", "hej");
    }

    public void instance() {
        // Write a message to the database
         database = FirebaseDatabase.getInstance();
         myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }

    public void read(){
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void writeNewUser(String userId, String name, String email) {

        myRef.child("users").child("Ludvig").setValue("Markus", "Filip");
    }
}
*/