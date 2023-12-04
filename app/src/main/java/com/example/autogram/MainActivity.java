package com.example.autogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

    //This page should act as a page where the user has to either sign in or sign up to get access to the app
/*
* if the user enters their credentials, it will be cross checked in the database
*/
public class MainActivity extends AppCompatActivity{
    Button navigateToSignUpPage, signInButton;
    TextInputEditText usernameInput, passwordInput;
    private MyDatabaseHelper dbHelper;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGGED_IN = "loggedIn";
    //This should be the page after the user has logged in
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInButton = findViewById(R.id.signInButton);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        dbHelper = new MyDatabaseHelper(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate username and password
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    showToast("Username and Password is both required");
                }else{
                    //Check if there is a match in the database
                    Cursor userCursor = dbHelper.isValidCredentials(username, password);
                    if(userCursor !=null){
                        saveLoginSession(username);
                        navigateToUserProfile(userCursor);
                    }
                    else{
                        showToast("Invalid username or password");
                    }//inner else statement
                }//else statement
            }
        });//Sign in Button OnClickListener Function

        navigateToSignUpPage = findViewById(R.id.toSignUpPage);
        navigateToSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userSignUp = new Intent(MainActivity.this, SignUpPage.class);
                startActivity(userSignUp);
            }
        });

    }
        private void saveLoginSession(String username) {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_USERNAME, username);
            editor.putBoolean(KEY_LOGGED_IN, true);
            editor.apply();
        }


        private void navigateToUserProfile(Cursor userCursor) {
            // Extract user information from the cursor
            @SuppressLint("Range") String userProfileUsername = userCursor.getString(userCursor.getColumnIndex("username"));
            showToast(userProfileUsername);
            // Start UserProfile activity and pass user information
            Intent userProfileIntent = new Intent(MainActivity.this, UserProfile.class);
            userProfileIntent.putExtra("USERNAME", userProfileUsername);
            startActivity(userProfileIntent);
        }

        private void showToast(String s) {
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }



    }
