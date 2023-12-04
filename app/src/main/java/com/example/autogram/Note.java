package com.example.autogram;

import android.graphics.Bitmap;
import android.database.sqlite.SQLiteDatabase;
public class Note {
    private final Bitmap image;
    private String title;
    private String content;
    private int key;
    private String username;

    public Bitmap getImage() {
        return image;
    }

    public int getKey() {
        return key;
    }
    public void setKey(int key) {
        this.key = key;
    }

    public Note(String title, String content, Bitmap image, int key, String username) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.key = key;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }
}
