package com.example.safepark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ParkOwnerSignUpActivity extends AppCompatActivity {
    private TextInputLayout nameField, emailField, phoneNumberField, passwordField, confirmPasswordField;
    private FirebaseAuth mAuth;

    public ParkOwnerSignUpActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkowner_signup);

        nameField = findViewById(R.id.parkOwnerNameField);
        emailField = findViewById(R.id.parkOwnerEmailField);
        phoneNumberField = findViewById(R.id.parkOwnerPhoneNumberField);
        passwordField = findViewById(R.id.parkOwnerPasswordField);
        confirmPasswordField = findViewById(R.id.parkOwnerConfirmPasswordField);

        mAuth = FirebaseAuth.getInstance();

        Button bankDetailsBtn = findViewById(R.id.bankDetailsBtn);
        bankDetailsBtn.setOnClickListener(v -> {
            verifyAndPassData(emailField.getEditText().getText().toString());
        });
    }

    private void verifyAndPassData(String email) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                    if (isNewUser) {
                        processData();
                    } else {
                        emailField.setError("User Exists");
                        emailField.requestFocus();
                        throwMessage("User Already Exists");
                    }
                });
    }

    private void processData() {
        nameField.setError(null);
        emailField.setError(null);
        phoneNumberField.setError(null);
        passwordField.setError(null);
        confirmPasswordField.setError(null);

        String name = nameField.getEditText().getText().toString();
        String email = emailField.getEditText().getText().toString();
        String phoneNumber = phoneNumberField.getEditText().getText().toString();
        String password = passwordField.getEditText().getText().toString();
        String confirmPassword = confirmPasswordField.getEditText().getText().toString();

        if (name.isEmpty()) {
            nameField.setError("Please enter name.");
            nameField.requestFocus();
            throwMessage("Name Required");
        } else if (name.length() < 3) {
            nameField.setError("Please enter a valid name.");
            nameField.requestFocus();
            throwMessage("Valid Name Required");
        } else if (email.isEmpty()) {
            emailField.setError("Please enter email.");
            emailField.requestFocus();
            throwMessage("Email Required");
        } else if (email.length() < 5) {
            emailField.setError("Valid Email Required");
            emailField.requestFocus();
            throwMessage("Valid Email Required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Invalid email format.");
            emailField.requestFocus();
            throwMessage("Invalid Email Format");
        } else if (phoneNumber.isEmpty()) {
            phoneNumberField.setError("Please enter phone number.");
            phoneNumberField.requestFocus();
            throwMessage("Phone Number Required");
        } else if (phoneNumber.length() < 9 || phoneNumber.length() > 10) {
            phoneNumberField.setError("Valid Phone Number Required");
            phoneNumberField.requestFocus();
            throwMessage("Valid Phone Number Required");
        } else if (password.isEmpty()) {
            passwordField.setError("Please enter password");
            passwordField.requestFocus();
            throwMessage("Password Required");
        } else if (password.length() < 8) {
            passwordField.setError("Password should contain at least 8 characters");
            passwordField.requestFocus();
            throwMessage("Password Isn't Secure Enough");
        } else if (confirmPassword.isEmpty()) {
            confirmPasswordField.setError("Please enter confirm password");
            confirmPasswordField.requestFocus();
            throwMessage("Confirm Password Required");
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordField.setError("Passwords don't match.");
            confirmPasswordField.requestFocus();
            throwMessage("Passwords don't match");
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("email", email);
            bundle.putString("phoneNumber", phoneNumber);
            bundle.putString("password", password);

            Intent intent = new Intent(getApplicationContext(), ParkOwnerSignUp2Activity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void throwMessage(String error) {
        Toast.makeText(ParkOwnerSignUpActivity.this, error, Toast.LENGTH_SHORT).show();
    }
}