package com.example.krypt;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREFS_NAME = "UserSessionPrefs";
    private static final String KEY_IS_ADMIN = "isAdmin";

    private static UserSession instance;
    private boolean isAdmin = false;

    private UserSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false);
    }

    public static UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context);
        }
        return instance;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin, Context context) {
        this.isAdmin = isAdmin;

        // Save admin status to SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_ADMIN, isAdmin);
        editor.apply();
    }
}