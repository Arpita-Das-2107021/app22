package com.example.krypt;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewModel extends AndroidViewModel {

    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfileLiveData;
    }

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
    }
}