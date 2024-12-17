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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddNoticeActivity extends AppCompatActivity {

    private EditText noticeTitle, noticeDescription;
    private Button postNoticeButton,cancelNoticeButton;
    private ProgressBar progressBar;
    private DatabaseReference noticesRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        noticeTitle = findViewById(R.id.noticeTitle);
        noticeDescription = findViewById(R.id.noticeDescription);
        postNoticeButton = findViewById(R.id.postNoticeButton);
        progressBar = findViewById(R.id.progressBar);
        cancelNoticeButton = findViewById(R.id.cancelButton);


        noticesRef = FirebaseDatabase.getInstance().getReference("notices");
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Get current user

        postNoticeButton.setOnClickListener(v -> postNotice());
        cancelNoticeButton.setOnClickListener(v -> finish());
    }

    private void postNotice() {
        String title = noticeTitle.getText().toString().trim();
        String description = noticeDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            noticeTitle.setError("Title is required");
            return;
        }
/*
        if (TextUtils.isEmpty(description)) {
            noticeDescription.setError("Description is required");
            return;
        }*/

        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to post a notice", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Generate a unique notice ID
        String noticeId = noticesRef.push().getKey();

        if (noticeId == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AddNoticeActivity.this, "Failed to generate notice ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Notice notice = new Notice(noticeId, title, description, System.currentTimeMillis(), userId);

        noticesRef.child(noticeId).setValue(notice).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(AddNoticeActivity.this, "Notice posted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddNoticeActivity.this, "Failed to post notice", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
