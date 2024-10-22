package com.example.bibliotecavirtual;

public class Review {
    private String userId;
    private String userName;
    private String comment;
    private float rating;

    public Review(String userId, String userName, String comment, float rating) {
        this.userId = userId;
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters y setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
