package com.comp3160.pockettru;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SGFragmnet extends Fragment
{

    ArrayList<StudyGroupModel> studyGroupList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StudyGroupRecViewAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String courseId;
    private Button add_sg_button, bookmark_sg_button;

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
        adapter = new StudyGroupRecViewAdapter(getContext(), studyGroupList, courseId);

        recyclerView.setAdapter(adapter);

        if (courseId != null) {
            fetchStudyGroups();
        } else {

            Toast.makeText(getContext(), "Error: Course ID not found.", Toast.LENGTH_SHORT).show();
        }
//        //Adding a course
        add_sg_button = view.findViewById(R.id.add_sg_button);
        add_sg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
        //bookmark button



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
                        if(!isAdded()){
                            return;
                        }
                        if(task.isSuccessful())
                        {

                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getContext(), "No study groups found for this course.", Toast.LENGTH_SHORT).show();
                            }

                            studyGroupList.clear();
                            for(QueryDocumentSnapshot document : task.getResult())
                            {
                                StudyGroupModel groupModel = new StudyGroupModel(document.getString("author"), document.getString("date"), document.getString("time"), document.getString("description"));
                                groupModel.setFirestoreDocumentId(document.getId());
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

        TextInputEditText author = view.findViewById(R.id.edit_text_author);
        Button dateButton = view.findViewById(R.id.button_select_date);
        Button timeButton = view.findViewById(R.id.button_select_time);
        TextInputEditText description = view.findViewById(R.id.edit_text_description);

        final Calendar calendar = Calendar.getInstance();

        dateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateButton.setText(dateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        timeButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view12, hourOfDay, minute) -> {
                timeButton.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });
        // Get current user and set author field
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            author.setText(currentUser.getEmail());
            author.setEnabled(false); // Make it unchangeable
        }

        alertDialog.setTitle("Add Study Group");
        alertDialog.setView(view).setPositiveButton("Make", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String authorText = author.getText().toString();
                String dateText = dateButton.getText().toString();
                String timeText = timeButton.getText().toString();
                String descriptionText = description.getText().toString();

                StudyGroupModel newGroup = new StudyGroupModel(authorText, dateText, timeText, descriptionText);

                // When adding a document to a collection that does not exist,
                // Firestore automatically creates the collection.
                db.collection(courseId).add(newGroup)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    studyGroupList.add(newGroup);
                                    adapter.notifyItemInserted(studyGroupList.size() - 1);
                                    Toast.makeText(getContext(), "Study group added!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to create study group.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }
}
