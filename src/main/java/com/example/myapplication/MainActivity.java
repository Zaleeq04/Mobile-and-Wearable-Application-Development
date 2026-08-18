package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // UI components for home screen, filtering and navigation
    Button addReportBtn, logoutBtn;
    RecyclerView reportsRecyclerView;
    Spinner filterSpinner;
    BottomNavigationView bottomNavigationView;

    // Lists used to store reports from Firestore
    ArrayList<Report> reportList;
    ArrayList<Report> fullReportList;

    // Adapter connects report data to the RecyclerView
    ReportAdapter reportAdapter;

    // Firebase services
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load main home screen layout
        setContentView(R.layout.activity_main);

        // Connect Java variables to XML views
        addReportBtn = findViewById(R.id.addReportBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        filterSpinner = findViewById(R.id.filterSpinner);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialise Firebase Firestore and Authentication
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialise report lists
        reportList = new ArrayList<>();
        fullReportList = new ArrayList<>();

        // Set up RecyclerView to display reports
        reportAdapter = new ReportAdapter(this, reportList);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportsRecyclerView.setAdapter(reportAdapter);

        // Filter options for report status and category
        String[] filters = {"All", "Lost", "Found", "wallet", "phone", "keys", "laptop", "bag", "battery"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filters
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        // Handles bottom navigation menu actions
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            // Stay on home feed
            if (id == R.id.nav_home) {
                return true;

                // Open report submission screen
            } else if (id == R.id.nav_report) {
                startActivity(new Intent(MainActivity.this, ReportItemActivity.class));
                return true;

                // Open user profile page
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;

                // Sign user out and return to login screen
            } else if (id == R.id.nav_logout) {
                auth.signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }

            return false;
        });

        // Filters report feed when a spinner option is selected
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();

                reportList.clear();

                // Show all reports if All is selected
                if (selected.equals("All")) {
                    reportList.addAll(fullReportList);

                } else {
                    // Filter reports by status or category
                    for (Report report : fullReportList) {
                        if (report.getStatus().equalsIgnoreCase(selected) ||
                                report.getCategory().equalsIgnoreCase(selected)) {
                            reportList.add(report);
                        }
                    }
                }

                // Refresh RecyclerView after filtering
                reportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Opens report submission screen
        addReportBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ReportItemActivity.class));
        });

        // Logs out user and clears previous activity history
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Load reports from Firestore when app opens
        loadReports();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload reports whenever returning to the home screen
        loadReports();
    }

    // Retrieves all reports from Firestore and displays them in the RecyclerView
    private void loadReports() {
        db.collection("reports")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reportList.clear();
                    fullReportList.clear();

                    // Convert each Firestore document into a Report object
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Report report = document.toObject(Report.class);

                        // Store Firestore document ID for edit/delete/update actions
                        report.setDocumentId(document.getId());

                        reportList.add(report);
                        fullReportList.add(report);
                    }

                    // Update RecyclerView after loading data
                    reportAdapter.notifyDataSetChanged();
                })

                .addOnFailureListener(e -> {
                    Toast.makeText(
                            MainActivity.this,
                            "Error loading reports: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}