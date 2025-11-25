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
    Fragment wolfPackFragment;
    Fragment settingsFragment;
    Fragment myTRUFragment;
    Fragment newsFragment;
    Fragment studyGroupsFragment;
    final Fragment sgFragment = new SGFragmnet();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active;

    private static final String STATE_ACTIVE_FRAGMENT_ID = "active_fragment_id";
    private int activeFragmentId = R.id.navigation_mytru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        AppCompatDelegate.setDefaultNightMode(isDarkModeOn ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if(savedInstanceState != null){
            activeFragmentId = savedInstanceState.getInt(STATE_ACTIVE_FRAGMENT_ID, R.id.navigation_mytru);
            // Find existing fragments by their tags
            myTRUFragment = fm.findFragmentByTag("1");
            newsFragment = fm.findFragmentByTag("2");
            studyGroupsFragment = fm.findFragmentByTag("3");
            wolfPackFragment = fm.findFragmentByTag("4");
            settingsFragment = fm.findFragmentByTag("5");
        }

        // Only create and add fragments
        if (myTRUFragment == null) {
            myTRUFragment = new MyTRUFragment();
            fm.beginTransaction().add(R.id.fragment_container, myTRUFragment, "1").hide(myTRUFragment).commit();
        }
        if (newsFragment == null) {
            newsFragment = new NewsFragment();
            fm.beginTransaction().add(R.id.fragment_container, newsFragment, "2").hide(newsFragment).commit();
        }
        if (studyGroupsFragment == null) {
            studyGroupsFragment = new StudyGroupsFragment();
            fm.beginTransaction().add(R.id.fragment_container, studyGroupsFragment, "3").hide(studyGroupsFragment).commit();
        }
        if (wolfPackFragment == null) {
            wolfPackFragment = new WolfpackScheduleFragment();
            fm.beginTransaction().add(R.id.fragment_container, wolfPackFragment, "4").hide(wolfPackFragment).commit();
        }
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            fm.beginTransaction().add(R.id.fragment_container, settingsFragment, "5").hide(settingsFragment).commit();
        }

        active = getFragmentById(activeFragmentId);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                fm.popBackStack("SG_FRAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                Fragment selectedFragment = getFragmentById(itemId);

                if(selectedFragment != active){
                    fm.beginTransaction().hide(active).show(selectedFragment).commit();
                    active = selectedFragment;
                    activeFragmentId = itemId;
                } else{
                    if (active instanceof  MyTRUFragment){
                        ((MyTRUFragment) active).refresh();
                    }else if( active instanceof StudyGroupsFragment){
                        fm.beginTransaction().detach(active).attach(active).commit();
                    }
                }
                return true;
            }
        });

        // Set selected item and show the active fragment
        // This needs to be done after the listener is set
        if (active != null) {
            fm.beginTransaction().show(active).commit();
            bottomNav.setSelectedItemId(activeFragmentId);
        }

    }

    private Fragment getFragmentById(int id){
        if(id == R.id.navigation_studygroups){
            return studyGroupsFragment;
        }else if(id == R.id.navigation_news){
            return newsFragment;
        }else if(id == R.id.navigation_wolfpack){
            return wolfPackFragment;
        }else if(id == R.id.navigation_settings){
            return settingsFragment;
        } else{
            return myTRUFragment;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_FRAGMENT_ID, activeFragmentId);
    }
}