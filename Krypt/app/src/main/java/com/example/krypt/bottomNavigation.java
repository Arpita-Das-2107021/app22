package com.example.krypt;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottomNavigation extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_navigation_bar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        switch (getClass().getSimpleName()) {
            case "DashboardActivity":
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
                break;
            case "ProfileActivity":
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                break;
            case "SOSActivity":
                bottomNavigationView.setSelectedItemId(R.id.nav_sos);
                break;
            case "AdminActivity":
                bottomNavigationView.setSelectedItemId(R.id.nav_admins);
                break;
            case "MapActivity":
                bottomNavigationView.setSelectedItemId(R.id.nav_share_location);
                break;
        }

        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class<?> targetActivity = null;

        if (item.getItemId() == R.id.nav_home) {
            targetActivity = DashboardActivity.class;
        } else if (item.getItemId() == R.id.nav_profile) {
            targetActivity = ProfileActivity.class;
        } else if (item.getItemId() == R.id.nav_sos) {
            targetActivity = SOSActivity.class;
        } else if (item.getItemId() == R.id.nav_admins) {
            targetActivity = AdminActivity.class;
        } else if (item.getItemId() == R.id.nav_share_location) {
            targetActivity = MapActivity.class;
        } else {
            return false;
        }

        if (!getClass().equals(targetActivity)) {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        return true;
    }
}