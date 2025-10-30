package com.example.pockettru;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "ThemePref";
    private static final String KEY_IS_DARK_MODE = "isDarkModeOn";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialTextView user_email = view.findViewById(R.id.email_text);
        user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        super.onViewCreated(view, savedInstanceState);
        MaterialButton logout_button = view.findViewById(R.id.logout_button);
        SwitchMaterial switchTheme = view.findViewById(R.id.switch_theme);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        MaterialButton deleteuser_button = view.findViewById(R.id.delete_button);


        boolean isDarkModeOn = sharedPreferences.getBoolean(KEY_IS_DARK_MODE, false);
        switchTheme.setChecked(isDarkModeOn);

        //switch theme
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_DARK_MODE, isChecked);
            editor.apply();

            int mode = isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES :
                    AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
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
        //delete profile
        deleteuser_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDeleteDialog();
            }
        });

    }
    // TODO : set a reauthontification dialog to delete ts
    public void showDeleteDialog()
    {
        new AlertDialog.Builder(getContext()).setTitle("Delete profile")
                .setMessage("Are you sure you want to delete your profile?")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Profile deleted successfully.", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(getActivity(), Auth.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }

                }).setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

    }
}
