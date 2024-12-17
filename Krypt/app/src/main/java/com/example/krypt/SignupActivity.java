package com.example.krypt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    private EditText emailEditText, currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button signUpButton,loginButton;
    private ProgressBar progressBar;
    private DatabaseReference studentsRef, adminsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.emailEditText);
        currentPasswordEditText = findViewById(R.id.passwordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        studentsRef = FirebaseDatabase.getInstance().getReference("students");
        adminsRef = FirebaseDatabase.getInstance().getReference("admins");
        auth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "New passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            checkUserTypeAndAuthenticate(email, currentPassword, newPassword);
        });
    }

    private void checkUserTypeAndAuthenticate(String email, String currentPassword, String newPassword) {
        progressBar.setVisibility(View.VISIBLE);

        adminsRef.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    verifyAndRegister(email, currentPassword, newPassword, "admin");
                } else {
                    // Check if user is in the "students" node
                    studentsRef.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressBar.setVisibility(View.GONE);

                            if (snapshot.exists()) {
                                verifyAndRegister(email, currentPassword, newPassword, "student");
                            } else {
                                Toast.makeText(SignupActivity.this, "No account found with this email.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignupActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignupActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyAndRegister(String email, String currentPassword, String newPassword, String userType) {
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = userType.equals("admin") ? adminsRef : studentsRef;

        ref.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);

                boolean isMatchFound = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String storedPassword = snapshot.child("Password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(currentPassword)) {
                        isMatchFound = true;
                        createUserInAuth(email, newPassword);
                        break;
                    }
                }

                if (!isMatchFound) {
                    Toast.makeText(SignupActivity.this, "Incorrect current password.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignupActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserInAuth(String email, String newPassword) {
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, newPassword)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
