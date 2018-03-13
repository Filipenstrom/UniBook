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
import java.util.List;

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
    public static final String TABLE_ADS = "ads";
    public static final String TABLE_PROGRAM = "program";
    public static final String TABLE_COURSES = "courses";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL STRING UNIQUE, PASSWORD TEXT, PIC BLOB)");
        //String sqlUsers = "CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL TEXT UNIQUE, PASSWORD VARCHAR)";
        //String sqlAds = "CREATE TABLE ads(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, price INTEGER, description VARCHAR, program TEXT, course TEXT, isdn VARCHAR, pic BLOB, userid INTEGER, FOREIGN KEY(userid) REFERENCES users(id));"; // bookid INTEGER, FOREIGN KEY(bookid) REFERENCED books(id));";
         db.execSQL("create table " + TABLE_ADS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, PRICE INTEGER, DESCRIPTION VARCHAR, PROGRAM TEXT, COURSE TEXT, ISDN VARCHAR, PIC BLOB, USERID INTEGER, FOREIGN KEY (USERID) REFERENCES users_table(ID))");
        //  String sqlBooks = "CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description VARCHAR, isdn VARCHAR, programid INTEGER, FOREIGN KEY(program_id) REFERENCED program(id), courseid INTEGER, FOREIGN(course_id) REFERENCES courses(id));";
        //  String sqlProgram = "CREATE TABLE program (id INTEGER PRIMARY KEY AUTOINCREMENT, programname TEXT UNIQUE, programcode INTEGER);";

        db.execSQL("create table " + TABLE_PROGRAM + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT UNIQUE, PROGRAMCODE VARCHAR)");
        //  String sqlCourses = "CREATE TABLE courses (id INTEGER PRIMARY KEY AUTOINCREMENT, coursename TEXT UNIQUE, beskrivning VARCHAR);";
        db.execSQL("create table " + TABLE_COURSES + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT UNIQUE, DESCRIPTION TEXT, COURSECODE INTEGER, PROGRAMID INTEGER, FOREIGN KEY (PROGRAMID) REFERENCES program(ID))");
        //  String sqlFavourites = "CREATE TABLE favourites (id INTEGER PRIMARY KEY AUTOINCREMENT, addid INTEGER, FOREIGN KEY(adds_id) REFERENCED adds(id), userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
        //  String sqlChats = "CREATE TABLE chats (id INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER, FOREIGNKEY(users_id) REFERENCED users(id), seconduserid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
        //  String sqlMessages = "CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, content VARCHAR, userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id), chatid INTEGER, FOREIGN KEY(chat_id) REFERENCED chats(id));";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);

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

    //Hämta all information om en användare, INTE HELT KLAR
    public User getUser(String user) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + " where mail = " + "'" + user + "'";
        Cursor cursor = sq.rawQuery(query, null);
        User userInfo = new User();

        if (cursor.moveToFirst()) {
            do {
                userInfo.setId(cursor.getString(0));
                userInfo.setName(cursor.getString(1));
                userInfo.setSurname(cursor.getString(2));
                userInfo.setMail(cursor.getString(3));
                userInfo.setPic(cursor.getBlob(5));
            }
            while (cursor.moveToNext());
        }

        return userInfo;
    }

    //Metod som skapar en annons för den inloggade användaren
    public boolean insertAd(String titel, String pris, String info, String isdn, String program, String kurs, String userid, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TITLE", titel);
        contentValues.put("PRICE", pris);
        contentValues.put("DESCRIPTION", info);
        contentValues.put("ISDN", isdn);
        contentValues.put("PROGRAM", program);
        contentValues.put("COURSE", kurs);
        contentValues.put("USERID", userid);
        contentValues.put("PIC", imageBytes);
        long result = db.insert(TABLE_ADS, null, contentValues);
        db.close();

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Hämtar all information förutom bild som tillhör en annons som en specifik användare lagt upp.
    public List<Ad> getMyAds(String user) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select ads.id, title, price, description, program, course, isdn, ads.pic from ads join users_table on ads.userid = users_table.ID where users_table.mail =" + "'" + user + "'";
        Cursor cursor = sq.rawQuery(query, null);
        List<Ad> annonsInnehall = new ArrayList<Ad>();

        if (cursor.moveToFirst())
        {
            do {
                Ad ad = new Ad();
                ad.setId(cursor.getString(0));
                ad.setTitle(cursor.getString(1));
                ad.setPrice(cursor.getString(2));
                ad.setinfo(cursor.getString(3));
                ad.setProgram(cursor.getString(4));
                ad.setCourse(cursor.getString(5));
                ad.setISDN(cursor.getString(6));
                ad.setPic(cursor.getBlob(7));
                annonsInnehall.add(ad);
            }
            while (cursor.moveToNext());
        }
        return annonsInnehall;
    }

    //Uppdatera informationen om en annons.
    public void updateAd(int id, String title, int pris, String info, String ISDN, String program, String course, byte[] bytes, int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("price", pris);
        contentValues.put("description", info);
        contentValues.put("isdn", ISDN);
        contentValues.put("program", program);
        contentValues.put("course", course);
        contentValues.put("pic", bytes);
        contentValues.put("userid", userid);

        db.update(TABLE_ADS, contentValues, "id="+id, null);
    }

    public void deleteAd(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADS,"id="+id, null);
    }
  
    public List<Ad> getAllAds(String inputQuery) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query;

        if(inputQuery == "")
            query = "select title, price, pic from ads";
        else
            query = "select title, price, pic from ads where title like " + "'%" + inputQuery + "%'";

        Cursor cursor = sq.rawQuery(query, null);

        List<Ad> adsContent = new ArrayList<Ad>();

        if (cursor.moveToFirst())
        {
            do {
                Ad ad = new Ad();
                ad.setTitle(cursor.getString(0));
                ad.setPrice(cursor.getString(1));
                ad.setPic(cursor.getBlob(2));
                adsContent.add(ad);
            }
            while (cursor.moveToNext());
        }
        return adsContent;
    }

    public void createProgram() {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "Systemvetenskap");
        contentValues.put("programcode", "ik");

        long result = db.insert("program", null, contentValues);
        db.close();
    }

    public ArrayList<String> getAllPrograms() {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select name from programs_table";
        Cursor cursor = sq.rawQuery(query, null);
        ArrayList<String> allPrograms = new ArrayList<String>();

        if (cursor.moveToFirst())
        {
            do {
                allPrograms.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        return allPrograms;
    }
}
