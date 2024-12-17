package com.example.krypt;

import java.util.List;

public class Post {
    private String userId;
    private String username;
    private String title;
    private String description;
    private List<String> mediaUrls;
    private List<String> mediaTypes;
    private long timestamp;
    private int voteCount;

    public Post() {}

    public Post(String userId, String username, String title, String description, List<String> mediaUrls, List<String> mediaTypes, long timestamp, int voteCount) {
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.description = description;
        this.mediaUrls = mediaUrls;
        this.mediaTypes = mediaTypes;
        this.timestamp = timestamp;
        this.voteCount = voteCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public List<String> getMediaTypes() {
        return mediaTypes;
    }

    public void setMediaTypes(List<String> mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}