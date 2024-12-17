package com.example.krypt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatrolActivity extends AppCompatActivity {

    private Button startPatrolButton, stopPatrolButton;
    private TextView currentLocationText;
    private FusedLocationProviderClient fusedLocationClient;

    private boolean isPatrolActive = false;
    private DatabaseReference patrolRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol);

        startPatrolButton = findViewById(R.id.startPatrolButton);
        stopPatrolButton = findViewById(R.id.stopPatrolButton);
        currentLocationText = findViewById(R.id.currentLocationText);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        patrolRef = FirebaseDatabase.getInstance().getReference("PatrolRoutes");

        startPatrolButton.setOnClickListener(v -> startPatrol());
        stopPatrolButton.setOnClickListener(v -> stopPatrol());
    }

    private void startPatrol() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        isPatrolActive = true;
        stopPatrolButton.setEnabled(true);
        startPatrolButton.setEnabled(false);
        Toast.makeText(this, "Patrol started!", Toast.LENGTH_SHORT).show();

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                logPatrolLocation(location);
            }
        });
    }

    private void stopPatrol() {
        isPatrolActive = false;
        stopPatrolButton.setEnabled(false);
        startPatrolButton.setEnabled(true);
        Toast.makeText(this, "Patrol stopped!", Toast.LENGTH_SHORT).show();
    }

    private void logPatrolLocation(Location location) {
        if (!isPatrolActive) return;

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String locationData = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude() + " at " + timestamp;

        currentLocationText.setText("Current Location: " + locationData);
        patrolRef.push().setValue(locationData);

        Toast.makeText(this, "Location logged: " + locationData, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPatrol();
            } else {
                Toast.makeText(this, "Permission denied. Cannot start patrol.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
