package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    // UI components for displaying user account information
    TextView userEmail, userId, userVerified;

    // Buttons for navigation and logout
    Button logoutProfileBtn, homeProfileBtn;

    // Firebase Authentication instance
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load profile screen layout
        setContentView(R.layout.activity_profile);

        // Initialise Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Connect Java variables to XML views
        userEmail = findViewById(R.id.userEmail);
        userId = findViewById(R.id.userId);
        userVerified = findViewById(R.id.userVerified);
        logoutProfileBtn = findViewById(R.id.logoutProfileBtn);
        homeProfileBtn = findViewById(R.id.homeProfileBtn);

        // Get currently logged-in Firebase user
        FirebaseUser user = auth.getCurrentUser();

        // Display user account details if logged in
        if (user != null) {
            userEmail.setText(user.getEmail());

            // Show shortened user ID for cleaner display
            String shortId = "ID: " + user.getUid().substring(0, 8);
            userId.setText(shortId);

            // Display email verification status with colour indicators
            if (user.isEmailVerified()) {
                userVerified.setText("Email Verified");
                userVerified.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                userVerified.setText("Email Not Verified");
                userVerified.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }

        // Return user to the main homepage
        homeProfileBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        // Sign user out and return to login screen
        logoutProfileBtn.setOnClickListener(v -> {
            auth.signOut();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

            // Clear activity stack so user cannot return without logging in
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        });
    }
}