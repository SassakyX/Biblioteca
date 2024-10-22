package com.example.bibliotecavirtual;

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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;
public class BookDetailActivity extends AppCompatActivity {
    private ImageButton backButton;
    private ImageButton botonFavorito;
    private String bookTitle;
    private String bookImageUrl;
    private String bookDescription; // Nueva variable para la descripción
    private String bookCategory; // Nueva variable para la categoría

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Inicializa el botón de favoritos
        botonFavorito = findViewById(R.id.BotonFavoritos);

        // Obtiene datos del Intent
        Intent intent = getIntent();
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

        // Añadir a favoritos
        botonFavorito.setOnClickListener(v -> guardarEnFavoritos(bookTitle, bookImageUrl, bookDescription, bookCategory));
        backButton.setOnClickListener(v -> startActivity(new Intent(BookDetailActivity.this, MainActivity.class)));
    }

    private void guardarEnFavoritos(String bookTitle, String bookImageUrl, String bookDescription, String bookCategory) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SharedPreferences sharedPreferences = getSharedPreferences("favoritos", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Set<String> favoritos = sharedPreferences.getStringSet(userId + "_favoritos", new HashSet<>());

            // Guardar todos los datos del libro en formato "titulo|urlImagen|descripcion|categoria"
            String libroFav = bookTitle + "|" + bookImageUrl + "|" + bookDescription + "|" + bookCategory;
            favoritos.add(libroFav);

            editor.putStringSet(userId + "_favoritos", favoritos);
            editor.apply();
            Log.d("Favoritos", "Favoritos guardados: " + favoritos);
            Log.d("Favoritos", "Favorito añadido: " + libroFav);
            Toast.makeText(this, "Libro añadido a favoritos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Inicia sesión para añadir a favoritos", Toast.LENGTH_SHORT).show();
        }
    }
}


