package com.example.placeofinterest.module;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

public class deviceLocationUpdate {
    LatLng location;

    public deviceLocationUpdate() {
    }

    public deviceLocationUpdate(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
        Log.d("deviceLocationUpdate", location.toString());
    }
}