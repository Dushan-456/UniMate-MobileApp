package com.s23010664.unimate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Unimate.db";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // users Table
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, first_name TEXT, last_name TEXT, gender TEXT, birthday TEXT, university TEXT, password TEXT)");

        // Event Table
        db.execSQL("CREATE TABLE event(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, organizer TEXT, date TEXT, time TEXT, location TEXT)");

        // Lecture Table
        db.execSQL("CREATE TABLE lecture(id INTEGER PRIMARY KEY AUTOINCREMENT, subject TEXT, date TEXT, time TEXT, type TEXT, zoomLink TEXT, location TEXT)");

        // Assignment Table
        db.execSQL("CREATE TABLE assignment(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, subject TEXT, dueDate TEXT, dueTime TEXT, submissionLink TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS event");
        db.execSQL("DROP TABLE IF EXISTS lecture");
        db.execSQL("DROP TABLE IF EXISTS assignment");
        onCreate(db);
    }

    // ==================== USERS ====================
    public boolean registerUser(String email, String firstName, String lastName, String gender,
                                String birthday, String university, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("email", email);
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("gender", gender);
        values.put("birthday", birthday);
        values.put("university", university);
        values.put("password", password);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // ==================== EVENT ====================
    public boolean insertEvent(String name, String organizer, String date, String time, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("organizer", organizer);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("location", location);
        long result = db.insert("event", null, cv);
        return result != -1;
    }

    public List<String> getEventList() {
        List<String> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM event", null);

        if (cursor.moveToFirst()) {
            do {
                String row = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                        + " - " + cursor.getString(cursor.getColumnIndexOrThrow("date"))
                        + " " + cursor.getString(cursor.getColumnIndexOrThrow("time"))
                        + " @ " + cursor.getString(cursor.getColumnIndexOrThrow("location"));
                events.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }

    // ==================== LECTURE ====================
    public boolean insertLecture(String subject, String date, String time, String type, String zoomLink, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("subject", subject);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("type", type);
        cv.put("zoomLink", zoomLink);
        cv.put("location", location);
        long result = db.insert("lecture", null, cv);
        return result != -1;
    }

    public List<String> getLectureList() {
        List<String> lectures = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM lecture", null);

        if (cursor.moveToFirst()) {
            do {
                String row = cursor.getString(cursor.getColumnIndexOrThrow("subject"))
                        + " (" + cursor.getString(cursor.getColumnIndexOrThrow("type")) + ") "
                        + cursor.getString(cursor.getColumnIndexOrThrow("date"))
                        + " " + cursor.getString(cursor.getColumnIndexOrThrow("time"));
                lectures.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lectures;
    }

    // ==================== ASSIGNMENT ====================
    public boolean insertAssignment(String name, String subject, String dueDate, String dueTime, String submissionLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("subject", subject);
        cv.put("dueDate", dueDate);
        cv.put("dueTime", dueTime);
        cv.put("submissionLink", submissionLink);
        long result = db.insert("assignment", null, cv);
        return result != -1;
    }

    public List<String> getAssignmentList() {
        List<String> assignments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM assignment", null);

        if (cursor.moveToFirst()) {
            do {
                String row = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                        + " (" + cursor.getString(cursor.getColumnIndexOrThrow("subject")) + ") "
                        + "Due: " + cursor.getString(cursor.getColumnIndexOrThrow("dueDate"))
                        + " " + cursor.getString(cursor.getColumnIndexOrThrow("dueTime"));
                assignments.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return assignments;
    }
}
