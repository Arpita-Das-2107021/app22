package com.example.krypt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<PanickedUser> users;

    public UserAdapter(List<PanickedUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        PanickedUser user = users.get(position);
        holder.userIdTextView.setText(user.getUserId());
        holder.locationTextView.setText("Lat: " + user.getLatitude() + ", Long: " + user.getLongitude());
        holder.messageTextView.setText(user.getMessage());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;
        TextView locationTextView;
        TextView messageTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.userIdTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
