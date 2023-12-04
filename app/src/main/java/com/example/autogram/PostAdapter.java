package com.example.autogram;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.NoteViewHolder> {
    private List<Note> noteList; // A list of notes

    public PostAdapter(List<Note> notes) {
        this.noteList = notes;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for a single note item and create a view holder.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticky_note_item, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        // Populate views in the view holder with data from the note at the given position.
        Note note = noteList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());
        holder.usernameView.setText(note.getUsername());

        // Set the image data
        holder.noteImage.setImageBitmap(note.getImage());

        // Set a click listener for the item view (the whole item)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trigger the onItemClick method when an item is clicked
                int position = holder.getAdapterPosition();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        ImageView noteImage; // Add the ImageView field for the image
        TextView usernameView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.noteTitle);
            contentTextView = itemView.findViewById(R.id.noteContent);
            noteImage = itemView.findViewById(R.id.postImage); // Initialize the ImageView field
            usernameView = itemView.findViewById(R.id.postOwnerUsername);
        }
    }


    private OnItemClickListener onItemClickListener;

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Setter for the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


}
