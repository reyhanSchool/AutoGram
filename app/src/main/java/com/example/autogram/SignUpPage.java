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
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        dbHelper = new MyDatabaseHelper(SignUpPage.this);

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
                    //String firstName, String lastName, String username, String email, String password
                    String firstName = firstname.getText().toString();
                    String lastName = lastname.getText().toString();
                    String userEmail = email.getText().toString();
                    String userPassword = password.getText().toString();
                    //check if email is already in the database.
                    boolean EmailIsSafe = dbHelper.isEmailValid(userEmail);

                    if(!dbHelper.isEmailValid(userEmail)){
                        long newRowId = dbHelper.insertDataIntoDatabase(firstName, lastName, null , userEmail, userPassword);
                        if(newRowId != -1){
                            //Once the user is successfully added into the database, they should be navigated to CreateUserProfileAndBio page to add
                            //their profile pic, name nd bio
                            Toast.makeText(SignUpPage.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            Intent registered = new Intent(SignUpPage.this, CreateUserProfileAndBio.class);
                            // Pass user id as extras to the next activity
                            registered.putExtra("USER_ID", newRowId);
                            startActivity(registered);
                        }else{
                            Toast.makeText(SignUpPage.this, "Error registering User", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SignUpPage.this,"Email is already registered", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(SignUpPage.this, "Please enter all the fields to register", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //This function will false if one of the text fields is empty
    private boolean validateInput() {
        return !TextUtils.isEmpty(firstname.getText().toString()) &&
                !TextUtils.isEmpty(lastname.getText().toString()) &&
                !TextUtils.isEmpty(email.getText().toString()) &&
                !TextUtils.isEmpty(password.getText().toString());
    }


}