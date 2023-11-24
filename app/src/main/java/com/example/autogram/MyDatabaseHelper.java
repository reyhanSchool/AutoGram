package com.example.autogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AutoGram.db";
    private static final int DATABASE_VERSION = 15;
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String PROFILEBIO = "profile_bio";

    SQLiteDatabase db;


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your database tables here
        db.execSQL("CREATE TABLE IF NOT EXISTS user ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIRST_NAME + " TEXT,"
                + LAST_NAME + " TEXT,"
                + USERNAME + " TEXT UNIQUE,"
                + EMAIL + " TEXT UNIQUE,"
                + PASSWORD + " TEXT,"
                + "profile_pic BLOB,"
                + PROFILEBIO + " TEXT)"
                + ";");

        db.execSQL("CREATE TABLE IF NOT EXISTS post ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER,"  // Foreign key referencing the 'user' table
                + "title TEXT,"
                + "content TEXT,"
                + "image_data BLOB,"
                + "FOREIGN KEY(user_id) REFERENCES user(id))"
                + ";");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
        db.execSQL("DROP TABLE IF EXISTS your_table_name");
        onCreate(db);
    }

    //Insert new users into database
    public long insertDataIntoDatabase(String firstName, String lastName, String username, String email, String password) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIRST_NAME, firstName);
        values.put(LAST_NAME, lastName);
        values.put(USERNAME, username);
        values.put(EMAIL, email);
        values.put(PASSWORD, password);

        // You can add additional columns and values as needed

        long newRowId = db.insert("user", null, values);

        // Close the database connection
        db.close();

        return newRowId;
    }

    //Check if provided username and password match records in the database
    public Cursor isValidCredentials(String username, String password){
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE " +
                "username = ? AND password = ?", new String[]{username, password});

        // Move to the first row of the cursor
        if (cursor.moveToFirst()) {
            // Valid credentials, return the cursor containing user information
            return cursor;
        } else {
            // Invalid credentials, return null
            return null;
        }
    }

    //need to update later to insert a profile pic
    public void completeUserProfile(long userId, String userDisplayName, String userBio) {
        db =this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, userDisplayName);
        values.put(PROFILEBIO, userBio);

        db.update("user", values, "id=?", new String[]{String.valueOf(userId)});
        db.close();
    }
}
