package com.example.placeofinterest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class POIAdapter extends RecyclerView.Adapter<POIAdapter.POIView> {

    List<String> titleList= new ArrayList<>();


    public POIAdapter(List<String> titleList) {
        this.titleList = titleList;

    }

    @NonNull
    @Override
    public POIView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_poi,parent,false);
        return new POIView(view) ;
    }
    int rowIndex  = -1;

    @Override
    public void onBindViewHolder(@NonNull POIView holder, int position) {

        holder.title.setText(titleList.get(position));

        }



    @Override
    public int getItemCount() {
        return titleList.size();
    }


    public class  POIView extends RecyclerView.ViewHolder {

        TextView title;

        public POIView(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.txt_Title);

        }


    }
}
