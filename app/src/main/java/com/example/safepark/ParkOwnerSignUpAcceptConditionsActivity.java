package com.example.safepark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ParkOwnerSignUpAcceptConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_owner_sign_up_accept_conditions);
    }

    public void conditionsProceedMethod(View view) {
        startActivity(new Intent(this, ParkOwnerSignUpActivity.class));
    }
}