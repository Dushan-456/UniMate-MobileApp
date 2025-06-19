package com.s23010664.unimate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Unimate.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "gender TEXT, " +
                "birthday TEXT, " +
                "university TEXT, " +
                "password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

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
}

