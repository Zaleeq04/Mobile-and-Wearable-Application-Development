package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // UI components for user login
    EditText email, password;
    Button loginBtn;
    TextView goRegister;

    // Firebase Authentication instance
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load login screen layout
        setContentView(R.layout.activity_login);

        // Initialise Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Connect Java variables to XML views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        goRegister = findViewById(R.id.goRegister);

        // Handle login button click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get user input from email and password fields
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                // Authenticate user with Firebase
                auth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(task -> {

                            // Login successful
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,
                                        "Login Successful",
                                        Toast.LENGTH_SHORT).show();

                                // Open main application screen
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            } else {
                                // Show login error message
                                Toast.makeText(LoginActivity.this,
                                        "Login Failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Navigate to registration screen if user does not have an account
        goRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }
}