package com.example.pockettru;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class StudyGroupRecViewAdapter extends RecyclerView.Adapter<StudyGroupRecViewAdapter.ViewHolder>
{
    private ArrayList<StudyGroupModel> studyGroupList;
    private Context context;

    public StudyGroupRecViewAdapter(ArrayList<StudyGroupModel> studyGroupList)
    {
        this.studyGroupList = studyGroupList;
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

    @Override
    public int getItemCount() {
        return studyGroupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView author, date, time, description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.sg_author_et);
            date = itemView.findViewById(R.id.sg_date_et);
            time = itemView.findViewById(R.id.sg_time_et);
            description = itemView.findViewById(R.id.sg_description_et);
        }
    }
}

