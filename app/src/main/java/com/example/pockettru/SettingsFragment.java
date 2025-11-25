package com.example.pockettru;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "ThemePref";
    private static final String KEY_IS_DARK_MODE = "isDarkModeOn";
    private MaterialTextView user_email;
    private FirebaseAuth mAuth;
    private RecyclerView bookmarksRecyclerView;
    private StudyGroupRecViewAdapter bookmarksAdapter;
    private BookmarkDBHandler bookmarkDBHandler;
    private List<StudyGroupModel> bookmarkedGroups;
    private MaterialTextView bookmarksTitle, noBookmarksText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user_email = view.findViewById(R.id.email_text);
        user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        super.onViewCreated(view, savedInstanceState);
        MaterialButton logout_button = view.findViewById(R.id.logout_button);
        SwitchMaterial switchTheme = view.findViewById(R.id.switch_theme);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        MaterialButton deleteuser_button = view.findViewById(R.id.delete_button);

        Button change_email = view.findViewById(R.id.change_email_button);

        bookmarksRecyclerView = view.findViewById(R.id.bookmarks_recycler_view);
        bookmarksTitle = view.findViewById(R.id.bookmarks_title);
        noBookmarksText = view.findViewById(R.id.text_no_bookmarks);
        bookmarkDBHandler = new BookmarkDBHandler(getContext());

        setupBookmarkRecyclerView();
        loadBookmarks();

        boolean isDarkModeOn = sharedPreferences.getBoolean(KEY_IS_DARK_MODE, false);
        switchTheme.setChecked(isDarkModeOn);

        //switch theme
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_DARK_MODE, isChecked);
            editor.apply();

            //stack overflow
            int mode = isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES :
                    AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode); // This can cause issues
        });

        //logout
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), Auth.class);
                startActivity(intent);
            }

        });
        user_email.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(getContext())
                        .setTitle("From Bogdan")
                        .setMessage("Developed with ❤️ for Melani")
                        .setPositiveButton("Que bonita!", null)
                        .show();
                return true;
            }
        });
        //delete profile
        deleteuser_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDeleteDialog();
            }
        });

        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeEmailDialog();
            }
        });

    }
    public void onResume() {
        super.onResume();
        // Reload bookmarks every time the fragment becomes visible

        if (bookmarkDBHandler != null) {
            loadBookmarks();
        }
    }
    private void setupBookmarkRecyclerView() {
        bookmarkedGroups = new ArrayList<>();
        bookmarksAdapter = new StudyGroupRecViewAdapter(getContext(), (ArrayList<StudyGroupModel>) bookmarkedGroups, true);
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookmarksRecyclerView.setAdapter(bookmarksAdapter);
    }
    private void loadBookmarks() {

        bookmarkedGroups.clear();

        List<StudyGroupModel> newBookmarks = bookmarkDBHandler.getAllStudyGroups();
        bookmarkedGroups.addAll(newBookmarks);

        bookmarksAdapter.notifyDataSetChanged();


        if (bookmarkedGroups.isEmpty()) {
            bookmarksTitle.setVisibility(View.GONE);
            noBookmarksText.setVisibility(View.VISIBLE);
        } else {
            bookmarksTitle.setVisibility(View.VISIBLE);
            noBookmarksText.setVisibility(View.GONE);
        }
    }

    //found this code online it works perfect
    private void showChangeEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Email");

        Context context = getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final EditText currentPasswordInput = new EditText(context);
        currentPasswordInput.setHint("Current Password");
        currentPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(currentPasswordInput);

        final EditText newEmailInput = new EditText(context);
        newEmailInput.setHint("New Email");
        newEmailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(newEmailInput);

        builder.setView(layout);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String currentPassword = currentPasswordInput.getText().toString();
            String newEmail = newEmailInput.getText().toString();

            if (currentPassword.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            reauthenticateAndChangeEmail(currentPassword, newEmail);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void reauthenticateAndChangeEmail(String password, String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SettingsFragment", "User re-authenticated.");
                updateUserEmail(newEmail);
            } else {
                Log.w("SettingsFragment", "Re-authentication failed.", task.getException());
                Toast.makeText(getContext(), "Error: Re-authentication failed. Check your password.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUserEmail(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Verification email sent to " + newEmail + ". Please verify to complete the change.", Toast.LENGTH_LONG).show();
                // Sign out the user
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), Auth.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            } else {
                Log.w("SettingsFragment", "verifyBeforeUpdateEmail:failure", task.getException());
                Toast.makeText(getContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Account");
        builder.setMessage("This action is irreversible. Are you sure you want to delete your account? You will need to enter your password to confirm.");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Enter your password");
        builder.setView(input);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String password = input.getText().toString();
            if (password.isEmpty()) {
                Toast.makeText(getContext(), "Password is required to delete account.", Toast.LENGTH_SHORT).show();
                return;
            }
            deleteUser(password);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    //works
    private void deleteUser(String password) {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                user.delete().addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), Auth.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to delete account.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Re-authentication failed. Please check your password.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

