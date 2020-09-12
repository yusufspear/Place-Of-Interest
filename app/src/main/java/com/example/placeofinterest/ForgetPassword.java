package com.example.placeofinterest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class ForgetPassword extends AppCompatActivity {

    TextInputLayout mForgetPassword;
    Button Reset;
    Toolbar toolbar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mAuth=FirebaseAuth.getInstance();
        initViews();
        Objects.requireNonNull(mForgetPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String hint = Objects.requireNonNull(mForgetPassword.getHint()).toString();
                if (hint.equals("Email is required. Can't be empty.")){
                    mForgetPassword.setHint("Invalid Email. Enter valid email address.");

                }else if (hint.equals("Invalid Email. Enter valid email address.")){
                    mForgetPassword.setHint("Enter Email");
                    mForgetPassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#000000")));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {



            }
        });

        Reset.setOnClickListener(this::forgetPassword);

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

    private void forgetPassword(View view) {

        String email = mForgetPassword.getEditText().getText().toString();

        if (validateEmailAddress()){

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ForgetPassword.this,"Please Check Your Email.",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgetPassword.this, MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    }else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(ForgetPassword.this,"Email not Exit in Database",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    private void initViews() {

        mForgetPassword = findViewById(R.id.et_forgetpassword);
        Reset = findViewById(R.id.btn_forget_password);
        toolbar = findViewById(R.id.toolbar_forgetPassword);
    }


    private boolean validateEmailAddress() {

        String email = mForgetPassword.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            mForgetPassword.setHint("Email is required. Can't be empty.");
            mForgetPassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));

            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mForgetPassword.setHint("Invalid Email. Enter valid email address.");
            mForgetPassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));

            return false;
        } else {
            mForgetPassword.setError(null);
            return true;
        }
    }
}
