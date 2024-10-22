package com.example.bibliotecavirtual;

public class Book {
    private String title;
    private String description;
    private String imageUrl;
    private String autor;
    private String category;

    public Book(String title, String autor, String description, String imageUrl, String category) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.autor = autor;
        this.category = category;
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



