package com.example.autogram;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;

public class CreateUserProfileAndBio extends AppCompatActivity {
    /*
    In this page, they will be able to add a profile pic if they want
    set their display name
    also be able to add a bio
    should be able to add their first post if they want
     */
private static final int REQUEST_IMAGE_CAPTURE =1;
private static final int REQUEST_IMAGE_PICK = 2;
private ImageView uploadProfilePic;
Button submitInfo;
MyDatabaseHelper dbHelper;
TextInputEditText displayName, displayBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile_and_bio);
        submitInfo = findViewById(R.id.submitInfo);
        displayName = findViewById(R.id.displayName);
        displayBio = findViewById(R.id.displayBio);
        dbHelper = new MyDatabaseHelper(this);

        //getting the user id from the sign up page
        Intent getUserID = getIntent();
        long userId = getUserID.getLongExtra("USER_ID", -1);


        submitInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUsernameInput()){
                    insertIntoDatabase(userId, displayName, displayBio);
                    String insertDisplayName = displayName.getText().toString();
                    Intent toUserProfile = new Intent(CreateUserProfileAndBio.this, UserProfile.class);
                    //passing the displayName to the userProfile page
                    toUserProfile.putExtra("USERNAME", insertDisplayName);
                    startActivity(toUserProfile);
                }
            }//onClick
        });

        uploadProfilePic = findViewById(R.id.uploadProfilePic);
        //Modify this so it works
        uploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void insertIntoDatabase(long userId, TextInputEditText displayName, TextInputEditText displayBio) {
        String userDisplayName = displayName.getText().toString();
        String userBio = displayBio.getText().toString();
        //Going to be inserting the displayName and bio into the database, need to update later to insert the photo
        dbHelper.completeUserProfile(userId, userDisplayName, userBio);
    }

    private void showPopup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");

        //Add a button for Gallery
        builder.setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //openGallery will handle opening the gallery and selecting the photo to be used
                openGallery();
            }
        });

        //Add the button for camera
        builder.setNeutralButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dispatchTackPictureIntent will handle using the camera to take the photo.
                dispatchTakePictureIntent();
            }
        });
    }

    //To open the photo gallery and select a photo
    private void openGallery() {

    }

    //To open the camera and take picture
    private void dispatchTakePictureIntent() {

    }

    //validate that user has entered a name
    private boolean validateUsernameInput(){
        return !TextUtils.isEmpty(displayName.getText().toString());
    }

}