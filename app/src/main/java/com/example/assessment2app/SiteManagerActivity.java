package com.example.assessment2app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;

public class SiteManagerActivity extends AppCompatActivity implements OnMapReadyCallback {

    // UI Elements
    private Button setupSiteButton, downloadDonorsButton, inputDonationDataButton, registerVolunteerButton;
    private Button saveSetupButton, saveDonorsButton, saveDonationDataButton, saveVolunteerButton;
    private Button backFromSetupButton, backFromDonorsButton, backFromDonationDataButton, backFromRegisterVolunteerButton;
    private Button backFromAboutUsButton;
    private LinearLayout savedDataLayout, savedRegister; // To display saved data////////////////////////
    private LinearLayout mainMenuLayout, setupSiteLayout, downloadDonorsLayout, inputDonationDataLayout, registerVolunteerLayout;
    private LinearLayout aboutUsLayout, footerLayout;
    private EditText siteAddressEditText, donationHoursEditText, bloodTypesEditText, addressInputDonationData, bloodTypes, amountOfBloodCollected, addressRegisterAsVolunteer, volunteerNameEditText, volunteerBloodTypesEditText;

    // Footer Elements
    private LinearLayout logoutButton, aboutUsButton;

    // Google Map and Location
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    private HashMap<Marker, String> markerAddressMap = new HashMap<>();

    // HashMap to store marker data
    private HashMap<Marker, MarkerData> markerDataMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_manager);

        savedDataLayout = findViewById(R.id.savedDataLayout);


        // Initialize UI Elements
        initializeUI();

        // Initialize Google Maps
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up button listeners
        setupListeners();
    }

    private void initializeUI() {
        setupSiteButton = findViewById(R.id.setupSiteButton);
        downloadDonorsButton = findViewById(R.id.downloadDonorsButton);
        inputDonationDataButton = findViewById(R.id.inputDonationDataButton);
        registerVolunteerButton = findViewById(R.id.registerVolunteerButton);
        saveSetupButton = findViewById(R.id.saveSetupButton);
        saveDonorsButton = findViewById(R.id.saveDonorsButton);
        saveDonationDataButton = findViewById(R.id.saveDonationDataButton);
        saveVolunteerButton = findViewById(R.id.saveVolunteerButton);
        backFromSetupButton = findViewById(R.id.backFromSetupButton);
        backFromDonorsButton = findViewById(R.id.backFromDonorsButton);
        backFromDonationDataButton = findViewById(R.id.backFromDonationDataButton);
        backFromRegisterVolunteerButton = findViewById(R.id.backFromRegisterVolunteerButton);
        backFromAboutUsButton = findViewById(R.id.backFromAboutUsButton);
        mainMenuLayout = findViewById(R.id.mainMenuLayout);
        setupSiteLayout = findViewById(R.id.setupSiteLayout);
        downloadDonorsLayout = findViewById(R.id.downloadDonorsLayout);
        inputDonationDataLayout = findViewById(R.id.inputDonationDataLayout);
        registerVolunteerLayout = findViewById(R.id.registerVolunteerLayout);
        aboutUsLayout = findViewById(R.id.aboutUsLayout);
        footerLayout = findViewById(R.id.footerLayout);
        siteAddressEditText = findViewById(R.id.siteAddressEditText);
        donationHoursEditText = findViewById(R.id.donationHoursEditText);
        bloodTypesEditText = findViewById(R.id.bloodTypesEditText);
        addressInputDonationData = findViewById(R.id.addressInputDonationData);
        bloodTypes = findViewById(R.id.bloodTypes);
        amountOfBloodCollected = findViewById(R.id.amountOfBloodCollected);
        addressRegisterAsVolunteer = findViewById(R.id.addressRegisterAsVolunteer);///////////
        volunteerNameEditText = findViewById(R.id.volunteerNameEditText);////////
        volunteerBloodTypesEditText = findViewById(R.id.volunteerBloodTypesEditText);////////
        logoutButton = findViewById(R.id.logoutButton);
        aboutUsButton = findViewById(R.id.aboutUsButton);
        savedRegister = findViewById(R.id.savedRegister);

    }

    private void setupListeners() {
        setupSiteButton.setOnClickListener(v -> showLayout(setupSiteLayout));
        downloadDonorsButton.setOnClickListener(v -> showLayout(downloadDonorsLayout));
        inputDonationDataButton.setOnClickListener(v -> showLayout(inputDonationDataLayout));
        registerVolunteerButton.setOnClickListener(v -> showLayout(registerVolunteerLayout));
        aboutUsButton.setOnClickListener(v -> {
            showLayout(aboutUsLayout);
            footerLayout.setVisibility(View.GONE);
        });
        backFromAboutUsButton.setOnClickListener(v -> {
            showLayout(mainMenuLayout);
            footerLayout.setVisibility(View.VISIBLE);
        });
        saveSetupButton.setOnClickListener(v -> saveSetupData());
        saveDonorsButton.setOnClickListener(v -> saveDonorsData());
        saveDonationDataButton.setOnClickListener(v -> saveDonationData());
        saveVolunteerButton.setOnClickListener(v -> saveVolunteerData());
        backFromSetupButton.setOnClickListener(v -> resetToMainMenu());
        backFromDonorsButton.setOnClickListener(v -> resetToMainMenu());
        backFromDonationDataButton.setOnClickListener(v -> resetToMainMenu());
        backFromRegisterVolunteerButton.setOnClickListener(v -> resetToMainMenu());
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(SiteManagerActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            // No data should be cleared here as the database is persistent.
            Toast.makeText(SiteManagerActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveDonorsData() {
        // Add logic to save donor data here
        // For example, you could collect data from input fields and save to a database or send to a server
        Toast.makeText(this, "Donors Data Saved!", Toast.LENGTH_SHORT).show();
    }

    private void saveDonationData() {
        // Get input values
        String location = addressInputDonationData.getText().toString().trim();
        String bloodAmount = amountOfBloodCollected.getText().toString().trim();
        String bloodType = bloodTypes.getText().toString().trim();

        // Validate Location
        if (location.isEmpty()) {
            Toast.makeText(this, "Location cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Amount of Blood (must be a number)
        if (!bloodAmount.matches("\\d+")) {
            Toast.makeText(this, "Amount of Blood Collected must be a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Blood Type
        String[] validBloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        boolean isValidType = false;
        for (String type : validBloodTypes) {
            if (bloodType.equalsIgnoreCase(type)) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            Toast.makeText(this, "Invalid Blood Type. Valid types: A+, A-, B+, B-, O+, O-, AB+, AB-.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data and display it dynamically
        String savedData = "Location: " + location + "\nBlood Amount: " + bloodAmount + " ml\nBlood Type: " + bloodType;
        Toast.makeText(this, "Donation Data Saved!", Toast.LENGTH_SHORT).show();

        // Display saved data
        addSavedDataToView(savedData);

    }

    private void addSavedDataToView(String savedData) {
        TextView textView = new TextView(this);
        textView.setText(savedData); // Hiển thị nội dung
        textView.setPadding(16, 16, 16, 16);


        textView.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        savedDataLayout.removeView(textView); // Xoá TextView
                        Toast.makeText(this, "Entry deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        savedDataLayout.addView(textView);
    }



    private void saveVolunteerData() {
        String location = addressRegisterAsVolunteer.getText().toString().trim();
        String volunteerName = volunteerNameEditText.getText().toString().trim();
        String bloodType = volunteerBloodTypesEditText.getText().toString().trim();

        // Validate inputs
        if (location.isEmpty()) {
            Toast.makeText(this, "Location cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (volunteerName.isEmpty()) {
            Toast.makeText(this, "Volunteer Name cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidBloodType(bloodType)) {
            Toast.makeText(this, "Invalid blood type. Use A+, A-, B+, B-, AB+, AB-, O+, O-.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format and display the saved data
        String savedData = "Location: " + location + "\nVolunteer Name: " + volunteerName + "\nBlood Type: " + bloodType;
        addSavedVolunteerDataToView(savedData);
    }

    private void addSavedVolunteerDataToView(String savedData) {
        TextView textView = new TextView(this);
        textView.setText(savedData);
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setPadding(16, 16, 16, 16);

        // Add delete functionality
        textView.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        savedRegister.removeView(textView); // Xoá TextView
                        Toast.makeText(this, "Entry deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        savedRegister.addView(textView);
    }



    private void showLayout(LinearLayout layoutToShow) {
        hideAllLayouts();
        layoutToShow.setVisibility(View.VISIBLE);
    }

    private void hideAllLayouts() {
        mainMenuLayout.setVisibility(View.GONE);
        setupSiteLayout.setVisibility(View.GONE);
        downloadDonorsLayout.setVisibility(View.GONE);
        inputDonationDataLayout.setVisibility(View.GONE);
        registerVolunteerLayout.setVisibility(View.GONE);
        aboutUsLayout.setVisibility(View.GONE);
    }

    private void resetToMainMenu() {
        hideAllLayouts();
        inputDonationDataLayout.setVisibility(LinearLayout.GONE);
        mainMenuLayout.setVisibility(View.VISIBLE);
        footerLayout.setVisibility(View.VISIBLE);
        clearInputFields();
    }

    private void clearInputFields() {
        siteAddressEditText.setText("");
        donationHoursEditText.setText("");
        bloodTypesEditText.setText("");
        addressInputDonationData.setText("");
        amountOfBloodCollected.setText("");
        bloodTypes.setText("");
        volunteerNameEditText.setText("");
        addressRegisterAsVolunteer.setText("");
        volunteerNameEditText.setText("");
        volunteerBloodTypesEditText.setText("");
    }

    private void saveSetupData() {
        String address = siteAddressEditText.getText().toString().trim();
        String hours = donationHoursEditText.getText().toString().trim();
        String bloodTypes = bloodTypesEditText.getText().toString().trim();

        // Validate Donation Hours Start
        if (!hours.equalsIgnoreCase("8AM") && !hours.equalsIgnoreCase("9AM")) {
            Toast.makeText(this, "Please enter a valid start time: 8AM or 9AM", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Blood Types
        if (!isValidBloodType(bloodTypes)) {
            Toast.makeText(this, "Please enter valid blood types: A+, A-, B+, B-, AB+, AB-, O+, O-", Toast.LENGTH_SHORT).show();
            return;
        }

        // Automatically set end time
        String endTime = "4PM";

        Toast.makeText(this, "Setup Data Saved!\nAddress: " + address +
                "\nStart Time: " + hours + "\nEnd Time: " + endTime +
                "\nRequired Blood Types: " + bloodTypes, Toast.LENGTH_SHORT).show();

        // Add marker if Google Map is ready
        if (googleMap != null) {
            LatLng latLng = googleMap.getCameraPosition().target;
            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
            if (marker != null) {
                MarkerData data = new MarkerData(address, hours, endTime, bloodTypes);
                markerDataMap.put(marker, data); // Store marker data
            }
        }
    }


    // Helper method to validate blood types
    private boolean isValidBloodType(String bloodTypes) {
        String[] validBloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        String[] enteredTypes = bloodTypes.split(","); // Allow multiple blood types separated by commas

        for (String type : enteredTypes) {
            boolean isValid = false;
            for (String validType : validBloodTypes) {
                if (type.trim().equalsIgnoreCase(validType)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                return false; // If any type is invalid, return false
            }
        }
        return true; // All types are valid
    }

    private void showSetupSiteLayout(Marker marker, MarkerData data) {
        // Display layout Set up donation site
        showLayout(setupSiteLayout);


        siteAddressEditText.setText(data.address);
        donationHoursEditText.setText(data.openingHours);
        bloodTypesEditText.setText(data.bloodTypes);

        // Update when clicking SAVE button
        saveSetupButton.setOnClickListener(v -> {
            String newOpeningHours = donationHoursEditText.getText().toString().trim();
            String newBloodTypes = bloodTypesEditText.getText().toString().trim();

            // Check if it is valid
            if (!newOpeningHours.equalsIgnoreCase("8AM") && !newOpeningHours.equalsIgnoreCase("9AM")) {
                Toast.makeText(this, "Invalid opening hours. Use 8AM or 9AM.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidBloodType(newBloodTypes)) {
                Toast.makeText(this, "Invalid blood types. Use valid types like A+, O-, etc.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update marker
            data.openingHours = newOpeningHours;
            data.bloodTypes = newBloodTypes;
            markerDataMap.put(marker, data); // Lưu lại thông tin mới
            Toast.makeText(this, "Marker updated successfully.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true); // Toolbar with directions
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        requestLocationPermission();

        googleMap.setOnMapClickListener(latLng -> {
            String address = getAddressFromLatLng(latLng);
            String openingHours = donationHoursEditText.getText().toString().trim();
            String closingHours = "4PM"; // Default closing time
            String bloodTypes = bloodTypesEditText.getText().toString().trim();

            // Validate user input
            boolean isValidOpeningHours = openingHours.equalsIgnoreCase("8AM") || openingHours.equalsIgnoreCase("9AM");
            boolean isValidBloodTypes = isValidBloodType(bloodTypes);

            if (!isValidOpeningHours) {
                Toast.makeText(this, "Invalid opening hours. Please enter 8AM or 9AM.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidBloodTypes) {
                Toast.makeText(this, "Invalid blood types. Please enter valid types like A+, O-, etc.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (address != null) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
                if (marker != null) {
                    MarkerData data = new MarkerData(address, openingHours, closingHours, bloodTypes);
                    markerDataMap.put(marker, data); // Store data for this marker
                    siteAddressEditText.setText(address);
                }
            } else {
                Toast.makeText(this, "Unable to fetch address", Toast.LENGTH_SHORT).show();
            }
        });


        googleMap.setOnMarkerClickListener(marker -> {
            MarkerData data = markerDataMap.get(marker);
            if (data != null) {
                // Show marker details in a dialog
                new AlertDialog.Builder(this)
                        .setTitle("Marker Information")
                        .setMessage("Address: " + data.address + "\n" +
                                "Opening Hours: " + data.openingHours + "\n" +
                                "Closing Hours: " + data.closingHours + "\n" +
                                "Required Blood Types: " + data.bloodTypes)
                        .setPositiveButton("Find Route", (dialog, which) -> {
                            // Find Route Functionality
                            LatLng markerLatLng = marker.getPosition();
                            String uri = "http://maps.google.com/maps?daddr=" + markerLatLng.latitude + "," + markerLatLng.longitude;
                            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("Delete", (dialog, which) -> {
                            new AlertDialog.Builder(this)
                                    .setTitle("Delete Marker")
                                    .setMessage("Are you sure you want to delete this marker?")
                                    .setPositiveButton("Yes", (deleteDialog, deleteWhich) -> {
                                        marker.remove();
                                        markerDataMap.remove(marker); // Remove marker data
                                        siteAddressEditText.setText("");
                                        Toast.makeText(this, "Marker deleted.", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("No", (deleteDialog, deleteWhich) -> deleteDialog.dismiss())
                                    .create()
                                    .show();
                        })
                        .setNegativeButton("Edit", (dialog, which) -> {
                            // Edit button
                            showSetupSiteLayout(marker, data);
                        })

                        .create()
                        .show();
            }
            return true;
        });


    }


    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            enableUserLocation();
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                googleMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("You are here"));
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
