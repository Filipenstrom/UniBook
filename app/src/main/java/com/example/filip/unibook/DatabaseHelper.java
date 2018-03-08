package com.example.filip.unibook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by filip on 2018-03-05.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "users.db";
    public static final String TABLE_NAME = "users_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "SURNAME";
    public static final String COL_4 = "MAIL";
    public static final String COL_5 = "PASSWORD";
    public static final String COL_6 = "PIC";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL STRING UNIQUE, PASSWORD TEXT, PIC BLOB)");
        String sqlUsers = "CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL TEXT UNIQUE, PASSWORD VARCHAR)";
        String sqlAds = "CREATE TABLE ads(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, price INTEGER, description VARCHAR, program TEXT, course TEXT, isdn VARCHAR, pic BLOB, userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));"; // bookid INTEGER, FOREIGN KEY(bookid) REFERENCED books(id));";
          db.execSQL(sqlAds);
        //  String sqlBooks = "CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description VARCHAR, isdn VARCHAR, programid INTEGER, FOREIGN KEY(program_id) REFERENCED program(id), courseid INTEGER, FOREIGN(course_id) REFERENCED courses(id));";
        //  String sqlProgram = "CREATE TABLE program (id INTEGER PRIMARY KEY AUTOINCREMENT, programname TEXT UNIQUE, programcode INTEGER);";
        //  String sqlCourses = "CREATE TABLE courses (id INTEGER PRIMARY KEY AUTOINCREMENT, coursename TEXT UNIQUE, beskrivning VARCHAR);";
        //  String sqlFavourites = "CREATE TABLE favourites (id INTEGER PRIMARY KEY AUTOINCREMENT, addid INTEGER, FOREIGN KEY(adds_id) REFERENCED adds(id), userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
        //  String sqlChats = "CREATE TABLE chats (id INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER, FOREIGNKEY(users_id) REFERENCED users(id), seconduserid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
        //  String sqlMessages = "CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, content VARCHAR, userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id), chatid INTEGER, FOREIGN KEY(chat_id) REFERENCED chats(id));";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String sqlAds = "drop table if exists ads";
        db.execSQL(sqlAds);
        onCreate(db);
    }

    //Metod som lägger in användare i databasen
    public boolean insertUser(String name, String surname, String mail, String password, byte[] imageBytes) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, mail);
        contentValues.put(COL_5, password);
        contentValues.put(COL_6, imageBytes);


        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    //Metod som kollar om användarens email och lösenord stämmer överens
    public String searchPass(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select mail, password from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        String anvandarNamn, losenord;
        losenord = "not found";
        if (cursor.moveToFirst()) {
            do {
                anvandarNamn = cursor.getString(0);

                if (anvandarNamn.equals(user)) {
                    losenord = cursor.getString(1);
                    break;
                }
            }
            while (cursor.moveToNext());
        }
        return losenord;
    }

    //Metod som hämtar användarens namn och efternamn
    public String getName(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select name, surname from " + TABLE_NAME + " where mail = " + "'" + user + "'";
        Cursor cursor = db.rawQuery(query, null);
        String namn = "";
        String efternamn = "";


        if (cursor.moveToFirst()) {
            do {
                namn = cursor.getString(0);
                efternamn = cursor.getString(1);
            }

            while (cursor.moveToNext());
        }

        String name = namn + " " + efternamn;

        return name;
    }


    //Hämta all information om en användare, INTE HELT KLAR
    public String[] getUser(String user) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + " where mail = " + "'" + user + "'";
        Cursor cursor = sq.rawQuery(query, null);
        String id = "";
        String name = "";
        String surname = "";
        String mail = "";

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getString(0);
                name = cursor.getString(1);
                surname = cursor.getString(2);
                mail = cursor.getString(3);
            }
            while (cursor.moveToNext());
        }

        String[] fullUser = new String[4];
        fullUser[0] = id;
        fullUser[1] = name;
        fullUser[2] = surname;
        fullUser[3] = mail;

        return fullUser;
    }


    //Hämta bild från databasen och retunera den som en byte[].
    public byte[] getProfileImg(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select PIC from " + TABLE_NAME + " where mail = " + "'" + user + "'";
        Cursor cursor = db.rawQuery(query, null);

        byte[] blob = new byte[1];

        if (cursor.moveToFirst()) {
            do {
                blob = cursor.getBlob(0);
            }
            while (cursor.moveToNext());
        }
        return blob;
    }




    //Metod som skapar en annons för den inloggade användaren
    public boolean insertAd(String titel, String pris, String info, String isdn, String program, String kurs, String userid, byte[] imageBytes) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", titel);
        contentValues.put("price", pris);
        contentValues.put("description", info);
        contentValues.put("isdn", isdn);
        contentValues.put("program", program);
        contentValues.put("course", kurs);
        contentValues.put("userid", userid);
        contentValues.put("pic", imageBytes);
        db.insert("ads", null, contentValues);

        return true;
    }

    public ArrayList getMyAds(String user) {


        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select * from ads join users on ads.userid = users.id where users.mail =" + "'" + user + "'";
        Cursor cursor = sq.rawQuery(query, null);

        ArrayList annonsInnehall = new ArrayList();

        if (cursor.moveToFirst())

        {
            do {
                ArrayList annonser = new ArrayList();
                annonser.add(cursor.getString(0));
                annonser.add(cursor.getString(1));
                annonser.add(cursor.getString(2));
                annonser.add(cursor.getString(3));
                annonser.add(cursor.getString(4));
                annonser.add(cursor.getString(5));
                annonser.add(cursor.getString(6));
                annonser.add(cursor.getString(7));
                annonser.add(cursor.getString(8));
                annonsInnehall.add(annonser);

            }
            while (cursor.moveToNext());

        }

        return annonsInnehall;

    }
}
