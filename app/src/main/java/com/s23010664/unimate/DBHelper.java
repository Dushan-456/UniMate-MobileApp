package com.s23010664.unimate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    public static final int DATABASE_VERSION = 3; // Updated to 3 for lost_n_found table

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

        // SQL statement to create the 'lost_n_found' table.
        final String SQL_CREATE_LOST_N_FOUND_TABLE = "CREATE TABLE lost_n_found (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT, " + // 'lost' or 'found'
                "title TEXT, " +
                "description TEXT, " +
                "location TEXT, " +
                "date TEXT, " +
                "contact TEXT, " +
                "image_uri TEXT)";

        // Execute the SQL statements to create the tables.
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_LECTURE_TABLE);
        db.execSQL(SQL_CREATE_ASSIGNMENT_TABLE);
        db.execSQL(SQL_CREATE_LOST_N_FOUND_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS lost_n_found");
        // Create tables again.
        onCreate(db);
    }

    // ==================== PASSWORD HASHING ====================

    /**
     * Hashes a plaintext password using SHA-256.
     * @param password The plaintext password.
     * @return The hex-encoded SHA-256 hash, or null if hashing fails.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
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

        // Hash the password before storing it.
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return false;

        values.put("email", email);
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("gender", gender);
        values.put("birthday", birthday);
        values.put("university", university);
        values.put("password", hashedPassword);

        long result = db.insert("users", null, values);
        return result != -1; // Returns true if a row was inserted, false otherwise.
    }

    /**
     * Checks if a user with the given credentials exists.
     * @return true if user exists, false otherwise.
     */
    public boolean checkLogin(String email, String password) {
        // Hash the input password to compare with the stored hash.
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return false;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, hashedPassword});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    /**
     * Retrieves user details by email.
     * @param email The user's email address.
     * @return A Cursor containing the user's data, or null if not found. Caller must close the cursor.
     */
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
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

    // ==================== COMBINED / FILTERED QUERY METHODS ====================

    /**
     * Returns all activities (events + lectures + assignments) combined into a single list.
     * Each entry is prefixed with its type for display: [Event], [Lecture], [Assignment].
     */
    public List<String> getAllActivities() {
        List<String> all = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Events
        Cursor c1 = db.rawQuery("SELECT * FROM event", null);
        if (c1.moveToFirst()) {
            do {
                String row = "[Event] " + c1.getString(c1.getColumnIndexOrThrow("name"))
                        + " - " + c1.getString(c1.getColumnIndexOrThrow("date"))
                        + " " + c1.getString(c1.getColumnIndexOrThrow("time"));
                all.add(row);
            } while (c1.moveToNext());
        }
        c1.close();

        // Lectures
        Cursor c2 = db.rawQuery("SELECT * FROM lecture", null);
        if (c2.moveToFirst()) {
            do {
                String row = "[Lecture] " + c2.getString(c2.getColumnIndexOrThrow("subject"))
                        + " (" + c2.getString(c2.getColumnIndexOrThrow("type")) + ") "
                        + c2.getString(c2.getColumnIndexOrThrow("date"))
                        + " " + c2.getString(c2.getColumnIndexOrThrow("time"));
                all.add(row);
            } while (c2.moveToNext());
        }
        c2.close();

        // Assignments
        Cursor c3 = db.rawQuery("SELECT * FROM assignment", null);
        if (c3.moveToFirst()) {
            do {
                String row = "[Assignment] " + c3.getString(c3.getColumnIndexOrThrow("name"))
                        + " (" + c3.getString(c3.getColumnIndexOrThrow("subject")) + ") "
                        + "Due: " + c3.getString(c3.getColumnIndexOrThrow("dueDate"))
                        + " " + c3.getString(c3.getColumnIndexOrThrow("dueTime"));
                all.add(row);
            } while (c3.moveToNext());
        }
        c3.close();

        return all;
    }

    /**
     * Returns events filtered by a specific date (format: YYYY-MM-DD).
     */
    public List<String> getEventsByDate(String date) {
        List<String> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM event WHERE date=?", new String[]{date});
        if (cursor.moveToFirst()) {
            do {
                String row = "[Event] " + cursor.getString(cursor.getColumnIndexOrThrow("name"))
                        + " " + cursor.getString(cursor.getColumnIndexOrThrow("time"))
                        + " @ " + cursor.getString(cursor.getColumnIndexOrThrow("location"));
                events.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }

    /**
     * Returns lectures filtered by a specific date (format: YYYY-MM-DD).
     */
    public List<String> getLecturesByDate(String date) {
        List<String> lectures = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM lecture WHERE date=?", new String[]{date});
        if (cursor.moveToFirst()) {
            do {
                String row = "[Lecture] " + cursor.getString(cursor.getColumnIndexOrThrow("subject"))
                        + " (" + cursor.getString(cursor.getColumnIndexOrThrow("type")) + ") "
                        + cursor.getString(cursor.getColumnIndexOrThrow("time"));
                lectures.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lectures;
    }

    /**
     * Returns assignments filtered by a specific due date (format: YYYY-MM-DD).
     */
    public List<String> getAssignmentsByDate(String date) {
        List<String> assignments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM assignment WHERE dueDate=?", new String[]{date});
        if (cursor.moveToFirst()) {
            do {
                String row = "[Assignment] " + cursor.getString(cursor.getColumnIndexOrThrow("name"))
                        + " (" + cursor.getString(cursor.getColumnIndexOrThrow("subject")) + ") "
                        + cursor.getString(cursor.getColumnIndexOrThrow("dueTime"));
                assignments.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return assignments;
    }

    /**
     * Returns all activities (events + lectures + assignments) for a given date.
     */
    public List<String> getActivitiesByDate(String date) {
        List<String> all = new ArrayList<>();
        all.addAll(getEventsByDate(date));
        all.addAll(getLecturesByDate(date));
        all.addAll(getAssignmentsByDate(date));
        return all;
    }

    /**
     * Returns activity counts for a given date as an int array:
     * [0] = event count, [1] = lecture count, [2] = assignment count.
     */
    public int[] getActivityCountsByDate(String date) {
        int[] counts = new int[3];
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM event WHERE date=?", new String[]{date});
        if (c1.moveToFirst()) counts[0] = c1.getInt(0);
        c1.close();

        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM lecture WHERE date=?", new String[]{date});
        if (c2.moveToFirst()) counts[1] = c2.getInt(0);
        c2.close();

        Cursor c3 = db.rawQuery("SELECT COUNT(*) FROM assignment WHERE dueDate=?", new String[]{date});
        if (c3.moveToFirst()) counts[2] = c3.getInt(0);
        c3.close();

        return counts;
    }

    // ==================== DELETE METHODS ====================

    public boolean deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("event", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteLecture(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("lecture", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteAssignment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("assignment", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    // ==================== ACTIVITY ITEM QUERY METHODS ====================

    /**
     * Returns all activities for a given date as ActivityItem objects.
     */
    public List<ActivityItem> getActivityItemsByDate(String date) {
        List<ActivityItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Events
        Cursor c1 = db.rawQuery("SELECT * FROM event WHERE date=?", new String[]{date});
        if (c1.moveToFirst()) {
            do {
                items.add(new ActivityItem(
                        "event",
                        c1.getInt(c1.getColumnIndexOrThrow("id")),
                        c1.getString(c1.getColumnIndexOrThrow("name")),
                        c1.getString(c1.getColumnIndexOrThrow("date")),
                        c1.getString(c1.getColumnIndexOrThrow("time"))
                ));
            } while (c1.moveToNext());
        }
        c1.close();

        // Lectures
        Cursor c2 = db.rawQuery("SELECT * FROM lecture WHERE date=?", new String[]{date});
        if (c2.moveToFirst()) {
            do {
                items.add(new ActivityItem(
                        "lecture",
                        c2.getInt(c2.getColumnIndexOrThrow("id")),
                        c2.getString(c2.getColumnIndexOrThrow("subject")),
                        c2.getString(c2.getColumnIndexOrThrow("date")),
                        c2.getString(c2.getColumnIndexOrThrow("time"))
                ));
            } while (c2.moveToNext());
        }
        c2.close();

        // Assignments
        Cursor c3 = db.rawQuery("SELECT * FROM assignment WHERE dueDate=?", new String[]{date});
        if (c3.moveToFirst()) {
            do {
                items.add(new ActivityItem(
                        "assignment",
                        c3.getInt(c3.getColumnIndexOrThrow("id")),
                        c3.getString(c3.getColumnIndexOrThrow("name")),
                        c3.getString(c3.getColumnIndexOrThrow("dueDate")),
                        c3.getString(c3.getColumnIndexOrThrow("dueTime"))
                ));
            } while (c3.moveToNext());
        }
        c3.close();

        return items;
    }

    /**
     * Returns all activities from all tables as ActivityItem objects.
     */
    public List<ActivityItem> getAllActivityItems() {
        List<ActivityItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c1 = db.rawQuery("SELECT * FROM event", null);
        if (c1.moveToFirst()) {
            do {
                items.add(new ActivityItem(
                        "event",
                        c1.getInt(c1.getColumnIndexOrThrow("id")),
                        c1.getString(c1.getColumnIndexOrThrow("name")),
                        c1.getString(c1.getColumnIndexOrThrow("date")),
                        c1.getString(c1.getColumnIndexOrThrow("time"))
                ));
            } while (c1.moveToNext());
        }
        c1.close();

        Cursor c2 = db.rawQuery("SELECT * FROM lecture", null);
        if (c2.moveToFirst()) {
            do {
                items.add(new ActivityItem(
                        "lecture",
                        c2.getInt(c2.getColumnIndexOrThrow("id")),
                        c2.getString(c2.getColumnIndexOrThrow("subject")),
                        c2.getString(c2.getColumnIndexOrThrow("date")),
                        c2.getString(c2.getColumnIndexOrThrow("time"))
                ));
            } while (c2.moveToNext());
        }
        c2.close();

        Cursor c3 = db.rawQuery("SELECT * FROM assignment", null);
        if (c3.moveToFirst()) {
            do {
                items.add(new ActivityItem(
                        "assignment",
                        c3.getInt(c3.getColumnIndexOrThrow("id")),
                        c3.getString(c3.getColumnIndexOrThrow("name")),
                        c3.getString(c3.getColumnIndexOrThrow("dueDate")),
                        c3.getString(c3.getColumnIndexOrThrow("dueTime"))
                ));
            } while (c3.moveToNext());
        }
        c3.close();

        return items;
    }

    /**
     * Returns all lecture ActivityItems.
     */
    public List<ActivityItem> getLectureItems() {
        List<ActivityItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM lecture", null);
        if (cursor.moveToFirst()) {
            do {
                items.add(new ActivityItem(
                        "lecture",
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("subject")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    /**
     * Returns all assignment ActivityItems.
     */
    public List<ActivityItem> getAssignmentItems() {
        List<ActivityItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM assignment", null);
        if (cursor.moveToFirst()) {
            do {
                items.add(new ActivityItem(
                        "assignment",
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("dueDate")),
                        cursor.getString(cursor.getColumnIndexOrThrow("dueTime"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    // ==================== LOST & FOUND METHODS ====================

    public boolean insertLostNFoundItem(String type, String title, String description, String location, String date, String contact, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("title", title);
        cv.put("description", description);
        cv.put("location", location);
        cv.put("date", date);
        cv.put("contact", contact);
        cv.put("image_uri", imageUri);
        long result = db.insert("lost_n_found", null, cv);
        return result != -1;
    }

    public List<LostNFoundItem> getLostNFoundItemsByType(String type) {
        List<LostNFoundItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM lost_n_found WHERE type=?", new String[]{type});
        if (cursor.moveToFirst()) {
            do {
                items.add(new LostNFoundItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("contact")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image_uri"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public int getLostNFoundItemsCount(String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM lost_n_found WHERE type=?", new String[]{type});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public boolean deleteLostNFoundItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("lost_n_found", "id=?", new String[]{String.valueOf(id)}) > 0;
    }
}