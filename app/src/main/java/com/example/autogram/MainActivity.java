package com.example.autogram;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

    //This page should act as a page where the user has to either sign in or sign up to get access to the app
/*
* if the user enters their credentials, it will be cross checked in the database
*/
public class MainActivity extends AppCompatActivity{
    Button navigateToSignUpPage;


    //This should be the page after the user has logged in
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigateToSignUpPage = findViewById(R.id.toSignUpPage);
        navigateToSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userSignUp = new Intent(MainActivity.this, SignUpPage.class);
                startActivity(userSignUp);
            }
        });



    }


}
