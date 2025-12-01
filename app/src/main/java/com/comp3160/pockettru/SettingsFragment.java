package com.comp3160.pockettru;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements OnMapReadyCallback {

    private final List<Marker> buildingMarkers = new ArrayList<>();
    private final List<Marker> foodMarkers = new ArrayList<>();
    private final List<Marker> parkingMarkers = new ArrayList<>();

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

        if (isGooglePlayServicesAvailable()){
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

            if(mapFragment != null){
                mapFragment.getMapAsync(this);
            }
        }else{
            Toast.makeText(getContext(), "Google Play services are not available.", Toast.LENGTH_LONG).show();
        }


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

        Button btnShowAll = view.findViewById(R.id.btn_show_all);
        Button btnShowBuildings = view.findViewById(R.id.btn_buildings);
        Button btnShowFoodServices = view.findViewById(R.id.btn_food_services);
        Button btnShowParking = view.findViewById(R.id.btn_parking);

        btnShowAll.setOnClickListener(v -> {
            toggleMarkers(buildingMarkers, true);
            toggleMarkers(foodMarkers, true);
            toggleMarkers(parkingMarkers, true);
        });

        btnShowBuildings.setOnClickListener(v -> {
            toggleMarkers(buildingMarkers, true);
            toggleMarkers(foodMarkers, false);
            toggleMarkers(parkingMarkers, false);
        });

        btnShowFoodServices.setOnClickListener(view1 -> {
            toggleMarkers(buildingMarkers, false);
            toggleMarkers(foodMarkers, true);
            toggleMarkers(parkingMarkers, false);
        });

        btnShowParking.setOnClickListener(view1 -> {
            toggleMarkers(buildingMarkers, false);
            toggleMarkers(foodMarkers, false);
            toggleMarkers(parkingMarkers, true);
        });

        ((MaterialButtonToggleGroup) view.findViewById(R.id.map_toggles)).check(R.id.btn_show_all);

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
                        .setMessage("Developed with ❤️")
                        .setPositiveButton("❤️", null)
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

    private boolean isGooglePlayServicesAvailable(){
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        return availability == ConnectionResult.SUCCESS;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map){
        try{
            boolean succes = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            if(!succes){
                Log.e("Settings Fragment", "Style failed");
            }
        }catch (Exception e){
            Log.e("Settings Fragment", "Can't find style", e);
        }

        BitmapDescriptor buidlingIcon = bitmapDescriptorFromVector(R.drawable.buildings, 100, 100);
        BitmapDescriptor foodIcon = bitmapDescriptorFromVector(R.drawable.food, 80, 80);
        BitmapDescriptor parkingIcon = bitmapDescriptorFromVector(R.drawable.parking, 100, 100);


        LatLng southWest = new LatLng(50.658, -120.375);
        LatLng northEast = new LatLng(50.678, -120.355);
        LatLngBounds bounds = new LatLngBounds(southWest, northEast);

        map.setLatLngBoundsForCameraTarget(bounds);
        map.setMinZoomPreference(15);
        map.setMaxZoomPreference(17);

        LatLng location = new LatLng(50.6725, -120.3660);

        buildingMarkers.clear();
        foodMarkers.clear();
        parkingMarkers.clear();

        LatLng om = new LatLng(50.671162,-120.361902);
        LatLng cac = new LatLng(50.672845862350464, -120.36647731601178);
        LatLng hol = new LatLng(50.67207500236342, -120.36500406533493);
        LatLng ib = new LatLng(50.67257481674408, -120.36412591376477);
        LatLng sb = new LatLng(50.66924877948645, -120.36237668737427);
        LatLng nb = new LatLng(50.66978732976576, -120.36152706776068);
        LatLng ol = new LatLng(50.67015580912266, -120.36148273297246);
        LatLng ae = new LatLng(50.67321319092225, -120.36436907602054);
        LatLng truGym = new LatLng(50.66961419430688, -120.36419362561185);

        LatLng starbucks = new LatLng(50.67150757712336, -120.3632749797488);
        LatLng scratchcafe = new LatLng(50.67011417566106, -120.36242822160033);
        LatLng timHortons = new LatLng(50.67195962426875, -120.36510368602853);
        LatLng theDen = new LatLng(50.67316196925504, -120.3666123609505);
        LatLng subway = new LatLng(50.67260230189388, -120.36378458302282);


        LatLng parkingH = new LatLng(50.673963849328686, -120.3660377804864);
        LatLng parkingN = new LatLng(50.67440128241239, -120.36843095933865);
        LatLng parkingC = new LatLng(50.67024054502503, -120.36330008557559);
        LatLng parkingS = new LatLng(50.668721243820826, -120.36304447504219);

        LatLng writingCenter = new LatLng(50.6709371668854, -120.36266454164269);
        LatLng makerspace = new LatLng(50.67194281448611, -120.36557498153613);


        //Buildings
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(om).title("Old Main").icon(buidlingIcon)));
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(cac).title("Campus Activity Center").icon(buidlingIcon)));
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(hol).title("House of Learning").icon(buidlingIcon)));
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(ib).title("International Building").icon(buidlingIcon)));
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(sb).title("Science Building").icon(buidlingIcon)));
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(nb).title("Nursing Building").icon(buidlingIcon)));
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(ol).title("OLARA").icon(buidlingIcon)));
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(ae).title("Arts and Educations").icon(buidlingIcon)));
        buildingMarkers.add(map.addMarker(new MarkerOptions().position(truGym).title("TRU Gym").icon(buidlingIcon)));



        //Food Services
        foodMarkers.add(map.addMarker(new MarkerOptions().position(starbucks).title("Starbucks").icon(foodIcon)));
        foodMarkers.add(map.addMarker(new MarkerOptions().position(scratchcafe).title("Scratch Café").icon(foodIcon)));
        foodMarkers.add(map.addMarker(new MarkerOptions().position(timHortons).title("Tim Hortons").icon(foodIcon)));
        foodMarkers.add(map.addMarker(new MarkerOptions().position(theDen).title("The Den").icon(foodIcon)));
        foodMarkers.add(map.addMarker(new MarkerOptions().position(subway).title("Subway").icon(foodIcon)));



        //Parking Lots
        parkingMarkers.add(map.addMarker(new MarkerOptions().position(parkingC).title("Parking Lot C").icon(parkingIcon)));
        parkingMarkers.add(map.addMarker(new MarkerOptions().position(parkingH).title("Parking Lot H").icon(parkingIcon)));
        parkingMarkers.add(map.addMarker(new MarkerOptions().position(parkingN).title("Parking Lot N").icon(parkingIcon)));
        parkingMarkers.add(map.addMarker(new MarkerOptions().position(parkingS).title("Parking Lot S").icon(parkingIcon)));



        //Student Services
        parkingMarkers.add(map.addMarker(new MarkerOptions().position(writingCenter).title("Writing Center").icon(parkingIcon)));
        parkingMarkers.add(map.addMarker(new MarkerOptions().position(makerspace).title("Makerspace").icon(parkingIcon)));




        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).tilt(20).build();


        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.getUiSettings().setZoomControlsEnabled(false);
        toggleMarkers(buildingMarkers, true);
        toggleMarkers(foodMarkers, true);
        toggleMarkers(parkingMarkers, true);

//        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    private void toggleMarkers(List<Marker> markers, boolean isVisible){
        for(Marker marker : markers){
            marker.setVisible(isVisible);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId, int width, int height){
        Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId);
        if(vectorDrawable == null)
            return null;
        vectorDrawable.setBounds(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}

