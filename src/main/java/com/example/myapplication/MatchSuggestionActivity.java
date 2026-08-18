package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MatchSuggestionActivity extends AppCompatActivity {

    // RecyclerView used to display matching found items
    RecyclerView recyclerView;

    // Adapter connects match data to RecyclerView
    ReportAdapter adapter;

    // Stores matched reports passed from ReportItemActivity
    ArrayList<Report> matches;

    // Button to return user to homepage
    Button backHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load match suggestion screen layout
        setContentView(R.layout.activity_match_suggestion);

        // Connect Java variables to XML views
        recyclerView = findViewById(R.id.matchRecyclerView);
        backHomeBtn = findViewById(R.id.backHomeBtn);

        // Retrieve matching reports passed from ReportItemActivity
        matches = (ArrayList<Report>) getIntent().getSerializableExtra("matches");

        // Prevent null errors if no matches were found
        if (matches == null) {
            matches = new ArrayList<>();
        }

        // Set up RecyclerView to display matching reports
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter(this, matches);
        recyclerView.setAdapter(adapter);

        // Return user to the homepage report feed
        backHomeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MatchSuggestionActivity.this, MainActivity.class);

            // Clear previous activities from stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        });
    }
}