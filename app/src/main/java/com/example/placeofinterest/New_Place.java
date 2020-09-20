package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class New_Place extends AppCompatActivity {


    private TextView Source, Destination;
    private GridView gridView;
    private FloatingActionButton Confirm;
    private static String TAG = "TAG";
    private String placeName=null, placeID;

    private String[] poiList;
    private List<String> listSelection;
    private ArrayAdapter<String> adapter;
    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new__place);

        initView();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");

        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment1);
        autocompleteSupportFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL));

        autocompleteSupportFragment.setHint("Search Here...");

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String text = autocompleteSupportFragment.getText(autocompleteSupportFragment.getId()).toString();
                Toast.makeText(New_Place.this, text, LENGTH_LONG).show();
                Log.i(TAG, "PlaceText: " + text);
                Toast.makeText(New_Place.this, "Place: " + place.getName() + ", " + place.getId(), LENGTH_LONG).show();
                if (place.getLatLng() != null) {
                    Log.i(TAG, "Place: " + place.getName());
                    placeName = place.getName();
                    placeID = place.getId();
                    Destination.setText("Destination: " + placeName);
                    Destination.setVisibility(View.VISIBLE);
                    Source.setVisibility(View.VISIBLE);

                }
                Log.i(TAG, "PlaceText: " + text);

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status.getStatusMessage());

            }
        });
        Confirm.setOnClickListener(this::onClick);

        reference.child(user.getUid()).child("POI List").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long Count = dataSnapshot.getChildrenCount();
                poiList = new String[(int) Count];
                for (int i = 0; i < Count; i++) {
                    poiList[i] = (String) dataSnapshot.child(String.valueOf(i + 1)).getValue();
                }
                Log.i(TAG, "onCreate: " + poiList.length);
                adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_multiple_choice, poiList);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initView() {

        Source = findViewById(R.id.source1);
        Destination = findViewById(R.id.destination1);
        gridView = findViewById(R.id.listView);
        Confirm = findViewById(R.id.fabConfirm);
    }


    public void onClick(View v) {

        listSelection = new ArrayList<>();
        if (v.getId() == R.id.fabConfirm) {
            SparseBooleanArray itemChecked = gridView.getCheckedItemPositions();
            for (int i = 0; i < itemChecked.size(); i++) {
                int key = itemChecked.keyAt(i);
                boolean value = itemChecked.get(key);
                if (value) {
                    listSelection.add(gridView.getItemAtPosition(key).toString());
                }
            }
            if (listSelection.size() >= 1 && placeName != null) {

                Intent intent = new Intent(this, Home.class);
                intent.putExtra("placeId", placeID);
                String listSize = String.valueOf(listSelection.size());
                intent.putExtra("fromWhich", "New_Place");
                intent.putStringArrayListExtra("list", (ArrayList<String>) listSelection);

                intent.putExtra("selectListSize", listSize);
                for (int i=0;i<listSelection.size();i++){
                    intent.putExtra(String.valueOf(i), listSelection.get(i));
                    Log.d(TAG, "Item Select ${i}" + listSelection.get(i));

                }
                startActivity(intent);
                finish();

//                home.directionCallByPOI(PlaceName,PlaceId,PlaceLatLang);

            } else {
                Toast.makeText(getApplicationContext(), "Enter details.", LENGTH_LONG).show();
            }
        }

    }

}
