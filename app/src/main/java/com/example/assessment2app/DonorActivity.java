package com.example.assessment2app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class DonorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 101;

    private DatabaseDonorHelper databaseDonorHelper;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private EditText searchDonationSite, filterHours, inputName;
    private Spinner bloodTypeSpinner;
    private RecyclerView recyclerViewDonors;
    private Button openRegisterForm, cancelButton, saveButton, backFromAboutUsButton;
    private LinearLayout registrationFormLayout, aboutUsLayout, footerLayout, logoutButton, aboutUsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        databaseDonorHelper = new DatabaseDonorHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initializeUI();
        setupSpinner();
        populateDonorsList();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupListeners();
    }

    private void initializeUI() {
        searchDonationSite = findViewById(R.id.searchDonationSite);
        filterHours = findViewById(R.id.filterHours);
        recyclerViewDonors = findViewById(R.id.recyclerViewDonors);
        openRegisterForm = findViewById(R.id.openRegisterForm);
        registrationFormLayout = findViewById(R.id.registrationFormLayout);
        inputName = findViewById(R.id.inputName);
        bloodTypeSpinner = findViewById(R.id.bloodTypeSpinner);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        aboutUsLayout = findViewById(R.id.aboutUsLayout);
        footerLayout = findViewById(R.id.footerLayout);
        logoutButton = findViewById(R.id.logoutButton);
        aboutUsButton = findViewById(R.id.aboutUsButton);
        backFromAboutUsButton = findViewById(R.id.backFromAboutUsButton);
    }

    private void setupListeners() {
        openRegisterForm.setOnClickListener(v -> {
            registrationFormLayout.setVisibility(View.VISIBLE);
            openRegisterForm.setVisibility(View.GONE);
        });

        cancelButton.setOnClickListener(v -> {
            registrationFormLayout.setVisibility(View.GONE);
            openRegisterForm.setVisibility(View.VISIBLE);
        });

        saveButton.setOnClickListener(v -> saveRegistration());

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(DonorActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            // No data should be cleared here as the database is persistent.
            Toast.makeText(DonorActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        });

        aboutUsButton.setOnClickListener(v -> {
            aboutUsLayout.setVisibility(View.VISIBLE);
            footerLayout.setVisibility(View.GONE);
        });

        backFromAboutUsButton.setOnClickListener(v -> {
            aboutUsLayout.setVisibility(View.GONE);
            footerLayout.setVisibility(View.VISIBLE);
        });
    }

    private void saveRegistration() {
        String name = inputName.getText().toString().trim();
        String bloodType = bloodTypeSpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bloodType.isEmpty()) {
            Toast.makeText(this, "Please select a blood type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseDonorHelper.addDonor(name, bloodType)) {
            Toast.makeText(this, "Donor registered successfully!", Toast.LENGTH_SHORT).show();
            populateDonorsList();
        } else {
            Toast.makeText(this, "Failed to register donor!", Toast.LENGTH_SHORT).show();
        }

        registrationFormLayout.setVisibility(View.GONE);
        openRegisterForm.setVisibility(View.VISIBLE);
    }

    private void setupSpinner() {
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bloodTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(adapter);
    }

    private void populateDonorsList() {
        List<String> donors = databaseDonorHelper.getAllDonors();
        recyclerViewDonors.setLayoutManager(new LinearLayoutManager(this));
        DonorAdapter adapter = new DonorAdapter(donors, databaseDonorHelper, this);
        recyclerViewDonors.setAdapter(adapter);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        enableUserLocation();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your Location"));
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission is required to show your location on the map", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorViewHolder> {

        private final List<String> donors;
        private final DatabaseDonorHelper databaseDonorHelper;
        private final DonorActivity activity;

        public DonorAdapter(List<String> donors, DatabaseDonorHelper databaseDonorHelper, DonorActivity activity) {
            this.donors = donors;
            this.databaseDonorHelper = databaseDonorHelper;
            this.activity = activity;
        }

        @NonNull
        @Override
        public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new DonorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
            String donor = donors.get(position);
            holder.textView.setText(donor);

            // Add click listener for deletion with confirmation
            holder.itemView.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this donor?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            String donorName = donor.split(" - ")[0]; // Extract the donor name
                            if (databaseDonorHelper.removeDonor(donorName)) { // Remove donor from database
                                donors.remove(position); // Remove donor from the list
                                notifyItemRemoved(position); // Notify RecyclerView about the change
                                Toast.makeText(v.getContext(), "Donor removed: " + donor, Toast.LENGTH_SHORT).show();
                                activity.populateDonorsList(); // Refresh the donor list in the activity
                            } else {
                                Toast.makeText(v.getContext(), "Failed to remove donor.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss the dialog on "No"
                        .create()
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return donors.size();
        }

        static class DonorViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public DonorViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
