package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
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
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String[] New = new String[6];
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Image");
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("User");
                if (user!=null){
            Log.i("which", "User NOT NULL ");
            if (user.getPhotoUrl()!=null){
                Log.i("which", "PhotoUrl was there: ");
                Glide.with(Profile.this)
                        .load(user.getPhotoUrl())
                        .into(userphoto);
            }

        }
        mRef.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);
                New[0] = dataSnapshot.child("username").getValue(String.class);
                New[1] = dataSnapshot.child("isnew").getValue(String.class);

                Log.i("List", "onDataChange: Execute");
                mOutputText.setText(New[1]);
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
//        if(requestCode==REQUEST_CODE_FOR_IMAGEUPLOAD && resultCode==RESULT_OK && data!=null) {
//            Uri uri = data.getData();
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .setAllowRotation(true)
//                    .start(this);
//        }
        if (requestCode == REQUEST_CODE_FOR_IMAGEUPLOAD && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            filePath=data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                handleUpload(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
//                bitmap.setWidth(100);
//                bitmap.setHeight(100);
            userphoto.setImageBitmap(bitmap);
            if(filePath!=null){
                String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference mStrRef = mStorageRef.child(uid+".jpeg");
                Log.i("which", "onSuccess: ");

                mStrRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(Profile.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                                Log.i("which", "onSuccess: ");
//                                getDownloadUrl(mStrRef);

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(Profile.this, "Uploading....", Toast.LENGTH_SHORT).show();


                            }
                        });

            }
        }
    }
    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("which", "onFailure: ",e.getCause() );
                    }
                });
    }

//    private void getDownloadUrl(StorageReference mStrRef) {
//        mStrRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                setUserProfileUrl(uri);
//            }
//        });
//    }
private void getDownloadUrl(StorageReference reference) {
    reference.getDownloadUrl()
            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.i("which", "onSuccess: " + uri);
                    setUserProfileUrl(uri);
                }
            });
}

//    private void setUserProfileUrl(Uri uri) {
//
//        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
//        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
//                .setPhotoUri(uri)
//                .build();
//
//        user.updateProfile(request)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(Profile.this, "Profile Update Successfully", Toast.LENGTH_SHORT).show();
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//    }
private void setUserProfileUrl(Uri uri) {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build();

    user.updateProfile(request)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Profile.this, "Updated succesfully", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
                }
            });
}
    private void ViewInstantProfile(View view) {
    }

    private void EditProfile(View view) {


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
