package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditReportActivity extends AppCompatActivity {

    // UI components used to edit an existing report
    EditText itemName, itemDescription, itemCategory, itemLocation;
    Spinner itemStatus;
    Button updateBtn, cancelBtn, selectImageBtn;
    ImageView itemImageView;

    // Firestore database instance
    FirebaseFirestore db;

    // Stores the Firestore document ID and selected image data
    String documentId;
    String imageBase64 = "";
    Uri imageUri;

    // Handles image selection from the phone gallery
    ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Reuses the report item layout for editing existing reports
        setContentView(R.layout.activity_report_item);

        // Connect Java variables to XML views
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemCategory = findViewById(R.id.itemCategory);
        itemLocation = findViewById(R.id.itemLocation);
        itemStatus = findViewById(R.id.itemStatus);
        updateBtn = findViewById(R.id.submitItemBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        itemImageView = findViewById(R.id.itemImageView);

        // Initialise Firestore
        db = FirebaseFirestore.getInstance();

        // Get the selected report document ID from ItemDetailActivity
        documentId = getIntent().getStringExtra("documentId");

        // Populate spinner with report status options
        String[] statusOptions = {"Lost", "Found"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                statusOptions
        );
        itemStatus.setAdapter(adapter);

        // Retrieve existing report data passed through the intent
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        String location = getIntent().getStringExtra("location");
        String status = getIntent().getStringExtra("status");
        imageBase64 = getIntent().getStringExtra("imageBase64");

        // Pre-fill form fields with the current report data
        itemName.setText(name);
        itemDescription.setText(description);
        itemCategory.setText(category);
        itemLocation.setText(location);

        // Select the correct status in the spinner
        if (status != null && status.equals("Found")) {
            itemStatus.setSelection(1);
        }

        // Decode and display the existing report image if available
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            itemImageView.setImageBitmap(bitmap);
        }

        // Rename submit button because this screen updates existing reports
        updateBtn.setText("Update Report");

        // Register image picker and convert the selected image to Base64
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        itemImageView.setImageURI(uri);
                        imageBase64 = convertImageToBase64(uri);
                    }
                }
        );

        // Open image picker when user wants to replace the image
        selectImageBtn.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        // Close edit screen without saving changes
        cancelBtn.setOnClickListener(v -> finish());

        // Save updated report data to Firestore
        updateBtn.setOnClickListener(v -> updateReport());
    }

    // Converts selected image into Base64 string for storing in Firestore
    private String convertImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Compress image to reduce database storage size
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {
            // Return empty string if image conversion fails
            return "";
        }
    }

    // Updates the existing Firestore document with edited report details
    private void updateReport() {
        Map<String, Object> updatedReport = new HashMap<>();

        // Store updated values from the form
        updatedReport.put("name", itemName.getText().toString().trim());
        updatedReport.put("description", itemDescription.getText().toString().trim());
        updatedReport.put("category", itemCategory.getText().toString().trim());
        updatedReport.put("location", itemLocation.getText().toString().trim());
        updatedReport.put("status", itemStatus.getSelectedItem().toString());
        updatedReport.put("imageBase64", imageBase64);

        // Update the selected report document in Firestore
        db.collection("reports")
                .document(documentId)
                .update(updatedReport)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Report updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                });
    }
}