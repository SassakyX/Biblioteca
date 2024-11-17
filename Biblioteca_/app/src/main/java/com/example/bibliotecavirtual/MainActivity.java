package com.example.bibliotecavirtual;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int CATEGORY_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private List<Book> filteredBookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa la lista de libros
        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();
        ImageButton buttonF = findViewById(R.id.ButtonF);
        buttonF.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Abrir actividad de favoritos
                Intent intent = new Intent(MainActivity.this, Favorites.class);
                startActivity(intent);
            }
        });

        // Configura el RecyclerView y el adaptador
        recyclerView = findViewById(R.id.book_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        // Configura el adaptador
        bookAdapter = new BookAdapter(filteredBookList, book -> {
            // Crea un intent para iniciar BookDetailActivity
            Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
            intent.putExtra("book_title", book.getTitle());
            intent.putExtra("book_autor", book.getAutor());
            intent.putExtra("book_description", book.getDescription());
            intent.putExtra("book_image_url", book.getImageUrl());
            intent.putExtra("book_category", book.getCategory());
            startActivity(intent);
        });
        recyclerView.setAdapter(bookAdapter);

        // Configura el botón para abrir la pantalla de categorías
        Button categoryButton = findViewById(R.id.button);
        categoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivityForResult(intent, CATEGORY_REQUEST_CODE);
        });

        // Configura Retrofit para consumir la API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://66da5709f47a05d55be492fd.mockapi.io/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookApiService service = retrofit.create(BookApiService.class);

        // Realiza la solicitud para obtener los libros
        service.getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.i("Main_APP", "Código de respuesta: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    bookList.clear(); // Limpia la lista antes de agregar los nuevos datos
                    bookList.addAll(response.body());
                    filteredBookList.addAll(bookList); // Inicialmente, muestra todos los libros
                    bookAdapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
                } else {
                    Log.e("Main_APP", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable throwable) {
                Log.e("Main_APP", "Error en la solicitud: " + throwable.getMessage());
            }
        });

        // Configura el SearchView
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText); // Llama al método de filtrado
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CATEGORY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedCategory = data.getStringExtra("selected_category");
            filterBooksByCategory(selectedCategory);
        }
    }

    private void filterBooks(String query) {
        filteredBookList.clear();
        if (query.isEmpty()) {
            filteredBookList.addAll(bookList); // Si no hay texto, muestra todos los libros
        } else {
            for (Book book : bookList) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredBookList.add(book); // Agrega solo los libros que coinciden con la búsqueda
                }
            }
        }
        bookAdapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
    }

    private void filterBooksByCategory(String category) {
        filteredBookList.clear();
        if (category == null) {
            filteredBookList.addAll(bookList);
        } else {
            for (Book book : bookList) {
                if (book.getCategory().equals(category)) {
                    filteredBookList.add(book);
                }
            }
        }
        bookAdapter.notifyDataSetChanged();
    }
}
