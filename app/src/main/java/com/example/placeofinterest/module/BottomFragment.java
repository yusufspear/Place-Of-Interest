package com.example.placeofinterest.module;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.placeofinterest.Home;
import com.example.placeofinterest.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BottomFragment extends Fragment {
    private View view;
    private TextView Device_Time, Distance, Time;
    private Thread thread;
    private Spinner spinner;
    private LatLng source,destination;
    private Double lat_s,long_s,lat_d,long_d;
    private int iCurrentSelection,j=1;
    private String mode_name;
    private TravelMode travelMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.bottom_fragment, container, false);
        initView();
        click();
        setData();
         iCurrentSelection = spinner.getSelectedItemPosition();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (iCurrentSelection != position){
                    mode_name = spinner.getItemAtPosition(position).toString();
                    iCurrentSelection = position;
                    Log.i(TAG, "onItemSelected: "+mode_name);
                    Home home= (Home) getActivity();
                    if (isAgain()){
                        home.calculateDirectionsForMode(source,destination,mode_name);
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        return view;
    }

    private boolean isAgain() {
        if(j==1)
            return true;
        else
            return false;
    }

    private void initView() {
        Device_Time = view.findViewById(R.id.deviceTime);
        spinner = view.findViewById(R.id.spinner_mode);
        Distance = view.findViewById(R.id.total_Distance);
        Time = view.findViewById(R.id.arriving_Time);

    }

    private void setData() {
        Bundle b2 = getArguments();

        String duration = "Time :"+b2.getString("duration");
        String distance = "Distance :"+b2.getString("distance");
        Distance.setText(distance);
        Time.setText(duration);
        lat_s = b2.getDouble("Lat_source");
        long_s = b2.getDouble("Lang_source");

        lat_d = b2.getDouble("Lat_destination");
        long_d = b2.getDouble("Lang_destination");

        source = new LatLng(lat_s,long_s);
        destination = new LatLng(lat_d,long_d);

        Log.i(TAG, "setData: Location"+source+destination);

        Log.i(TAG, "setData: " + "BottomFragment " + distance + duration);


    }

    private void click() {
        Home home = (Home) getActivity();


        thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);

                        home.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String currentTime = simpleDateFormat.format(calendar.getTime());
                                Device_Time.setText(currentTime);
                                Log.i(TAG, "run: " + "BottomFragment Device Time is RUNING");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, "run: " + e.getMessage());
                }
            }
        };
        thread.start();

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: ");
    }
}
