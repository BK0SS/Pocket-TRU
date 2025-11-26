package com.comp3160.pockettru;

import android.transition.TransitionManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class CourseListRecViewAdapter extends RecyclerView.Adapter<CourseListRecViewAdapter.ViewHolder> implements Filterable
{
    private List<CourseModel> courseListFull;
    private List<CourseModel> courseListFiltered;
    private RecyclerView recyclerView;

    private Context context;

    public CourseListRecViewAdapter(List<CourseModel> courseList, Context context, RecyclerView recyclerView)
    {
        this.context = context;
        this.courseListFiltered = courseList;
        this.courseListFull = new ArrayList<>(courseList);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public CourseListRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListRecViewAdapter.ViewHolder holder, int position) {
        CourseModel courseModel = courseListFiltered.get(position);
        holder.bind(courseModel, position);
//        holder.id.setText(courseModel.getCourseID());
//        holder.name.setText(courseModel.getCourseName());
//        holder.description.setText(courseModel.getDescription());
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(descriptionLayout.getVisibility() == View.VISIBLE){
//
////                CourseModel courseModel = courseListFiltered.get(position);
//                String courseID = courseModel.getCourseID();
//                Bundle bundle = new Bundle();
//                bundle.putString("COURSE_ID", courseID);
//
//                SGFragmnet sgFragment = new SGFragmnet();
//                sgFragment.setArguments(bundle);
//
//                ((MainActivity)context).getSupportFragmentManager().beginTransaction()
//                        .replace(android.R.id.content, sgFragment).addToBackStack(null).commit();
////                        .replace(R.id.fragment_container, sgFragment).commit();
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return courseListFiltered.size();
    }

    public Filter getFilter(){
        return courseFilter;
    }

    private final Filter courseFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<CourseModel> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(courseListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase(Locale.ROOT).trim();
                for(CourseModel item : courseListFull){
                    if (item.getCourseID().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                    item.getCourseName().toLowerCase(Locale.ROOT).contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, @NonNull FilterResults filterResults) {
            courseListFiltered.clear();
            courseListFiltered.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id,name,description;
        LinearLayout descriptionLayout, clickableHeaderLayout;
        ImageView arrowImageView;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            id = itemView.findViewById(R.id.course_id_et);
            name = itemView.findViewById(R.id.course_name_et);
            description = itemView.findViewById(R.id.course_description_et);
            descriptionLayout = itemView.findViewById(R.id.course_description_ll);
            clickableHeaderLayout = itemView.findViewById(R.id.clickable_header_ll);
            arrowImageView = itemView.findViewById(R.id.arrow_img_view);
        }

        void bind(CourseModel courseModel, int position){
            id.setText(courseModel.getCourseID());
            name.setText(courseModel.getCourseName());
            description.setText(courseModel.getDescription());

            itemView.setOnClickListener(null);
            clickableHeaderLayout.setOnClickListener(null);

            itemView.setOnClickListener(view -> {
            if(descriptionLayout.getVisibility() == View.VISIBLE) {


                String courseID = courseModel.getCourseID();
                Bundle bundle = new Bundle();
                bundle.putString("COURSE_ID", courseID);

                SGFragmnet sgFragmnet = new SGFragmnet();
                sgFragmnet.setArguments(bundle);

                ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, sgFragmnet).addToBackStack("SG_FRAG").commit();
//
//
//                clickableHeaderLayout.setOnClickListener(null);
//                itemView.setOnClickListener(null);
            }else{
                TransitionManager.beginDelayedTransition(recyclerView);
                descriptionLayout.setVisibility(View.VISIBLE);
                arrowImageView.setRotation(180);
            }
//            clickableHeaderLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    boolean isVisible = descriptionLayout.getVisibility() == View.VISIBLE;
//
//                    TransitionManager.beginDelayedTransition(recyclerView);
//
//                    if(isVisible){
//                        descriptionLayout.setVisibility(View.GONE);
//                        arrowImageView.setRotation(0);
//                    } else{
//                        descriptionLayout.setVisibility(View.VISIBLE);
//                        arrowImageView.setRotation(180);
//                    }
//                }
//            });

            });

            clickableHeaderLayout.setOnClickListener(view -> {
                if(descriptionLayout.getVisibility() == View.VISIBLE){
                    TransitionManager.beginDelayedTransition(recyclerView);
                    descriptionLayout.setVisibility(View.GONE);
                    arrowImageView.setRotation(0);
                } else{
                    itemView.performClick();
                }
            });
//
//            itemView.setOnClickListener(view ->{
//                String courseID = courseModel.getCourseID();
//                Bundle bundle = new Bundle();
//                bundle.putString("COURSE_ID", courseID);
//
//                SGFragmnet sgFragmnet = new SGFragmnet();
//                sgFragmnet.setArguments(bundle);
//
//                ((MainActivity) context).getSupportFragmentManager().beginTransaction()
//                        .replace(android.R.id.content, sgFragmnet)
//                        .addToBackStack(null).commit();
//
//            });
        }
    }
}
