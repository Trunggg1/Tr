package com.example.assessment2app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class SuperUserActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView recyclerViewSites;
    private EditText inputReportCriteria;
    private Button generateReportButton, backFromAboutUsButton;
    private LinearLayout aboutUsLayout, footerLayout, logoutButton, aboutUsButton;

    private GoogleMap googleMap;
    private List<String> donationSites; // Dummy list for donation sites

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user); // Use your XML file name here

        // Initialize UI components
        initializeUI();

        // Populate donation sites
        populateDonationSites();

        // Setup listeners
        setupListeners();

        // Setup Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initializeUI() {
        recyclerViewSites = findViewById(R.id.recyclerViewSites);
        inputReportCriteria = findViewById(R.id.inputReportCriteria);
        generateReportButton = findViewById(R.id.generateReportButton);
        aboutUsLayout = findViewById(R.id.aboutUsLayout);
        footerLayout = findViewById(R.id.footerLayout);
        logoutButton = findViewById(R.id.logoutButton);
        aboutUsButton = findViewById(R.id.aboutUsButton);
    }

    private void populateDonationSites() {
        donationSites = new ArrayList<>();
        // Add some dummy data
        donationSites.add("Site 1 - Blood Type: A+ - 5L");
        donationSites.add("Site 2 - Blood Type: O+ - 8L");
        donationSites.add("Site 3 - Blood Type: AB- - 3L");

        // Set up RecyclerView
        recyclerViewSites.setLayoutManager(new LinearLayoutManager(this));
        SiteAdapter adapter = new SiteAdapter(donationSites);
        recyclerViewSites.setAdapter(adapter);
    }

    private void setupListeners() {
        generateReportButton.setOnClickListener(v -> {
            String criteria = inputReportCriteria.getText().toString().trim();
            if (criteria.isEmpty()) {
                Toast.makeText(SuperUserActivity.this, "Please enter criteria!", Toast.LENGTH_SHORT).show();
            } else {
                // Generate a dummy report
                Toast.makeText(SuperUserActivity.this, "Generated report for: " + criteria, Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(SuperUserActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(SuperUserActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Add dummy marker
        LatLng sampleLocation = new LatLng(10.762622, 106.660172);
        googleMap.addMarker(new MarkerOptions().position(sampleLocation).title("Sample Donation Site"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sampleLocation, 15));
    }

    private static class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteViewHolder> {
        private final List<String> sites;

        public SiteAdapter(List<String> sites) {
            this.sites = sites;
        }

        @NonNull
        @Override
        public SiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Use android.R.layout.simple_list_item_1 for a simple text item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new SiteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SiteViewHolder holder, int position) {
            // Set text for each item
            holder.textView.setText(sites.get(position));
        }

        @Override
        public int getItemCount() {
            return sites.size();
        }

        // ViewHolder for the site items
        static class SiteViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public SiteViewHolder(@NonNull View itemView) {
                super(itemView);
                // Use android.R.id.text1 for simple_list_item_1
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }

}
