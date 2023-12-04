package com.example.autogram;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class create_note extends AppCompatActivity {

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private int selectedColor = 16770541; // To store the selected color
    private static final int CAMERA_REQUEST = 1888;
    private static ActivityResultLauncher<Intent> cameraLauncher;
    private static ActivityResultLauncher<Intent> galleryLauncher;
    private File imageFile = null;
    private Bitmap selectedBitmap = null;
    ImageButton toHome;
    EditText postTitle, postDescription;
    ImageView postPhoto = null;
    Button deleteButton, createNoteButton;
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        Intent getFromOtherPage = getIntent();
        String userProfileName = getFromOtherPage.getStringExtra("USERNAME");
        //Finding all the elements in the page
        postTitle = findViewById(R.id.postTitle);
        postDescription = findViewById(R.id.postDescription);
        postPhoto = findViewById(R.id.userSelectedPhoto);
        toHome = findViewById(R.id.cancelUpload);
        deleteButton = findViewById(R.id.deleteButton);
        createNoteButton = findViewById(R.id.createNoteButton);

        dbHelper = new MyDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        postId = intent.getIntExtra("NOTE_ID", -1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Camera permission has not been granted, request it
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            }
        }

        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the new activity
                Intent intent = new Intent(create_note.this, HomePage.class);
                startActivity(intent);
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dbHelper.insertPhotoIntoDatabase(userProfileName, title, content, postPhotoConverted);
                /*postTitle = findViewById(R.id.postTitle);
        postDescription = findViewById(R.id.postDescription);
        postPhoto = findViewById(R.id.userSelectedPhoto); */

                // Extract the text entered by the user
                String title = postTitle.getText().toString();
                String content = postDescription.getText().toString();
                dbHelper.deletePhotoFromDatabase(userProfileName, title, content);
                Intent deleteAction = new Intent(create_note.this, HomePage.class);

                startActivity(deleteAction);
            }
        });

        // editing the existing note
        if (intent != null) {
            int key = intent.getIntExtra("key", -1);
            if (key > -1) {
                // Fetch the data from the database using the key (e.g., _id)
                Note note = getNoteFromDatabase(key);

                if (note != null) {
                    // Now, populate your UI fields with the data
                    postTitle.setText(note.getTitle());
                    postDescription.setText(note.getContent());
                    postPhoto.setImageBitmap(note.getImage());
                }
            }
        }

        //When the button is clicked, it will open the photo gallery on the device and allow the user to select a photo
        //THe photo will be assigned to the imageVIew postPhoto and displayed near the bottom
        Button openGalleryButton = findViewById(R.id.openGalleryButton);
        openGalleryButton.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                // Handle the gallery result here
                Uri selectedImageUri = result.getData().getData();
                findViewById(R.id.placeHolder).setVisibility(View.INVISIBLE);
                postPhoto.setImageURI(selectedImageUri);
                findViewById(R.id.selectedImageLayout).setVisibility(View.VISIBLE);
            }
        });

        //When this button is clicked, it will pass the user's display name, title, description, image to be inserted to the database
        createNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Extract the text entered by the user
                String title = postTitle.getText().toString();
                String content = postDescription.getText().toString();

                // throw error if title is empty
                if (title.isEmpty()) {
                    // Set the visibility of error text to visible
                    Toast.makeText(create_note.this,"Please enter a title", Toast.LENGTH_LONG).show();
                } else {
                        Bitmap postPhotoConverted = ((BitmapDrawable) postPhoto.getDrawable()).getBitmap();
                        String savedUsername = getSavedUsername();
                        try{

                                ContentValues values = new ContentValues();
                                values.put("title", title);
                                values.put("content", content);
                                // If an image is selected, convert it to a byte array and store it in the database
                                Bitmap imageBitmap = ((BitmapDrawable) postPhoto.getDrawable()).getBitmap();
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                byte[] imageBytes = outputStream.toByteArray();
                                values.put("image_data", imageBytes);
                                //insert to database
                                db.insert("post", null, values);


                            Toast.makeText(create_note.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                            // Optionally, you can navigate back to the main activity or do something else
                            Intent backToMainIntent = new Intent(create_note.this, HomePage.class);
                            startActivity(backToMainIntent);
                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(create_note.this, "Did Not Save", Toast.LENGTH_SHORT).show();
                        }


                }

            }
        });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleCameraResult(result);
                    }
                }
        );


            Button openCameraButton = findViewById(R.id.openCameraButton);
            openCameraButton.setOnClickListener(v -> {
                cameraMethod();
            }
        );

    } //End of OnCreate

    private String getSavedUsername() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USERNAME, "");
    }


    private Note getNoteFromDatabase(int noteId) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "post",
                new String[]{"title", "content", "image_data"},
                "id = ?",  // Assuming "_id" is the column that holds the note ID
                new String[]{String.valueOf(noteId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow("image_data"));

            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            cursor.close();

            return new Note(title, content, imageBitmap, noteId);
        }

        return null;
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void updateNoteInDatabase(int noteId, String updatedTitle, String updatedContent, Bitmap updatedImage) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(create_note.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", updatedTitle);
        values.put("content", updatedContent);


        // Convert the Bitmap to a byte array for storage in the database
        byte[] imageData = bitmapToByteArray(updatedImage);
        values.put("image_data", imageData);

        int rowsAffected = db.update(
                "post",
                values,
                "id = ?",
                new String[]{String.valueOf(noteId)}
        );

        db.close();

        if (rowsAffected > 0) {
            // The update was successful
            Log.i("UpdateNote", "Note updated successfully");
        } else {
            // The update failed
            Log.e("UpdateNote", "Error updating the note");
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void cameraMethod() {
        // Create an intent to capture an image using the camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Launch the camera activity and handle the result using cameraLauncher
        cameraLauncher.launch(takePictureIntent);
    }

    private void handleCameraResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data"); // Retrieve the captured image bitmap

                    // Display the captured image in an ImageView
                    postPhoto.setImageBitmap(imageBitmap);
                }
            }
        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            // The image capture was canceled by the user
            Toast.makeText(create_note.this, "Image capture canceled", Toast.LENGTH_SHORT).show();
        } else {
            // The image capture failed, handle this situation if necessary
            Toast.makeText(create_note.this, "Image capture failed", Toast.LENGTH_SHORT).show();
        }
    }

}

