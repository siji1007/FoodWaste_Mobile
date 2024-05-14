package com.example.foodwaste;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class TermsAndCondition extends AppCompatActivity {
    private CheckBox checkBox;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms_and_condition);

        // Initialize views
        checkBox = findViewById(R.id.checkbox);
        confirmButton = findViewById(R.id.confirm);

        // Set initial state of confirm button
        confirmButton.setEnabled(checkBox.isChecked());

        // Set onClickListener for checkbox
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable or disable confirm button based on checkbox state
                confirmButton.setEnabled(checkBox.isChecked());
            }
        });

        // Set onClickListener for confirm button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get IP address
                    String ipAddress = getIPAddress();

                    // Display IP address in a toast
                    Toast.makeText(TermsAndCondition.this, "IP Address: " + ipAddress, Toast.LENGTH_LONG).show();

                    // If confirm button is clicked, go to Register activity
                    Intent intent = new Intent(TermsAndCondition.this, Register.class);
                    startActivity(intent);
                    finish(); // Finish the current activity (TermsAndCondition)
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : addresses) {
                    if (!address.isLoopbackAddress()) {
                        String sAddr = address.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (isIPv4) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "IP Address not available";
    }
}
