package com.example.filip.unibook;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by marku on 2018-03-27.
 */

public class FirebaseHelper {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private DatabaseReference mChildReference = mRootReference.child("users");

    public FirebaseHelper(){

    }

    public void insertUser(String id, String name, String surname, String mail, String adress, String phone, String school) {

        FBUser balle = new FBUser(id, name, surname, mail, adress, phone, school);

        mChildReference.child(id).setValue(balle);
    }
}