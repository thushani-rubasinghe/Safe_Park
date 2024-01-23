package com.example.safepark;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safepark.obj.ParkOwner;
import com.example.safepark.obj.VehicleOwner;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email, password;
    private FirebaseAuth mAuth;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = new Intent(LoginActivity.this, VehicleOwnerNavigationActivity.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance("https://safepark-c3e3f-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

            Query query = rootRef.child("VehicleOwner").orderByChild("email").equalTo(user.getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        intent.putExtra("userType", "VehicleOwner");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            query = rootRef.child("ParkOwner").orderByChild("email").equalTo(user.getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        intent.putExtra("Type", "ParkOwner");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            startActivity(intent);
        }
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailField);
        password = findViewById(R.id.passwordField);

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(v -> openUserType());

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            loginUser();
        });
    }

    public void openUserType() {
        startActivity(new Intent(this, UserTypeActivity.class));
    }

    public void loginUser() {
        if (validateUsername() && validatePassword()) {
            isUser();
        }
    }

    private void isUser() {
        email.setError(null);
        password.setError(null);

        String email = this.email.getEditText().getText().toString().trim();
        String password = this.password.getEditText().getText().toString().trim();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {

                            DatabaseReference rootRef = FirebaseDatabase.getInstance("https://safepark-c3e3f-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

                            Query query = rootRef.child("VehicleOwner").orderByChild("email").equalTo(email);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                                            VehicleOwner vehicleOwner = user.getValue(VehicleOwner.class);
                                            if (vehicleOwner.getPassword().equals(password)) {
                                                Toast.makeText(getApplicationContext(), "Welcome " + vehicleOwner.getName() + "!", Toast.LENGTH_LONG).show();
                                                intent.putExtra("userType", "VehicleOwner");
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                            query = rootRef.child("ParkOwner").orderByChild("email").equalTo(email);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                                            ParkOwner parkOwner = user.getValue(ParkOwner.class);
                                            if (parkOwner.getPassword().equals(password)) {
                                                Toast.makeText(getApplicationContext(), "Welcome " + parkOwner.getName() + "!", Toast.LENGTH_LONG).show();

                                                intent.putExtra("userType", "ParkOwner");
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Account Not Found", Toast.LENGTH_SHORT).show());
            } else {
                this.password.setError("Empty Fields Found");
                Toast.makeText(LoginActivity.this, "Empty Fields Are not Allowed", Toast.LENGTH_SHORT).show();
            }
        } else if (email.isEmpty()) {
            this.email.setError("Empty Fields Found");
            Toast.makeText(LoginActivity.this, "Empty Fields Are not Allowed", Toast.LENGTH_SHORT).show();
        } else {
            this.email.setError("Pleas Enter Correct Email");
            Toast.makeText(LoginActivity.this, "Invalid Email Format", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validateUsername() {
        String email = this.email.getEditText().getText().toString();
        if (email.isEmpty()) {
            this.email.setError("Field cannot be empty");
            Toast.makeText(getApplicationContext(), "Email Required", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            this.email.setError(null);
            this.email.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String pass = password.getEditText().getText().toString();
        if (pass.isEmpty()) {
            password.setError("Field cannot be empty");
            Toast.makeText(getApplicationContext(), "Password Required", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
}
