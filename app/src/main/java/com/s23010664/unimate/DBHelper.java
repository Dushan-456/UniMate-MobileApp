package com.s23010664.unimate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class to manage database creation and version management.
 * This class handles all CRUD (Create, Read, Update, Delete) operations for the app.
 */
public class DBHelper extends SQLiteOpenHelper {

    // --- Database Constants ---
    public static final String DATABASE_NAME = "Unimate.db";
    // Increment the version number if you change the database schema.
    public static final int DATABASE_VERSION = 2; // Updated from 1 to 2 because we changed the 'event' table schema

    /**
     * Constructor for DBHelper.
     * @param context The context of the application.
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // --- Table Creation Queries ---

        // SQL statement to create the 'users' table.
        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "gender TEXT, " +
                "birthday TEXT, " +
                "university TEXT, " +
                "password TEXT)";

        // SQL statement to create the 'event' table.
        // **UPDATED**: Added latitude and longitude columns.
        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE event (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "organizer TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "location TEXT, " +
                "latitude REAL, " +   // New column for latitude
                "longitude REAL)";    // New column for longitude

        // SQL statement to create the 'lecture' table.
        final String SQL_CREATE_LECTURE_TABLE = "CREATE TABLE lecture (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subject TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "type TEXT, " +
                "zoomLink TEXT, " +
                "location TEXT)";

        // SQL statement to create the 'assignment' table.
        final String SQL_CREATE_ASSIGNMENT_TABLE = "CREATE TABLE assignment (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "subject TEXT, " +
                "dueDate TEXT, " +
                "dueTime TEXT, " +
                "submissionLink TEXT)";

        // Execute the SQL statements to create the tables.
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_LECTURE_TABLE);
        db.execSQL(SQL_CREATE_ASSIGNMENT_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. This method will drop all existing tables
     * and recreate them. For a production app, you would use ALTER TABLE to migrate data.
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist.
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS event");
        db.execSQL("DROP TABLE IF EXISTS lecture");
        db.execSQL("DROP TABLE IF EXISTS assignment");
        // Create tables again.
        onCreate(db);
    }

    // ==================== USERS TABLE METHODS ====================

    /**
     * Registers a new user in the database.
     * @return true if insertion is successful, false otherwise.
     */
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
        return result != -1; // Returns true if a row was inserted, false otherwise.
    }

    /**
     * Checks if a user with the given credentials exists.
     * @return true if user exists, false otherwise.
     */
    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // ==================== EVENT TABLE METHODS ====================

    /**
     * Inserts a new event into the database.
     * **UPDATED**: This method now accepts latitude and longitude.
     *
     * @param name Name of the event.
     * @param organizer Organizer of the event.
     * @param date Date of the event.
     * @param time Time of the event.
     * @param location String description of the location.
     * @param latitude The latitude of the event location.
     * @param longitude The longitude of the event location.
     * @return true if insertion is successful, false otherwise.
     */
    public boolean insertEvent(String name, String organizer, String date, String time, String location, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("organizer", organizer);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("location", location);
        // Add the new location data.
        cv.put("latitude", latitude);
        cv.put("longitude", longitude);

        long result = db.insert("event", null, cv);
        return result != -1;
    }

    /**
     * Retrieves all events from the database and formats them into a list of strings.
     * @return A List of strings, where each string represents an event.
     */
    public List<String> getEventList() {
        List<String> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM event", null);

        if (cursor.moveToFirst()) {
            do {
                // Formatting the string for display. You can modify this as needed.
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

    // ==================== LECTURE TABLE METHODS ====================

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

    // ==================== ASSIGNMENT TABLE METHODS ====================

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