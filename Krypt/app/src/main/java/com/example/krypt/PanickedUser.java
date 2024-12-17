package com.example.krypt;

public class PanickedUser {
    private final String userId;
    private final double latitude;
    private final double longitude;
    private final String message;

    public PanickedUser(String userId, double latitude, double longitude, String message) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMessage() {
        return message;
    }
}
