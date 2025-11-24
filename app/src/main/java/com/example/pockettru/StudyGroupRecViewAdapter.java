package com.example.pockettru;

import android.transition.TransitionManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudyGroupRecViewAdapter extends RecyclerView.Adapter<StudyGroupRecViewAdapter.ViewHolder>
{
    private ArrayList<StudyGroupModel> studyGroupList;

    public StudyGroupRecViewAdapter(ArrayList<StudyGroupModel> studyGroupList)
    {
        this.studyGroupList = studyGroupList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
