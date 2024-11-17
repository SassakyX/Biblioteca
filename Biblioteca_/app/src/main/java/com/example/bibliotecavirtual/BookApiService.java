package com.example.bibliotecavirtual;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface BookApiService {
    @GET("/Libros")
    Call <List<Book>> getBooks();

    @GET("/Libros/{id}")
    Call < Book > find(@Path("id") int id);

    @GET("books")
    Call<List<Book>> getBooksByCategory(@Query("category") String category);
}

