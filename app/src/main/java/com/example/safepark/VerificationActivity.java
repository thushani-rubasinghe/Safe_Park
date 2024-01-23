package com.example.safepark;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class VerificationActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(isEmailVerified(), 20000, 1000);

        Button resendLinkBtn = findViewById(R.id.resendLinkBtn);
        resendLinkBtn.setOnClickListener(v -> {
            isEmailVerified();
        });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        if (!user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(VerificationActivity.this, "Verification Mail Sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VerificationActivity.this, "Error Occured: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private TimerTask isEmailVerified() {
        firebaseAuth.getCurrentUser().reload().addOnSuccessListener(aVoid -> {
            if (firebaseAuth.getCurrentUser() != null) {
                boolean isEmailVerified = firebaseAuth.getCurrentUser().isEmailVerified();
                if (isEmailVerified) {
                    Toast.makeText(getApplicationContext(), "Your Email Has Been Verified", Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                    startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                } else {
                    sendVerificationEmail(firebaseAuth.getCurrentUser());
                }
            }
        });
        return null;
    }
}