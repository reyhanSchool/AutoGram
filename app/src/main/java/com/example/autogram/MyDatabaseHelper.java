package com.example.autogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AutoGram.db";
    private static final int DATABASE_VERSION = 15;
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String PROFILEBIO = "profile_bio";
    private static final String SUBSCRIPTION_TABLE = "subscription";
    private static final String SUBSCRIPTION_ID = "subscription_id";
    private static final String SUBSCRIBER_ID = "subscriber_id";

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

        // Create a table for tracking subscriptions
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SUBSCRIPTION_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SUBSCRIBER_ID + " INTEGER,"
                + SUBSCRIPTION_ID + " INTEGER,"
                + "FOREIGN KEY(" + SUBSCRIBER_ID + ") REFERENCES user(id),"
                + "FOREIGN KEY(" + SUBSCRIPTION_ID + ") REFERENCES user(id))"
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

    public boolean isEmailValid(String userEmail) {
        db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM user WHERE " +
                "email = ?", new String[]{userEmail});
        boolean emailExists=cursor.moveToFirst();

        //Return true if the email is in the database
        return emailExists;
    }

    //This function should return the user bio from the database
    public String getUserBio(String userProfileName) {
        db=this.getReadableDatabase();
        String query = "SELECT " + PROFILEBIO +
                " FROM user" +
                " WHERE " + USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{userProfileName});
        String userBio = null;
        if(cursor.moveToFirst()){
            userBio = cursor.getString(cursor.getColumnIndexOrThrow(PROFILEBIO));
        }
        return userBio;
    }

    //Method to insert a subscription
    public long insertSubscription(long subscriberId, long subcriptionId){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SUBSCRIPTION_ID, subcriptionId);
        values.put(SUBSCRIPTION_ID, subcriptionId);
        long newRowId = db.insert(SUBSCRIPTION_TABLE, null, values);
        db.close();
        return newRowId;
    }

    //Method to remove a subscription
    public int removeSubscription(long subscriberId, long subscriptionId){
        db=this.getReadableDatabase();
        int rowsDeleted = db.delete(
                SUBSCRIPTION_TABLE,
                SUBSCRIBER_ID + " = ? AND " + SUBSCRIPTION_ID + " = ?",
                new String[]{String.valueOf(subscriberId), String.valueOf(subscriptionId)}
        );

        // Close the database connection
        db.close();

        return rowsDeleted;
    }

    // Add a method to retrieve the list of subscribed IDs for a user
    public List<Long> getSubscribedIds(long subscriberId) {
        db = this.getReadableDatabase();

        List<Long> subscribedIds = new ArrayList<>();

        String query = "SELECT " + SUBSCRIPTION_ID +
                " FROM " + SUBSCRIPTION_TABLE +
                " WHERE " + SUBSCRIBER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(subscriberId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long subscriptionId = cursor.getLong(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
                subscribedIds.add(subscriptionId);
            } while (cursor.moveToNext());

            // Close the cursor
            cursor.close();
        }

        // Close the database
        db.close();

        return subscribedIds;
    }


    public void insertPhotoIntoDatabase(String userProfileName, String title, String content, Bitmap postPhotoConverted) {
        db = this.getWritableDatabase();

        //Find the user_id with the given userProfileName
        long userID = getUserIdByUsername(userProfileName);

        if(userID != -1){
            ContentValues values = new ContentValues();
            values.put("user_id", userID);
            values.put("title", title);
            values.put("content", content);

            //Convert the Bitmap to BLOB
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            postPhotoConverted.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put("image_data", byteArray);

            //insert to database
            db.insert("post", null, values);

        }

        db.close();
    }

    private long getUserIdByUsername(String username){
        db = this.getReadableDatabase();

        String query = "SELECT id FROM user WHERE " + USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        long userId = -1; // Default value if the username is not found

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            cursor.close(); // Close the cursor after extracting the value
        }

        // Close the database connection
        db.close();

        return userId;
    }
}
