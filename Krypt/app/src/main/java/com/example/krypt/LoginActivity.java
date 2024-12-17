package com.example.krypt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private FirebaseAuth mAuth;

    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signUpButton);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Save login state to SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();

                                    // Check admin status and save it to UserSession
                                    checkAdminStatus(user.getEmail());
                                    //loadUserProfile(user.getEmail());
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                Log.e("FirebaseAuth", "Error: " + task.getException());
                            }
                        });
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    private void checkAdminStatus(String userEmail) {
        if (userEmail == null) {
            Toast.makeText(this, "Failed to retrieve user email", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("admins");
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = false;

                for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                    String adminEmail = adminSnapshot.child("Email").getValue(String.class);
                    if (userEmail.equals(adminEmail)) {
                        isAdmin = true;
                        break;
                    }
                }

                UserSession.getInstance(LoginActivity.this).setAdmin(isAdmin, LoginActivity.this);
                Log.d("LoginActivity", "Admin status set: " + isAdmin);

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error checking admin status", Toast.LENGTH_SHORT).show();
            }
        });
    }/*
    public void loadUserProfile(String email) {
        DatabaseReference databaseReference;
        boolean isAdmin = UserSession.getInstance(getApplication()).isAdmin();

        if (isAdmin) {
            databaseReference = FirebaseDatabase.getInstance().getReference("admins");
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("students");
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userEmail = userSnapshot.child("Email").getValue(String.class);
                    if (email.equalsIgnoreCase(userEmail)) {
                        String name = userSnapshot.child("Name").getValue(String.class);
                        String contact = userSnapshot.child("Contact").getValue(String.class);

                        if (isAdmin) {
                            UserProfile userProfile = new UserProfile(name, userEmail, contact, isAdmin);
                            userProfileLiveData.setValue(userProfile);
                        } else {
                            String roll = userSnapshot.child("Roll").getValue(String.class);
                            String department = userSnapshot.child("Department").getValue(String.class);
                            UserProfile userProfile = new UserProfile(name, userEmail, roll, department, isAdmin);
                            userProfileLiveData.setValue(userProfile);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userProfileLiveData.setValue(null);
            }
        });
    }*/
}