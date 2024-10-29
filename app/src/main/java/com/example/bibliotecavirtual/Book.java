package com.example.bibliotecavirtual;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Book {
    private String title;
    private String description;
    private String imageUrl;
    private String autor;
    private String category;
    @SerializedName("averageRating")
    private float averageRating;
    private List<Review> reviews;
    private int id; // Campo para almacenar el ID generado por MockAPI

    public Book(String title, String autor, String description, String imageUrl, String category, float averageRating, List<Review> reviews, int id) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.autor = autor;
        this.category = category;
        this.averageRating = averageRating;
        this.reviews = reviews;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() { // Cambiado de int a String
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;}
    public String getAutor()
    {
        return autor;
    }

    }



