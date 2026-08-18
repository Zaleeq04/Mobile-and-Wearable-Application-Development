package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// RecyclerView adapter used to display reports in the main feed and match suggestion screen
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    // Stores report data and application context
    ArrayList<Report> reportList;
    Context context;

    // Constructor receives report data from activity
    public ReportAdapter(Context context, ArrayList<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the item_report XML layout for each RecyclerView card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);

        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        // Get report at current position
        Report report = reportList.get(position);

        // Display report details on the card
        holder.name.setText(report.getName());
        holder.description.setText(report.getDescription());
        holder.category.setText("Category: " + report.getCategory());
        holder.location.setText("Location: " + report.getLocation());
        holder.status.setText("Status: " + report.getStatus());

        // Decode Base64 image string and display report image
        if (report.getImageBase64() != null && !report.getImageBase64().isEmpty()) {
            byte[] decodedBytes = Base64.decode(report.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.reportImage.setImageBitmap(bitmap);

        } else {
            // Clear image if no image exists
            holder.reportImage.setImageDrawable(null);
        }

        // Open detailed report screen when user clicks a report card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);

            // Pass selected report data to ItemDetailActivity
            intent.putExtra("name", report.getName());
            intent.putExtra("description", report.getDescription());
            intent.putExtra("category", report.getCategory());
            intent.putExtra("location", report.getLocation());
            intent.putExtra("status", report.getStatus());
            intent.putExtra("imageBase64", report.getImageBase64());
            intent.putExtra("documentId", report.getDocumentId());
            intent.putExtra("userId", report.getUserId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        // Returns total number of reports in the list
        return reportList.size();
    }

    // ViewHolder stores references to each report card UI component
    public static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView name, description, category, location, status;
        ImageView reportImage;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            // Connect Java variables to item_report XML views
            reportImage = itemView.findViewById(R.id.reportImage);
            name = itemView.findViewById(R.id.itemNameText);
            description = itemView.findViewById(R.id.itemDescriptionText);
            category = itemView.findViewById(R.id.itemCategoryText);
            location = itemView.findViewById(R.id.itemLocationText);
            status = itemView.findViewById(R.id.itemStatusText);
        }
    }
}