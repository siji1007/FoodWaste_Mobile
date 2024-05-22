package com.example.foodwaste;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MapFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 3;

    private MapView mapView;
    private Button uploadImageButton;
    private MyLocationNewOverlay myLocationOverlay;
    private View rootView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "Unknown");

        TextView roleTextView = rootView.findViewById(R.id.role);
        roleTextView.setText(role + " view");


        // Initialize the MapView
        Context ctx = requireContext().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        mapView = rootView.findViewById(R.id.mapview); // Assign the MapView to the class variable
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true); // Enable built-in zoom controls
        mapView.setMultiTouchControls(true); // Enable multi-touch controls

        // Initialize the MyLocationNewOverlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableFollowLocation();
        myLocationOverlay.enableMyLocation();
        mapView.getOverlayManager().add(myLocationOverlay);

        // Set the zoom level
        IMapController mapController = mapView.getController();
        mapController.setZoom(14.0); // Set the desired zoom level




        // Request permissions if necessary
        requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});

        TextView navigateDescription = rootView.findViewById(R.id.NavigateDescription);
        if ("Customer".equals(role)) {
            Toast.makeText(getContext(), "This is the customer view", Toast.LENGTH_SHORT).show();


            addMarker(14.134910, 122.924079);

            addMarker(14.126303, 122.937971);

            rootView.findViewById(R.id.mapDesign).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.camera).setVisibility(View.GONE);
            rootView.findViewById(R.id.descritionScan).setVisibility(View.GONE);


            navigateDescription.setText("Time to navigate Vendors.");


            //LATITUDE AND LONG OF VENDORS HERE

            fetchAndDisplayData("/vendor-details");

        }else if ("Vendor".equals(role)){
            Toast.makeText(getContext(), "This is the vendor view", Toast.LENGTH_SHORT).show();

            addMarker(14.134910, 122.924079);
            addMarker(14.126303, 122.937971);


            rootView.findViewById(R.id.mapDesign).setVisibility(View.GONE);
            rootView.findViewById(R.id.camera).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.descritionScan).setVisibility(View.VISIBLE);

            navigateDescription.setText("Time to navigate Organizations.");


            LottieAnimationView uploadImage = rootView.findViewById(R.id.camera);
            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestCameraPermission();
                }
            });





            //LATITUDE AND LONG OF ORGANIZATIONS HERE

            fetchAndDisplayData("/organization-details");




        }else if ("Organization".equals(role)){
            Toast.makeText(getContext(), "This is the organization view", Toast.LENGTH_SHORT).show();

            addMarker(14.134910, 122.924079);
            addMarker(14.126303, 122.937971);

            rootView.findViewById(R.id.mapDesign).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.camera).setVisibility(View.GONE);
            rootView.findViewById(R.id.descritionScan).setVisibility(View.GONE);

            navigateDescription.setText("Time to navigate vendors.");


        }else {
            Toast.makeText(getContext(), "No selected role", Toast.LENGTH_SHORT).show();
        }
        //LATITUDE AND LONG OF VENDORS HERE
        fetchAndDisplayData("/vendor-details");

        return rootView;
    }




    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
           openCamera();
        }
    }


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, proceed to get the current location
                // Zoom the map view
                IMapController mapController = mapView.getController();
                mapController.setZoom(16.0); // Set the desired zoom level
            } else {
                // Location permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }






    private void addMarker(double latitude, double longitude) {
        GeoPoint point = new GeoPoint(latitude, longitude);
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setIcon(getResources().getDrawable(R.drawable.pinlocation));
        // Set the anchor to center
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mapView.getOverlays().add(marker);
        mapView.getController().setCenter(point);

    }

    private void fetchAndDisplayData(String endpoint) {
        NetworkUtils.fetchData(endpoint, new NetworkUtils.DataCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                // Handle the JSON response here
                for (int i = 0; i < data.length(); i++) {
                    try {
                        Toast.makeText(requireContext(),"Data from " + endpoint + ": " + data.getJSONObject(i).toString(), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error here
                Log.e(TAG, "Failed to fetch data from " + endpoint, e);
            }
        });
    }






    }


