// AdminViewModel.java
package com.example.krypt;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminViewModel extends ViewModel {
    private final MutableLiveData<List<Admin>> adminListLiveData = new MutableLiveData<>();

    public LiveData<List<Admin>> getAdminListLiveData() {
        return adminListLiveData;
    }

    public void loadAdminInfo() {
        if (adminListLiveData.getValue() != null) {
            return;
        }

        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("admins");

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Admin> adminList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("Name").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    String contact = snapshot.child("Contact").getValue(String.class);

                    Admin admin = new Admin(name, email, contact);
                    adminList.add(admin);
                }
                adminListLiveData.setValue(adminList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}