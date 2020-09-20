package com.example.placeofinterest;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;

import com.example.placeofinterest.adapter.historyAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static com.example.placeofinterest.R.id.add;
import static com.example.placeofinterest.R.id.chip_history;
import static com.example.placeofinterest.R.id.chip_profile;

public class History extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
    RecyclerView recyclerView;
    historyAdapter historyAdapter;
    private List<String> destTitle = new ArrayList<>();
    private List<URL> destUrl = new ArrayList<>();
    private List<String> searchTime = new ArrayList<>();
    private List<String> destPoiType = new ArrayList<>();
    private List<Float> ratingValue = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_history);
        initViews();

        destTitle.add(0,"Kalina University");
        destUrl.add(0,null);
        destPoiType.add(0,"Restaurant");
        ratingValue.add(0,4.0f);
        searchTime.add(0,"5:29 PM");

        destTitle.add(1,"New Panvel");
        destUrl.add(1,null);
        destPoiType.add(1,"Park");
        ratingValue.add(1,4.5f);
        searchTime.add(1,"12:00 PM");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new historyAdapter(destTitle,destUrl,searchTime,destPoiType,ratingValue);
        recyclerView.setAdapter(historyAdapter);

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
                        finish();
                        break;

                    case R.id.chip_home:
                        startActivity(new Intent(History.this, Home.class));
                        overridePendingTransition(0,0);
                        finish();

                        break;

                }

            }
        });



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
        recyclerView = findViewById(R.id.recyclerView_History);



    }
    @Override
    protected void onPause() {
        super.onPause();

    }


}
