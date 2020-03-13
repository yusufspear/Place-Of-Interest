//package com.example.placeofinterest;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class POIAdapter extends RecyclerView.Adapter<POIAdapter.POIView> {
//
//    List<Integer> imageList= new ArrayList<>();
//    List<String> titleList= new ArrayList<>();
//
//    public POIAdapter(List<Integer> imageList, List<String> titleList) {
//        this.imageList = imageList;
//        this.titleList = titleList;
//    }
//
//    @NonNull
//    @Override
//    public POIView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_poi,parent,false);
//        return new POIView(view) ;
//    }
//    int rowIndex  = -1;
//
//    @Override
//    public void onBindViewHolder(@NonNull POIView holder, int position) {
//
//        holder.image_POI.setImageResource(imageList.get(position));
//        holder.title.setText(titleList.get(position));
//
//        }
//
//
//
//    @Override
//    public int getItemCount() {
//        return titleList.size();
//    }
//
//
//    public class  POIView extends RecyclerView.ViewHolder {
//
//        ImageView image_POI;
//        TextView title;
//
//        public POIView(@NonNull View itemView) {
//
//            super(itemView);
//            image_POI = itemView.findViewById(R.id.img_POI);
//            title = itemView.findViewById(R.id.txt_Title);
//
////            itemView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////
////                    if (mListener !=null){
////
////                        int position = getAdapterPosition();
////                        if(position!= RecyclerView.NO_POSITION){
////                            mListener.onItemClick(position);
////                        }
////                    }
////                }
////            });
//        }
//
//
//    }
//}
