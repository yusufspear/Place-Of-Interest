package com.example.placeofinterest.module;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.placeofinterest.Home;
import com.example.placeofinterest.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TopFragment extends Fragment {

    private View view;
    private TextView display_text;
    private CardView cardView;
    private EditText line1,line2;
    private ImageButton imageButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view=inflater.inflate(R.layout.top_fragment, container,false);
        initView();
        line1.setInputType(InputType.TYPE_NULL);
        line2.setInputType(InputType.TYPE_NULL);
        imageButton.setOnClickListener(this::backClick);
        setData();
        return view;
    }

    private void setData() {
        Bundle b1=getArguments();
        String source = b1.getString("source");
        String destination=b1.getString("destination");
        line1.setText(source);
        line2.setText(destination);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void initView() {
        line1 =view.findViewById(R.id.source);
        line2 =view.findViewById(R.id.destination);
        imageButton=view.findViewById(R.id.back_arrow);

    }

    private void backClick(View view) {

        Home home= (Home) getActivity();
        if (home != null) {
            home.closeFragment();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
    }

}
