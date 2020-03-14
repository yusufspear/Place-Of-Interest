package com.example.placeofinterest;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Priyanka
 */

class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private String googlePlacesData;
    private GoogleMap mMap;
    String url;
    private Context mContext;

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

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");


            double lat = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lat")));
            double lng = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lng")));

            LatLng latLng = new LatLng( lat, lng);
            Home home=new Home();
            mContext=home.getContext();
//            mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(placeName)
//                    .snippet(  "ADDRESS:  "+vicinity +"\nRATING: "+rating+"\nCONTACT: "+"null")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            mMap.addMarker(new MarkerOptions().position(latLng).title(placeName)
                    .snippet( "ADDRESS : "+ vicinity)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
//            mMap.setInfoWindowAdapter(new CustomInfoWindow_Adapter());
//            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                @SuppressLint("InflateParams")
//                @Override
//                public void onInfoWindowClick(Marker marker) {
//                    final View mWindows;
//                    mWindows = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
//                    TextView titletxt=(TextView) mWindows.findViewById(R.id.title);
//                    TextView snippettxt=(TextView) mWindows.findViewById(R.id.snippet);
//                    String title = marker.getTitle();
//
//                    if (!title.equals("")){
//                        String snippet = marker.getSnippet();
//                        titletxt.setText(title);
//                        snippettxt.setText(snippet);
//
//                    }
//                }
//            });

        }
    }
}