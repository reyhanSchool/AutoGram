package com.example.autogram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserProfile extends AppCompatActivity {
    //This page will be for the user's profile
    /*
     * This page will be for the user's profile
     * It will contain their chosen username, profile pic and also their bio.
     * It should have a recycler view to keep all and display all the photos they have uploaded */

    private ImageView profilePic;
    private TextView profileUsername, profileBio;
    private RecyclerView userPosting;
    private FloatingActionButton createPost;
    private Button toHome, toUserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Retrieve information from MainActivity after user logs in
        Intent intent = getIntent();
        String userProfileName = intent.getStringExtra("USERNAME");
        Toast.makeText(this, userProfileName, Toast.LENGTH_SHORT).show();
        //Find all the elements in the xml file
        profilePic = findViewById(R.id.profilePicture);
        profileUsername = findViewById(R.id.usernameDisplay);
        profileBio = findViewById(R.id.bioDisplay);
        userPosting = findViewById(R.id.userPostings);
        createPost = findViewById(R.id.CreateNoteFloatingButton);
        toHome = findViewById(R.id.toHomePage);
        toUserProfile = findViewById(R.id.toUserProfile);

        //to Navigate to the home page where it shows the feed
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHomePage = new Intent(UserProfile.this, HomePage.class);
                startActivity(toHomePage);
            }
        });

        //to Navigate to the create new post page
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCreatePost = new Intent(UserProfile.this, create_note.class);
                startActivity(toCreatePost);
            }
        });

        toUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserProfile.this, "You are on your profile page", Toast.LENGTH_SHORT).show();
            }
        });

    }
}