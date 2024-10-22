package com.example.bibliotecavirtual;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int CATEGORY_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private RecyclerView recommendedRecyclerView;
    private BookAdapter bookAdapter;
    private BookAdapter recommendedAdapter;
    private List<Book> bookList;
    private List<Book> filteredBookList;
    private List<Book> recommendedBookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa la lista de libros
        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();
        recommendedBookList = new ArrayList<>();

        // Configura el RecyclerView para todos los libros
        recyclerView = findViewById(R.id.book_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Configura el adaptador para todos los libros
        bookAdapter = new BookAdapter(filteredBookList, book -> openBookDetailActivity(book));
        recyclerView.setAdapter(bookAdapter);

        // Configura el RecyclerView para libros recomendados
        recommendedRecyclerView = findViewById(R.id.recommended_book_list);
        LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recommendedRecyclerView.setLayoutManager(recommendedLayoutManager);

        // Configura el adaptador para libros recomendados
        recommendedAdapter = new BookAdapter(recommendedBookList, book -> openBookDetailActivity(book));
        recommendedRecyclerView.setAdapter(recommendedAdapter);

        // Llamar al servicio para obtener todos los libros
        fetchBooksFromAPI();

        // Configura el botón para abrir la pantalla de categorías
        Button categoryButton = findViewById(R.id.button);
        categoryButton.setOnClickListener(v -> openCategoryActivity());

        // Configura el SearchView para la búsqueda de libros
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        // Configura el botón para abrir la actividad de favoritos
        ImageButton buttonF = findViewById(R.id.ButtonF);
        buttonF.setOnClickListener(v -> openFavoritesActivity());
    }

    private void openBookDetailActivity(Book book) {
        Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
        intent.putExtra("book_id", book.getId());
        intent.putExtra("book_title", book.getTitle());
        intent.putExtra("book_autor", book.getAutor());
        intent.putExtra("book_description", book.getDescription());
        intent.putExtra("book_image_url", book.getImageUrl());
        intent.putExtra("book_category", book.getCategory());
        startActivity(intent);
    }

    private void openCategoryActivity() {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        startActivityForResult(intent, CATEGORY_REQUEST_CODE);
    }

    private void openFavoritesActivity() {
        Intent intent = new Intent(MainActivity.this, Favorites.class);
        startActivity(intent);
    }

    private void fetchBooksFromAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://66da5709f47a05d55be492fd.mockapi.io/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookApiService service = retrofit.create(BookApiService.class);

        service.getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookList.clear();
                    bookList.addAll(response.body());

                    // Guardar los libros para usarlos sin conexión
                    guardarLibrosOffline(bookList);

                    // Obtener las reseñas para cada libro y actualizar el promedio de calificación
                    fetchReviewsForAllBooks();
                } else {
                    Log.e("Main_APP", "Error en la respuesta: " + response.code());
                    cargarLibrosOffline(); // Cargar desde almacenamiento local si falla
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable throwable) {
                Log.e("Main_APP", "Error en la solicitud: " + throwable.getMessage());
                cargarLibrosOffline(); // Cargar desde almacenamiento local si falla
            }
        });
    }

    private void guardarLibrosOffline(List<Book> books) {
        SharedPreferences sharedPreferences = getSharedPreferences("libros_offline", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> libros = new HashSet<>();

        for (Book book : books) {
            // Incluir averageRating al guardar offline
            String libroData = book.getTitle() + "|" + book.getImageUrl() + "|" + book.getDescription() + "|" + book.getCategory() + "|" + book.getAverageRating() + "|" + book.getId();
            libros.add(libroData);
        }

        editor.putStringSet("lista_libros", libros);
        editor.apply();
    }

    private void cargarLibrosOffline() {
        SharedPreferences sharedPreferences = getSharedPreferences("libros_offline", MODE_PRIVATE);
        Set<String> librosGuardados = sharedPreferences.getStringSet("lista_libros", new HashSet<>());

        if (librosGuardados != null && !librosGuardados.isEmpty()) {
            bookList.clear();
            for (String libroData : librosGuardados) {
                String[] datosLibro = libroData.split("\\|");
                if (datosLibro.length == 6) { // Incluye el averageRating
                    String title = datosLibro[0];
                    String imageUrl = datosLibro[1];
                    String description = datosLibro[2];
                    String category = datosLibro[3];
                    float averageRating = Float.parseFloat(datosLibro[4]);
                    int id = Integer.parseInt(datosLibro[5]);

                    Book book = new Book(title, "", description, imageUrl, category, averageRating, new ArrayList<>(), id);
                    bookList.add(book);

                    // Verifica si debe añadirse a la lista de recomendados
                    if (averageRating > 3.9) {
                        recommendedBookList.add(book);
                    }
                }
            }
            refreshAdapters();
        } else {
            Toast.makeText(this, "No hay datos de libros disponibles sin conexión.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchReviewsForAllBooks() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://66da5709f47a05d55be492fd.mockapi.io/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookApiService service = retrofit.create(BookApiService.class);

        for (Book book : bookList) {
            service.getReviews(book.getId()).enqueue(new Callback<List<Review>>() {
                @Override
                public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Review> reviews = response.body();
                        book.setReviews(reviews);

                        // Calcula el averageRating basado en las reseñas obtenidas
                        float totalRating = 0;
                        for (Review review : reviews) {
                            totalRating += review.getRating();
                        }
                        float averageRating = reviews.size() > 0 ? totalRating / reviews.size() : 0;
                        book.setAverageRating(averageRating);

                        // Agregar a recomendados si cumple la condición
                        if (averageRating > 3.9) {
                            if (!recommendedBookList.contains(book)) {
                                recommendedBookList.add(book);
                            }
                        }

                        // Guardar los libros con las calificaciones actualizadas
                        guardarLibrosOffline(bookList);
                        refreshAdapters();
                    }
                }

                @Override
                public void onFailure(Call<List<Review>> call, Throwable t) {
                    Log.e("Main_APP", "Error al cargar reseñas para libro: " + book.getTitle());
                }
            });
        }
    }

    private void refreshAdapters() {
        filteredBookList.clear();
        filteredBookList.addAll(bookList);
        bookAdapter.notifyDataSetChanged();
        recommendedAdapter.notifyDataSetChanged();
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
        bookAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CATEGORY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedCategory = data.getStringExtra("selected_category");
            filterBooksByCategory(selectedCategory);
        }
    }
}
