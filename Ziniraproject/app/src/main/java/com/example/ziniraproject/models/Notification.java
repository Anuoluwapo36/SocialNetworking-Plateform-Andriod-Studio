package com.example.ziniraproject.models;

public class Notification {
    private String type;
    private String from_user_id;

    private long timestamp;
    private boolean read;

    // Empty constructor required for Firebase
    public Notification() {}

    // Constructor with all fields
    public Notification(String type, String from_user_id, long timestamp, boolean read) {
        this.type = type;
        this.from_user_id = from_user_id;
        this.timestamp = timestamp;
        this.read = read;
    }

    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}


