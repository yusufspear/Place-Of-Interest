package com.example.placeofinterest;

import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private static final String TAG ="TAG";

    private String googlePlacesData;
    private GoogleMap mMap;
    String url;
    Home home=new Home();
    ArrayList list_Lat = new ArrayList<Double>();
    ArrayList list_Long = new ArrayList<Double>();
    private LatLng latLng;

    private Context context;

    public GetNearbyPlacesData(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s){

        List<HashMap<String, String>> nearbyPlaceList;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        Log.d("nearbyplacesdata","called parse method");
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    {
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String rating = googlePlace.get("rating");
            float ret= Float.parseFloat(rating);
            String user_ratings_total = googlePlace.get("user_ratings_total");
//            if (ret>=3.0){
//
//                String placeName = googlePlace.get("place_name");
//                String vicinity = googlePlace.get("vicinity");
//                double lat = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lat")));
//                double lng = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lng")));
//
//                latLng = new LatLng(lat, lng);
//
//                mMap.addMarker(new MarkerOptions().position(latLng).title(placeName)
//                        .snippet("ADDRESS : " + vicinity + "\nRATING : " + rating + " (" + user_ratings_total + ")"));
//
//                Log.i(TAG, "showNearbyPlaces: " + i + nearbyPlaceList);
//
//            }
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lat")));
            double lng = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lng")));

            latLng = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions().position(latLng).title(placeName)
                    .snippet("ADDRESS : " + vicinity + "\nRATING : " + rating + " (" + user_ratings_total + ")"));

            Log.i(TAG, "showNearbyPlaces: " + i + nearbyPlaceList);
        }
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        Log.i(TAG, "showNearbyPlaces: List Print"+list_Lat+" "+list_Long);

//        home.addList(list_Lat,list_Long);

    }

}