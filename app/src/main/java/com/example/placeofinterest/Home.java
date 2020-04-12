package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.example.placeofinterest.module.PolylineData;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
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
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.placeofinterest.R.id.chip_home;


public class Home extends AppCompatActivity implements OnMapReadyCallback, OnPolylineClickListener {

    private static final double Lat= 19.077806;
    private static final double Long= 72.883056;
    private static final int GPS_REQUEST_CODE =9001;
    private static final int PROXIMITY_RADIUS = 20000;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    public static GoogleMap mMap;
    FloatingActionButton mCurrentLocation, mDirection;
    FrameLayout frameLayout;
    Button rest,atm,hotel,school;
    private static String TAG="TAG";
    BottomNavigationView bottomNavigationView;
    ChipNavigationBar chipNavigationBar;
    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;
    HandlerThread handlerThread;
    private View doorView;
    private String apiKey,apiKey_nearByPlace;
    private Location deviceCurrentLocation;
    private SupportMapFragment supportMapFragment;

    private static double CurrentLocation_Lat;
    private static double CurrentLocation_Long;
    private Context mContext;

    private FirebaseAuth mAuth;
    private DatabaseReference mDataRef;
    private FirebaseUser user;

    public ArrayList list_Lat = new ArrayList<Double>();
    public ArrayList list_Long = new ArrayList<Double>();
    public ArrayList<LatLng> MarkerPoints;

    private GeoApiContext mGeoApiContext=null;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: CALLED");
//        doorView  = getWindow().getDecorView();
////        d.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.TYPE_STATUS_BAR);
////        d.setFlags(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            doorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
//        }
        hideSystemUI();
        mAuth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        mDataRef = FirebaseDatabase.getInstance().getReference("User");
        isCurrentLocation();

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
        if (mGeoApiContext==null){
            mGeoApiContext =new GeoApiContext.Builder().apiKey(apiKey_nearByPlace).build();
        }

        // Initializing
        MarkerPoints = new ArrayList<>();

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
                        Marker marker = null;
                        marker = mMap.addMarker(new MarkerOptions().position(address).title(place.getName())
                                .snippet(  "ADDRESS:  "+place.getAddress() +"\nRATING: "+place.getRating()+" ("+place.getUserRatingsTotal()+" )"
                                        +"\nCONTACT: "+place.getPhoneNumber()+"\nWEBSITE: "+place.getWebsiteUri()));
                        marker.hideInfoWindow();

                        MarkerPoints.add(1,address);
                        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(address,18);
                        mMap.animateCamera(cameraUpdate, 1500, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                        mDirection.setVisibility(View.VISIBLE);
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
        mDirection.setOnClickListener(this::direction);
        mLocationClient= new FusedLocationProviderClient(this);
        mLocationCallback = new  LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null){
                    Log.i("loc", "onLocationResult: Execute");
                    return;
                }

                runOnUiThread(() -> {
                    Location address= locationResult.getLastLocation();
                    CurrentLocation_Lat=address.getLatitude();
                    CurrentLocation_Long=address.getLongitude();

                    Toast.makeText(Home.this,"Lat:- " +address.getLatitude()+"\n Long:- "+address.getLongitude(), LENGTH_LONG).show();
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());

                    Log.i("loc", "onLocationResult: Execute"+latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(getApplicationContext())));

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


//        bottomNavigationView.setSelectedItemId(R.id.home);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//
//                    case R.id.home:
//                        return true;
//
//                    case R.id.search:
//                        startActivity(new Intent(Home.this, Search.class));
//                        overridePendingTransition(0,0);
//                        return true;
//
//                    case R.id.history:
//                        startActivity(new Intent(Home.this, History.class));
//                        overridePendingTransition(0,0);
//                        return true;
//
//                    case R.id.profile:
//                        startActivity(new Intent(Home.this, Profile.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                }
//                return false;
//            }
//        });

        chipNavigationBar.setItemSelected(chip_home,true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i){

                    case R.id.chip_home:
                        break;

                    case R.id.chip_history:
                        startActivity(new Intent(Home.this, History.class));
                        overridePendingTransition(0,0);
                        finish();

                        break;

                    case R.id.chip_profile:
                        startActivity(new Intent(Home.this, Profile.class));
                        overridePendingTransition(0,0);
                        finish();

                        break;

                }

            }
        });


    }


//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
//    }
//    @Nullable
////    @Override
////    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
////        hideSystemUI();
////        return super.onWindowStartingActionMode(callback);
////
////    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSystemUI();

            }
        }, 2000);
    }


    private void isCurrentLocation() {

        mDataRef.child(user.getUid()).child("Location").child("lastLocation")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        CurrentLocation_Lat =dataSnapshot.child("Latitude").getValue(Double.class);
                        CurrentLocation_Long =dataSnapshot.child("Longitude").getValue(Double.class);

                        Log.i(TAG, "onDataChange: Execute");
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


    private String getUrl(double latitude , double longitude , String nearbyPlace,String type){

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+3000);
        googlePlaceUrl.append("&type="+null);
        googlePlaceUrl.append("&keyword="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+apiKey_nearByPlace);
        Log.i(TAG, "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    private String getUrl_Direction(LatLng origin,LatLng dest){

//        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
//        googlePlaceUrl.append("origin=");
//        googlePlaceUrl.append("&destination=19.076194,72.886309");
//        googlePlaceUrl.append("&key="+apiKey_nearByPlace);
//        Log.i(TAG, "url = "+googlePlaceUrl.toString());
//
//        return googlePlaceUrl.toString();

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=true";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key="+apiKey_nearByPlace;
        Log.i(TAG, "getUrl_Direction: " + url);
        return url;
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
                        MarkerPoints.add(0,latLng);
                        deviceCurrentLocation=address;
                        showMarker(latLng);

                        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,18);
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
//                    Marker marker =mMap.addMarker(new MarkerOptions().position(address).title(place.getName())
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

//        bottomNavigationView=findViewById(R.id.chip_navigation_bar);
        frameLayout = findViewById(R.id.map_fragment);
        mCurrentLocation = findViewById(R.id.fab_myLocation);
        mDirection = findViewById(R.id.fab_direction);
        rest=findViewById(R.id.restaurant);
        atm=findViewById(R.id.atm);
        hotel=findViewById(R.id.hotel);
        school=findViewById(R.id.school);
        chipNavigationBar=findViewById(R.id.chip_navigation_bar);
//        searchView = findViewById(R.id.search_View);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("Map", "IT is Ready");
        mMap =googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.setInfoWindowAdapter(new CustomInfoWindow_Adapter(Home.this));
        LatLng latLng=new LatLng(CurrentLocation_Lat, CurrentLocation_Long);
        Log.i(TAG, "onMapReady: "+CurrentLocation_Lat+CurrentLocation_Long);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        mMap.setOnPolylineClickListener(Home.this);

//        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
//            @Override
//            public void onPolylineClick(Polyline polyline) {
//
//                for(PolylineData polylineData: mPolyLinesData){
//                    Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
//                    if(polyline.getId().equals(polylineData.getPolyline().getId())){
//                        polylineData.getPolyline().setColor(Color.BLUE);
//                        polylineData.getPolyline().setZIndex(1);
//                    }
//                    else{
//                        polylineData.getPolyline().setColor(Color.GRAY);
//                        polylineData.getPolyline().setZIndex(0);
//                    }
//                }
//
//            }
//        });
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng point) {
//// Already two locations
//
//                if (MarkerPoints.size() > 1) {
//                    MarkerPoints.clear();
//                    mMap.clear();
//                }
//// Adding new item to the ArrayList
//                MarkerPoints.add(point);
//// Creating MarkerOptions
//                MarkerOptions options = new MarkerOptions();
//// Setting the position of the marker
//                options.position(point);
///**
// * For the start location, the color of marker is GREEN and
// * for the end location, the color of marker is RED.
// */
//                if (MarkerPoints.size() == 1) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                } else if (MarkerPoints.size() == 2) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                }
//// Add new marker to the Google Map Android API V2
//                mMap.addMarker(options);
//// Checks, whether start and end locations are captured
//                if (MarkerPoints.size() >= 2) {
//                    LatLng origin = MarkerPoints.get(0);
//                    LatLng dest = MarkerPoints.get(1);
//// Getting URL to the Google Directions API
//                    String url = getUrl_Direction(origin, dest);
//                    Log.d("onMapClick", url.toString());
//                    FetchUrl FetchUrl = new FetchUrl();
//// Start downloading json data from Google Directions API
//                    FetchUrl.execute(url);
////move map camera
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
//                }
//            }
//        });
    }
    private void calculateDirections(LatLng origin,LatLng dest){
        Log.i(TAG, "calculateDirections: Executed");
//        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
//                marker.getPosition().latitude,
//                marker.getPosition().longitude
//        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.alternatives(true);
        directions.origin(new com.google.maps.model.LatLng(origin.latitude,origin.longitude));
        Log.i(TAG, "calculateDirections: destination: " + dest.toString());
        directions.mode(TravelMode.WALKING);
        directions.alternatives(true);
        directions.destination(new com.google.maps.model.LatLng(dest.latitude,dest.longitude)).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].legs[0].distance);
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].legs[0].duration);
                Log.i(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(Color.GRAY);
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                }
            }
        });
    }
    @Override
    public void onPolylineClick(Polyline polyline) {
        for(PolylineData polylineData: mPolyLinesData){
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(Color.CYAN);
                polylineData.getPolyline().setZIndex(1);
            }
            else{
                polylineData.getPolyline().setColor(Color.GRAY);
                polylineData.getPolyline().setZIndex(0);
            }
        }
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
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(mContext);
        GetNearbyPlacesDataog getNearbyPlacesDataog=new GetNearbyPlacesDataog(mContext);

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
                String search = "restaurant";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, search,null);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.school: {
                mMap.clear();
                String search = "School",type="college";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, search,type);
//                    String url = getUrlog();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);

                Toast.makeText(Home.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.atm: {
                mMap.clear();
                String search = "atm";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, search,null);

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
                String search = "hotel";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, search,null);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Hotels", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
    private void location(double Lang, double Long) {

        LatLng latLng=new LatLng(Lang,Long);
        showMarker(latLng);

    }

    private void showMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(bitmapDescriptorFromVector(this));
        mMap.addMarker(markerOptions);

    }

    public void addlist(ArrayList arrayList_Lat, ArrayList arrayList_Long) {

            list_Lat= (ArrayList) arrayList_Lat.clone();
            this.list_Long = (ArrayList) arrayList_Long.clone();

        double v1= (double) list_Lat.get(5);
        double v2= (double) list_Long.get(5);
        LatLng latlang=new LatLng(v1,v2);
//            showMarker(latlang);
        Log.i(TAG, "addList Print: "+list_Lat.get(5)+" "+list_Long.get(5)+latlang);
//        location(v1,v2);
    }

    private void direction(View view) {

        // Getting URL to the Google Directions API
        LatLng origin=MarkerPoints.get(0);
        LatLng destination=MarkerPoints.get(1);
        calculateDirections(origin,destination);

//        //Using URL to find Direction
//        String url = getUrl_Direction(origin, distination);
//        Log.d("onMapClick", url.toString());
//        FetchUrl FetchUrl = new FetchUrl();
//        // Start downloading json data from Google Directions API
//        FetchUrl.execute(url);
//        //move map camera
//
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        builder.include(origin);
//        builder.include(distination);
//
//        LatLngBounds tmpBounds = builder.build();
//        LatLng center = tmpBounds.getCenter();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(tmpBounds, 300));



//        mMap.animateCamera(cameraUpdate, 1500, new GoogleMap.CancelableCallback() {
//            @Override
//            public void onFinish() {
//            }
//
//            @Override
//            public void onCancel() {
//            }
//        });



    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context applicationContext) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_hospital);
        Drawable vectorDrawable = applicationContext.getResources().getDrawable(R.drawable.ic_school);
        Objects.requireNonNull(vectorDrawable).setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        Log.i(TAG, "bitmapDescriptorFromVectorGetNearby Loction: ");
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");

    }

    @Override
    protected void onStop() {
        super.onStop();

        mDataRef.child(user.getUid()).child("Location").child("lastLocation").child("Latitude").setValue(CurrentLocation_Lat);
        mDataRef.child(user.getUid()).child("Location").child("lastLocation").child("Longitude").setValue(CurrentLocation_Long);
        Log.i(TAG, "onStop: "+CurrentLocation_Lat+CurrentLocation_Long);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (CurrentLocation_Lat!=0){

            Log.i(TAG, "onResume: IF Excuted " +CurrentLocation_Lat+CurrentLocation_Long);
            mDataRef.child(user.getUid()).child("Location").child("lastLocation").child("Latitude").setValue(CurrentLocation_Lat);
            mDataRef.child(user.getUid()).child("Location").child("lastLocation").child("Longitude").setValue(CurrentLocation_Long);
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

        Log.i(TAG, "onDestroy: "+CurrentLocation_Lat+CurrentLocation_Long);

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

