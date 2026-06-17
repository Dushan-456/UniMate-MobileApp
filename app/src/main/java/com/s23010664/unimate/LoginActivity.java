package com.s23010664.unimate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText userNameField, passwordField;
    private Button btnSignIn;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        // --- Bind UI Elements ---
        userNameField = findViewById(R.id.userName);
        passwordField = findViewById(R.id.pwd);
        btnSignIn = findViewById(R.id.btnSignIn);

        // --- Navigate to Register ---
        TextView reg_text = findViewById(R.id.toRegister);
        reg_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        // --- Sign In Button ---
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    /**
     * Validates the login fields and checks credentials against the database.
     * On success, saves the session and navigates to MainActivity.
     */
    private void loginUser() {
        String email = userNameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // --- Validation ---
        if (email.isEmpty()) {
            userNameField.setError("Email is required");
            userNameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            passwordField.requestFocus();
            return;
        }

        // --- Check credentials ---
        boolean isValid = dbHelper.checkLogin(email, password);

        if (isValid) {
            // Save login session to SharedPreferences.
            SharedPreferences prefs = getSharedPreferences("UniMatePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userEmail", email);
            editor.apply();

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to MainActivity and clear the back stack.
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}