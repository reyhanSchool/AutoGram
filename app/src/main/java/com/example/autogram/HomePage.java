package com.example.autogram;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements SearchView.OnQueryTextListener, PostAdapter.OnItemClickListener  {
    private RecyclerView recyclerView;
    private MyDatabaseHelper dbHelper;
    private PostAdapter postAdapter;
    private List<Note> notes;

    //This should be the page after the user has logged in
    //The recyclerview will display
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        SearchView searchView = findViewById(R.id.searchViewHomePage);
        searchView.setOnQueryTextListener(this);

        FloatingActionButton fab = findViewById(R.id.CreateNoteFloatingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the new activity
                Intent intent = new Intent(HomePage.this, create_note.class);
                startActivity(intent);
            }
        });

        dbHelper = new MyDatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewHomePage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Read data from the database
        notes = readNotesFromDatabase();
        postAdapter = new PostAdapter(notes);
        recyclerView.setAdapter(postAdapter);
        postAdapter.setOnItemClickListener(this); // Set the click listener


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
                    String title = cursor2.getString(cursor2.getColumnIndexOrThrow("title"));
                    String content = cursor2.getString(cursor2.getColumnIndexOrThrow("content"));
                    byte[] imageData = cursor2.getBlob(cursor2.getColumnIndexOrThrow("image_data"));
                    int key = cursor2.getInt(cursor2.getColumnIndexOrThrow("id"));

                    // Convert the byte array to a Bitmap
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                    // Create a Note object and add it to the list
                    Note note = new Note(title, content, imageBitmap, key);
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
