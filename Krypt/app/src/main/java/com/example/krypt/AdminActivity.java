package com.example.krypt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;

public class AdminActivity extends bottomNavigation {

    private LinearLayout adminContainer;
    private ProgressBar progressBar;
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_admin, findViewById(R.id.content_frame));

        adminContainer = findViewById(R.id.adminContainer);
        progressBar = findViewById(R.id.progressBar);

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        adminViewModel.getAdminListLiveData().observe(this, this::displayAdmins);


        progressBar.setVisibility(View.VISIBLE);
        adminViewModel.loadAdminInfo();
    }

    private void displayAdmins(List<Admin> adminList) {
        adminContainer.removeAllViews();

        if (adminList != null && !adminList.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(this);

            for (Admin admin : adminList) {

                LinearLayout adminLayout = (LinearLayout) inflater.inflate(R.layout.admin_item, adminContainer, false);


                TextView nameTextView = adminLayout.findViewById(R.id.nameTextView);
                TextView emailTextView = adminLayout.findViewById(R.id.emailTextView);
                TextView contactTextView = adminLayout.findViewById(R.id.contactTextView);
                ImageView phoneImageView = adminLayout.findViewById(R.id.phoneImageView);
                ImageView emailImageView = adminLayout.findViewById(R.id.emailImageView);

                nameTextView.setText(admin.getName() != null ? admin.getName() : "N/A");
                emailTextView.setText(admin.getEmail());
                contactTextView.setText(admin.getContact());

                phoneImageView.setOnClickListener(v -> {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + admin.getContact()));
                    startActivity(dialIntent);
                });

                emailImageView.setOnClickListener(v -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + admin.getEmail()));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{admin.getEmail()});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                });


                adminContainer.addView(adminLayout);
            }
        }

        progressBar.setVisibility(View.GONE);
    }
}
