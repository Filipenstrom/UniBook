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
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, MAIL STRING UNIQUE, PASSWORD TEXT, PIC BLOB, ADRESS TEXT, PHONE VARCHAR, SCHOOL TEXT)");
        db.execSQL("create table " + TABLE_ADS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, PRICE INTEGER, DESCRIPTION VARCHAR, PROGRAM TEXT, COURSE TEXT, ISDN VARCHAR, PIC BLOB, USERID INTEGER, PROGRAMID INTEGER, COURSEID INTEGER,  FOREIGN KEY (USERID) REFERENCES users_table(ID), FOREIGN KEY (PROGRAMID) REFERENCES program(ID), FOREIGN KEY (COURSEID) REFERENCES courses(ID))");
        //  String sqlBooks = "CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description VARCHAR, isdn VARCHAR, programid INTEGER, FOREIGN KEY(program_id) REFERENCED program(id), courseid INTEGER, FOREIGN(course_id) REFERENCES courses(id));"
        db.execSQL("create table " + TABLE_PROGRAM + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT UNIQUE, PROGRAMCODE VARCHAR)");
        db.execSQL("create table " + TABLE_REPORTS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, ADTITLE TEXT, AUTHORNAME TEXT, AUTHORMAIL TEXT, MESSAGE TEXT, AUTHORID INTEGER, ADID INTEGER, FOREIGN KEY(ADID) REFERENCES ads(ID), FOREIGN KEY(AUTHORID) REFERENCES users_table(ID))");
        db.execSQL("create table " + TABLE_COURSES + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT UNIQUE, DESCRIPTION TEXT, COURSECODE INTEGER, PROGRAMID INTEGER, FOREIGN KEY (PROGRAMID) REFERENCES program(ID))");
        db.execSQL("create table " + TABLE_FAVORITES + "(id INTEGER PRIMARY KEY AUTOINCREMENT, addid INTEGER, userid INTEGER, FOREIGN KEY(addid) REFERENCES adds(id), FOREIGN KEY(userid) REFERENCES users_table(id))");
        db.execSQL("create table " + TABLE_NOTIFICATIONS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, adnoti TEXT, userid INTEGER, FOREIGN KEY(userid) REFERENCES users_table(id))");
        //  String sqlChats = "CREATE TABLE chats (id INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER, FOREIGNKEY(users_id) REFERENCED users(id), seconduserid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id));";
        //  String sqlMessages = "CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, content VARCHAR, userid INTEGER, FOREIGN KEY(users_id) REFERENCED users(id), chatid INTEGER, FOREIGN KEY(chat_id) REFERENCED chats(id));";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);

        onCreate(db);
    }

    public void addFavorite(String adid, String userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("adid", adid);
        contentValues.put("userid", userid);
        db.insert(TABLE_FAVORITES, null, contentValues);

    }

    public void addNotis(String notisText, String userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("adnoti", notisText);
        contentValues.put("userid", userid);
        db.insert(TABLE_NOTIFICATIONS, null, contentValues);
    }

    public boolean insertReport(String adTitle, String authorName, String authorMail, String message, int authorID, int adID) {
        SQLiteDatabase sq = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("adTitle", adTitle);
        contentValues.put("authorName", authorName);
        contentValues.put("authorMail", authorMail);
        contentValues.put("message", message);
        contentValues.put("authorID", authorID);
        contentValues.put("adID", adID);

        long result = sq.insert(TABLE_REPORTS, null, contentValues);
        sq.close();

        if (result == -1) {
            return false;
        } else {
            return true;
        }
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

    //Metod som lägger in användare i databasen
    public boolean insertUser(String name, String surname, String mail, String password, byte[] imageBytes, String adress, String phone, String school) {
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
                userInfo.setPhone(cursor.getString(7));
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
                userInfo.setPhone(cursor.getString(7));
                userInfo.setSchool(cursor.getString(8));
            }
            while (cursor.moveToNext());
        }

        return userInfo;
    }

    //Metod som skapar en annons för den inloggade användaren
    public boolean insertAd(String titel, String pris, String info, String isdn, String program, String kurs, String userid, byte[] imageBytes, String programId, String courseId) {
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
        contentValues.put("PROGRAMID", programId);
        contentValues.put("COURSEID", courseId);
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
  
    public List<Ad> getAllAds(String inputQuery, String chosenProgram, String chosenCourse) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String query;

        if(inputQuery.equals("") && chosenProgram.equals("") && chosenCourse.equals("")){

            query = "select ads.title, ads.price, ads.pic, ads.id, ads.description, ads.program, ads.course from ads";
        } else if(chosenProgram.equals("")) {

            query = "select ads.title, ads.price, ads.pic, ads.id, ads.description, ads.program, ads.course from ads where title like '%" + inputQuery + "%'";
        }else if(chosenCourse.equals("")){

            query = "select ads.title, ads.price, ads.pic, ads.id, ads.description, ads.program, ads.course from ads join courses on ads.COURSEID = courses.ID \n" +
                    "\tjoin program on courses.PROGRAMID = program.ID where ads.title like '%" + inputQuery + "%' and program.NAME = '" + chosenProgram + "'";
        }
        else{
            query = "select ads.title, ads.price, ads.pic, ads.id, ads.description, ads.program, ads.course from ads join courses on ads.COURSEID = courses.ID \n" +
                    "\tjoin program on courses.PROGRAMID = program.ID where ads.title like '%in%' and program.NAME = '" + chosenProgram + "' and courses.NAME = '" + chosenCourse + "'";
        }


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
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("name", "Ekonomi");
        contentValues1.put("programcode", "ik");

        db.insert("program", null, contentValues1);
        db.insert("program", null, contentValues);
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
        String query = "select courses.name, courses.id from courses join program on programid = program.id where program.name = '" + programNamn + "'";
        Cursor cursor = db.rawQuery(query, null);

        List<Course> courses = new ArrayList<Course>();
        if(cursor.moveToFirst())
        {
            do {
                Course course = new Course();
                course.setName(cursor.getString(0));
                String id = Integer.toString(cursor.getInt(1));
                course.setId(id);
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

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("name", "Redovisning A");
        contentValues1.put("description", "kuken");
        contentValues1.put("coursecode", "11");
        contentValues1.put("programid", "2");

        db.insert("courses", null, contentValues);
        db.insert("courses", null, contentValues1);
        db.close();
    }
}
