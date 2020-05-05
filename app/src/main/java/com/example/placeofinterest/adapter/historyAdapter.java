package com.example.placeofinterest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placeofinterest.POIAdapter;
import com.example.placeofinterest.R;

import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class historyAdapter extends RecyclerView.Adapter {

    private List<String> destTitle = new ArrayList<>();
    private List<URL> destUrl = new ArrayList<>();
    private List<Time> searchTime = new ArrayList<>();
    private List<String> destPoiType = new ArrayList<>();
    private List<Float> ratingValue = new ArrayList<>();

    public historyAdapter(List<String> destTitle, List<URL> destUrl, List<Time> searchTime, List<String> destPoiType, List<Float> ratingValue) {
        this.destTitle = destTitle;
        this.destUrl = destUrl;
        this.searchTime = searchTime;
        this.destPoiType = destPoiType;
        this.ratingValue = ratingValue;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row_template, parent, false);
        return new historyAdapter.HistoryView(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return destTitle.size();
    }


    public class HistoryView extends RecyclerView.ViewHolder {

        TextView destination_title,destination_rating,destination_url,destination_time,destination_poi_type;
        RatingBar ratingBar;
        ImageView dropdown;

        public HistoryView(@NonNull View itemView) {

            super(itemView);
            destination_title = itemView.findViewById(R.id.destination_title);
            destination_rating = itemView.findViewById(R.id.destination_rating);
            destination_url = itemView.findViewById(R.id.destination_url);
            destination_time = itemView.findViewById(R.id.destination_time);
            destination_poi_type = itemView.findViewById(R.id.destination_poi_type);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            dropdown = itemView.findViewById(R.id.dropdown);

        }
    }
}
