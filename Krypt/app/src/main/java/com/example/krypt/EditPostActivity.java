package com.example.krypt;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPostActivity extends AppCompatActivity {

    private EditText editTitle, editDescription;
    private Button saveButton,cancelButton;
    private ProgressBar progressBar;
    private DatabaseReference postRef;
    private String postId, department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        progressBar = findViewById(R.id.progressBar);

        postId = getIntent().getStringExtra("postId");
        department = getIntent().getStringExtra("department");

        if (postId == null || department == null) {
            Toast.makeText(this, "Invalid post or department", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        postRef = FirebaseDatabase.getInstance().getReference("reports").child(department).child(postId);

        loadPostDetails();

        saveButton.setOnClickListener(v -> savePostDetails());
        cancelButton.setOnClickListener(v -> {
            Toast.makeText(EditPostActivity.this, "Changes discarded", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadPostDetails() {
        progressBar.setVisibility(View.VISIBLE);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    editTitle.setText(post.getTitle());
                    editDescription.setText(post.getDescription());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditPostActivity.this, "Failed to load post details", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void savePostDetails() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTitle.setError("Title is required");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editDescription.setError("Description is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        postRef.child("title").setValue(title);
        postRef.child("description").setValue(description).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(EditPostActivity.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditPostActivity.this, "Failed to update post", Toast.LENGTH_SHORT).show();
            }
        });
    }
}