package com.example.autogram;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

        Intent intent = getIntent();


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
                Intent deleteAction = new Intent(create_note.this, HomePage.class);

                // get the note id
                if (deleteAction != null) {
                    int key = deleteAction.getIntExtra("key", -1);
                    Log.i("Key", String.valueOf(key));
                    if (key > -1) {
                        // delete note
                        deleteNoteFromDatabase(key);
                    }
                }
                deleteAction.putExtra("USERNAME", userProfileName);
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

                        try{
                            dbHelper.insertPhotoIntoDatabase(userProfileName, title, content, postPhotoConverted);
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



        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    // The image is captured and saved to the specified file. Use the file you created earlier.
                    Uri photoUri = FileProvider.getUriForFile(this, "com.example.autogram.fileprovider", imageFile);


                    postPhoto.setImageURI(photoUri);
                    findViewById(R.id.selectedImageLayout).setVisibility(View.VISIBLE);
                } else {
                    Log.i("Camera Result", "Result data is null");
                }
            } else {
                Log.i("Camera Result", "Result code is not RESULT_OK");
            }
        });


        Button openCameraButton = findViewById(R.id.openCameraButton);
        openCameraButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
            } else {
                // Permission is already granted, proceed with camera functionality.
                PackageManager packageManager = getPackageManager();
                if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Toast.makeText(getApplicationContext(), "No camera available on this device", Toast.LENGTH_SHORT).show();
                } else {
                    // Create an image file where the captured image will be saved
                    try {
                        imageFile = createImageFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Get the content URI using FileProvider
                    Uri photoUri = FileProvider.getUriForFile(this, "com.example.autogram.fileprovider", imageFile);
                    // Start the camera app and pass the photoUri for image capture
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    cameraLauncher.launch(cameraIntent);
                }
            }
        });

    }


    private void deleteNoteFromDatabase(int noteId) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(create_note.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsAffected = db.delete(
                "post",
                "id = ?",
                new String[]{String.valueOf(noteId)}
        );

        db.close();

        if (rowsAffected > 0) {
            // The note has been deleted successfully
            Log.i("DeleteNote", "Note deleted successfully");
        } else {
            // No rows were deleted; the note might not exist
            Log.e("DeleteNote", "Error deleting the note");
        }
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
}

