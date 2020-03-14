package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;


public class Home extends AppCompatActivity implements OnMapReadyCallback {

    private static final double Lat= 19.077806;
    private static final double Long= 72.883056;
    private static final int GPS_REQUEST_CODE =9001;
    private static final int PROXIMITY_RADIUS = 20000;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    GoogleMap mMap;
    FloatingActionButton mCurrentLocation, mDirection;
    FrameLayout frameLayout;
    Button rest,atm,hotel,school;
    //    SearchView searchView;
    private static String TAG="TAG";
    BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;
    HandlerThread handlerThread;
    private View doorView;
    private String apiKey,apiKey_nearByPlace;
    private Location deviceCurrentLocation;
    private SupportMapFragment supportMapFragment;

    private MaterialSearchBar materialSearchBar;
    private List<AutocompletePrediction> predictionList;
    private static double CurrentLocation_Lat= 0.0;
    private static double CurrentLocation_Long=0.0;
    private Context mContext;

    private FirebaseAuth mAuth;
    private DatabaseReference mDataRef;
    private FirebaseUser user;

    public Home() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: CALLED");
        doorView  = getWindow().getDecorView();
//        d.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.TYPE_STATUS_BAR);
//        d.setFlags(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            doorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        mAuth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        mDataRef = FirebaseDatabase.getInstance().getReference("User");

        setContentView(R.layout.activity_home);
        GoogleMapOptions options= new GoogleMapOptions()
                .zoomControlsEnabled(true)
                .mapToolbarEnabled(true);

        supportMapFragment= SupportMapFragment.newInstance(options);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_fragment,supportMapFragment)
                .commit();

        supportMapFragment.getMapAsync(this);
        initViews();
        apiKey = getString(R.string.google_maps_key);
        apiKey_nearByPlace= getString(R.string.browser_key_for_NearByPlaceAPI);


        if (!Places.isInitialized()) {
            Places.initialize(Home.this, apiKey);
        }
        PlacesClient placesClient = Places.createClient(Home.this);


        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteSupportFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG,Place.Field.ADDRESS,
        Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL));

        autocompleteSupportFragment.setHint("Search Here...");

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String text= autocompleteSupportFragment.getText(autocompleteSupportFragment.getId()).toString();
                Toast.makeText(Home.this, text, LENGTH_LONG).show();
                Log.i(TAG, "PlaceText: " + text);
                mMap.clear();
                Toast.makeText(Home.this,"Place: " + place.getName() + ", " + place.getId(), LENGTH_LONG).show();
                    if (place.getLatLng()!=null){
                        Log.i(TAG, "Place: " + place.getName());

                        LatLng address = place.getLatLng();
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(4.0f));
                        Marker marker =mMap.addMarker(new MarkerOptions().position(address).draggable(true).title(place.getName())
                                .snippet(  "ADDRESS:  "+place.getAddress() +"\nRATING: "+place.getRating()+"\nCONTACT: "+place.getPhoneNumber()));
                        marker.hideInfoWindow();
                        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(address,18);
                        mMap.animateCamera(cameraUpdate, 1500, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                    }
                Log.i(TAG, "PlaceText: " + text);

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status.getStatusMessage());

            }
        });
//        Object[] dataTransfer=new Object[2];
//        GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
//        atm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mMap.clear();
//                String ATM = "Atm";
//                String url = getUrl(Lat, Long, ATM);
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
//                Log.i(TAG, "onClick: Click");
//                Toast.makeText(Home.this, "Showing Nearby ATM", LENGTH_LONG).show();
//            }
//        });

        mCurrentLocation.setOnClickListener(this::CurrentLocation);

        mLocationClient= new FusedLocationProviderClient(this);
        mLocationCallback = new  LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null){
                    Log.i("loc", "onLocationResult: Execute");
                    return;
                }

                runOnUiThread(() -> {
                    Log.i("loc", "onLocationResult: Execute");
                    Location address= locationResult.getLastLocation();
                    CurrentLocation_Lat=address.getLatitude();
                    CurrentLocation_Long=address.getLongitude();

                    Toast.makeText(Home.this,"Lat:- " +address.getLatitude()+"\n Long:- "+address.getLongitude(), LENGTH_LONG).show();
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                });

            }
        };

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                String address= searchView.getQuery().toString().trim();
//                if (address!=null ){
//                    Geocoder geocoder = new Geocoder(Home.this, Locale.ENGLISH);
//                    try {
//                        List<Address>  addressList=geocoder.getFromLocationName(address,10);
//
//                        if (addressList.size()>0){
//
//                            Address location=addressList.get(0);
//                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
////                            showMarker(latLng);
//                            mMap.addMarker(new MarkerOptions().title(s).position(latLng).icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_custom_marker_black_24dp)));
//                            CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,19);
//                            mMap.animateCamera(cameraUpdate, 1000, new GoogleMap.CancelableCallback() {
//                                @Override
//                                public void onFinish() {
////                        Toast.makeText(Home.this, "Finished", Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onCancel() {
////                        Toast.makeText(Home.this, "Cancelled", Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                return false;
//            }
//
//            private BitmapDescriptor bitmapDescriptorFromVector(Context applicationContext, int ic_custom_marker_black_24dp) {
//                Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext,ic_custom_marker_black_24dp);
//                Objects.requireNonNull(vectorDrawable).setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
//                Bitmap.Config config;
//                Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bitmap);
//                vectorDrawable.draw(canvas);
//                return BitmapDescriptorFactory.fromBitmap(bitmap);            }
//
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchView.setVisibility(View.INVISIBLE);
//
//            }
//        });

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.home:
                        return true;

                    case R.id.search:
                        startActivity(new Intent(Home.this, Search.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.history:
                        startActivity(new Intent(Home.this, History.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(Home.this, Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });



    }

    private void isCurrentLocation() {

        mDataRef.child(user.getUid()).child("Location").child("lastLocation")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        CurrentLocation_Lat =dataSnapshot.child("Latitude").getValue(Double.class);
                        CurrentLocation_Long =dataSnapshot.child("Longitude").getValue(Double.class);

                        Log.i("List", "onDataChange: Execute");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
//        savedInstanceState.putDouble("lat", CurrentLocation_Lat);
//        savedInstanceState.putDouble("long", CurrentLocation_Long);
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        CurrentLocation_Lat=savedInstanceState.getDouble("lat");
//        CurrentLocation_Long=savedInstanceState.getDouble("long");
//    }


    private String getUrl(double latitude , double longitude , String nearbyPlace){

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+2500);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+apiKey_nearByPlace);
        Log.i(TAG, "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }
    private void CurrentLocation(View view) {

        if (isGPSEnabled()){

            mLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()){
                        LocationUpdate();
                        Location address = task.getResult();
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f));
                        LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                        deviceCurrentLocation=address;
                        showMarker(latLng);

                        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,19);
                        mMap.animateCamera(cameraUpdate, 1000, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                            }

                            @Override
                            public void onCancel() {
                            }
                        });

                    }else {
                        Log.i("Task ", "onComplete: Not Execute");
                    }
                }
            });

        }

    }
    private  boolean isGPSEnabled(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnable  = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnable){
            return true;
        }else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("It is Required For Finding Current Location of the Device")
                    .setCancelable(true)
                    .setPositiveButton("YES",(dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent,GPS_REQUEST_CODE);
                    })
                    .setNegativeButton("NO",(dialogInterface, i) -> {
                        LatLng latLng=new LatLng(CurrentLocation_Lat, CurrentLocation_Long);
                        Log.i(TAG, "onMapReady: "+CurrentLocation_Lat+CurrentLocation_Long);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,19));
                    })
                    .show();
            return false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== GPS_REQUEST_CODE){

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnable  = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnable) {
                Toast.makeText(this, "GPS Enabled", LENGTH_LONG).show();
                CameraUpdate cameraUpdate= CameraUpdateFactory.zoomTo(3);

            }else{
                Toast.makeText(this,"GPS NOT Enabled", LENGTH_LONG).show();

            }
        }

//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                mMap.clear();
//
//                Toast.makeText(Home.this,"Place: " + place.getName() + ", " + place.getId(), LENGTH_LONG).show();
//                if (place.getLatLng()!=null){
//                    Log.i(TAG, "Place: " + place.getName());
//
//                    LatLng address = place.getLatLng();
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(4.0f));
//                    Marker marker =mMap.addMarker(new MarkerOptions().position(address).draggable(true).title(place.getName())
//                            .snippet(  "ADDRESS:  "+place.getAddress() +"\nRATING: "+place.getRating()+"\nCONTACT: "+place.getPhoneNumber()));
//                    marker.hideInfoWindow();
//                    mMap.setInfoWindowAdapter(new CustomInfoWindow_Adapter(Home.this));
//                    CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(address,18);
//                    mMap.animateCamera(cameraUpdate, 1500, new GoogleMap.CancelableCallback() {
//                        @Override
//                        public void onFinish() {
//                        }
//
//                        @Override
//                        public void onCancel() {
//                        }
//                    });
//                }                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//                Toast.makeText(Home.this, "Working Fine", LENGTH_LONG).show();
//
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Log.i(TAG, status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
    }

    private void initViews() {

        bottomNavigationView=findViewById(R.id.bottom_navigation_bar);
        frameLayout = findViewById(R.id.map_fragment);
        mCurrentLocation = findViewById(R.id.fab_myLocation);
        mDirection = findViewById(R.id.fab_direction);
        rest=findViewById(R.id.restaurant);
        atm=findViewById(R.id.atm);
        hotel=findViewById(R.id.hotel);
        school=findViewById(R.id.school);
//        searchView = findViewById(R.id.search_View);

//        materialSearchBar = findViewById(R.id.searchBar);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("Map", "IT is Ready");
        mMap =googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setBuildingsEnabled(true);

        mMap.setInfoWindowAdapter(new CustomInfoWindow_Adapter(Home.this));
        LatLng latLng=new LatLng(CurrentLocation_Lat, CurrentLocation_Long);
        Log.i(TAG, "onMapReady: "+CurrentLocation_Lat+CurrentLocation_Long);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,19));

    }

    private void LocationUpdate(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(700);

        handlerThread = new HandlerThread("LocationCallbackHandler");
        handlerThread.start();

        mLocationClient.requestLocationUpdates(locationRequest,mLocationCallback, Looper.getMainLooper());
    }

//    private void lastLocation(){
//        mLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if (task.isSuccessful()){
//                    Location address = task.getResult();
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f));
//                    LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
//                    deviceCurrentLocation=address;
//                    showMarker(latLng);
//
//                    CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,19);
//                    mMap.animateCamera(cameraUpdate, 1000, new GoogleMap.CancelableCallback() {
//                        @Override
//                        public void onFinish() {
////                        Toast.makeText(Home.this, "Finished", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCancel() {
////                        Toast.makeText(Home.this, "Cancelled", Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//                }else {
//                    Log.i("Task ", "onComplete: Not Execute");
//                }
//            }
//        });
//
//    }

    public void onClick(View v)
    {
        Object[] dataTransfer = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(v.getId()) {
//            case R.id.btnSearch:
//                EditText txtPlace = findViewById(R.id.txtPlace);
//                String location = txtPlace.getText().toString();
//                List<Address> addressList;
//
////                if (location != "") {
////                    Geocoder geocoder = new Geocoder(this);
////                    try {
////                        addressList = geocoder.getFromLocationName(location, 5);
////
////                        if (addressList != null) {
////
////                            for (int i = 0; i < addressList.size(); i++) {
////
////                                LatLng latLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
////                                MarkerOptions markerOptions = new MarkerOptions();
////                                markerOptions.position(latLng);
////                                markerOptions.title(location);
////                                mMap.addMarker(markerOptions);
////                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
////                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
////                            }
////                        }
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
//                break;

//            case R.id.btnHospitals: {
//
//                mMap.clear();
//                String Hospital = "hospital";
//                String url = getUrl(latitude, longitude, Hospital);
//
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
//                Toast.makeText(MapsActivity2.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
//                break;
//            }
//            case R.id.btnBanks: {
//
//                mMap.clear();
//                String Bank = "bank";
//                String url = getUrl(latitude, longitude, Bank);
//
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
//                Toast.makeText(MapsActivity2.this, "Showing Nearby Banks", Toast.LENGTH_SHORT).show();
//                break;
//            }
            case R.id.restaurant: {
                mMap.clear();
                String Restaurant = "restaurant";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, Restaurant);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.school: {
                mMap.clear();
                String School = "school";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, School);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.atm: {
                mMap.clear();
                String Atm = "atm";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, Atm);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Atms", Toast.LENGTH_SHORT).show();
                break;
            }
//            case R.id.btnSchools: {
//                mMap.clear();
//                String School = "school";
//                String url = getUrl(latitude, longitude, School);
//
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
//                Toast.makeText(MapsActivity2.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
//                break;
//            }
            case R.id.hotel: {
                mMap.clear();
                String Hotel = "hotel";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, Hotel);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Hotels", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

     Context getContext(){
        return mContext=Home.this;
    }

    private void location(double Lang, double Long) {

        LatLng latLng=new LatLng(Lang,Long);
        showMarker(latLng);

    }

    private void showMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (CurrentLocation_Lat!=0){

            Log.i(TAG, "onResume: IF Excuted " +CurrentLocation_Lat+CurrentLocation_Long);
            mDataRef.child(user.getUid()).child("Location").child("lastLocation").child("Latitude").setValue(CurrentLocation_Lat);
            mDataRef.child(user.getUid()).child("Location").child("lastLocation").child("Longitude").setValue(CurrentLocation_Long);
            isCurrentLocation();
        }


    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        mDataRef.child(user.getUid()).child("Location").child("lastLocation").child("Latitude").setValue(CurrentLocation_Lat);
        mDataRef.child(user.getUid()).child("Location").child("lastLocation").child("Longitude").setValue(CurrentLocation_Long);

        if (handlerThread!=null){
            handlerThread.quitSafely();
        }
    }
}


//public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
//
//    GoogleMap mMap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        GoogleMapOptions options= new GoogleMapOptions()
//                .zoomControlsEnabled(true)
//                .mapToolbarEnabled(true);
//
//        SupportMapFragment supportMapFragment= SupportMapFragment.newInstance(options);
//
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.map_fragment,supportMapFragment)
//                .commit();
//
//        supportMapFragment.getMapAsync(this);
//
//        String apiKey = getString(R.string.google_maps_key);
//
//
//            Places.initialize(getApplicationContext(), apiKey);
//            Log.i("PlaceSelect", "Place: " + ", ");
//
//        PlacesClient placesClient = Places.createClient(this);
//
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES,
//                Place.Field.PHONE_NUMBER, Place.Field.PLUS_CODE,Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                Log.i("which", "onError: "+ place.getPlusCode());
//
//
//                if (place.getLatLng()!=null){
//                    Log.i("which", "onError: "+ place.getRating());
//                    Log.i("which", "onError: "+ place.getUserRatingsTotal());
//
//
//                    LatLng address = place.getLatLng();
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));
//                    mMap.addMarker(new MarkerOptions().position(address).title(place.getName()).snippet(place.getRating().toString()));
//                    CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(address,15);
//                    mMap.animateCamera(cameraUpdate, 1500, new GoogleMap.CancelableCallback() {
//                    @Override
//                    public void onFinish() {
//                            Toast.makeText(MainActivity.this, "Finished", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                            Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//                }
//
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//
//                Log.i("which", "onError: "+ status.getStatusMessage()+ status.getStatus());
//
//            }
//
//        });
//    }

