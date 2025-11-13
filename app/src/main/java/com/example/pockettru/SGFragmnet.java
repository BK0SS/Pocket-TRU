package com.example.pockettru;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SGFragmnet extends Fragment
{

    ArrayList<StudyGroupModel> studyGroupList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StudyGroupRecViewAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String courseId;
    private Button add_sg_button;

    //This cheks the bundle for course id from the other fragment
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString("COURSE_ID");
        }
    }

    //TODO: 1.make entries in firebase for each course 2. make this look pretier and user friendly

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sg_fragment, container, false);

        recyclerView = view.findViewById(R.id.studygroup_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudyGroupRecViewAdapter(studyGroupList);

        recyclerView.setAdapter(adapter);

        if (courseId != null) {
            fetchStudyGroups();
        } else {

            Toast.makeText(getContext(), "Error: Course ID not found.", Toast.LENGTH_SHORT).show();
        }


        //Adding a course
        add_sg_button = view.findViewById(R.id.add_sg_button);
        add_sg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });

        return view;
    }

    private void fetchStudyGroups()
    {
        // The courseId is the name of the collection itself.
        // We just need to get all documents from that collection.
        db.collection(courseId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {

                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getContext(), "No study groups found for this course.", Toast.LENGTH_SHORT).show();
                            }

                            studyGroupList.clear();
                            for(QueryDocumentSnapshot document : task.getResult())
                            {
                                StudyGroupModel groupModel = new StudyGroupModel(document.getString("author"), document.getString("date"), document.getString("time"), document.getString("description"));
                                studyGroupList.add(groupModel);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void dialog()
    {
        View view = getLayoutInflater().inflate(R.layout.create_sg_dialog, null);
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(getContext());

        TextInputEditText author = view.findViewById(R.id.author_dialog);
        TextInputEditText date = view.findViewById(R.id.date_dialog);
        TextInputEditText time = view.findViewById(R.id.time_dialog);
        TextInputEditText description = view.findViewById(R.id.description_dialog);

        alertDialog.setTitle("Add Study Group");
        alertDialog.setView(view).setPositiveButton("Make", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String authorText = author.getText().toString();
                String dateText = date.getText().toString();
                String timeText = time.getText().toString();
                String descriptionText = description.getText().toString();

                StudyGroupModel newGroup = new StudyGroupModel(authorText, dateText, timeText, descriptionText);

                db.collection(courseId).add(newGroup);
                studyGroupList.add(newGroup);
                adapter.notifyItemInserted(studyGroupList.size() - 1);
                Toast.makeText(getContext(), "Study group added!", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();




    }
}
