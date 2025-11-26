package com.comp3160.pockettru;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class StudyGroupRecViewAdapter extends RecyclerView.Adapter<StudyGroupRecViewAdapter.ViewHolder>
{
    private Context context;
    private ArrayList<StudyGroupModel> studyGroupList;
    private BookmarkDBHandler bookmarkDBHandler;
    private boolean isBookmarkMode = false;
    private String collectionName;
    public StudyGroupRecViewAdapter(Context context, ArrayList<StudyGroupModel> studyGroupList, String collectionName) { // <-- Modify constructor
        this.context = context;
        this.studyGroupList = studyGroupList;
        this.bookmarkDBHandler = new BookmarkDBHandler(context);
        this.collectionName = collectionName; // <-- Initialize it
    }


    public StudyGroupRecViewAdapter(Context context, ArrayList<StudyGroupModel> studyGroupList, boolean isBookmarkMode) {
        this.context = context;
        this.studyGroupList = studyGroupList;
        this.isBookmarkMode = isBookmarkMode; // Set the mode
        this.bookmarkDBHandler = new BookmarkDBHandler(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.studygroup_row,parent,false);
        return new StudyGroupRecViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       StudyGroupModel studyGroupModel = studyGroupList.get(position);
        holder.author.setText(studyGroupModel.getAuthor());
        holder.date.setText(studyGroupModel.getDate());
        holder.time.setText(studyGroupModel.getTime());
        holder.description.setText(studyGroupModel.getDescription());
        if (bookmarkDBHandler.isBookmarked(studyGroupModel.getDescription())) {
            holder.bookmark_sg_button.setText("Un-Bookmark");
        } else {
            holder.bookmark_sg_button.setText("Bookmark");
        }
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String groupAuthorEmail = studyGroupModel.getAuthor();
        String currentUserEmail = (currentUser != null) ? currentUser.getEmail() : null;

        // Check if the current user is the author
        if (currentUserEmail != null && currentUserEmail.equalsIgnoreCase(groupAuthorEmail)) {
            // User is the author
            holder.delete_sg_button.setVisibility(View.VISIBLE);

            holder.delete_sg_button.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Study Group")
                        .setMessage("Are you sure you want to permanently delete this study group?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteStudyGroupFromFirestore(studyGroupModel.getFirestoreDocumentId(), holder.getAdapterPosition());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        } else {

            holder.delete_sg_button.setVisibility(View.GONE);
        }


        holder.bookmark_sg_button.setOnClickListener(v -> {

            if (bookmarkDBHandler.isBookmarked(studyGroupModel.getDescription())) {

                new AlertDialog.Builder(context)
                        .setTitle("Remove Bookmark")
                        .setMessage("Are you sure you want to remove this bookmark?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            int currentPosition = holder.getAdapterPosition();
                            // Check if the item position is still valid
                            if (currentPosition == RecyclerView.NO_POSITION) return;

                            // Delete from the database
                            bookmarkDBHandler.deleteStudyGroup(studyGroupModel.getDescription());
                            Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                            if (isBookmarkMode) {

                                studyGroupList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                notifyItemRangeChanged(currentPosition, studyGroupList.size());
                            } else {

                                holder.bookmark_sg_button.setText("Bookmark");
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {

                bookmarkDBHandler.addStudyGroup(studyGroupModel);
                holder.bookmark_sg_button.setText("Un-Bookmark");
                Toast.makeText(context, "Study group bookmarked!", Toast.LENGTH_SHORT).show();
            }
        });


        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailDialog(studyGroupModel.getAuthor());
            }
        });
    }

    private void showEmailDialog(String emailAddress) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_send_email, null);
        builder.setView(dialogView);

        final EditText messageEditText = dialogView.findViewById(R.id.email_message_et);
        builder.setTitle("Send a message to " + emailAddress);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = messageEditText.getText().toString();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Study Group Inquiry");
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void deleteStudyGroupFromFirestore(String documentId, int position) {
        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(context, "Error: Cannot delete item without an ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the dynamic collection name passed to the adapter
        FirebaseFirestore.getInstance().collection(collectionName).document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Deletion successful
                    Toast.makeText(context, "Study group deleted.", Toast.LENGTH_SHORT).show();
                    if (position >= 0 && position < studyGroupList.size()) {
                        studyGroupList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, studyGroupList.size());
                    }
                }).addOnFailureListener(e -> {
                    // Deletion failed
                    Toast.makeText(context, "Error: Could not delete study group.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return studyGroupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView author, date, time, description;
        Button bookmark_sg_button, delete_sg_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.sg_author_et);
            date = itemView.findViewById(R.id.sg_date_et);
            time = itemView.findViewById(R.id.sg_time_et);
            description = itemView.findViewById(R.id.sg_description_et);
            bookmark_sg_button = itemView.findViewById(R.id.bookmark_button);
            delete_sg_button = itemView.findViewById(R.id.delete_sg_button);
        }
    }
}

