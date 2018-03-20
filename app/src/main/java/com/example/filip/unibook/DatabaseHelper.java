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
    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_REPORTS = "reports";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL STRING UNIQUE, PASSWORD TEXT, PIC BLOB, ADRESS TEXT, PHONE INT, SCHOOL TEXT)");
        //String sqlUsers = "CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL TEXT UNIQUE, PASSWORD VARCHAR)";
        //String sqlAds = "CREATE TABLE ads(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, price INTEGER, description VARCHAR, program TEXT, course TEXT, isdn VARCHAR, pic BLOB, userid INTEGER, FOREIGN KEY(userid) REFERENCES users(id));"; // bookid INTEGER, FOREIGN KEY(bookid) REFERENCED books(id));";
         db.execSQL("create table " + TABLE_ADS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, PRICE INTEGER, DESCRIPTION VARCHAR, PROGRAM TEXT, COURSE TEXT, ISDN VARCHAR, PIC BLOB, USERID INTEGER, FOREIGN KEY (USERID) REFERENCES users_table(ID))");
        //  String sqlBooks = "CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description VARCHAR, isdn VARCHAR, programid INTEGER, FOREIGN KEY(program_id) REFERENCED program(id), courseid INTEGER, FOREIGN(course_id) REFERENCES courses(id));";
        //  String sqlProgram = "CREATE TABLE program (id INTEGER PRIMARY KEY AUTOINCREMENT, programname TEXT UNIQUE, programcode INTEGER);";
        db.execSQL("create table " + TABLE_PROGRAM + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT UNIQUE, PROGRAMCODE VARCHAR)");
        //  String sqlCourses = "CREATE TABLE courses (id INTEGER PRIMARY KEY AUTOINCREMENT, coursename TEXT UNIQUE, beskrivning VARCHAR);";
        db.execSQL("create table " + TABLE_COURSES + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT UNIQUE, DESCRIPTION TEXT, COURSECODE INTEGER, PROGRAMID INTEGER, FOREIGN KEY (PROGRAMID) REFERENCES program(ID))");
        db.execSQL("create table " + TABLE_FAVORITES + "(id INTEGER PRIMARY KEY AUTOINCREMENT, addid INTEGER, userid INTEGER, FOREIGN KEY(addid) REFERENCES adds(id), FOREIGN KEY(userid) REFERENCES users_table(id))");
        db.execSQL("create table " + TABLE_NOTIFICATIONS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, adnoti TEXT, bookcounter INT, userid INTEGER, FOREIGN KEY(userid) REFERENCES users_table(id))");
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

    public void addFavorite(String adid, String userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("adid", adid);
        contentValues.put("userid", userid);
        db.insert(TABLE_FAVORITES, null, contentValues);

    }

    public void addNotis(String notisText, String userid, int bookcounter){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("adnoti", notisText);
        contentValues.put("userid", userid);
        contentValues.put("bookcounter", bookcounter);
        db.insert(TABLE_NOTIFICATIONS, null, contentValues);
    }

    public List<String> getNotis(int userid) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select adnoti from " + TABLE_NOTIFICATIONS + " where userid = " + "'" + userid + "'";
        Cursor cursor = sq.rawQuery(query, null);
        List<String> notisText = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                notisText.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }

        return notisText;
    }

    public int getNotisCounter(String adnoti){
        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select bookcounter from " + TABLE_NOTIFICATIONS + " where adnoti = " + "'" + adnoti + "'";
        Cursor cursor = sq.rawQuery(query, null);
        int bookcounter = 0;

        if (cursor.moveToFirst()) {
            do {
                bookcounter = cursor.getInt(0);
            }
            while (cursor.moveToNext());
        }

        return bookcounter;
    }

    public void setNotisCounter(String adnoti, int counter){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("bookcounter", counter);

        db.update(TABLE_NOTIFICATIONS, contentValues, "adnoti='"+adnoti+"'", null);
    }

    //Metod som lägger in användare i databasen
    public boolean insertUser(String name, String surname, String mail, String password, byte[] imageBytes, String adress, int phone, String school) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, mail);
        contentValues.put(COL_5, password);
        contentValues.put(COL_6, imageBytes);
        contentValues.put("adress", adress);
        contentValues.put("phone", phone);
        contentValues.put("school", school);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void updateUser(String id, String name, String surname, String mail, byte[] bytes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, mail);
        contentValues.put(COL_6, bytes);

        db.update(TABLE_NAME, contentValues, "id="+id, null);
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

    ////Hämta all information om en användare, INTE HELT KLAR
    public User getUser(String mail) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select * from users_table where mail = '"+ mail + "'";
        Cursor cursor = sq.rawQuery(query, null);
        User userInfo = new User();

        if (cursor.moveToFirst()) {
            do {
                userInfo.setId(cursor.getString(0));
                userInfo.setName(cursor.getString(1));
                userInfo.setSurname(cursor.getString(2));
                userInfo.setMail(cursor.getString(3));
                userInfo.setPic(cursor.getBlob(5));
                userInfo.setAdress(cursor.getString(6));
                userInfo.setPhone(cursor.getInt(7));
                userInfo.setSchool(cursor.getString(8));
            }
            while (cursor.moveToNext());
        }

        return userInfo;
    }

    //Hämta all information om en användare, INTE HELT KLAR
    public User getUserWithId(int id) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query = "select * from users_table where id = " + id;
        Cursor cursor = sq.rawQuery(query, null);
        User userInfo = new User();

        if (cursor.moveToFirst()) {
            do {
                userInfo.setId(cursor.getString(0));
                userInfo.setName(cursor.getString(1));
                userInfo.setSurname(cursor.getString(2));
                userInfo.setMail(cursor.getString(3));
                userInfo.setPic(cursor.getBlob(5));
                userInfo.setAdress(cursor.getString(6));
                userInfo.setPhone(cursor.getInt(7));
                userInfo.setSchool(cursor.getString(8));
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
            query = "select title, price, pic, id, description, program, course from ads";
        else
            query = "select title, price, pic, id, description, program, course from ads where title like '%" + inputQuery + "%'";

        Cursor cursor = sq.rawQuery(query, null);

        List<Ad> adsContent = new ArrayList<Ad>();

        if (cursor.moveToFirst())
        {
            do {
                Ad ad = new Ad();
                ad.setTitle(cursor.getString(0));
                ad.setPrice(cursor.getString(1));
                ad.setPic(cursor.getBlob(2));
                ad.setId(cursor.getString(3));
                ad.setinfo(cursor.getString(4));
                ad.setProgram(cursor.getString(5));
                ad.setCourse(cursor.getString(6));
                adsContent.add(ad);
            }
            while (cursor.moveToNext());
        }
        return adsContent;
    }

    public Ad getAd(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select title, price, pic, description, program, course, userid, id from ads where ads.id = " + id;

        Cursor cursor = db.rawQuery(query, null);
        Ad ad = new Ad();
        if (cursor.moveToFirst()) {
            do {
                ad.setTitle(cursor.getString(0));
                ad.setPrice(cursor.getString(1));
                ad.setPic(cursor.getBlob(2));
                ad.setinfo(cursor.getString(3));
                ad.setProgram(cursor.getString(4));
                ad.setCourse(cursor.getString(5));
                ad.setUserId(cursor.getInt(6));
                int adId = cursor.getInt(7);
                String stringId = Integer.toString(adId);
                ad.setId(stringId);
            }
            while(cursor.moveToNext());
        }
        return ad;
    }

    public void createProgram() {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "Systemvetenskap");
        contentValues.put("programcode", "ik");

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("name", "Psykologi");
        contentValues2.put("programcode", "Ps");

        long result = db.insert("program", null, contentValues);
        long result2 = db.insert("program", null, contentValues2);
        db.close();
    }

    public List<Program> getPrograms(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from program";
        Cursor cursor = db.rawQuery(query, null);

        List<Program> programs = new ArrayList<Program>();
        if (cursor.moveToFirst())
        {
            do {
                Program program = new Program();
                program.setId(cursor.getString(0));
                program.setName(cursor.getString(1));
                program.setProgramCode(cursor.getString(2));
                programs.add(program);
            }
            while(cursor.moveToNext());
        }

        return programs;
    }

    public List<Course> getCourses(String programNamn){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select courses.name from courses join program on programid = program.id where program.name = '" + programNamn + "'";
        Cursor cursor = db.rawQuery(query, null);

        List<Course> courses = new ArrayList<Course>();
        if(cursor.moveToFirst())
        {
            do {
                Course course = new Course();
                course.setName(cursor.getString(0));
                courses.add(course);

            }
            while(cursor.moveToNext());
        }

        return courses;
    }

    public void createCourse(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "Informatik A");
        contentValues.put("description", "bajs");
        contentValues.put("coursecode", "22");
        contentValues.put("programid", "1");

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("name", "Hjärnan");
        contentValues2.put("description", "snopp");
        contentValues2.put("coursecode", "23");
        contentValues2.put("programid", "2");

        long result = db.insert("courses", null, contentValues);
        long result2 = db.insert("courses", null, contentValues2);
        db.close();
    }
}
