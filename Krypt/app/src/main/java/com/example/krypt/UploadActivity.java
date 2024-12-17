package com.example.krypt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";

    private EditText titleEditText, descriptionEditText;
    private ImageButton uploadImageButton, uploadVideoButton, uploadPdfButton;
    private Button postButton;
    private ProgressBar progressBar;

    private List<Uri> mediaUris;
    private List<String> mediaUrls;
    private List<String> mediaTypes;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference postsDatabaseRef;
    private StorageReference storageRef;

    private String department;
    private boolean isAdmin;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // UI References
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadVideoButton = findViewById(R.id.uploadVideoButton);
        uploadPdfButton = findViewById(R.id.uploadPdfButton);
        postButton = findViewById(R.id.postButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        department = getIntent().getStringExtra("department");
        if (department == null || department.isEmpty()) {
            Toast.makeText(this, "Invalid department", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        postsDatabaseRef = FirebaseDatabase.getInstance()
                .getReference("reports")
                .child(department);

        mediaUris = new ArrayList<>();
        mediaUrls = new ArrayList<>();
        mediaTypes = new ArrayList<>();

        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in to upload files.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        isAdmin = UserSession.getInstance(this).isAdmin();

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        mediaUris.add(fileUri);
                        Toast.makeText(this, "File Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        uploadImageButton.setOnClickListener(v -> selectFile("image/*", "image"));
        uploadVideoButton.setOnClickListener(v -> selectFile("video/*", "video"));
        uploadPdfButton.setOnClickListener(v -> selectFile("application/pdf", "pdf"));

        postButton.setOnClickListener(v -> uploadPost());
    }

    private void selectFile(String mimeType, String mediaType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        filePickerLauncher.launch(intent);
        mediaTypes.add(mediaType); // Save media type
    }

    private void uploadPost() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in the title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mediaUrls.clear();

        if (mediaUris.isEmpty()) {
            savePostToDatabase(title, description);
        } else {
            uploadMediaFiles(title, description);
        }
    }

    private void uploadMediaFiles(String title, String description) {
        for (int i = 0; i < mediaUris.size(); i++) {
            Uri fileUri = mediaUris.get(i);
            String uniqueFileName = currentUser.getUid() + "_" + System.currentTimeMillis() + "_" + i;
            StorageReference fileRef = storageRef.child(uniqueFileName);

            fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    mediaUrls.add(uri.toString());
                    Log.d(TAG, "File uploaded: " + uri);

                    if (mediaUrls.size() == mediaUris.size()) {
                        savePostToDatabase(title, description);
                    }
                });
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "File upload failed", e);
            });
        }
    }
    // Define the callback interface
    public interface NameCallback {
        void onNameReceived(String name);
    }

    private void savePostToDatabase(String title, String description) {
        String postId = postsDatabaseRef.push().getKey();
        if (postId == null) {
            Toast.makeText(this, "Failed to generate post ID", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        long timestamp = System.currentTimeMillis();
        String email = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        if (email != null) {
            getName(email, new NameCallback() {
                @Override
                public void onNameReceived(String name) {
                    String userName = (name != null) ? name : "Unknown"; // Fallback to "Unknown" if no name found
                    Log.d("NameCallback", "Name retrieved: " + userName);

                    // Now that we have the username, create the post
                    createPost(postId, title, description, userName, timestamp);
                }
            });
        } else {
            Log.e("FirebaseAuth", "User not logged in or email is null");
        }
    }

    public void getName(String email, final NameCallback callback) {
        DatabaseReference databaseReference;

        // Check if the user is an admin
        boolean isAdmin = UserSession.getInstance(getApplication()).isAdmin();

        if (isAdmin) {
            databaseReference = FirebaseDatabase.getInstance().getReference("admins");
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("students");
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = null;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userEmail = userSnapshot.child("Email").getValue(String.class);
                    if (email.equalsIgnoreCase(userEmail)) {
                        // Retrieve Name and other user details
                        name = userSnapshot.child("Name").getValue(String.class);
                        break; // If we found the email, no need to continue looping
                    }
                }
                callback.onNameReceived(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Error fetching user details: " + error.getMessage());
                callback.onNameReceived(null); // Handle error case by returning null
            }
        });
    }

    private void fetchUsernameAndCreatePost(String postId, String title, String description, long timestamp) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);

                if (username == null || username.isEmpty()) {
                    Log.w(TAG, "Username not found for UID: " + currentUser.getUid());
                    username = "Unknown User";
                }

                // Proceed to create the post with the fetched username
                createPost(postId, title, description, username, timestamp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching username: " + error.getMessage(), error.toException());
                createPost(postId, title, description, "Unknown User", timestamp);
            }
        });
    }

    private void createPost(String postId, String title, String description, String username, long timestamp) {
        Post post = new Post(
                currentUser.getUid(),
                username,
                title,
                description,
                mediaUrls,
                mediaTypes,
                timestamp,
                0
        );

        postsDatabaseRef.child(postId).setValue(post)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post successfully added to Realtime Database");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Post Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    // Refresh the list of posts
                    Intent intent = new Intent(UploadActivity.this, DepartmentActivity.class);
                    intent.putExtra("department", department);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding post: ", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to upload post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
