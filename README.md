# Mobile-and-Wearable-Application-Development

# UniFind – Lost and Found Mobile App

UniFind is an Android mobile application designed to help university students report, find and manage lost and found items.

## Technologies Used

- Android Studio
- Java
- XML
- Firebase Authentication
- Firebase Cloud Firestore

## Main Features

- User registration and login
- Report lost or found items
- Add item images
- View shared lost and found reports
- Filter reports by status and category
- View detailed item information
- Edit and delete reports
- Mark items as resolved
- User profile and email verification status
- Automatic possible-match suggestions

## Unique Feature

The application includes a **possible match suggestion feature**. When a user reports a lost item, the application checks Firebase Firestore for found items with the same category and displays possible matches.

This helps users find their belongings more quickly without manually searching through every report.

## Firebase

Firebase Authentication is used to manage user accounts and login.

Cloud Firestore is used to store and retrieve lost and found reports, including:

- Item name
- Description
- Category
- Location
- Status
- Image data
- User ID

## Project Structure

- `app/src/main/java/` – Java application code
- `app/src/main/res/layout/` – XML screen layouts
- `app/src/main/res/drawable/` – Images and icons
- `app/src/main/res/values/` – Colours and strings
- `AndroidManifest.xml` – Application configuration

## Development

Developed as an Android application prototype using Android Studio, Java, XML and Firebase.
