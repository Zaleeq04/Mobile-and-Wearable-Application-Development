package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    // UI components for user registration
    EditText email, password;
    Button registerBtn;
    TextView loginLink;

    // Firebase Authentication instance
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load registration screen layout
        setContentView(R.layout.activity_register);

        // Initialise Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Connect Java variables to XML views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);
        loginLink = findViewById(R.id.loginLink);

        // Allows existing users to return to the login screen
        loginLink.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Handles new user registration
        registerBtn.setOnClickListener(v -> {

            // Get user input from registration form
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            // Create Firebase user account with email and password
            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {

                        // Registration successful
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // Redirect user to login screen after account creation
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        } else {
                            // Display registration error message
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });
    }
}