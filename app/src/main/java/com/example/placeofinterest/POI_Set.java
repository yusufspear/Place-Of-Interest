package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static androidx.constraintlayout.widget.Constraints.TAG;

import com.example.placeofinterest.dialog.AddPlaceDialog;
import com.google.android.material.circularreveal.CircularRevealHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.ls.LSInput;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class POI_Set extends AppCompatActivity {


    RecyclerView recyclerView;
    POIAdapter poiAdapter;
    FloatingActionButton fab_add, fab_delete;

    //    String New_Place;
    List<String> titleList = new ArrayList<>();
    //    List<Integer> ItemNumber= new ArrayList<>();
//    private HashMap<Integer, Integer> ItemNumber=new HashMap<>();
//    Integer[] ItemNumber= new Integer[40];
//    int i,j=0;
//    Boolean aBoolean=false;
    private String[] User_POI_List;
    private String[] position;
    private Object[] positionFinal;
    private FirebaseUser user;
    int i = 0, SIZE, Count = 0;
    private DatabaseReference mDataRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi__set);

        initViews();

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDataRef = FirebaseDatabase.getInstance().getReference("User");

        fab_add.setOnClickListener(this::add);
        fab_delete.setOnClickListener(this::delete);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        getPOIList(user);
        Count = titleList.size();


    }

    private void getPOIList(FirebaseUser user) {
        if (user != null) {

            mDataRef.child(user.getUid()).child("POI List").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long Count = dataSnapshot.getChildrenCount();
                    User_POI_List = new String[(int) Count];
                    Log.i(TAG, "onDataChange: Count" + Count);
                    for (i = 0; i < Count; i++) {
                        User_POI_List[i] = (String) dataSnapshot.child(String.valueOf(i + 1)).getValue();
                        titleList.add(User_POI_List[i]);
                        Log.i(TAG, "onDataChange: POI LIST" + User_POI_List.length + User_POI_List[i]);
                    }
                    poiAdapter = new POIAdapter(titleList);
                    recyclerView.setAdapter(poiAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void delete(View view) {

        if (mDataRef.child(user.getUid()).child("Delete POI List") != null) {


            mDataRef.child(user.getUid()).child("Delete POI List").child("index").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Integer> list = new ArrayList();

                    long Count = dataSnapshot.getChildrenCount();
                    Log.i(TAG, "onDataChange: Count" + Count + "TITLE LIST SIZE " + titleList.size());
                    for (int i = 0; i < Count; i++) {
                        int temp = dataSnapshot.child(String.valueOf(i)).getValue(Integer.class);
                        Log.i(TAG, "onDataChange: Selected Item" + i + temp);
                        list.add(temp);
                        Log.i(TAG, "onDataChange: Count" + i + Count + "TITLE LIST SIZE " + titleList.size());

                        titleList.remove(temp);
                        poiAdapter.notifyItemRemoved(temp);

                    }
                    poiAdapter.notifyDataSetChanged();


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
        startActivity(new Intent(this, Home.class));
        finish();

    }

    private void add(View view) {
        openDialog();
    }

    public void returnValue(String string) {
        Log.i(TAG, "returnValue: titleList Size Before " + titleList.size() + Count);
        int position = titleList.size() + 1;
        Count = titleList.size();
        titleList.add(string);

        Log.i(TAG, "returnValue: titleList Size After " + titleList.size() + Count);
        poiAdapter.notifyItemInserted(position);
        poiAdapter.notifyDataSetChanged();

    }

    public void deleteItemInfo(HashMap data, int size) {
        Log.i(TAG, "deleteItemInfo: " + data + " Size " + data.size() + data.get(0) + "\n Fixed Size " + size);
        int j = 0;
        SIZE = data.size();
        positionFinal = new Integer[SIZE];

        for (int i = 0; i < size; i++) {
            Object value = data.get(i);
            Object value1 = data.get(size + 1);
            if (value != value1) {
                Log.i(TAG, "deleteItemInfo: " + value);
                positionFinal[j] = value;
                Log.i(TAG, "deleteItemInfo: " + positionFinal.length);
                j++;
            }
        }
        for (int i = 0; i < positionFinal.length; i++) {
            Log.i(TAG, "deleteItemInfo: Final List" + positionFinal[i]);
        }
        saveInFirebase(positionFinal);


    }

    private void saveInFirebase(Object[] positionFinal) {
        DatabaseReference reference;
        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        List list = new ArrayList();
        for (int i = 0; i < positionFinal.length; i++) {
            list.add(positionFinal[i]);
            Log.i(TAG, "deleteItemInfo: Final List" + positionFinal[i] + " List " + list.get(i));

        }
        reference.child(firebaseUser.getUid()).child("Delete POI List").child("index").setValue(list);
    }

    public void openDialog() {
        AddPlaceDialog dialog = new AddPlaceDialog();
        dialog.show(getSupportFragmentManager(), "Adding Dialog");
    }

    private void initViews() {

        recyclerView = findViewById(R.id.recyclerView);
        fab_add = findViewById(R.id.fab_add);
        fab_delete = findViewById(R.id.fab_delete);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (){

//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onPause: returnValue: titleList Size Before " + titleList.size() + "Count " + Count);
        int size = titleList.size();
        mDataRef.child(user.getUid()).child("POI List").removeValue();
        Log.i(TAG, "onResume: Size" + size);
        for (int j = 0; j < size; j++) {
            String name = titleList.get(j);
            mDataRef.child(user.getUid()).child("POI List").child(String.valueOf(j + 1)).setValue(name);
            Log.i(TAG, "onResume: " + name + "Size of Title List" + size);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataRef.child(user.getUid()).child("Delete POI List").removeValue();

    }
}
