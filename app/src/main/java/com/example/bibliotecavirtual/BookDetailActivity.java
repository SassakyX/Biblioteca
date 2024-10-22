package com.example.bibliotecavirtual;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RatingBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {
    private TextView bookAverageRating;
    private RecyclerView reviewRecyclerView;
    private List<Review> reviewList;
    private ReviewAdapter reviewAdapter;
    private float averageRating;
    private ImageButton backButton;
    private ImageButton botonFavorito;
    private String bookTitle;
    private String bookImageUrl;
    private String bookDescription;
    private String bookCategory;
    private int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Inicializa el botón de favoritos
        botonFavorito = findViewById(R.id.BotonFavoritos);

        // Obtiene datos del Intent
        Intent intent = getIntent();
        bookId = intent.getIntExtra("book_id", -1); // Asegúrate de pasar el ID del libro
        bookTitle = intent.getStringExtra("book_title");
        bookDescription = intent.getStringExtra("book_description");
        bookImageUrl = intent.getStringExtra("book_image_url");
        bookCategory = intent.getStringExtra("book_category");

        // Configuración de vistas
        backButton = findViewById(R.id.Menu);
        ImageView bookImageLarge = findViewById(R.id.book_image_large);
        TextView bookTitleLarge = findViewById(R.id.book_title_large);
        TextView bookDescriptionLarge = findViewById(R.id.book_description_large);
        TextView bookCategoryLarge = findViewById(R.id.book_category_large);

        // Cargar imagen y texto
        Glide.with(this).load(bookImageUrl).into(bookImageLarge);
        bookTitleLarge.setText(bookTitle);
        bookDescriptionLarge.setText(bookDescription);
        bookCategoryLarge.setText(bookCategory);

        // Configurar el botón de favoritos
        botonFavorito.setOnClickListener(v -> guardarEnFavoritos(bookTitle, bookImageUrl, bookDescription, bookCategory, bookId));
        backButton.setOnClickListener(v -> finish()); // Vuelve a la actividad anterior

        // Inicializar vistas
        bookAverageRating = findViewById(R.id.book_average_rating);
        reviewRecyclerView = findViewById(R.id.review_recycler_view);

        // Configurar RecyclerView para mostrar reseñas
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        // Cargar datos del libro y reseñas
        loadBookReviews();

        // Configurar botón para agregar reseña
        Button addReviewButton = findViewById(R.id.submit_review_button);
        if (addReviewButton != null) {
            addReviewButton.setOnClickListener(v -> showAddReviewDialog());
        }
    }

    private void guardarEnFavoritos(String bookTitle, String bookImageUrl, String bookDescription, String bookCategory, int bookId) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SharedPreferences sharedPreferences = getSharedPreferences("favoritos", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Recupera los favoritos actuales del usuario y crea un conjunto para modificar
            Set<String> favoritos = new HashSet<>(sharedPreferences.getStringSet(userId + "_favoritos", new HashSet<>()));

            // Formatear los datos del libro, incluyendo el promedio de calificación
            String libroFav = bookTitle + "|" + bookImageUrl + "|" + bookDescription + "|" + bookCategory + "|" + averageRating + "|" + bookId;
            favoritos.add(libroFav); // Agregar el libro formateado a los favoritos

            // Guardar los favoritos actualizados en SharedPreferences
            editor.putStringSet(userId + "_favoritos", favoritos);
            editor.apply();

            Log.d("Favoritos", "Libro añadido a favoritos: " + libroFav);
            Toast.makeText(this, "Libro añadido a favoritos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Inicia sesión para añadir a favoritos", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadBookReviews() {
        // Llamada a la API para obtener reseñas
        BookApiService service = ApiClient.getClient("https://66da5709f47a05d55be492fd.mockapi.io/v1/").create(BookApiService.class);

        // Usar el bookId obtenido del Intent
        service.getReviews(bookId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewList.clear();
                    reviewList.addAll(response.body());
                    reviewAdapter.notifyDataSetChanged();

                    // Actualizar detalles del libro después de cargar las reseñas
                    updateAverageRating();
                } else {
                    Toast.makeText(BookDetailActivity.this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(BookDetailActivity.this, "Error de red al cargar reseñas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAverageRating() {
        if (reviewList != null && !reviewList.isEmpty()) {
            float totalRating = 0;
            for (Review review : reviewList) {
                totalRating += review.getRating();
            }
            averageRating = totalRating / reviewList.size();
        } else {
            averageRating = 0; // Si no hay reseñas, el promedio es 0
        }

        bookAverageRating.setText(String.format("%.1f", averageRating)); // Formatear para mostrar un decimal
    }

    private void showAddReviewDialog() {
        // Crear un diálogo personalizado para agregar una reseña
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_review, null);

        EditText reviewComment = view.findViewById(R.id.review_comment);
        RatingBar reviewRatingBar = view.findViewById(R.id.review_rating);

        builder.setView(view);
        builder.setTitle("Agregar Reseña");
        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String comment = reviewComment.getText().toString().trim();
            float rating = reviewRatingBar.getRating();
            SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
            String userName = preferences.getString("userEmail", "Usuario Anónimo");

            if (!comment.isEmpty() && rating > 0) {
                // Crear una nueva reseña y enviarla a la API
                Review newReview = new Review("userId", userName, comment, rating);
                submitReview(newReview);
            } else {
                Toast.makeText(this, "Por favor, agrega un comentario y una calificación.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitReview(Review review) {
        if (bookId == -1) {
            Toast.makeText(BookDetailActivity.this, "ID del libro no válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        BookApiService service = ApiClient.getClient("https://66da5709f47a05d55be492fd.mockapi.io/v1/").create(BookApiService.class);

        Log.d("BookDetailActivity", "Enviando reseña para bookId: " + bookId);

        // Llamada para agregar la reseña
        service.addReview(bookId, review).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookDetailActivity.this, "Reseña agregada exitosamente", Toast.LENGTH_SHORT).show();
                    loadBookReviews();
                } else {
                    Toast.makeText(BookDetailActivity.this, "Error al agregar la reseña. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(BookDetailActivity.this, "Error de red al agregar la reseña", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
