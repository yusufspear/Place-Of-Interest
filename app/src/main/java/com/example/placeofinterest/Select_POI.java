package com.example.placeofinterest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static android.telephony.PhoneNumberUtils.compare;


public class Select_POI extends AppCompatActivity {

    private static final String TAG ="TAG" ;
    private View doorView;
    private ArrayList col = new ArrayList();
    Boolean[] list =new Boolean[7];
    String[] list_Name =new String[7];

    String[] list_poi =new String[7];
    private HashMap<String, Boolean> status = new HashMap<>();
    private HashMap<String, String> status_Name = new HashMap<>();


    FloatingActionButton fab_confirm;
    ImageView img_gym,img_hotel, img_restaurant,img_music_venues,img_park,img_mall,img_cinema;
    TextView txt_gym,txt_hotel,txt_restaurant,txt_music_venues,txt_park,txt_mall,txt_cinema;
    CardView cardView_gym,cardView_park,cardView_hotel, cardView_restaurant,cardView_music_venues,cardView_mall,cardView_cinema;

    private FirebaseUser user;
    private DatabaseReference mDataRef;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_poi);
        doorView  = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            doorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initviews();
        cardView_gym.setOnClickListener(this::GymClick);
        cardView_cinema.setOnClickListener(this::CinemaClick);
        cardView_hotel.setOnClickListener(this::HotelClick);
        cardView_mall.setOnClickListener(this::MallClick);
        cardView_music_venues.setOnClickListener(this::MusicVinuesClick);
        cardView_park.setOnClickListener(this::ParkClick);
        cardView_restaurant.setOnClickListener(this::RestaurantClick);

        fab_confirm.setOnClickListener(this::confirm);

        user=FirebaseAuth.getInstance().getCurrentUser();
        mDataRef = FirebaseDatabase.getInstance().getReference("User");

        defaultSelectPoi();

    }

    private void defaultSelectPoi() {

        status.put("Gym",false);
        status.put("Cinema",false);
        status.put("Hotel",false);
        status.put("Mall",false);
        status.put("Music Vinues",false);
        status.put("Park",false);
        status.put("Restaurant",false);
    }

    private void confirm(View view) {

        int total=0;

        for (int i=0; i<6; i++){
            list[i]=false;
        }

        list[0]=status.get("Gym");
        list[1]=status.get("Mall");
        list[2]=status.get("Park");
        list[3]=status.get("Music Vinues");
        list[4]=status.get("Hotel");
        list[5]=status.get("Restaurant");
        list[6]=status.get("Cinema");

        list_Name[0]=status_Name.get("Gym");
        list_Name[1]=status_Name.get("Mall");
        list_Name[2]=status_Name.get("Park");
        list_Name[3]=status_Name.get("Music Vinues");
        list_Name[4]=status_Name.get("Hotel");
        list_Name[5]=status_Name.get("Restaurant");
        list_Name[6]=status_Name.get("Cinema");


        for (int i=0,j=0; i<6; i++){
            if (list[i]){
                total++;
                list_poi[j]=list_Name[i];
                Log.i(TAG, "list_poi"+j+list_poi[j]+ total);
                j++;
            }
        }
        if (total>=3){
            for (int i=0,j=1;i<total;i++,j++){

                mDataRef.child(user.getUid()).child("POI List").child(String.valueOf(j)).setValue(list_poi[i]);
            }
            String isNew="false";
            mDataRef.child(user.getUid()).child("isnew").setValue(isNew);
            startActivity(new Intent(this,Home.class));
            finish();
        }
        else {
            Toast.makeText(this, "Select At least 3", Toast.LENGTH_LONG).show();
        }

    }

    private void RestaurantClick(View view) {

        Poi_Status(cardView_restaurant, img_restaurant,"Restaurant");
    }

    private void ParkClick(View view) {

        Poi_Status(cardView_park,img_park,"Park");
    }

    private void MusicVinuesClick(View view) {

        Poi_Status(cardView_music_venues,img_music_venues,"Music Vinues");
    }

    private void MallClick(View view) {

        Poi_Status(cardView_mall,img_mall,"Mall");
    }

    private void HotelClick(View view) {

        Poi_Status(cardView_hotel,img_hotel,"Hotel");
    }

    private void CinemaClick(View view) {

        Poi_Status(cardView_cinema,img_cinema,"Cinema");
    }

    private void GymClick(View view) {

        Poi_Status(cardView_gym,img_gym,"Gym");
    }

    private void Poi_Status(CardView cardView, ImageView imageView,String viewName) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cardView.getCardBackgroundColor()==getColorStateList(R.color.colorWhite)){

                status.put(viewName,true);
                status_Name.put(viewName,viewName);
                imageView.setAlpha(0.95f);
                cardView.setCardBackgroundColor(getResources().getColor(R.color.Yellow));
                Log.i(TAG, "GymClick: IF RUN"+status.get(viewName));

            }
            else{
                status.put(viewName,false);
                status_Name.remove(viewName);
                imageView.setAlpha(1f);
                cardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                Log.i(TAG, "GymClick: ELSE RUN");
            }
        }

    }

    private void initviews() {

        cardView_gym=findViewById(R.id.cardView_gym);
        cardView_mall=findViewById(R.id.cardView_mall);
        cardView_park=findViewById(R.id.cardView_park);
        cardView_music_venues=findViewById(R.id.cardView_music_venues);
        cardView_hotel=findViewById(R.id.cardView_hotel);
        cardView_restaurant =findViewById(R.id.cardView_restaurant);
        cardView_cinema=findViewById(R.id.cardView_cinema);

        img_gym=findViewById(R.id.img_gym);
        img_mall=findViewById(R.id.img_mall);
        img_park=findViewById(R.id.img_park);
        img_music_venues=findViewById(R.id.img_music_venues);
        img_hotel=findViewById(R.id.img_hotel);
        img_restaurant =findViewById(R.id.img_restaurant);
        img_cinema=findViewById(R.id.img_cinema);

        fab_confirm = findViewById(R.id.fab_confirm);

    }


}
