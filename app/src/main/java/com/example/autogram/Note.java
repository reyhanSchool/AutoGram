package com.example.autogram;

import android.graphics.Bitmap;

public class Note {
    private final Bitmap image;
    private String title;
    private String content;
    private int key;

    public Bitmap getImage() {
        return image;
    }

    public int getKey() {
        return key;
    }
    public void setKey(int key) {
        this.key = key;
    }

    public Note(String title, String content, Bitmap image, int key) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}