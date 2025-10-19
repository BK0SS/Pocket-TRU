package com.example.pockettru;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseListRecViewAdapter extends RecyclerView.Adapter<CourseListRecViewAdapter.ViewHolder>
{
    private List<CourseModel> courseList;
    private Context context;

    CourseListRecViewAdapter(List<CourseModel> courseList, Context context)
    {
        this.courseList = courseList;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseListRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListRecViewAdapter.ViewHolder holder, int position) {
        CourseModel courseModel = courseList.get(position);
        holder.id.setText(courseModel.getCourseID());
        holder.name.setText(courseModel.getCourseName());
        holder.description.setText(courseModel.getDescription());

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id,name,description;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            id = itemView.findViewById(R.id.course_id_et);
            name = itemView.findViewById(R.id.course_name_et);
            description = itemView.findViewById(R.id.course_description_et);

        }
    }
}
