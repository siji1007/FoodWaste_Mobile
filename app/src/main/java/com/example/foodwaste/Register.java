package com.example.foodwaste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Register extends AppCompatActivity {

    private CardView customerCard, vendorCard, organizationCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        customerCard = findViewById(R.id.customerID);
        vendorCard = findViewById(R.id.vendorID);
        organizationCard = findViewById(R.id.organizationID);

        customerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionDialog("Customer");
            }
        });

        vendorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionDialog("Vendor");
            }
        });

        organizationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionDialog("Organization");
            }
        });
    }

    private void showSelectionDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select " + type);
        builder.setMessage("Do you want to register as " + type + "?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Navigate to MainActivity
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
