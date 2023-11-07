package com.example.autogram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class UserProfile extends AppCompatActivity {
    //This page will be for the user's profile
    /*
     * This page will be for the user's profile
     * It will contain their chosen username, profile pic and also their bio.
     * It should have a recycler view to keep all and display all the photos they have uploaded */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }
}