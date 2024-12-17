package com.example.krypt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DepartmentActivity extends bottomNavigation {

    private DatabaseReference databaseReference;
    private ListView reportsListView;
    private ArrayList<Post> posts;
    private ArrayList<String> postKeys;
    private PostAdapter postAdapter;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Spinner departmentSpinner;

    private FirebaseAuth auth;
    private String department;

    Button addReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_department, findViewById(R.id.content_frame));
        auth = FirebaseAuth.getInstance();

        // Views initialization
        drawerLayout = findViewById(R.id.drawerLayout2);
        navigationView = findViewById(R.id.navigationView2);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        View headerView = navigationView.getHeaderView(0);
        departmentSpinner = headerView.findViewById(R.id.departmentSpinner);
        addReportButton = findViewById(R.id.addReportButton);

        setSupportActionBar(toolbar);

        department = getIntent().getStringExtra("department");
        if (department == null || department.isEmpty()) {
            department = "Dashboard";
        }
        setTitle(department);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setUpSpinner();
        setupNavigation();

        reportsListView = findViewById(R.id.reportsListView);
        posts = new ArrayList<>();
        postKeys = new ArrayList<>();
        postAdapter = new PostAdapter(this, posts, postKeys, department);
        reportsListView.setAdapter(postAdapter);

        loadReports();

        addReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(DepartmentActivity.this, UploadActivity.class);
            intent.putExtra("department", department);
            startActivity(intent);
        });
    }

    private void loadReports() {
        if (department == null || department.equals("Select Dept.")) {
            posts.clear();
            postKeys.clear();
            postAdapter.notifyDataSetChanged();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("reports")
                .child(department);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                postKeys.clear();
                for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                    Post post = reportSnapshot.getValue(Post.class);
                    String key = reportSnapshot.getKey();
                    if (post != null && key != null) {
                        posts.add(post);
                        postKeys.add(key);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(DepartmentActivity.this, "Failed to load reports: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpSpinner() {
        String[] departments = {
                "Select Dept.", "ARCH", "BME", "BECM", "CHEM", "CSE", "CE", "EEE", "ECE", "ESE",
                "HUM", "IEM", "MATH", "ME", "PHY", "TE", "URP", "LE"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(spinnerAdapter);

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }
                String selectedDepartment = parentView.getItemAtPosition(position).toString();
                if (!selectedDepartment.equals(department)) {
                    Intent intent = new Intent(DepartmentActivity.this, DepartmentActivity.class);
                    intent.putExtra("department", selectedDepartment);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                startActivity(new Intent(DepartmentActivity.this, ProfileActivity.class));
            } else if (id == R.id.menu_home) {
                Intent homeIntent = new Intent(DepartmentActivity.this, DashboardActivity.class);
                startActivity(homeIntent);
                finish(); // End the current activity
            } else if (id == R.id.menu_logout) {
                logout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void logout() {
        auth.signOut();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(DepartmentActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}