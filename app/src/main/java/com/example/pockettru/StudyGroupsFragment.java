package com.example.pockettru;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.Fragment;

public class StudyGroupsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CourseListRecViewAdapter adapter;
    private List<CourseModel> studyGroupList;
    private ArrayList<CourseModel> courseList;
    private DBHandler db;
    private SearchView searchView;
    private CardView courseRow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_studygroups, container, false);

        recyclerView = view.findViewById(R.id.courselist_recyclerview);
        searchView = view.findViewById(R.id.search_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize  study group list
        studyGroupList = new ArrayList<>();
        courseList = new ArrayList<>();


        db = new DBHandler(getContext());

        try{
            db.createDataBase();
            courseList = db.readCourses();
            Log.d("StudyGroupsFragment", "Courses loaded from DB: " + courseList.size());
        } catch (IOException e){
            e.printStackTrace();
        }



        // TODO: Add your logic to fetch study groups from a database
        // For now add some dummy data.
//        studyGroupList.add(new CourseModel("CS_101", "Introduction to Computer Science", "Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua Ut enim ad minim veniam quis nostrud exercitation ullamco laboris nisi ut aliquip"));
//        studyGroupList.add(new CourseModel("MATH_250B", "Advanced Calculus", "Students cover bla bla bla"));
//        studyGroupList.add(new CourseModel("PHYS_225", "Introduction to Physics", "Students cover bla bla bla"));


        adapter = new CourseListRecViewAdapter(courseList, getContext(), recyclerView);
        recyclerView.setAdapter(adapter);

        setupSearch();

        return view;
    }

    private void setupSearch(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

}