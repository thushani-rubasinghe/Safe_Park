package com.example.safepark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ParkOwnerSignUp2Activity extends AppCompatActivity {

    private TextInputLayout bankName, bankCode, branchName, branchCode, fullName, accountNumber, nicNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkowner_signup2);

        bankName = findViewById(R.id.bankNameField);
        bankCode = findViewById(R.id.bankCodeField);
        branchName = findViewById(R.id.branchNameField);
        branchCode = findViewById(R.id.branchCodeField);
        fullName = findViewById(R.id.fullNameField);
        accountNumber = findViewById(R.id.accountNumberField);
        nicNumber = findViewById(R.id.nicNumberField);

        Button parkDetailsBtn = findViewById(R.id.parkDetailsBtn);
        parkDetailsBtn.setOnClickListener(v -> {
            processBankData();
        });
    }

    private void processBankData() {
        String bankName = this.bankName.getEditText().getText().toString();
        String bankCode = this.bankCode.getEditText().getText().toString();
        String branchName = this.branchName.getEditText().getText().toString();
        String branchCode = this.branchCode.getEditText().getText().toString();
        String fullName = this.fullName.getEditText().getText().toString();
        String accountNumber = this.accountNumber.getEditText().getText().toString();
        String nicNumber = this.nicNumber.getEditText().getText().toString();

        if (bankName.isEmpty()) {
            this.bankName.setError("Bank Required");
            this.bankName.requestFocus();
            throwMessage("Bank Required");
        } else if (bankName.length() < 5) {
            this.bankName.setError("Valid Bank Required");
            this.bankName.requestFocus();
            throwMessage("Valid Bank Required");
        } else if (bankCode.isEmpty()) {
            this.bankCode.setError("Bank Code Required");
            this.bankCode.requestFocus();
            throwMessage("Bank Code Required");
        } else if (bankCode.length() != 4) {
            this.bankCode.setError("Valid Bank Code Required");
            this.bankCode.requestFocus();
            throwMessage("Valid Bank Code Required");
        } else if (branchName.isEmpty()) {
            this.branchName.setError("Branch Required");
            this.branchName.requestFocus();
            throwMessage("Branch Required");
        } else if (branchName.length() < 3) {
            this.branchName.setError("Valid Branch Required");
            this.branchName.requestFocus();
            throwMessage("Valid Branch Required");
        } else if (branchCode.isEmpty()) {
            this.branchCode.setError("Branch Code Required");
            this.branchCode.requestFocus();
            throwMessage("Branch Code Required");
        } else if (branchCode.length() != 3) {
            this.branchCode.setError("Valid Branch Code Required");
            this.branchCode.requestFocus();
            throwMessage("Valid Branch Code Required");
        } else if (fullName.isEmpty()) {
            this.fullName.setError("Name Required");
            this.fullName.requestFocus();
            throwMessage("Name Required");
        } else if (fullName.length() < 3) {
            this.fullName.setError("Valid Name Required");
            this.fullName.requestFocus();
            throwMessage("Valid Name Required");
        } else if (accountNumber.isEmpty()) {
            this.accountNumber.setError("Account No. Required");
            this.accountNumber.requestFocus();
            throwMessage("Account No. Required");
        } else if (accountNumber.length() < 5) {
            this.accountNumber.setError("Valid Account No. Required");
            this.accountNumber.requestFocus();
            throwMessage("Valid Account No. Required");
        } else if (nicNumber.isEmpty()) {
            this.nicNumber.setError("NIC No. Required");
            this.nicNumber.requestFocus();
            throwMessage("NIC No. Required");
        } else if (nicNumber.length() < 10) {
            this.nicNumber.setError("Valid NIC No. Required");
            this.nicNumber.requestFocus();
            throwMessage("Valid NIC No. Required");
        } else if (!nicNumber.matches("^[0-9]{9}[vV]$") && !nicNumber.matches("^[0-9]{7}[0][0-9]{4}$")) {
            this.nicNumber.setError("Invalid NIC Format");
            this.nicNumber.requestFocus();
            throwMessage("Invalid NIC Format");
        } else {
            Bundle bundle = new Bundle();
            bundle.putBundle("personalDetails", getIntent().getExtras());
            bundle.putString("bankName", bankName);
            bundle.putString("bankCode", bankCode);
            bundle.putString("branchName", branchName);
            bundle.putString("branchCode", branchCode);
            bundle.putString("fullName", fullName);
            bundle.putString("accountNumber", accountNumber);
            bundle.putString("nicNumber", nicNumber);

            Intent intent = new Intent(ParkOwnerSignUp2Activity.this, ParkOwnerSignUp3Activity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void throwMessage(String error) {
        Toast.makeText(ParkOwnerSignUp2Activity.this, error, Toast.LENGTH_SHORT).show();
    }

}