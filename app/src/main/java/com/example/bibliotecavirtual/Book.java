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

    // Agregar el campo para la URL del libro
    private String bookUrl;

    // Constructor actualizado para incluir la URL del libro
    public Book(String title, String autor, String description, String imageUrl, String category, float averageRating, List<Review> reviews, int id, String bookUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.autor = autor;
        this.category = category;
        this.averageRating = averageRating;
        this.reviews = reviews;
        this.id = id;
        this.bookUrl = bookUrl;
    }

    // Getter y Setter para el ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter y Setter para el promedio de calificación
    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    // Getter y Setter para las reseñas
    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    // Getter y Setter para el título
    public String getTitle() {
        return title;
    }

    // Getter y Setter para la descripción
    public String getDescription() {
        return description;
    }

    // Getter y Setter para la URL de la imagen
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter y Setter para la categoría
    public String getCategory() {
        return category;
    }

    // Getter y Setter para el autor
    public String getAutor() {
        return autor;
    }

    // Getter y Setter para la URL del libro
    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }
}


