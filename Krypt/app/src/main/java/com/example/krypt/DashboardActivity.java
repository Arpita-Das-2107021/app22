package com.example.krypt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends bottomNavigation {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Spinner departmentSpinner;
    private Button addNoticeButton;
    private RecyclerView noticesRecyclerView;
    private NoticesAdapter noticesAdapter;
    private List<Notice> noticeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_dashboard, findViewById(R.id.content_frame));

        noticeList = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        View headerView = navigationView.getHeaderView(0);
        departmentSpinner = headerView.findViewById(R.id.departmentSpinner);
        addNoticeButton = findViewById(R.id.addNotice);
        noticesRecyclerView = findViewById(R.id.noticesRecyclerView);

        addNoticeButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddNoticeActivity.class);
            startActivity(intent);
        });

        addNoticeButton.setVisibility(View.GONE);

        if (UserSession.getInstance(this).isAdmin()) {
            Log.d("DashboardActivity", "User is an admin");
            addNoticeButton.setVisibility(View.VISIBLE);
        } else {
            Log.d("DashboardActivity", "User is not an admin");
        }


        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setUpSpinner();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            } else if (id == R.id.menu_home) {
                Toast.makeText(this, "You are already on Home", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_logout) {
                logout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        noticesAdapter = new NoticesAdapter(noticeList, this); // Pass activity context
        noticesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticesRecyclerView.setAdapter(noticesAdapter);

        loadNotices();
    }

    private void loadNotices() {
        DatabaseReference noticesRef = FirebaseDatabase.getInstance().getReference("notices");
        noticesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noticeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notice notice = snapshot.getValue(Notice.class);
                    if (notice != null) {
                        noticeList.add(notice);
                    }
                }
                noticesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this, "Failed to load notices: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void setUpSpinner() {
        String[] departments = {
                "Select Dept.",
                "ARCH", "BME", "BECM", "CHEM", "CSE", "CE", "EEE", "ECE",
                "ESE", "HUM", "IEM", "MATH", "ME", "PHY", "TE", "URP", "LE"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(spinnerAdapter);

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstSelection = true; // Track if it's the first selection

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false; // Skip the first spinner selection
                    return;
                }
                String selectedDepartment = parentView.getItemAtPosition(position).toString();
                if (!selectedDepartment.equals("Select Dept.")) {
                    Intent departmentIntent = new Intent(DashboardActivity.this, DepartmentActivity.class);
                    departmentIntent.putExtra("department", selectedDepartment);
                    startActivity(departmentIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
}