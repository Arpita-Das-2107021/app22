package com.example.krypt;

public class LocationMessage {
    private User user;
    private double latitude;
    private double longitude;
    private String message;

    LocationMessage(){}

    public LocationMessage(User user, double latitude, double longitude, String message) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
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