package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Profile extends AppCompatActivity {

    private static final int REQUEST_CODE_FOR_IMAGEUPLOAD =10;

    Button signout,addNewPlace,viewInstant,editProfile;
    private TextView mOutputText;
    ImageView userphoto,uploadImageButton;
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private Uri filePath;
    private StorageReference mStorageRef;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View doorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            doorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }
        setContentView(R.layout.activity_profile);
        initViews();

        editProfile.setOnClickListener(this::EditProfile);
        viewInstant.setOnClickListener(this::ViewInstantProfile);
        addNewPlace.setOnClickListener(this::UpdattePOI);
        uploadImageButton.setOnClickListener(this::UploadImage);

        mAuth=FirebaseAuth.getInstance();
//        FirebaseUser user=mAuth.getCurrentUser();
        String[] New = new String[6];
        mStorageRef = FirebaseStorage.getInstance().getReference();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("User");

        mRef.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);
                New[0] = dataSnapshot.child("username").getValue(String.class);
                New[1] = dataSnapshot.child("isnew").getValue(String.class);

                Log.i("List", "onDataChange: Execute");
                mOutputText.setText(New[1]);
//
//                if (New[0].equals("false")){
//                    Log.i("if Ex", "onCreate: if Exiciye");
//                    mOutputText.setText(New[0] + " HI");
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        signout.setOnClickListener(this::signOutUser);

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.profile:
                        return true;

                    case R.id.home:
                        startActivity(new Intent(Profile.this, Home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.search:
                        startActivity(new Intent(Profile.this, Search.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.history:
                        startActivity(new Intent(Profile.this, History.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }

    private void UploadImage(View view) {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Please Select Your Picture"),REQUEST_CODE_FOR_IMAGEUPLOAD);


    }

    private void UpdattePOI(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_IMAGEUPLOAD && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            filePath=data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                userphoto.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void ViewInstantProfile(View view) {
    }

    private void EditProfile(View view) {

        if(filePath!=null){
            Toast.makeText(Profile.this, "Demo", Toast.LENGTH_SHORT).show();

            StorageReference mStrRef = mStorageRef.child("image/"+ UUID.randomUUID().toString());
            Log.i("OnSucc", "onSuccess: ");

            mStrRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Profile.this, "Working", Toast.LENGTH_SHORT).show();
                            Log.i("OnSucc", "onSuccess: ");

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

        }
    }


    private void signOutUser(View view){
            mAuth.signOut();
            startActivity(new Intent(this,MainActivity.class));
            //updateUI();

        }

    private void initViews() {

        bottomNavigationView=findViewById(R.id.bottom_navigation_bar);
        signout=findViewById(R.id.btn_singoutuser);
        mOutputText=findViewById(R.id.txt1);
        addNewPlace =findViewById(R.id.addnew_place);
        viewInstant =findViewById(R.id.view_instant_profile);
        editProfile =findViewById(R.id.edit_profile);
        userphoto = findViewById(R.id.userImage);
        uploadImageButton =findViewById(R.id.uploadImage);

//        FragmentHome = findViewById(R.id.fragment_home);

    }


}
