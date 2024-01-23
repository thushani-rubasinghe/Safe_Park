package com.example.safepark;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserTypeActivity extends AppCompatActivity {

    private Button proceedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usertype);

        Spinner spinner = findViewById(R.id.spinner);
        proceedBtn = findViewById(R.id.proceedAfterChoosingTypeBtn);

        List<String> categories = new ArrayList<>();
        categories.add("Vehicle Owner");
        categories.add("Park Owner");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Vehicle Owner")) {
                    proceedBtn.setOnClickListener(v -> startActivity(new Intent(UserTypeActivity.this, VehicleOwnerSignUpActivity.class)));
                } else if (parent.getItemAtPosition(position).equals("Park Owner")) {
                    proceedBtn.setOnClickListener(v -> startActivity(new Intent(UserTypeActivity.this, ParkOwnerSignUpAcceptConditionsActivity.class)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}