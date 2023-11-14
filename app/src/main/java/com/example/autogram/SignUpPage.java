package com.example.autogram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class SignUpPage extends AppCompatActivity {
    Button loginReturn, userRegistered;
    TextInputEditText firstname, lastname, email, password;
    MyDatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        loginReturn = findViewById(R.id.backToLogin);
        userRegistered = findViewById(R.id.registerUserAccount);
        firstname = findViewById(R.id.firstNameInput);
        lastname = findViewById(R.id.lastNameInput);
        email = findViewById(R.id.signUpUsername);
        password = findViewById(R.id.signUpPassword);

        loginReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToLogin = new Intent(SignUpPage.this, MainActivity.class);
                startActivity(returnToLogin);
            }
        });

        userRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateInput()){
                    long newRowId = dbhelper.insertDataIntoDatabase( firstname.getText().toString(),
                            firstname.getText().toString(),
                            lastname.getText().toString(),
                            email.getText().toString(),
                            password.getText().toString());
                    if(newRowId != -1){
                        Toast.makeText(SignUpPage.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        Intent registered = new Intent(SignUpPage.this, UserProfile.class);
                        startActivity(registered);
                    }else{
                        Toast.makeText(SignUpPage.this, "Error registering User", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignUpPage.this, "Please enter all the fields to register", Toast.LENGTH_SHORT).show();
                }

            }
        });

        /*Once the user creates an account, they should be taken to a new page to edit their profile
         * Such as add a profile name that can be different from their first name
         * and also add a profile pic for the user
         * */



    }

    //This function will false if one of the text fields is empty
    private boolean validateInput() {
        return !TextUtils.isEmpty(firstname.getText().toString()) &&
                !TextUtils.isEmpty(lastname.getText().toString()) &&
                !TextUtils.isEmpty(email.getText().toString()) &&
                !TextUtils.isEmpty(password.getText().toString());
    }


}