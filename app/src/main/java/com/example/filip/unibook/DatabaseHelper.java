package com.example.filip.unibook;

import android.content.Context;
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
    public static final String COL_6 = "PIC";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlUsers = "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, surname TEXT, mail TEXT UNIQUE, password VARCHAR, pic BLOB);";
        String sqlAdds = "CREATE TABLE adds(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, price INTEGER, description VARCHAR, userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id), bookid INTEGER, FOREIGN KEY(bookid) REFERENCED books(id));";
        String sqlBooks = "CREATE TABLE books(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description VARCHAR, isdn VARCHAR, programid INTEGER, FOREIGN KEY(program_id) REFERENCED program(id), courseid INTEGER, FOREIGN(course_id) REFERENCED courses(id));";
        String sqlProgram = "CREATE TABLE program(id INTEGER PRIMARY KEY AUTOINCREMENT, programname TEXT UNIQUE, programcode INTEGER);";
        String sqlCourses = "CREATE TABLE courses(id INTEGER PRIMARY KEY AUTOINCREMENT, coursename TEXT UNIQUE, beskrivning VARCHAR);";
        String sqlFavourites = "CREATE TABLE favourites(id INTEGER PRIMARY KEY AUTOINCREMENT, addid INTEGER, FOREIGN KEY(adds_id) REFERENCED adds(id), userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
        String sqlChats = "CREATE TABLE chats(id INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER, FOREIGNKEY(users_id) REFERENCED users(id), seconduserid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
        String sqlMessages = "CREATE TABLE messages(id INTEGER PRIMARY KEY AUTOINCREMENT, content VARCHAR, userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id), chatid INTEGER, FOREIGN KEY(chat_id) REFERENCED chats(id));";


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlUsers = "DROP TABLE IF EXISTS users";

        db.execSQL(sqlUsers);

    }
}
