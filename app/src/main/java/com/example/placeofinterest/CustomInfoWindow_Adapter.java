package com.example.placeofinterest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow_Adapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindows;

    CustomInfoWindow_Adapter(Context mContext) {
        mWindows = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
    }
    private void connectWindow(Marker marker,View view){
        TextView titletxt=view.findViewById(R.id.title);
        TextView snippettxt=view.findViewById(R.id.snippet);
        String title = marker.getTitle();

        if (!title.equals("")){
            String snippet = marker.getSnippet();
            titletxt.setText(title);
            snippettxt.setText(snippet);

        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        connectWindow(marker,mWindows);
        return mWindows;
    }

    @Override
    public View getInfoContents(Marker marker) {
        connectWindow(marker,mWindows);
        return mWindows;
    }

}
