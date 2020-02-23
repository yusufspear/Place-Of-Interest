package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class History extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
//    TextView display;
//    FrameLayout FragmentHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View doorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            doorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }
        setContentView(R.layout.activity_history);
        initViews();

        bottomNavigationView.setSelectedItemId(R.id.history);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.history:
                        return true;

                    case R.id.home:
                        startActivity(new Intent(History.this, Home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.search:
                        startActivity(new Intent(History.this, Search.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(History.this, Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });



    }

    private void initViews() {

        bottomNavigationView=findViewById(R.id.bottom_navigation_bar);
//        FragmentHome = findViewById(R.id.fragment_home);


    }


}
