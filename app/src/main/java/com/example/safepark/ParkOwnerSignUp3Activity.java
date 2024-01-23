package com.example.safepark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.safepark.obj.BankDetails;
import com.example.safepark.obj.ParkDetails;
import com.example.safepark.obj.ParkOwner;
import com.example.safepark.obj.VehicleOwner;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ParkOwnerSignUp3Activity extends AppCompatActivity {

    private TextInputLayout parkNameField, parkAddressField;
    private TextInputLayout carSlotsField, carPerHourCost;
    private TextInputLayout lorrySlotsField, lorryPerHourCost;
    private TextInputLayout bikeSlotsField, bikePerHourCost;
    private TextInputLayout vanSlotsField, vanPerHourCost;

    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkowner_signup3);
        Bundle bundle = getIntent().getBundleExtra("personalDetails");
        System.out.println(bundle.get("name"));

        mAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance("https://safepark-c3e3f-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = rootNode.getReference("users").child("ParkOwner");

        parkNameField = findViewById(R.id.parkOwnerParkNameField);
        parkAddressField = findViewById(R.id.parkOwnerParkAddressField);

        carSlotsField = findViewById(R.id.availableCarSlotsField);
        carPerHourCost = findViewById(R.id.carPerHourCost);

        lorrySlotsField = findViewById(R.id.availableLorrySlotsField);
        lorryPerHourCost = findViewById(R.id.lorryPerHourCost);

        bikeSlotsField = findViewById(R.id.availableBikeSlotsField);
        bikePerHourCost = findViewById(R.id.bikePerHourCost);

        vanSlotsField = findViewById(R.id.availableVanSlotsField);
        vanPerHourCost = findViewById(R.id.vanPerHourCost);

        Button parkOwnerRegisterBtn = findViewById(R.id.parkOwnerRegisterBtn);
        parkOwnerRegisterBtn.setOnClickListener(v -> {
            registerParkOwner();
        });
    }

    private void registerParkOwner() {
        String parkName = parkNameField.getEditText().getText().toString();
        String parkAddress = parkAddressField.getEditText().getText().toString();

        String carSlots = carSlotsField.getEditText().getText().toString();
        String cphCost = carPerHourCost.getEditText().getText().toString();

        String lorrySlots = lorrySlotsField.getEditText().getText().toString();
        String lphCost = lorryPerHourCost.getEditText().getText().toString();

        String bikeSlots = bikeSlotsField.getEditText().getText().toString();
        String bphCost = bikePerHourCost.getEditText().getText().toString();

        String vanSlots = vanSlotsField.getEditText().getText().toString();
        String vphCost = vanPerHourCost.getEditText().getText().toString();

        if (parkName.isEmpty()) {
            parkNameField.setError("Please Enter Park Name");
            parkNameField.requestFocus();
            throwMessage("Please Enter Park Name");
        } else if (parkName.length() < 3) {
            parkNameField.setError("Please Enter A Valid Park Name");
            parkNameField.requestFocus();
            throwMessage("Please Enter A Valid Park Name");
        } else if (parkAddress.isEmpty()) {
            parkAddressField.setError("Please Enter Park Address");
            parkAddressField.requestFocus();
            throwMessage("Please Enter Park Address");
        } else if (parkAddress.length() < 3) {
            parkAddressField.setError("Please Enter A Valid Park Address");
            parkAddressField.requestFocus();
            throwMessage("Please Enter A Valid Park Address");
        } else if (carSlots.isEmpty()) {
            carSlotsField.setError("Please Enter Available Car Slots");
            carSlotsField.requestFocus();
            throwMessage("Please Enter Available Car Slots");
        } else if (Integer.parseInt(carSlots) < 1) {
            carSlotsField.setError("Please Enter A Valid Car Slot Amount");
            carSlotsField.requestFocus();
            throwMessage("Please Enter A Valid Car Slot Amount");
        } else if (cphCost.isEmpty()) {
            carPerHourCost.setError("Please Enter Car Per Hour Cost");
            carPerHourCost.requestFocus();
            throwMessage("Please Enter Car Cost Per Hour");
        } else if (Integer.parseInt(cphCost) < 10) {
            carPerHourCost.setError("Please Enter A Valid Car Per Hour Cost");
            carPerHourCost.requestFocus();
            throwMessage("Please Enter A Valid Car Per Hour Cost");
        } else if (lorrySlots.isEmpty()) {
            lorrySlotsField.setError("Please Enter Available Lorry Slots");
            lorrySlotsField.requestFocus();
            throwMessage("Please Enter Available Lorry Slots");
        } else if (Integer.parseInt(lorrySlots) < 1) {
            lorrySlotsField.setError("Please Enter A Valid Lorry Slot Amount");
            lorrySlotsField.requestFocus();
            throwMessage("Please Enter A Valid Lorry Slot Amount");
        } else if (lphCost.isEmpty()) {
            lorryPerHourCost.setError("Please Enter Lorry Per Hour Cost");
            lorryPerHourCost.requestFocus();
            throwMessage("Please Enter Lorry Cost Per Hour");
        } else if (Integer.parseInt(lphCost) < 10) {
            lorryPerHourCost.setError("Please Enter A Valid Lorry Per Hour Cost");
            lorryPerHourCost.requestFocus();
            throwMessage("Please Enter A Valid Lorry Per Hour Cost");
        } else if (bikeSlots.isEmpty()) {
            bikeSlotsField.setError("Please Enter Available Bike Slots");
            bikeSlotsField.requestFocus();
            throwMessage("Please Enter Available Bike Slots");
        } else if (Integer.parseInt(bikeSlots) < 1) {
            bikeSlotsField.setError("Please Enter A Valid Bike Slot Amount");
            bikeSlotsField.requestFocus();
            throwMessage("Please Enter A Valid Bike Slot Amount");
        } else if (bphCost.isEmpty()) {
            bikePerHourCost.setError("Please Enter Bike Per Hour Cost");
            bikePerHourCost.requestFocus();
            throwMessage("Please Enter Lorry Bike Per Hour");
        } else if (Integer.parseInt(bphCost) < 10) {
            bikePerHourCost.setError("Please Enter A Valid Bike Per Hour Cost");
            bikePerHourCost.requestFocus();
            throwMessage("Please Enter A Valid Bike Per Hour Cost");
        } else if (vanSlots.isEmpty()) {
            vanSlotsField.setError("Please Enter Available Van Slots");
            vanSlotsField.requestFocus();
            throwMessage("Please Enter Available Van Slots");
        } else if (Integer.parseInt(vanSlots) < 1) {
            vanSlotsField.setError("Please Enter A Valid Van Slot Amount");
            vanSlotsField.requestFocus();
            throwMessage("Please Enter A Valid Van Slot Amount");
        } else if (vphCost.isEmpty()) {
            vanPerHourCost.setError("Please Enter Van Per Hour Cost");
            vanPerHourCost.requestFocus();
            throwMessage("Please Enter Lorry Van Per Hour");
        } else if (Integer.parseInt(vphCost) < 10) {
            vanPerHourCost.setError("Please Enter A Valid Van Per Hour Cost");
            vanPerHourCost.requestFocus();
            throwMessage("Please Enter A Valid Van Per Hour Cost");
        } else {
            int cs = Integer.parseInt(carSlots);
            int cph = Integer.parseInt(cphCost);
            int ls = Integer.parseInt(lorrySlots);
            int lph = Integer.parseInt(lphCost);
            int bs = Integer.parseInt(bikeSlots);
            int bph = Integer.parseInt(bphCost);
            int vs = Integer.parseInt(vanSlots);
            int vph = Integer.parseInt(vphCost);

            Bundle bundle = new Bundle();
            bundle.putBundle("personalDetails", getIntent().getBundleExtra("personalDetails"));

            bundle.putString("bankName", getIntent().getStringExtra("bankName"));
            bundle.putString("bankCode", getIntent().getStringExtra("bankCode"));
            bundle.putString("branchName", getIntent().getStringExtra("branchName"));
            bundle.putString("branchCode", getIntent().getStringExtra("branchCode"));
            bundle.putString("fullName", getIntent().getStringExtra("fullName"));
            bundle.putString("accountNumber", getIntent().getStringExtra("accountNumber"));
            bundle.putString("nicNumber", getIntent().getStringExtra("nicNumber"));
            bundle.putString("parkName", parkName);
            bundle.putString("parkAddress", parkAddress);
            bundle.putInt("cs", cs);
            bundle.putInt("cph", cph);
            bundle.putInt("ls", ls);
            bundle.putInt("lph", lph);
            bundle.putInt("bs", bs);
            bundle.putInt("bph", bph);
            bundle.putInt("vs", vs);
            bundle.putInt("vph", vph);

            Intent intent = new Intent(ParkOwnerSignUp3Activity.this, ParkOwnerSignUpLocationSelectActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void throwMessage(String error) {
        Toast.makeText(ParkOwnerSignUp3Activity.this, error, Toast.LENGTH_SHORT).show();
    }
}