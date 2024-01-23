package com.example.safepark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.safepark.obj.VehicleOwner;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class VehicleOwnerSignUpActivity extends AppCompatActivity {

    private TextInputLayout name, email, phoneNumber, password, confirmPassword;

    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicleowner_signup);
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        Button registerBtn = findViewById(R.id.vehicleOwnerRegisterBtn);
        registerBtn.setOnClickListener(v -> {
            rootNode = FirebaseDatabase.getInstance("https://safepark-c3e3f-default-rtdb.asia-southeast1.firebasedatabase.app");
            reference = rootNode.getReference("users").child("VehicleOwner");
            verifyAndRegisterUser(email.getEditText().getText().toString());
        });
    }

    private void verifyAndRegisterUser(String email) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                    if (isNewUser) {
                        createUser(reference);
                    } else {
                        this.email.setError("User Exists");
                        this.email.requestFocus();
                        throwMessage("User Already Exists");
                    }
                });
    }

    private void createUser(DatabaseReference reference) {
        name.setError(null);
        email.setError(null);
        phoneNumber.setError(null);
        password.setError(null);
        confirmPassword.setError(null);

        String name = this.name.getEditText().getText().toString();
        String email = this.email.getEditText().getText().toString();
        String phoneNumber = this.phoneNumber.getEditText().getText().toString();
        String password = this.password.getEditText().getText().toString();
        String confirmPassword = this.confirmPassword.getEditText().getText().toString();

        if (name.isEmpty()) {
            this.name.setError("Please enter name.");
            throwMessage("Name Required");
        } else if (name.length() < 3) {
            this.name.setError("Please enter a valid name.");
            throwMessage("Valid Name Required");
        } else if (email.isEmpty()) {
            this.email.setError("Please enter email.");
            throwMessage("Email Required");
        } else if (email.length() < 5) {
            this.email.setError("Valid Email Required");
            throwMessage("Valid Email Required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Invalid email format.");
            throwMessage("Invalid Email Format");
        } else if (phoneNumber.isEmpty()) {
            this.phoneNumber.setError("Please enter phone number.");
            throwMessage("Phone Number Required");
        } else if (phoneNumber.length() < 9 || phoneNumber.length() > 10) {
            this.phoneNumber.setError("Valid Phone Number Required");
            throwMessage("Valid Phone Number Required");
        } else if (password.isEmpty()) {
            this.password.setError("Please enter password");
            throwMessage("Password Required");
        } else if (password.length() < 8) {
            this.password.setError("Password should contain at least 8 characters");
            throwMessage("Password Isn't Secure Enough");
        } else if (confirmPassword.isEmpty()) {
            this.confirmPassword.setError("Please enter confirm password");
            throwMessage("Confirm Password Required");
        } else if (!confirmPassword.equals(password)) {
            this.confirmPassword.setError("Passwords don't match.");
            throwMessage("Passwords don't match");
        } else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(VehicleOwnerSignUpActivity.this, task -> {
                        if (task.isSuccessful()) {
                            throwMessage("Successfully Registered!");
                            startActivity(new Intent(VehicleOwnerSignUpActivity.this, VerificationActivity.class));
                        } else {
                            throwMessage("Registration Failed: " + task.getException());
                        }
                    });

            VehicleOwner vehicleOwner = new VehicleOwner(name, email, phoneNumber, password);
            reference.child(vehicleOwner.getUniqueID()).setValue(vehicleOwner);
        }
    }

    private void throwMessage(String error) {
        Toast.makeText(VehicleOwnerSignUpActivity.this, error, Toast.LENGTH_SHORT).show();
    }
}