package com.example.bibliotecavirtual;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText editnombre, editcorreo, editcontraseña;
    private Button botonregistrar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencia a los elementos de la UI
        editnombre = findViewById(R.id.editnombre);
        editcorreo = findViewById(R.id.editcorreo);
        editcontraseña = findViewById(R.id.editcontraseña);
        botonregistrar = findViewById(R.id.botonregistrar);

        // Configura el botón de registro
        botonregistrar.setOnClickListener(v -> {
            registerUser();
        });
    }
    private void registerUser() {
        String nombre = editnombre.getText().toString().trim();
        String email = editcorreo.getText().toString().trim();
        String password = editcontraseña.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Log.e("RegisterActivity", "Error al registrarse", task.getException());
                        Toast.makeText(RegisterActivity.this, "Error al registrarse: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
