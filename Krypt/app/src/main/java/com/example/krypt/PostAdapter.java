package com.example.krypt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostAdapter extends ArrayAdapter<Post> {
    private final String currentUserId;
    private final String department;
    private final ArrayList<String> postKeys;
    private final boolean isAdmin;

    public PostAdapter(Context context, ArrayList<Post> posts, ArrayList<String> postKeys, String department) {
        super(context, 0, posts);
        this.department = department;
        this.postKeys = postKeys;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        this.currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        this.isAdmin = UserSession.getInstance(context).isAdmin();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.clear();

        Post post = getItem(position);
        String postId = postKeys.get(position);

        if (post != null) {
            holder.postUsername.setText(post.getUsername()); // Ensure username is set
            holder.postTitle.setText(post.getTitle());
            holder.postDetails.setText(post.getDescription());

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(new Date(post.getTimestamp()));
            holder.postTime.setText(formattedDate);

            if (isAdmin || (currentUserId != null && currentUserId.equals(post.getUserId()))) {
                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setOnClickListener(v -> deletePost(postId));
            } else {
                holder.deleteButton.setVisibility(View.GONE);
            }
            if ((currentUserId != null && currentUserId.equals(post.getUserId()))) {
                holder.editButton.setVisibility(View.VISIBLE);
                holder.editButton.setOnClickListener(v -> editPost(postId));
            } else {
                holder.editButton.setVisibility(View.GONE);
            }

            holder.voteCount.setText(String.valueOf(post.getVoteCount()));

            holder.upvoteButton.setOnClickListener(v -> {
                int currentVotes = post.getVoteCount();
                post.setVoteCount(currentVotes + 1);
                holder.voteCount.setText(String.valueOf(post.getVoteCount()));
                updateVoteCountInDatabase(postId, post.getVoteCount());
            });

            holder.downvoteButton.setOnClickListener(v -> {
                int currentVotes = post.getVoteCount();
                post.setVoteCount(currentVotes - 1);
                holder.voteCount.setText(String.valueOf(post.getVoteCount()));
                updateVoteCountInDatabase(postId, post.getVoteCount());
            });
        }

        return convertView;
    }

    private void editPost(String postId) {
        if (postId == null || department == null) {
            Toast.makeText(getContext(), "Invalid post or department", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed to edit the post
        Intent intent = new Intent(getContext(), EditPostActivity.class);
        intent.putExtra("department", department);
        intent.putExtra("postId", postId);
        getContext().startActivity(intent);
    }

    private void deletePost(String postId) {
        if (postId == null || department == null) {
            Toast.makeText(getContext(), "Invalid post or department", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Proceed to delete the post
                    FirebaseDatabase.getInstance()
                            .getReference("reports")
                            .child(department)
                            .child(postId)
                            .removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete post: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void updateVoteCountInDatabase(String postId, int voteCount) {
        DatabaseReference postRef = FirebaseDatabase.getInstance()
                .getReference("reports")
                .child(department)
                .child(postId);
        postRef.child("voteCount").setValue(voteCount);
    }

    static class ViewHolder {
        TextView postUsername;
        TextView postTitle;
        TextView postDetails;
        TextView postTime;
        TextView voteCount;
        ImageView deleteButton, editButton;
        ImageView upvoteButton, downvoteButton;
        LinearLayout mediaContainer;

        ViewHolder(View view) {
            postUsername = view.findViewById(R.id.postUsername);
            postTitle = view.findViewById(R.id.postTitle);
            postDetails = view.findViewById(R.id.postDetails);
            postTime = view.findViewById(R.id.postTime);
            voteCount = view.findViewById(R.id.voteCount);
            deleteButton = view.findViewById(R.id.deleteButton);
            editButton = view.findViewById(R.id.editButton);
            upvoteButton = view.findViewById(R.id.upvoteButton);
            downvoteButton = view.findViewById(R.id.downvoteButton);
            mediaContainer = view.findViewById(R.id.mediaContainer);
        }

        void clear() {
            postUsername.setText("");
            postTitle.setText("");
            postDetails.setText("");
            postTime.setText("");
            voteCount.setText("");
            mediaContainer.removeAllViews();
            deleteButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }
    }
}