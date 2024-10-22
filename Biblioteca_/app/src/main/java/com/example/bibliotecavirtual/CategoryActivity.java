package com.example.bibliotecavirtual;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CategoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(v -> logoutUser());

        Button fictionButton = findViewById(R.id.button_fiction);
        Button nonFictionButton = findViewById(R.id.button_non_fiction);
        Button historyButton = findViewById(R.id.button_history);
        Button mysteryButton = findViewById(R.id.button_mystery);
        Button scienceButton = findViewById(R.id.button_science);


        fictionButton.setOnClickListener(v -> returnCategory("Ficción"));
        nonFictionButton.setOnClickListener(v -> returnCategory("No Ficción"));
        historyButton.setOnClickListener(v -> returnCategory("Historia"));
        mysteryButton.setOnClickListener(v -> returnCategory("Misterio"));
        scienceButton.setOnClickListener(v -> returnCategory("Ciencia"));


    }

    private void returnCategory(String category) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_category", category);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private FirebaseAuth mAuth;
    private Button logoutButton;
    private void logoutUser() {

        mAuth.signOut(); // Cierra la sesión de Firebase

        // Limpia las preferencias de sesión
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");  // O cualquier otra preferencia que guardes
        editor.apply();

        // Regresa a la actividad de Login
        startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
        finish();
    }
}
