package com.example.autogram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpPage extends AppCompatActivity {
    Button loginReturn, userRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        loginReturn = findViewById(R.id.backToLogin);
        userRegistered = findViewById(R.id.registerUserAccount);

        loginReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToLogin = new Intent(SignUpPage.this, MainActivity.class);
                startActivity(returnToLogin);
            }
        });

        /*Once the user creates an account, they should be taken to a new page to edit their profile
         * Such as add a profile name that can be different from their first name
         * and also add a profile pic for the user
         * */



    }
}