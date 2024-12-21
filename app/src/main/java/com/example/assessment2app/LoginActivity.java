package com.example.assessment2app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, createAccountButton;
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize DatabaseHelper
        userDatabaseHelper = new UserDatabaseHelper(this);

        // Bind UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        // Login button listener
        loginButton.setOnClickListener(v -> loginUser());

        // Redirect to SignUpActivity
        createAccountButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDatabaseHelper.authenticateUser(email, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Call the redirectToPage method to navigate based on the email domain
            redirectToPage(email);
        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToPage(String email) {
        if (email.endsWith("@sitemanagers")) {
            startActivity(new Intent(LoginActivity.this, SiteManagerActivity.class));
        } else if (email.endsWith("@donors")) {
            startActivity(new Intent(LoginActivity.this, DonorActivity.class));
        } else if (email.endsWith("@superuser")) {
            startActivity(new Intent(LoginActivity.this, SuperUserActivity.class));
        } else {
            Toast.makeText(this, "Invalid email domain!", Toast.LENGTH_SHORT).show();
        }
        finish(); // Close the login activity
    }
}
