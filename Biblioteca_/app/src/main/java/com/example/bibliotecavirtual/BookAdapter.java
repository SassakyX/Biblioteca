package com.example.bibliotecavirtual;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnItemClickListener onItemClickListener;

    public BookAdapter(List<Book> bookList, OnItemClickListener onItemClickListener) {
        this.bookList = bookList != null ? bookList : new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.bookAutor.setText(book.getAutor());
        holder.bookCategory.setText(book.getCategory()); // Agrega la categoría

        // Verifica la URL
        Log.i("BookAdapter", "Cargando imagen desde URL: " + book.getImageUrl());

        // Usa Glide para cargar la imagen desde la URL
        Glide.with(holder.itemView.getContext())
                .load(book.getImageUrl())
                .placeholder(R.drawable.dwadwa) // Imagen de marcador de posición
                .error(R.drawable.dwadwa) // Imagen en caso de error
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Error al cargar imagen: " + e.getMessage());
                        return false; // Propagar el error para que Glide maneje el error por defecto
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false; // No interceptar la carga exitosa
                    }
                })
                .into(holder.bookImage); // Asigna la imagen al ImageView

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(book));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void updateBookList(List<Book> newBookList) {
        this.bookList = newBookList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        TextView bookAutor;
        TextView bookCategory; // Agrega la vista para la categoría
        ImageView bookImage;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookAutor = itemView.findViewById(R.id.book_autor);
            bookCategory = itemView.findViewById(R.id.book_category);
            bookImage = itemView.findViewById(R.id.book_image);
        }
    }
}
