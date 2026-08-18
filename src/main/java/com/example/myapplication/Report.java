package com.example.myapplication;

// Report model class used to represent lost and found item data
public class Report implements java.io.Serializable {

    // Report details stored in Firebase Firestore
    private String name;
    private String description;
    private String category;
    private String location;
    private String status;
    private String imageBase64;

    // Stores the Firebase user ID of the report owner
    private String userId;

    // Stores the Firestore document ID for edit, delete and update actions
    private String documentId;

    // Required empty constructor for Firebase Firestore object conversion
    public Report() {
    }

    // Returns the item name
    public String getName() {
        return name;
    }

    // Returns the item description
    public String getDescription() {
        return description;
    }

    // Returns the item category
    public String getCategory() {
        return category;
    }

    // Returns the reported location
    public String getLocation() {
        return location;
    }

    // Returns the item status (Lost / Found / Resolved)
    public String getStatus() {
        return status;
    }

    // Returns the stored Base64 image string
    public String getImageBase64() {
        return imageBase64;
    }

    // Returns the Firestore document ID
    public String getDocumentId() {
        return documentId;
    }

    // Sets the Firestore document ID
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Returns the report owner's Firebase user ID
    public String getUserId() {
        return userId;
    }

    // Stores the report owner's Firebase user ID
    public void setUserId(String userId) {
        this.userId = userId;
    }
}