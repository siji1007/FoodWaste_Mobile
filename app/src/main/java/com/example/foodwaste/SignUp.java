package com.example.foodwaste;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.api.IMapController;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    private String street;
    private String municipality;
    private String province;
    private String postal;
    private Double Lat;
    private Double Longitude;

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize MapView and MyLocationNewOverlay
        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableFollowLocation();
        myLocationOverlay.enableMyLocation();
        mapView.getOverlayManager().add(myLocationOverlay);

        // Set the zoom level
        IMapController mapController = mapView.getController();
        mapController.setZoom(14.0);

        // Find and set onClickListener for the "Current Location" button
        Button currentLocButton = findViewById(R.id.CurrentLoc);
        EditText Street = findViewById(R.id.Street);
        EditText Municipality = findViewById(R.id.Baranngay);
        EditText State = findViewById(R.id.Province);
        EditText Postal = findViewById(R.id.Postal);


        currentLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocationOverlay.getMyLocation() != null) {
                    double latitude = myLocationOverlay.getMyLocation().getLatitude();
                    double longitude = myLocationOverlay.getMyLocation().getLongitude();
                    // Display latitude and longitude in a toast message
                    Toast.makeText(SignUp.this, "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_SHORT).show();

                    // Reverse geocoding to get the address components
                    Geocoder geocoder = new Geocoder(SignUp.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            // Set the address components to EditText fields
                            Street.setText(address.getThoroughfare());
                            Municipality.setText(address.getLocality()); // Corrected to set Municipality
                           State.setText(address.getSubAdminArea()); //Province like camarines norte
                            Postal.setText(address.getPostalCode());
                            street = address.getThoroughfare();
                            municipality = address.getLocality();
                            province = address.getSubAdminArea();
                            postal = address.getPostalCode();
                            Longitude = longitude;
                            Lat = latitude;
                        } else {
                            Toast.makeText(SignUp.this, "Address not found for the given coordinates.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SignUp.this, "Error occurred while retrieving address.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUp.this, "Unable to retrieve current location.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void register(View v) {
        EditText usernameView = findViewById(R.id.username);
        EditText fullNameView = findViewById(R.id.fullname);
        EditText emailView = findViewById(R.id.email);
        EditText passwordView = findViewById(R.id.password);

        String username = usernameView.getText().toString().trim();
        String fullName = fullNameView.getText().toString().trim();
        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        Spinner _spinner = findViewById(R.id.spinner);
        String selectedItem = _spinner.getSelectedItem().toString().trim();

        if (fullName.length() == 0 || email.length() == 0 || username.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
        }else if(selectedItem.equals("Select User Type")) {
            Toast.makeText(getApplicationContext(), "Please select a user type", Toast.LENGTH_LONG).show();
        }else if (!email.contains("@gmail.com")) {
                Toast.makeText(getApplicationContext(), "Please use a Gmail address", Toast.LENGTH_LONG).show();
            } else {
            JSONObject registrationForm = new JSONObject();
            try {
                registrationForm.put("subject", "register");
                registrationForm.put("fullname", fullName);
                registrationForm.put("email", email);
                registrationForm.put("username", username);
                registrationForm.put("password", password);
                registrationForm.put("user_type", selectedItem);
                registrationForm.put("strt", street.trim());
                registrationForm.put("muni", municipality.trim());
                registrationForm.put("prov", province.trim());
                registrationForm.put("zipCode", postal.trim());
                registrationForm.put("lati", Lat.toString().trim());
                registrationForm.put("longitude", Longitude.toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create( registrationForm.toString(), MediaType.parse("application/json; charset=utf-8"));

            postRequest(MainActivity.postUrl, body);
        }
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {

                try {
                    final String responseString = response.body().string().trim();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseString.equals("success")) {
                                Toast.makeText(getApplicationContext(), "Registration completed successfully.", Toast.LENGTH_LONG).show();
                                finish();
                            } else if (responseString.equals("username")) {
                                Toast.makeText(getApplicationContext(), "Username already exists. Please chose another username.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}