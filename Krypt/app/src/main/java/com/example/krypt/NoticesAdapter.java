package com.example.krypt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.NoticeViewHolder> {

    private List<Notice> noticeList;
    private Context context;
    private boolean isAdmin;

    public NoticesAdapter(List<Notice> noticeList, Context context) {
        this.noticeList = noticeList != null ? noticeList : new ArrayList<>();
        this.context = context;
        this.isAdmin = UserSession.getInstance(context).isAdmin();
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = noticeList.get(position);

        holder.noticeTitle.setText(notice.getTitle());
        holder.noticeDescription.setText(notice.getDescription());

        if (isAdmin) {
            holder.deleteNoticeButton.setVisibility(View.VISIBLE);
            holder.editNoticeButton.setVisibility(View.VISIBLE);

            holder.deleteNoticeButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Notice")
                        .setMessage("Are you sure you want to delete this notice?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteNotice(notice.getId(), position))
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            holder.editNoticeButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Edit Notice")
                        .setMessage("Do you want to edit this notice?")
                        .setPositiveButton("Edit", (dialog, which) -> {
                            Intent intent = new Intent(context, EditPostActivity.class);
                            intent.putExtra("postId", notice.getId());
                            intent.putExtra("title", notice.getTitle());
                            intent.putExtra("description", notice.getDescription());
                            context.startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        } else {
            holder.deleteNoticeButton.setVisibility(View.GONE);
            holder.editNoticeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return noticeList != null ? noticeList.size() : 0;
    }

    private void deleteNotice(String noticeId, int position) {
        DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("notices").child(noticeId);
        noticeRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                noticeList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, noticeList.size()); // Keep position sync
                Toast.makeText(context, "Notice deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete notice", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView noticeTitle, noticeDescription;
        ImageButton deleteNoticeButton, editNoticeButton;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            noticeTitle = itemView.findViewById(R.id.noticeTitle);
            noticeDescription = itemView.findViewById(R.id.noticeDescription);
            deleteNoticeButton = itemView.findViewById(R.id.deleteNoticeButton);
            editNoticeButton = itemView.findViewById(R.id.editNoticeButton);
        }
    }
}