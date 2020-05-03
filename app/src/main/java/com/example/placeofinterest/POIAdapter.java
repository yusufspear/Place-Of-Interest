package com.example.placeofinterest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.internal.ContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.graphics.Color.BLACK;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.placeofinterest.R.*;


public class POIAdapter extends RecyclerView.Adapter<POIAdapter.POIView> {

    private List<String> titleList = new ArrayList<>();
    private HashMap<Integer, Integer> ItemNumber;



    public POIAdapter(List<String> titleList) {
        this.titleList = titleList;
    }

    @NonNull
    @Override
    public POIView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(layout.row_poi, parent, false);
        return new POIView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull POIView holder, int position) {

        holder.title.setText(titleList.get(position));
        holder.title.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                if (holder.title.getCurrentTextColor() == BLACK) {
                    holder.title.setTextColor(Color.YELLOW);
                    holder.title.setBackgroundColor(color.Pale_Yellow);
                    holder.title.setAlpha(0.95f);
                    Log.i(TAG, "onClick: Item" + position);
                    ItemCount(view,position);
                } else {
                    holder.title.setTextColor(BLACK);
                    holder.title.setBackgroundColor(Color.WHITE);
                    ItemCount(view,position);

                }
            }
        });

    }

    public void ItemCount(View view,int position) {


        if (ItemNumber.containsValue(position)) {
            ItemNumber.remove(position);
            Log.i(TAG, "Item Removed at Position  " + position + "  And Size of HashMap is" + ItemNumber.size());
            add();

        } else {
            ItemNumber.put(position, position);
            Log.i(TAG, "Item Add at Position  " + position + "  And Size of HashMap is" + ItemNumber.size());
            add();

        }
        for (int i=0 ; i<ItemNumber.size();i++){
            Log.i(TAG, "ItemCount: HashMap at index "+i+" Value"+ItemNumber.get(i));
        }
    }

    private void add() {
        POI_Set poiSet =new POI_Set();
        Log.i(TAG, "add: ");
        poiSet.deleteItemInfo(ItemNumber,titleList.size());

    }


    @Override
    public int getItemCount() {
         ItemNumber= new HashMap<>(titleList.size());
        Log.i(TAG, "getItemCount: HashMap Size"+titleList.size()+ItemNumber.size());
        return titleList.size();
    }



    public class POIView extends RecyclerView.ViewHolder {

        TextView title;

        public POIView(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(id.txt_Title);

        }


    }
}
