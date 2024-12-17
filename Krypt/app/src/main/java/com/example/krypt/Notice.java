package com.example.krypt;

public class Notice {
    private String id;
    private String title;
    private String description;
    private long timestamp;
    private String userId; // New field for tracking ownership

    public Notice() {

    }

    public Notice(String id, String title, String description, long timestamp, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
