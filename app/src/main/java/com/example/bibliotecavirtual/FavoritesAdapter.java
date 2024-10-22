package com.example.bibliotecavirtual;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {
    private List<Book> favoriteBooks; // Mantener la lista de objetos Book

    public FavoritesAdapter(List<Book> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_book, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Book book = favoriteBooks.get(position); // Obtener el objeto Book directamente
        holder.bookTitleTextView.setText(book.getTitle());
        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.getContext())
                .load(book.getImageUrl())
                .into(holder.bookCoverImageView);

        // Listener para abrir detalles del libro al pulsar la imagen
        holder.bookCoverImageView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), BookDetailActivity.class);
            intent.putExtra("book_title", book.getTitle());
            intent.putExtra("book_description", book.getDescription()); // Asegúrate de que 'description' no sea null
            intent.putExtra("book_image_url", book.getImageUrl());
            intent.putExtra("book_category", book.getCategory()); // Asegúrate de que 'category' no sea null
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteBooks.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitleTextView;
        ImageView bookCoverImageView; // Asegúrate de que el ID sea correcto

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.book_titleF);
            bookCoverImageView = itemView.findViewById(R.id.book_imageF); // Asegúrate de que este ID sea correcto
        }
    }
}

