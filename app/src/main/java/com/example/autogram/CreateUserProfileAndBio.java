package com.example.autogram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CreateUserProfileAndBio extends AppCompatActivity {
    /*
    In this page, they will be able to add a profile pic if they want
    set their display name
    also be able to add a bio
    should be able to add their first post if they want
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile_and_bio);
    }
}