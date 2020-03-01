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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
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
    GoogleMap mMap;
    FloatingActionButton mCurrentLocation, mDirection;
    FrameLayout frameLayout;
    Button rest,atm,hotel;
    //    SearchView searchView;
    private static String TAG="which";
    BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;
    HandlerThread handlerThread;
    private View doorView;
    private String apiKey;
    private Location deviceCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doorView  = getWindow().getDecorView();
//        d.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.TYPE_STATUS_BAR);
//        d.setFlags(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            doorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        setContentView(R.layout.activity_home);
        GoogleMapOptions options= new GoogleMapOptions()
                .zoomControlsEnabled(true)
                .mapToolbarEnabled(true);

        SupportMapFragment supportMapFragment= SupportMapFragment.newInstance(options);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_fragment,supportMapFragment)
                .commit();

        supportMapFragment.getMapAsync(this);
        initViews();
        apiKey = getString(R.string.google_maps_key);

        if (!Places.isInitialized()) {
            Places.initialize(Home.this, apiKey);
        }
        PlacesClient placesClient = Places.createClient(Home.this);


        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteSupportFragment.getView().setBackgroundResource(R.drawable.searchfield);
        autocompleteSupportFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG,Place.Field.ADDRESS,
        Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.clear();
                Toast.makeText(Home.this,"Place: " + place.getName() + ", " + place.getId(), LENGTH_LONG).show();
                    if (place.getLatLng()!=null){
                        Log.i(TAG, "Place: " + place.getName());

                        LatLng address = place.getLatLng();
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(4.0f));
                        Marker marker =mMap.addMarker(new MarkerOptions().position(address).draggable(true).title(place.getName())
                                .snippet(  "ADDRESS:  "+place.getAddress() +"\nRATING: "+place.getRating()+"\nCONTACT: "+place.getPhoneNumber()));
                        marker.hideInfoWindow();
                        mMap.setInfoWindowAdapter(new CustomInfoWindow_Adapter(Home.this));
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
            }


            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);

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

//    private String getUrl(double latitude , double longitude , String nearbyPlace) {
//
//        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%2B61293744000&inputtype=phonenumber&fields=place_id");
////        googlePlaceUrl.append("location="+"19.080690,72.883062");
////        googlePlaceUrl.append("&radius="+5000);
////        googlePlaceUrl.append("&type="+"atm");
////        googlePlaceUrl.append("&sensor=true");
//        googlePlaceUrl.append("&key="+apiKey);
//
//        Log.d("which", "url = "+googlePlaceUrl.toString());
//
//        return googlePlaceUrl.toString();
//    }
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
//                        Toast.makeText(Home.this, "Finished", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {
//                        Toast.makeText(Home.this, "Cancelled", Toast.LENGTH_SHORT).show();

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
            Context context;
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("It is Required For Finding Current Location of the Device")
                    .setCancelable(false)
                    .setPositiveButton("Yes",(dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent,GPS_REQUEST_CODE);
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
    }

    private void initViews() {

        bottomNavigationView=findViewById(R.id.bottom_navigation_bar);
        frameLayout = findViewById(R.id.map_fragment);
        mCurrentLocation = findViewById(R.id.fab_myLocation);
        mDirection = findViewById(R.id.fab_direction);
        rest=findViewById(R.id.restaurant);
        atm=findViewById(R.id.atm);
        hotel=findViewById(R.id.hotel);
//        searchView = findViewById(R.id.search_View);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("Map", "IT is Ready");
        mMap =googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(true);
        LatLng latLng=new LatLng(23.114342, 79.170558);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,6));

//        mMap.setMyLocationEnabled(true);

    }

    private void LocationUpdate(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3500);
        locationRequest.setFastestInterval(1500);

        handlerThread = new HandlerThread("LocationCallbackHandler");
        handlerThread.start();

        mLocationClient.requestLocationUpdates(locationRequest,mLocationCallback, Looper.getMainLooper());
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

