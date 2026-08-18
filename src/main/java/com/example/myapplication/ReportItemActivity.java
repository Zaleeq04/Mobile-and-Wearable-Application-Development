package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportItemActivity extends AppCompatActivity {

    // UI components for report submission form
    EditText itemName, itemDescription, itemCategory, itemLocation;
    Spinner itemStatus;
    Button submitItemBtn, cancelBtn, selectImageBtn;
    ImageView itemImageView;

    // Stores selected image information
    Uri imageUri;
    String imageBase64 = "";

    // Firebase services
    FirebaseFirestore db;
    FirebaseAuth auth;

    // Handles image selection from phone gallery
    ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load report submission layout
        setContentView(R.layout.activity_report_item);

        // Connect Java variables to XML views
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemCategory = findViewById(R.id.itemCategory);
        itemLocation = findViewById(R.id.itemLocation);
        itemStatus = findViewById(R.id.itemStatus);
        submitItemBtn = findViewById(R.id.submitItemBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        itemImageView = findViewById(R.id.itemImageView);

        // Initialise Firebase services
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Populate report status spinner
        String[] statusOptions = {"Lost", "Found"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                statusOptions
        );
        itemStatus.setAdapter(adapter);

        // Register image picker for selecting images from device gallery
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        itemImageView.setImageURI(imageUri);

                        // Convert selected image to Base64 for Firestore storage
                        imageBase64 = convertImageToBase64(uri);
                    }
                }
        );

        // Open gallery for image selection
        selectImageBtn.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Cancel report submission and close screen
        cancelBtn.setOnClickListener(v -> finish());

        // Submit report when button is clicked
        submitItemBtn.setOnClickListener(v -> submitReport());
    }

    // Converts selected image into Base64 string for database storage
    private String convertImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Compress image to reduce storage size
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {
            Toast.makeText(this, "Image conversion failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return "";
        }
    }

    // Validates form input before saving report
    private void submitReport() {
        String name = itemName.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String category = itemCategory.getText().toString().trim();
        String location = itemLocation.getText().toString().trim();
        String status = itemStatus.getSelectedItem().toString();

        // Prevent empty submissions
        if (name.isEmpty() || description.isEmpty() || category.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save validated report to Firestore
        saveReportToFirestore(name, description, category, location, status, imageBase64);
    }

    // Saves report data to Firebase Firestore
    private void saveReportToFirestore(String name, String description, String category,
                                       String location, String status, String imageBase64) {

        // Get logged-in user ID for ownership tracking
        String userId = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid()
                : "unknown";

        // Store report data in HashMap
        Map<String, Object> report = new HashMap<>();
        report.put("name", name);
        report.put("description", description);
        report.put("category", category);
        report.put("location", location);
        report.put("status", status);
        report.put("imageBase64", imageBase64);
        report.put("userId", userId);
        report.put("approved", false);

        // Save report to Firestore database
        db.collection("reports")
                .add(report)
                .addOnSuccessListener(documentReference -> {

                    // Trigger smart match feature for lost items
                    if (status.equals("Lost")) {
                        checkForMatches(category);

                    } else {
                        Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })

                .addOnFailureListener(e -> {
                    Toast.makeText(
                            ReportItemActivity.this,
                            "Submit failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Unique feature: checks Firestore for matching found items
    private void checkForMatches(String category) {
        db.collection("reports")
                .whereEqualTo("status", "Found")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    ArrayList<Report> matches = new ArrayList<>();

                    // Convert matching Firestore documents into Report objects
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Report report = document.toObject(Report.class);
                        matches.add(report);
                    }

                    // Open match suggestion screen if matches exist
                    if (!matches.isEmpty()) {
                        Intent intent = new Intent(ReportItemActivity.this, MatchSuggestionActivity.class);
                        intent.putExtra("matches", matches);
                        startActivity(intent);

                    } else {
                        Toast.makeText(this, "No matches found yet", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                })

                .addOnFailureListener(e -> {
                    Toast.makeText(
                            ReportItemActivity.this,
                            "Match check failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                    finish();
                });
    }
}
