package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.file.Path;
import java.util.Objects;

public class Register_Page extends AppCompatActivity {


    private TextInputLayout FullName, Email, Password, Repassword, PhoneNumber;
    private RadioButton GenderM;
    private Button Register, Backbtn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        initViews();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("User");
        Register.setOnClickListener(this::createUser);
        Backbtn.setOnClickListener(this::backButton);


    }

    private void backButton(View view) {

        startActivity(new Intent(Register_Page.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    private void initViews() {

        FullName = findViewById(R.id.et_Fullname);
        Email = findViewById(R.id.et_email);
        Password = findViewById(R.id.et_password);
        Repassword = findViewById(R.id.et_repassword);
        PhoneNumber = findViewById(R.id.et_phone);
        Register = findViewById(R.id.btn_registeruser);
        GenderM = findViewById(R.id.rdMale);
        Backbtn = findViewById(R.id.btn_back);
    }


    // Read from the database

    private void createUser(View view) {


        String gender;
        String fullName = Objects.requireNonNull(FullName.getEditText()).getText().toString().trim();
        String phoneNumber = Objects.requireNonNull(PhoneNumber.getEditText()).getText().toString().trim();
        String email = Objects.requireNonNull(Email.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(Password.getEditText()).getText().toString().trim();
        String repassword = Objects.requireNonNull(Repassword.getEditText()).getText().toString().trim();
        String isNew="true";

        if (GenderM.isChecked()) {
            gender = "Male";
        } else {
            gender = "FeMale";

        }
        Objects.requireNonNull(Email.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String hint = Objects.requireNonNull(Email.getHint()).toString();
                if (hint.equals("Email is required. Can't be empty.")){
                    Email.setHint("Invalid Email. Enter valid email address.");

                }else if (hint.equals("Invalid Email. Enter valid email address.")){
                    Email.setHint("Enter Email");
                    Email.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#000000")));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(Password.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String hint= Objects.requireNonNull(Password.getHint()).toString();
                if (hint.equals("Password is required. Can't be empty.")){
                    Password.setHint("Enter Password");
                    Password.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#000000")));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (!validateEmailAddress() | !validatePassword() | !validateName() | !validatePhoneNumber()) {
            // Email or Password not valid,
            return;
        }
        //Email and Password valid, create user here

        if (!password.equals(repassword)) {
            Toast.makeText(this, "Password NOT Match", Toast.LENGTH_LONG).show();
        } else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                               @Override
                                               public void onComplete(@NonNull Task<AuthResult> task) {
                                                   if (task.isSuccessful()) {
                                                       Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification()
                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                       if (task.isSuccessful()) {

                                                                           getValues(fullName, email, password, phoneNumber, gender,isNew);

                                                                           User user = new User(fullName, email, password, phoneNumber, gender,isNew);
                                                                           mRef.child(mAuth.getCurrentUser().getUid()).setValue(user);
                                                                           double currentLocation=0.0;
                                                                           mRef.child(mAuth.getCurrentUser().getUid()).child("Location").child("lastLocation").child("Latitude").setValue(currentLocation);
                                                                           mRef.child(mAuth.getCurrentUser().getUid()).child("Location").child("lastLocation").child("Longitude").setValue(currentLocation);
                                                                           Toast.makeText(Register_Page.this, "User Created Successfully\n Waiting For Authentication ", Toast.LENGTH_LONG).show();
                                                                           startActivity(new Intent(Register_Page.this, MainActivity.class));
                                                                           overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                                                       } else {

                                                                           Toast.makeText(Register_Page.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                       }
                                                                   }
                                                               });


                                                   } else {
                                                       Toast.makeText(Register_Page.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                   }
                                               }
                                           }
                    )
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Register_Page.this, "Email Already in Database", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private void getValues(String fullName, String email, String password, String phoneNumber, String gender, String isNew) {
        User user = new User(fullName, email, password, phoneNumber, gender, isNew);
        user.setUsername(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        user.setGender(gender);
        user.setisnew(isNew);

    }


    private boolean validateEmailAddress() {

        String email = Email.getEditText().getText().toString().trim();
        Email.getEditText().getBackground().clearColorFilter();

        if (email.isEmpty()) {
            Email.setHint("Email is required. Can't be empty.");
            Email.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));

            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setHint("Invalid Email. Enter valid email address.");
            Email.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));

            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {

        String password = Password.getEditText().getText().toString().trim();
        Password.getEditText().getBackground().clearColorFilter();

        if (password.isEmpty()) {
            Password.setHint("Password is required. Can't be empty.");
            Password.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));

            return false;
        } else {
            return true;
        }
    }


    private boolean validateName() {

        String fullName = FullName.getEditText().getText().toString().trim();

        if (fullName.isEmpty()) {
            FullName.setHint("Name Field Can't be empty!");
            PhoneNumber.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));

            return false;
        } else if (fullName.length() < 5) {
            FullName.setHint("Name is Too Short!");
            FullName.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            return true;
        } else {
            return true;
        }

    }

    private boolean validatePhoneNumber() {

        String phoneNumber = PhoneNumber.getEditText().getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            PhoneNumber.setHint("PhoneNumber is required");
            PhoneNumber.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));

            return false;
        } else if (phoneNumber.length() < 10) {
            PhoneNumber.setHint("10 Digit Required!");
            PhoneNumber.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));

            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
