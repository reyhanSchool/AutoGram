package com.example.autogram;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile_and_bio);

        uploadProfilePic = findViewById(R.id.uploadProfilePic);
        uploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });

    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        openGallery();
                        break;
                }
            }
        });//end of builder.setItems
    }

    //To open the photo gallery and select a photo
    private void openGallery() {

    }

    //To open the camera and take picture
    private void dispatchTakePictureIntent() {

    }
}