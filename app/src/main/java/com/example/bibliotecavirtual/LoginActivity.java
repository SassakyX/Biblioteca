package com.example.bibliotecavirtual;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private FirebaseAuth mAuth;
    private Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // El usuario ya ha iniciado sesión, ir a MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }else {
            setContentView(R.layout.activity_login);
            // Configuración normal del login
            mAuth = FirebaseAuth.getInstance();

            emailEditText = findViewById(R.id.email_edit_text);
            passwordEditText = findViewById(R.id.password_edit_text);
            loginButton = findViewById(R.id.login_button);
            registerButton = findViewById(R.id.register_button);
            skipButton = findViewById(R.id.skip_button);

            loginButton.setOnClickListener(v -> loginUser());
            registerButton.setOnClickListener(v -> registerUser());
            skipButton.setOnClickListener(v -> startMainActivityWithoutLogin());

        }


    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Iniciar MainActivity
                        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userEmail", email);
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }  else {
                        // Manejar el error
                        String errorMessage;
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            errorMessage = "Credenciales incorrectas. Intente nuevamente.";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            errorMessage = "Credenciales incorrectas. Intente nuevamente.";
                        } catch (Exception e) {
                            errorMessage = "Error al iniciar sesión: " + e.getMessage();
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void startMainActivityWithoutLogin() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

}

