package com.example.placeofinterest;

import java.util.List;

public class fetchPlaceList {

    public List<String> placeList;

    public fetchPlaceList() {
    }

    public fetchPlaceList(List<String> placeList) {
        this.placeList = placeList;
    }

    public List<String> getPlaceList() {
        return placeList;
    }

    public void setPlaceList(List<String> placeList) {
        this.placeList = placeList;
    }
}
