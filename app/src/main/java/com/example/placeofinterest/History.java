package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import static com.example.placeofinterest.R.id.chip_history;
import static com.example.placeofinterest.R.id.chip_profile;

public class History extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
//    TextView display;
//    FrameLayout FragmentHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View doorView = getWindow().getDecorView();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            doorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
//        }
        hideSystemUI();
        setContentView(R.layout.activity_history);
        initViews();

        chipNavigationBar.setItemSelected(chip_history,true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i){

                    case R.id.chip_history:
                        break;

                    case R.id.chip_profile:
                        startActivity(new Intent(History.this, Profile.class));
                        overridePendingTransition(0,0);
                        break;

                    case R.id.chip_home:
                        startActivity(new Intent(History.this, Home.class));
                        overridePendingTransition(0,0);
                        break;

                }

            }
        });

//        bottomNavigationView.setSelectedItemId(R.id.history);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//
//                    case R.id.history:
//                        return true;
//
//                    case R.id.home:
//                        startActivity(new Intent(History.this, Home.class));
//                        overridePendingTransition(0,0);
//                        return true;
//
//                    case R.id.search:
//                        startActivity(new Intent(History.this, Search.class));
//                        overridePendingTransition(0,0);
//                        return true;
//
//                    case R.id.profile:
//                        startActivity(new Intent(History.this, Profile.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                }
//                return false;
//            }
//        });



    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);


        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
    private void initViews() {

        chipNavigationBar=findViewById(R.id.chip_navigation_bar);
//        bottomNavigationView=findViewById(R.id.chip_navigation_bar);
//        FragmentHome = findViewById(R.id.fragment_home);


    }


}
