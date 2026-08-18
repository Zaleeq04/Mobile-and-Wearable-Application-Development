package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class ItemDetailActivity extends AppCompatActivity {

    // UI components used to display selected report details
    TextView name, description, category, location, status;
    ImageView detailImage;
    Button backBtn, editBtn, resolvedBtn, deleteBtn;

    // Firestore database and selected report document ID
    FirebaseFirestore db;
    String documentId;

    // Firebase Authentication used to check current logged-in user
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Initialise Firebase services
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Connect Java variables to XML buttons
        backBtn = findViewById(R.id.backBtn);
        editBtn = findViewById(R.id.editBtn);
        resolvedBtn = findViewById(R.id.resolvedBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        // Connect Java variables to XML detail views
        detailImage = findViewById(R.id.detailImage);
        name = findViewById(R.id.detailName);
        description = findViewById(R.id.detailDescription);
        category = findViewById(R.id.detailCategory);
        location = findViewById(R.id.detailLocation);
        status = findViewById(R.id.detailStatus);

        // Receive selected report data from ReportAdapter
        String itemName = getIntent().getStringExtra("name");
        String itemDescription = getIntent().getStringExtra("description");
        String itemCategory = getIntent().getStringExtra("category");
        String itemLocation = getIntent().getStringExtra("location");
        String itemStatus = getIntent().getStringExtra("status");
        String imageBase64 = getIntent().getStringExtra("imageBase64");

        // Firestore document ID is required for edit, delete and resolve actions
        documentId = getIntent().getStringExtra("documentId");

        // Compare report owner with current logged-in user
        String ownerUserId = getIntent().getStringExtra("userId");
        String currentUserId = auth.getCurrentUser().getUid();

        // Hide edit/delete/resolve buttons from users who do not own the report
        if (!currentUserId.equals(ownerUserId)) {
            editBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            resolvedBtn.setVisibility(View.GONE);
        }

        // Display report details on screen
        name.setText(itemName);
        description.setText("Description: " + itemDescription);
        category.setText("Category: " + itemCategory);
        location.setText("Location: " + itemLocation);
        status.setText("Status: " + itemStatus);

        // Decode Base64 image string and display it in ImageView
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            detailImage.setImageBitmap(bitmap);
        }

        // Return to previous screen
        backBtn.setOnClickListener(v -> finish());

        // Open edit screen and pass current report data
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailActivity.this, EditReportActivity.class);
            intent.putExtra("documentId", documentId);
            intent.putExtra("name", itemName);
            intent.putExtra("description", itemDescription);
            intent.putExtra("category", itemCategory);
            intent.putExtra("location", itemLocation);
            intent.putExtra("status", itemStatus);
            intent.putExtra("imageBase64", imageBase64);
            startActivity(intent);
        });

        // Mark report as resolved by updating its Firestore status field
        resolvedBtn.setOnClickListener(v -> {
            if (documentId == null) {
                Toast.makeText(this, "Report ID missing", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("reports")
                    .document(documentId)
                    .update("status", "Resolved")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Report marked as resolved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update report", Toast.LENGTH_SHORT).show()
                    );
        });

        // Delete selected report from Firestore
        deleteBtn.setOnClickListener(v -> {
            if (documentId == null) {
                Toast.makeText(this, "Report ID missing", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("reports")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Report deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to delete report", Toast.LENGTH_SHORT).show()
                    );
        });
    }
}