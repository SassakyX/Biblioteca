package com.example.bibliotecavirtual;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.POST;
import retrofit2.http.Body;

import java.util.List;

public interface BookApiService {


    @GET("/Libros/{id}/reviews")
    Call<List<Review>> getReviews(@Path("id") int bookId);

    @POST("/Libros/{id}/reviews")
    Call<Void> addReview(@Path("id") int bookId, @Body Review review);


    @GET("/Libros")
    Call<List<Book>> getBooks();


    @GET("/Libros/{id}")
    Call < Book > find(@Path("id") int id);

    @GET("books")
    Call<List<Book>> getBooksByCategory(@Query("category") String category);
}

