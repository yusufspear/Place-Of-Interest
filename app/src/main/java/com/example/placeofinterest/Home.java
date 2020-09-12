package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.placeofinterest.module.BottomFragment;
import com.example.placeofinterest.module.PolylineData;
import com.example.placeofinterest.module.TopFragment;
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
import com.google.android.gms.maps.MapView;
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
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.placeofinterest.R.id.chip_home;


public class Home extends AppCompatActivity implements OnMapReadyCallback, OnPolylineClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final double Lat = 19.077806;
    private static final double Long = 72.883056;
    private static final int GPS_REQUEST_CODE = 9001;
    private static final int PROXIMITY_RADIUS = 20000;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    public static GoogleMap mMap;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private ImageButton top_left_menu;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    FloatingActionButton mCurrentLocation, mDirection;
    FrameLayout frameLayout, MapView;
    Button rest, atm, hotel, school;
    private static String TAG = "TAG";
    BottomNavigationView bottomNavigationView;
    ChipNavigationBar chipNavigationBar;
    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;
    HandlerThread handlerThread;
    private View doorView;
    private String apiKey, apiKey_nearByPlace;
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
    public ArrayList<LatLng> MarkerPoints = new ArrayList<>(2);

    private GeoApiContext mGeoApiContext = null;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();

    private FragmentManager manager = getSupportFragmentManager();//create an instance of fragment manager
    private FragmentTransaction transaction;//create an instance of Fragment-transaction
    private BottomFragment bottomFragment;
    private TopFragment topFragment;
    private CardView CardView_Place_Autocomplete_Fragment;
    private HorizontalScrollView horizontalScrollView;
    private Bundle b1, b2;
    private String source, place_name;
    private int newFragment = 0;
    private Polyline polyline;
    private LatLng origin, destination;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: CALLED");
        hideSystemUI();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDataRef = FirebaseDatabase.getInstance().getReference("User");


        mDataRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);
                String isNew = user1.getisnew();
                if (isNew.equals("true")) {
                    startActivity(new Intent(getApplicationContext(), Select_POI.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        MarkerPoints = new ArrayList<>(2);
        isCurrentLocation();

        setContentView(R.layout.activity_home);
        GoogleMapOptions options = new GoogleMapOptions()
                .zoomControlsEnabled(true)
                .mapToolbarEnabled(true);

        supportMapFragment = SupportMapFragment.newInstance(options);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_fragment, supportMapFragment)
                .commit();

        supportMapFragment.getMapAsync(this);


        initViews();
        apiKey = getString(R.string.google_maps_key);
        apiKey_nearByPlace = getString(R.string.browser_key);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(apiKey_nearByPlace).build();
        }


        if (!Places.isInitialized()) {
            Places.initialize(Home.this, apiKey);
        }
        placesClient = Places.createClient(Home.this);
        topFragment = new TopFragment();
        bottomFragment = new BottomFragment();
        b1 = new Bundle();
        b2 = new Bundle();
        Intent intent = getIntent();
        if (intent.getStringExtra("placeId") != null) {
            String placeId = intent.getStringExtra("placeId");
            Log.i(TAG, "onCreate: Call Susscss. Place ID" + placeId);
            directionCallByPOI(placeId);
        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);

        top_left_menu.setOnClickListener(this::menuPress);
        navigationView.setNavigationItemSelectedListener(this);

        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteSupportFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL));

        autocompleteSupportFragment.setHint("Search Here...");

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String text = autocompleteSupportFragment.getText(autocompleteSupportFragment.getId()).toString();
                Log.i(TAG, "PlaceText: " + text);
                mMap.clear();
                Toast.makeText(getApplicationContext(), "Place: " + place.getName(), LENGTH_LONG).show();
                if (place.getLatLng() != null) {
                    if (!b1.isEmpty()) {
                        b1.remove("destination");
                    }
                    Log.i(TAG, "Place: " + place.getName());
                    place_name = place.getName();
                    b1.putString("destination", place_name);

                    LatLng address = place.getLatLng();
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(4.0f));
                    Marker marker = null;
                    marker = mMap.addMarker(new MarkerOptions().position(address).title(place.getName())
                            .snippet("ADDRESS:  " + place.getAddress() + "\nRATING: " + place.getRating() + " (" + place.getUserRatingsTotal() + " )"
                                    + "\nCONTACT: " + place.getPhoneNumber() + "\nWEBSITE: " + place.getWebsiteUri()));
                    marker.hideInfoWindow();
                    MarkerPoints.add(1, address);
                    Log.i(TAG, "onPlaceSelected: " + MarkerPoints.get(1).toString());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(address, 18);
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

        mCurrentLocation.setOnClickListener(this::CurrentLocation);
        mDirection.setOnClickListener(this::direction);
        mLocationClient = new FusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    Log.i("loc", "onLocationResult: Execute");
                    return;
                }

                runOnUiThread(() -> {
                    Location address = locationResult.getLastLocation();
                    CurrentLocation_Lat = address.getLatitude();
                    CurrentLocation_Long = address.getLongitude();
//                    Toast.makeText(Home.this,"Lat:- " +address.getLatitude()+"\n Long:- "+address.getLongitude(), LENGTH_LONG).show();
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    MarkerPoints.add(0, latLng);
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
//                    mMap.animateCamera(cameraUpdate);

                    Log.i("loc", "onLocationResult: Execute" + latLng);

                });

            }
        };


        chipNavigationBar.setItemSelected(chip_home, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i) {

                    case R.id.chip_home:
                        break;

                    case R.id.chip_history:
                        startActivity(new Intent(Home.this, History.class));
                        overridePendingTransition(0, 0);
                        finish();

                        break;

                    case R.id.chip_profile:
                        startActivity(new Intent(Home.this, Profile.class));
                        overridePendingTransition(0, 0);
                        finish();

                        break;

                }

            }
        });


    }


    private void menuPress(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideSystemUI() {

        View decorView = getWindow().getDecorView();
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSystemUI();

            }
        }, 1500);
    }


    private void isCurrentLocation() {

        mDataRef.child(user.getUid()).child("Location").child("lastLocation")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        CurrentLocation_Lat = dataSnapshot.child("Latitude").getValue(Double.class);
                        CurrentLocation_Long = dataSnapshot.child("Longitude").getValue(Double.class);
                        MarkerPoints.add(0, new LatLng(CurrentLocation_Lat, CurrentLocation_Long));
                        Log.i(TAG, "onDataChange: Execute" + CurrentLocation_Lat + CurrentLocation_Long);
                        LatLng latLng = new LatLng(CurrentLocation_Lat, CurrentLocation_Long);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace, String type) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + 500);
        googlePlaceUrl.append("&keyword=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + apiKey_nearByPlace);
        Log.i(TAG, "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    private void CurrentLocation(View view) {

        if (isGPSEnabled()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        LocationUpdate();
                        Location address = task.getResult();
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f));
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                        MarkerPoints.add(0, latLng);
                        deviceCurrentLocation = address;

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                        mMap.animateCamera(cameraUpdate);

                    } else {
                        Log.i("Task ", "onComplete: Not Execute");
                    }
                }
            });

        }

    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnable = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnable) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("It is Required For Finding Current Location of the Device")
                    .setCancelable(true)
                    .setPositiveButton("YES", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> {
                        LatLng latLng = new LatLng(CurrentLocation_Lat, CurrentLocation_Long);
                        MarkerPoints.add(0, latLng);
                        Log.i(TAG, "onMapReady: " + CurrentLocation_Lat + CurrentLocation_Long);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                    })
                    .show();
            return false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnable = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnable) {
                Toast.makeText(this, "GPS Enabled", LENGTH_LONG).show();
                CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(3);

            } else {
                Toast.makeText(this, "GPS NOT Enabled", LENGTH_LONG).show();

            }
        }

    }

    private void initViews() {

        mCurrentLocation = findViewById(R.id.fab_myLocation);
        mDirection = findViewById(R.id.fab_direction);
        rest = findViewById(R.id.restaurant);
        atm = findViewById(R.id.atm);
        hotel = findViewById(R.id.hotel);
        school = findViewById(R.id.school);
        drawerLayout = findViewById(R.id.drawer_layout);
        top_left_menu = findViewById(R.id.imageButton);
        navigationView = findViewById(R.id.nav_view);
        MapView = findViewById(R.id.map_fragment);
        chipNavigationBar = findViewById(R.id.chip_navigation_bar);
        CardView_Place_Autocomplete_Fragment = findViewById(R.id.cardView_Search);
        horizontalScrollView = findViewById(R.id.scrollable_linearLayout);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("Map", "IT is Ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        // change compass position
        if (MapView != null &&
                MapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the view
            View locationCompass = ((View) MapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationCompass.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.setMargins(200, 400, 500, 100);
        }
        mMap.setInfoWindowAdapter(new CustomInfoWindow_Adapter(Home.this));
        LatLng latLng = new LatLng(CurrentLocation_Lat, CurrentLocation_Long);
        Log.i(TAG, "onMapReady: " + CurrentLocation_Lat + CurrentLocation_Long);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
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

    private void calculateDirections(LatLng origin, LatLng dest) {
        Log.i(TAG, "calculateDirections: Executed");

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude));
        Log.i(TAG, "calculateDirections: destination: " + dest.toString());
        directions.mode(TravelMode.DRIVING);
        directions.alternatives(true);
        directions.destination(new com.google.maps.model.LatLng(dest.latitude, dest.longitude)).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].legs[0].distance);
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].legs[0].duration);
                Log.i(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylineToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage());

            }
        });
    }

    public void calculateDirectionsForMode(LatLng origin, LatLng dest, String travelMode) {

        Log.i(TAG, "calculateDirections: Executed");

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude));
        Log.i(TAG, "calculateDirections: destination: " + dest.toString());
        directions.mode(TravelMode.valueOf(travelMode));
        directions.alternatives(true);
        directions.destination(new com.google.maps.model.LatLng(dest.latitude, dest.longitude)).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].legs[0].distance);
                Log.i(TAG, "calculateDirections: routes: " + result.routes[0].legs[0].duration);
                Log.i(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                Log.i(TAG, "onResult: Bottom Polyline Add Successfully " + travelMode);
//                addPolylineToMap(result);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: result routes: " + result.routes.length);
                        if (mPolyLinesData.size() > 0) {
                            for (PolylineData polylineData : mPolyLinesData) {
                                polylineData.getPolyline().remove();
                            }
                            mPolyLinesData.clear();
                            mPolyLinesData = new ArrayList<>();
                        }

                        for (DirectionsRoute route : result.routes) {
                            Log.d(TAG, "run: leg: " + route.legs[0].toString());
                            Log.d(TAG, "run: leg: " + route.legs[0].distance);
                            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                            List<LatLng> newDecodedPath = new ArrayList<>();

                            // This loops through all the LatLng coordinates of ONE polyline.
                            for (com.google.maps.model.LatLng latLng : decodedPath) {

                                newDecodedPath.add(new LatLng(
                                        latLng.lat,
                                        latLng.lng
                                ));
                            }
                            Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                            polyline.setColor(Color.GRAY);

                            polyline.setClickable(true);
                            mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));
                            Log.i(TAG, "onResult: Bottom mPolylineData Add Successfully " + result.routes.length);

//                            onPolylineClick(polyline);

                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage());

            }
        });

    }

    private void addPolylineToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if (mPolyLinesData.size() > 0) {
                    for (PolylineData polylineData : mPolyLinesData) {
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    Log.d(TAG, "run: leg: " + route.legs[0].distance);
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(Color.GRAY);

                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    onPolylineClick(polyline);

                }
            }
        });

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        for (PolylineData polylineData : mPolyLinesData) {
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());

            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(Color.RED);
                polylineData.getPolyline().setZIndex(1);

                Duration duration = polylineData.getLeg().duration;
                Distance distance = polylineData.getLeg().distance;
                b2.putString("duration", duration.toString());
                b2.putString("distance", distance.toString());

                newFragment = 1;

                if (isFirst()) {
                    transaction = manager.beginTransaction();
                    transaction.detach(bottomFragment);
                    transaction.attach(bottomFragment);
                    bottomFragment.setArguments(b2);
                    transaction.commit();
                    Log.i(TAG, "onPolylineClick: " + "Distance And Duration add Successfully to bottom Fragment");
                }


            } else {
                polylineData.getPolyline().setColor(Color.GRAY);
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    private boolean isFirst() {
        if (newFragment == 1)
            return true;
        else {
            return false;
        }

    }


    @SuppressLint("MissingPermission")
    private void LocationUpdate() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);

        handlerThread = new HandlerThread("LocationCallbackHandler");
        handlerThread.start();

        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }

    public void onClick(View v) {
        Object[] dataTransfer = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(mContext);
        GetNearbyPlacesDataog getNearbyPlacesDataog = new GetNearbyPlacesDataog(mContext);

        switch (v.getId()) {
            case R.id.restaurant: {
                mMap.clear();
                String search = "restaurant";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, search, null);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.school: {
                mMap.clear();
                String search = "School", type = "college";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, search, type);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.atm: {
                mMap.clear();
                String search = "atm";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, search, null);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Atms", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.hotel: {
                mMap.clear();
                String search = "hotel";
                String url = getUrl(CurrentLocation_Lat, CurrentLocation_Long, search, null);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Home.this, "Showing Nearby Hotels", Toast.LENGTH_SHORT).show();

                break;
            }
        }
    }

    private void showBackground() {

        CardView_Place_Autocomplete_Fragment.setVisibility(View.VISIBLE);
        horizontalScrollView.setVisibility(View.VISIBLE);
        navigationView.setVisibility(View.VISIBLE);
        mCurrentLocation.setVisibility(View.VISIBLE);
    }

    private void hideBackground() {

        CardView_Place_Autocomplete_Fragment.setVisibility(View.INVISIBLE);
        horizontalScrollView.setVisibility(View.INVISIBLE);
        mDirection.setVisibility(View.INVISIBLE);
        navigationView.setVisibility(View.INVISIBLE);
        mCurrentLocation.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void closeFragment() {

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(destination).visible(false));
        b1.putString("destination", place_name);

        LatLng address = destination;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(address, 18);
        mMap.animateCamera(cameraUpdate, 1500, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onCancel() {
            }
        });
        transaction = manager.beginTransaction();
        Fragment f1 = manager.findFragmentById(R.id.fragment_TOP);
        Fragment f2 = manager.findFragmentById(R.id.fragment_BOTTOM);
        transaction.setTransitionStyle(R.style.Theme_Design_BottomSheetDialog);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.remove(f1);
        transaction.remove(f2);
        transaction.commit();
        showBackground();
    }

    private void location(double Lang, double Long) {

        LatLng latLng = new LatLng(Lang, Long);
        showMarker(latLng);

    }

    private void showMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(bitmapDescriptorFromVector(this));
        mMap.addMarker(markerOptions);

    }

    public void addList(ArrayList arrayList_Lat, ArrayList arrayList_Long) {

        list_Lat = (ArrayList) arrayList_Lat.clone();
        this.list_Long = (ArrayList) arrayList_Long.clone();

        double v1 = (double) list_Lat.get(5);
        double v2 = (double) list_Long.get(5);
        LatLng latlang = new LatLng(v1, v2);
//            showMarker(latlang);
        Log.i(TAG, "addList Print: " + list_Lat.get(5) + " " + list_Long.get(5) + latlang);
//        location(v1,v2);
    }

    public void directionCallByPOI(String destinationID) {

        Log.i(TAG, "directionCallByPOI: " + destinationID);
        String placeId = destinationID;
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL);

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        Log.i(TAG, "directionCallByPOI: PLace Init");
        placesClient.fetchPlace(request).addOnSuccessListener((fetchPlaceResponse) -> {
            Place place = fetchPlaceResponse.getPlace();
            LatLng location = place.getLatLng();
            String name = place.getName();
            Log.i(TAG, "Place found: " + place.getName() + "Location" + location);
            MarkerPoints.add(1, location);
            b1.putString("destination", name);
            Marker marker = null;
            marker = mMap.addMarker(new MarkerOptions().position(location).title(place.getName())
                    .snippet("ADDRESS:  " + place.getAddress() + "\nRATING: " + place.getRating() + " (" + place.getUserRatingsTotal() + " )"
                            + "\nCONTACT: " + place.getPhoneNumber() + "\nWEBSITE: " + place.getWebsiteUri()));
            marker.hideInfoWindow();
            direction(mDirection);


        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                String errorMessage = apiException.getMessage();
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
//        MarkerPoints = new ArrayList<>(2);
//        // Getting URL to the Google Directions API
//        origin = MarkerPoints.get(0);
//        destination = destinationLatLang;
//        Log.i(TAG, "direction: " + MarkerPoints.get(0) + MarkerPoints.get(1));
//        calculateDirections(origin, destination);
//        b2.putDouble("Lat_source", origin.latitude);
//        b2.putDouble("Lang_source", origin.longitude);
//
//        b2.putDouble("Lat_destination", destination.latitude);
//        b2.putDouble("Lang_destination", destination.longitude);
//
//
//        DestinationMarkerAdd(destinationID);
//        source = "My Location";
//        b1.putString("source", source);
//        b1.putString("destination", destinationName);
//        Log.i(TAG, "direction: "+b1.getString("destination")+ b2.getString("duration")+
//                b2.getString("distance")+b2.getDouble("Lat_source"));
//        transaction=manager.beginTransaction();
//
//        transaction.add(R.id.fragment_TOP, topFragment);
//        transaction.add(R.id.fragment_BOTTOM, bottomFragment);
//
//        transaction.setTransitionStyle(R.style.Theme_Design_BottomSheetDialog);
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        topFragment.setArguments(b1);
//        bottomFragment.setArguments(b2);
//        hideBackground();
//        transaction.commit();

    }

    private void DestinationMarkerAdd(String destinationID) {

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL));
    }

    private void direction(View view) {

        // Getting URL to the Google Directions API
        origin = MarkerPoints.get(0);
        destination = MarkerPoints.get(1);
        Log.i(TAG, "directionFinal: " + MarkerPoints.get(0) + MarkerPoints.get(1));
        Log.i(TAG, "directionFinal: " + origin + destination);
        calculateDirections(origin, destination);
        b2.putDouble("Lat_source", origin.latitude);
        b2.putDouble("Lang_source", origin.longitude);

        b2.putDouble("Lat_destination", destination.latitude);
        b2.putDouble("Lang_destination", destination.longitude);

        source = "My Location";
        b1.putString("source", source);
        Log.i(TAG, "direction: " + b1.getString("destination") + b2.getString("duration") +
                b2.getString("distance") + b2.getDouble("Lat_source"));
        transaction = manager.beginTransaction();

        transaction.add(R.id.fragment_TOP, topFragment);
        transaction.add(R.id.fragment_BOTTOM, bottomFragment);

        transaction.setTransitionStyle(R.style.Theme_Design_BottomSheetDialog);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        topFragment.setArguments(b1);
        bottomFragment.setArguments(b2);
        hideBackground();
        transaction.commit();
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        bounds.include(origin);
        bounds.include(destination);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));



    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context applicationContext) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_hospital);
        Drawable vectorDrawable = applicationContext.getResources().getDrawable(R.drawable.ic_school);
        Objects.requireNonNull(vectorDrawable).setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
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
        Log.i(TAG, "onStop: " + CurrentLocation_Lat + CurrentLocation_Long);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");

        if (CurrentLocation_Lat != 0) {

            Log.i(TAG, "onResume: IF Excuted " + CurrentLocation_Lat + CurrentLocation_Long);
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

        Log.i(TAG, "onDestroy: " + CurrentLocation_Lat + CurrentLocation_Long);

        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.add_new_place:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(Home.this, New_Place.class));
                finish();
                break;

            case R.id.favorite_place:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(Home.this, POI_Set.class));
                finish();
                break;
        }
        return true;
    }
}

