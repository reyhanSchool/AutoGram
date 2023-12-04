package com.example.autogram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements SearchView.OnQueryTextListener, PostAdapter.OnItemClickListener  {
    private static final String KEY_USERNAME = "username";
    private static final String PREFS_NAME = "LoginPrefs";

    private RecyclerView recyclerView;
    private MyDatabaseHelper dbHelper;
    private PostAdapter postAdapter;
    private List<Note> notes;
    private FloatingActionButton createNewNote;
    Button toHome,toProfile;
    Button logoutButton;
    //This should be the page after the user has logged in
    //The recyclerview will display
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //Getting the username parameter passed from the userprofile page
        Intent getUsername = getIntent();
        String userProfileName = getUsername.getStringExtra("USERNAME");

        SearchView searchView = findViewById(R.id.searchViewHomePage);
        searchView.setOnQueryTextListener(this);

        createNewNote = findViewById(R.id.CreateNoteFloatingButton);
        createNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the new activity
                Intent createNote = new Intent(HomePage.this, create_note.class);
                createNote.putExtra("USERNAME", userProfileName);
                startActivity(createNote);
            }
        });

        toHome = findViewById(R.id.toHomePage);
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigateHome = new Intent(HomePage.this, HomePage.class);
                navigateHome.putExtra("USERNAME", userProfileName);
                startActivity(navigateHome);
            }
        });

        toProfile = findViewById(R.id.toUserProfile);
        toProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passing the username parameter along with the intent
                Intent toProfilePage = new Intent(HomePage.this, UserProfile.class);
                toProfilePage.putExtra("USERNAME", userProfileName);
                startActivity(toProfilePage);
            }
        });

        //The notes should display only who the user follows (is subscribed to) and shown in chronological order
        //However if the user is subscribed to no one, all the posts in the database should be displayed
        dbHelper = new MyDatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewHomePage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Read data from the database
        notes = readNotesFromDatabase();
        postAdapter = new PostAdapter(notes);
        recyclerView.setAdapter(postAdapter);
        postAdapter.setOnItemClickListener(this); // Set the click listener

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the SharedPreferences to log out the user
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to the login page
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });


    }

    public void onFollowButtonClick(View view) {
        TextView followButton = (TextView) view;
        String buttonText = followButton.getText().toString();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String followerUsername = preferences.getString(KEY_USERNAME, "");
        long followerId = dbHelper.getUserIdByUsername(followerUsername);

        View parentView = (View) view.getParent();

        TextView postOwnerUsernameTextView = parentView.findViewById(R.id.postOwnerUsername);
        String followingUsername = postOwnerUsernameTextView.getText().toString();

        long followingId = dbHelper.getUserIdByUsername(followingUsername);

        if ("Follow".equals(buttonText)) {
            long newRowId = dbHelper.followUser(followerId, followingId);
            if (newRowId != -1) {
                showToast("User Followed");
                followButton.setText("Unfollow");
            } else {
                showToast("Error following user");
            }
        } else {
            int rowsDeleted = dbHelper.unfollowUser(followerId, followingId);
            if (rowsDeleted > 0) {
                showToast("User Unfollowed");
                followButton.setText("Follow");
            } else {
                showToast("Error unfollowing user");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        // Handle the item click here
        Note clickedNote = notes.get(position);

        // Start the new activity and pass the note data
        Intent intent = new Intent(HomePage.this, create_note.class);
        intent.putExtra("key", clickedNote.getKey());
        startActivity(intent);
    }
    public void renderNotes(List<Note> notes){
        // Read data from the database
        postAdapter = new PostAdapter(notes);
        recyclerView.setAdapter(postAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // This method is called when the user submits the search query.
        // You can implement your search logic here, if needed.
        if (query.isEmpty()){
            notes = readNotesFromDatabase();
        }
        notes = filterNotes(query);
        renderNotes(notes);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // This method is called when the text in the search view changes (e.g., as the user types).
        if (newText.isEmpty()){
            notes = readNotesFromDatabase();
        }
        notes = filterNotes(newText);
        renderNotes(notes);
        return true;
    }

    private List<Note> filterNotes(String searchText) {
        List<Note> filteredNotes = new ArrayList<>();
        for (Note note : notes) {
            // You can customize the filtering criteria based on your requirements.
            // Here, we check if the title contains the search text (case-insensitive).
            if (note.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                filteredNotes.add(note);
            }
        }
        return filteredNotes;
    }


    private List<Note> readNotesFromDatabase() {
        List<Note> notes = new ArrayList<>();

        // Get a readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Specify the table name you want to check
        String tableName = "post";

        // Check if the table exists
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});

        if (cursor != null && cursor.moveToFirst()) {
            // The table exists, proceed to read data
            cursor.close();

            // Query the database to retrieve notes
            Cursor cursor2 = db.query(
                    tableName, // The table to query
                    new String[]{"id","title", "content", "image_data"}, // The columns to retrieve
                    null, // The columns for the WHERE clause (null here means all rows)
                    null, // The values for the WHERE clause
                    null, // Don't group the rows
                    null, // Don't filter by row groups
                    null  // The sort order (null for default)
            );

            // Iterate through the cursor to retrieve notes
            if (cursor2 != null) {
                while (cursor2.moveToNext()) {
                    SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

                    String title = cursor2.getString(cursor2.getColumnIndexOrThrow("title"));
                    String content = cursor2.getString(cursor2.getColumnIndexOrThrow("content"));
                    byte[] imageData = cursor2.getBlob(cursor2.getColumnIndexOrThrow("image_data"));
                    int key = cursor2.getInt(cursor2.getColumnIndexOrThrow("id"));
                    String username = preferences.getString(KEY_USERNAME, "");
                    // Convert the byte array to a Bitmap
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                    // Create a Note object and add it to the list
                    Note note = new Note(title, content, imageBitmap, key, username);
                    notes.add(note);
                }

                cursor2.close();
            }
        }
        if (notes.isEmpty()){
            // Set the visibility of textView3
            TextView textView3 = findViewById(R.id.textView3);
            textView3.setVisibility(View.VISIBLE);
        }

        db.close();

        return notes;
    }


}
