package com.example.pockettru;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        AppCompatDelegate.setDefaultNightMode(isDarkModeOn ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashScreen.setOnExitAnimationListener(splashScreenViewProvider -> {
                final View iconView = splashScreenViewProvider.getIconView();
                Animation rotateAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade1);

                rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        splashScreenViewProvider.remove();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                iconView.startAnimation(rotateAnimation);
    });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_mytru) {
                   selectedFragment = new MyTRUFragment();
                } else if (itemId == R.id.navigation_studygroups) {
                   selectedFragment = new StudyGroupsFragment();
                } else if (itemId == R.id.navigation_news) {
                    selectedFragment = new NewsFragment();
                } else if (itemId == R.id.navigation_wolfpack) {
                    selectedFragment = new WolfpackScheduleFragment();
                } else if (itemId == R.id.navigation_settings) {
                    selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });

        // Set the default fragment when the app starts
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MyTRUFragment()) // Replace with your default Fragment
                    .commit();
        }
    }
}