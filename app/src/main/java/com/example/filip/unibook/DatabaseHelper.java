package com.example.filip.unibook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by filip on 2018-03-05.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "unibook.db";
    public static final String TABLE_NAME = "users";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "SURNAME";
    public static final String COL_4 = "MAIL";
    public static final String COL_5 = "PASSWORD";
    //public static final String COL_6 = "PIC";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String sqlUsers = "CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL TEXT UNIQUE, PASSWORD VARCHAR)";
      //  String sqlAdds = "CREATE TABLE adds (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, price INTEGER, description VARCHAR, userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id), bookid INTEGER, FOREIGN KEY(bookid) REFERENCED books(id));";
      //  String sqlBooks = "CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description VARCHAR, isdn VARCHAR, programid INTEGER, FOREIGN KEY(program_id) REFERENCED program(id), courseid INTEGER, FOREIGN(course_id) REFERENCED courses(id));";
      //  String sqlProgram = "CREATE TABLE program (id INTEGER PRIMARY KEY AUTOINCREMENT, programname TEXT UNIQUE, programcode INTEGER);";
      //  String sqlCourses = "CREATE TABLE courses (id INTEGER PRIMARY KEY AUTOINCREMENT, coursename TEXT UNIQUE, beskrivning VARCHAR);";
      //  String sqlFavourites = "CREATE TABLE favourites (id INTEGER PRIMARY KEY AUTOINCREMENT, addid INTEGER, FOREIGN KEY(adds_id) REFERENCED adds(id), userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
      //  String sqlChats = "CREATE TABLE chats (id INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER, FOREIGNKEY(users_id) REFERENCED users(id), seconduserid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
      //  String sqlMessages = "CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, content VARCHAR, userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id), chatid INTEGER, FOREIGN KEY(chat_id) REFERENCED chats(id));";

        db.execSQL("CREATE TABLE " +  TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL TEXT, PASSWORD VARCHAR)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlUsers = "DROP TABLE IF EXISTS users";
        String sqlAdds = "DROP TABLE IF EXISTS adds";
        String sqlBooks = "DROP TABLE IF EXISTS books";
        String sqlProgram = "DROP TABLE IF EXISTS program";
        String sqlCourses = "DROP TABLE IF EXISTS courses";
        String sqlFavourites = "DROP TABLE IF EXISTS favourites";
        String sqlChats = "DROP TABLE IF EXISTS chats";
        String sqlMessages = "DROP TABLE IF EXISTS users";

        db.execSQL(sqlUsers);
        db.execSQL(sqlAdds);
        db.execSQL(sqlProgram);
        db.execSQL(sqlCourses);
        db.execSQL(sqlFavourites);
        db.execSQL(sqlChats);
        db.execSQL(sqlMessages);

    }

    public boolean insertUser(String name, String surname, String mail, String password){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, mail);
        contentValues.put(COL_5, password);


        long result = db.insert(TABLE_NAME, null, contentValues);
        db.insert(TABLE_NAME, null, contentValues);
        if(result == -1) {
            return false;
        }
        else {
            return true;
        }

    }

    public String searchPass(String user){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select mail, password from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        String anvandarNamn, losenord;
        losenord = "not found";
        if(cursor.moveToFirst()){
            do{
                anvandarNamn = cursor.getString(0);

                if(anvandarNamn.equals(user)){
                    losenord = cursor.getString(1);
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        return losenord;
    }

    public String getName(String user){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select name, surname from " + TABLE_NAME + " where mail = " + "'" + user  + "'";
        Cursor cursor = db.rawQuery(query, null);
        String namn = "";
        String efternamn = "";


        if(cursor.moveToFirst()){
            do{
                namn = cursor.getString(0);
                efternamn = cursor.getString(1);
            }

            while (cursor.moveToNext());
        }

        String name = namn + " " + efternamn;

        return name;
    }

}
