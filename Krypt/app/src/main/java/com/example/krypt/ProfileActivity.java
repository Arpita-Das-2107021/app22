package com.example.krypt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends bottomNavigation {

    private TextView nameTextView, rollTextView, departmentTextView, emailTextView,nameView,RollView,DepartmentView,emailView;
    private Button updatePasswordButton, locationShareButton, chatButton;
    private ProgressBar progressBar;
    private ImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Spinner departmentSpinner;

    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Profile");

        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.content_frame));

        nameTextView = findViewById(R.id.nameTextView);
        rollTextView = findViewById(R.id.rollTextView);
        departmentTextView = findViewById(R.id.departmentTextView);
        emailTextView = findViewById(R.id.emailTextView);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        progressBar = findViewById(R.id.progressBar);
        profileImage = findViewById(R.id.profileImageView);
        progressBar.setVisibility(View.GONE);
        nameView=findViewById(R.id.name);
        RollView=findViewById(R.id.roll);
        DepartmentView=findViewById(R.id.department);
        emailView=findViewById(R.id.email);

        nameView.setVisibility(View.GONE);
        RollView.setVisibility(View.GONE);
        DepartmentView.setVisibility(View.GONE);
        emailView.setVisibility(View.GONE);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);
        departmentSpinner = headerView.findViewById(R.id.departmentSpinner);

        Toolbar toolbar = findViewById(R.id.toolbarprofile);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        profileViewModel.getUserProfile().observe(this, userProfile -> {
            if (userProfile != null) {
                nameTextView.setText(userProfile.getName());
                nameTextView.setTextColor(ContextCompat.getColor(this, R.color.darkGreen));
                emailTextView.setText(userProfile.getEmail());
                emailTextView.setTextColor(ContextCompat.getColor(this, R.color.darkGreen));
                if (userProfile.isAdmin()) {
                    nameView.setVisibility(View.VISIBLE);
                    RollView.setText("Email:  ");
                    DepartmentView.setText("Contact:  ");
                    RollView.setVisibility(View.VISIBLE);
                    DepartmentView.setVisibility(View.VISIBLE);
                    emailView.setVisibility(View.GONE);
                    rollTextView.setText(userProfile.getEmail());
                    departmentTextView.setText(userProfile.getContact());
                    emailTextView.setVisibility(View.GONE);
                    //rollTextView.setVisibility(View.GONE);
                    //departmentTextView.setVisibility(View.GONE);
                    profileImage.setImageResource(R.drawable.img_5);
                } else {
                    nameView.setVisibility(View.VISIBLE);
                    RollView.setVisibility(View.VISIBLE);
                    DepartmentView.setVisibility(View.VISIBLE);
                    emailView.setVisibility(View.VISIBLE);
                    rollTextView.setText(userProfile.getRoll());
                    rollTextView.setTextColor(ContextCompat.getColor(this, R.color.darkGreen));
                    departmentTextView.setText(userProfile.getDepartment());
                    departmentTextView.setTextColor(ContextCompat.getColor(this, R.color.darkGreen));
                }
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        profileViewModel.loadUserProfile(currentUser.getEmail());


        setUpSpinner();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_profile) {
                Toast.makeText(this, "You are already on Profile.", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_home) {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                finish();
            } else if (id == R.id.menu_logout) {
                logout();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        updatePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
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
            private boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }
                String selectedDepartment = parentView.getItemAtPosition(position).toString();
                if (!selectedDepartment.equals("Select Dept.")) {
                    Intent departmentIntent = new Intent(ProfileActivity.this, DepartmentActivity.class);
                    departmentIntent.putExtra("department", selectedDepartment);
                    startActivity(departmentIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private void logout() {
        mAuth.signOut();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}