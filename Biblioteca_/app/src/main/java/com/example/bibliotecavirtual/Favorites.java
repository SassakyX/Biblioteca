package com.example.bibliotecavirtual;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Set;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Favorites extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private FavoritesAdapter favoritesAdapter;
    private List<Book> favoriteBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_books);

        recyclerViewFavorites = findViewById(R.id.list_favorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        favoriteBooks = new ArrayList<>();
        mostrarFavoritos();
    }

    private void mostrarFavoritos() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SharedPreferences sharedPreferences = getSharedPreferences("favoritos", MODE_PRIVATE);

            Set<String> favoritos = sharedPreferences.getStringSet(userId + "_favoritos", new HashSet<>());
            Log.d("Favoritos", "Favoritos cargados: " + favoritos);
            if (favoritos != null && !favoritos.isEmpty()) {
                for (String favorito : favoritos) {
                    String[] datosLibro = favorito.split("\\|");
                    if (datosLibro.length == 4) {
                        String title = datosLibro[0];
                        String imageUrl = datosLibro[1];
                        String description = datosLibro[2];
                        String category = datosLibro[3];

                        Book book = new Book(title, "", description, imageUrl, category);
                        favoriteBooks.add(book);
                    }
                }

                favoritesAdapter = new FavoritesAdapter(favoriteBooks);
                recyclerViewFavorites.setAdapter(favoritesAdapter);
                favoritesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No tienes libros en favoritos", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Inicia sesión para ver tus favoritos", Toast.LENGTH_SHORT).show();
        }

    }
}

