package com.example.placeofinterest.module;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class NewPLaceFetchLocationList {

    LatLng location;
    Marker marker;

    public NewPLaceFetchLocationList() {
    }

    public NewPLaceFetchLocationList(LatLng location, Marker marker) {
        this.location = location;
        this.marker = marker;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
