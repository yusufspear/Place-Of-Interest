package com.example.placeofinterest.module;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.placeofinterest.History;
import com.example.placeofinterest.Home;
import com.example.placeofinterest.MainActivity;
import com.example.placeofinterest.R;
import com.example.placeofinterest.SplashScreen;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.os.Looper.*;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.placeofinterest.R.id.chip_history;


public class TopFragment extends Fragment {

    private View view;
    private EditText line1, line2;
    private ImageButton imageButton;
    private String placeId;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("User");
    private static double CurrentLocation_Lat;
    private static double CurrentLocation_Long;
    private List<String> listSelection;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.top_fragment, container, false);
        initView();
        line1.setInputType(InputType.TYPE_NULL);
        line2.setInputType(InputType.TYPE_NULL);
        imageButton.setOnClickListener(this::backClick);
        setData();
        mRef.child(user.getUid()).child("Location").child("lastLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CurrentLocation_Lat = dataSnapshot.child("Latitude").getValue(Double.class);
                CurrentLocation_Long = dataSnapshot.child("Longitude").getValue(Double.class);
                Log.i(TAG, "TopFragment onDataChange: Execute" + CurrentLocation_Lat + CurrentLocation_Long);
                LatLng latLng = new LatLng(CurrentLocation_Lat, CurrentLocation_Long);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }


    private void setData() {
        Bundle b1 = getArguments();
        listSelection =new ArrayList<>();
        String source = b1.getString("source");
        String destination = b1.getString("destination");
        String fromWhich = b1.getString("fromWhich");
        placeId = b1.getString("placeID");
        line1.setText(source);
        line2.setText(destination);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void initView() {
        line1 = view.findViewById(R.id.source);
        line2 = view.findViewById(R.id.destination);
        imageButton = view.findViewById(R.id.back_arrow);

    }

    private void backClick(View view) {

        Home home = (Home) getActivity();
        if (home != null) {
            String isFromFragment = "YES";
            home.closeFragment(placeId, isFromFragment);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Intent intent = new Intent(getContext(), Home.class);
//        intent.putExtra("placeId", placeId);
//        String isFromFragment="YES";
//        intent.putExtra("isFromFragment", isFromFragment);
//        startActivity(new Intent(getContext(), Home.class));
//
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
    }

}
