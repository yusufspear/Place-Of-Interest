package com.example.placeofinterest.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placeofinterest.Home;
import com.example.placeofinterest.POI_Set;
import com.example.placeofinterest.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AddPlaceDialog extends AppCompatDialogFragment {

        private EditText input;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.add_poi_dialog, null);

            builder.setView(view)
                    .setTitle("Add New Place")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String new_poi = input.getText().toString();
                            POI_Set poiSet= (POI_Set) getActivity();
                            poiSet.returnValue(new_poi);
                        }
                    });

            input = view.findViewById(R.id.input);

            return builder.create();
        }


}
