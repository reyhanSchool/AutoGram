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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageButton fab = findViewById(R.id.imageButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the new activity
                Intent intent = new Intent(create_note.this, MainActivity.class);
                startActivity(intent);
            }
        });



        Intent intent = getIntent();

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the note id
                if (intent != null) {
                    int key = intent.getIntExtra("key", -1);
                    Log.i("Key", String.valueOf(key));
                    if (key > -1) {
                        // delete note
                        deleteNoteFromDatabase(key);
                    }
                }
                Intent intent = new Intent(create_note.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // editing the existing note
        if (intent != null) {
            int key = intent.getIntExtra("key", -1);
            Log.i("Key", String.valueOf(key));
            if (key > -1) {
                // Fetch the data from the database using the key (e.g., _id)
                Note note = getNoteFromDatabase(key);
                Log.i("Hi", "reyhan");

                if (note != null) {
                    Log.i("Hi", "reyhan its found the note");
                    // Now, populate your UI fields with the data
                    EditText titleEditText = findViewById(R.id.editTextText);
                    EditText contentEditText = findViewById(R.id.editTextTextMultiLine2);
                    ImageView imageView = findViewById(R.id.selectedImageView);

                    titleEditText.setText(note.getTitle());
                    contentEditText.setText(note.getContent());
                    imageView.setImageBitmap(note.getImage());
                }
            }
        }

        Button createNoteButton = findViewById(R.id.createNoteButton);
        createNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // take inputs from forms
                // Find the EditText widgets by their IDs
                EditText titleEditText = findViewById(R.id.editTextText);
                EditText contentEditText = findViewById(R.id.editTextTextMultiLine2);

                // Extract the text entered by the user
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                // throw error if title is empty
                if (title.isEmpty()) {
                    // Set the visibility of error text to visible
                    TextView textView3 = findViewById(R.id.ErrorMessage);
                    textView3.setVisibility(View.VISIBLE);
                } else {
                    // Get the note's key from the intent
                    int key = intent.getIntExtra("key", -1);

                    if (key > -1) {
                        // Retrieve the image from the ImageView
                        ImageView imageView = findViewById(R.id.selectedImageView);
                        Bitmap updatedImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                        // Update the existing note in the database
                        deleteNoteFromDatabase(key);
                        updateNoteInDatabase(key, title, content, updatedImage, selectedColor);

                        // Optionally, you can navigate back to the main activity or do something else
                        Intent backToMainIntent = new Intent(create_note.this, MainActivity.class);
                        startActivity(backToMainIntent);
                    }
                }

                // input into database
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(create_note.this); // Use your database helper class
                SQLiteDatabase db = dbHelper.getWritableDatabase(); // Open the database

                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("content", content);
                values.put("color", selectedColor);

                ImageView imageView = findViewById(R.id.selectedImageView);

                // Get the Drawable from the ImageView
                Drawable drawable = imageView.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                // sample image
                // Load a sample image from a resource (assuming it's in your app's resources)
                // Uri photoUri = FileProvider.getUriForFile(create_note.this, "com.example.noteme.fileprovider", imageFile);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                values.put("image_data", byteArray);

                long newRowId = db.insert("post", null, values);

                if (newRowId == -1) {
                    // Insertion failed
                    Log.e("DatabaseInsert", "Error inserting data into the database");
                } else {
                    // Insertion successful
                    Log.i("DatabaseInsert", "Data inserted successfully with row ID: " + newRowId);
                }

                db.close(); // Close the database

                // Start the new activity
                Intent intent = new Intent(create_note.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button openGalleryButton = findViewById(R.id.openGalleryButton);
        openGalleryButton.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    // The image is captured and saved to the specified file. Use the file you created earlier.
                    Uri photoUri = FileProvider.getUriForFile(this, "com.example.autogram.fileprovider", imageFile);

                    // Set the image in ImageView for display
                    ImageView imageView = findViewById(R.id.selectedImageView);
                    findViewById(R.id.placeHolder).setVisibility(View.INVISIBLE);
                    imageView.setImageURI(photoUri);
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

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                // Handle the gallery result here
                Uri selectedImageUri = result.getData().getData();
                ImageView imageView = findViewById(R.id.selectedImageView); // Assuming you have an ImageView in your layout
                findViewById(R.id.placeHolder).setVisibility(View.INVISIBLE);
                imageView.setImageURI(selectedImageUri);
                findViewById(R.id.selectedImageLayout).setVisibility(View.VISIBLE);
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
                new String[]{"title", "content", "color", "image_data"},
                "id = ?",  // Assuming "_id" is the column that holds the note ID
                new String[]{String.valueOf(noteId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            String color_string = cursor.getString(cursor.getColumnIndexOrThrow("color"));
            byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow("image_data"));

            int color = Integer.parseInt(color_string);
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

    public void onColorTagClicked(View view) {
        // Called when a color tag is clicked
        // Get the content description (color identifier) from the clicked view
        int colorInt = Color.parseColor(view.getContentDescription().toString());
        selectedColor = colorInt;

        setSelectedColor(view);
    }
    private void resetColorTagsSize() {
        LinearLayout colorTagContainer = findViewById(R.id.ColorTagLayout);
        // Iterate through all color tags to reset their size
        for (int i = 0; i < colorTagContainer.getChildCount(); i++) {
            View colorTag = colorTagContainer.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = colorTag.getLayoutParams();

            // Reset the width and height to their original values
            layoutParams.width = 100;
            layoutParams.height = 100;

            // Apply the updated layout parameters
            colorTag.setLayoutParams(layoutParams);
        }
    }

    private void setSelectedColor(View view){
        resetColorTagsSize();

        // Set the desired width and height in pixels
        int newWidthInPixels = 200; // Replace with your desired width
        int newHeightInPixels = 200; // Replace with your desired height

        // Get the existing layout parameters of the ImageButton
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        // Update the width and height of the ImageButton
        layoutParams.width = newWidthInPixels;
        layoutParams.height = newHeightInPixels;

        // Apply the updated layout parameters to the ImageButton (the 'view' parameter)
        view.setLayoutParams(layoutParams);

    }
    private void updateNoteInDatabase(int noteId, String updatedTitle, String updatedContent, Bitmap updatedImage, int color) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(create_note.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", updatedTitle);
        values.put("content", updatedContent);
        values.put("color", color);

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

