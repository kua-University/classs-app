package com.example.classcsschedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton, signupButton;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);

        loginButton.setOnClickListener(v -> loginUser());
        signupButton.setOnClickListener(v -> openSignUpPage());
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim().toLowerCase();
        String password = passwordInput.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Enter a valid password (at least 6 characters)");
            passwordInput.requestFocus();
            return;
        }

        Log.d("LoginDebug", "Attempting to log in user with email: " + email);

        db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.e("LoginDebug", "No user found for email: " + email);
                        Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    String storedPassword = document.getString("password");
                    String userType = document.getString("user_type");
                    String adminId = document.getId();

                    if (storedPassword == null || userType == null) {
                        Log.e("LoginDebug", "User data is incomplete in Firestore.");
                        Toast.makeText(MainActivity.this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (storedPassword.equals(password)) {
                        Log.d("LoginDebug", "Login successful! UserType: " + userType);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userType", userType);
                        editor.putString("adminId", adminId);  // ✅ Save adminId
                        editor.apply();

                        Log.d("LoginDebug", "Stored userType in SharedPreferences: " + userType);
                        redirectToDashboard(userType);
                    } else {
                        Log.e("LoginDebug", "Incorrect password entered.");
                        Toast.makeText(MainActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoginDebug", "Firestore query failed: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Login failed. Try again!", Toast.LENGTH_SHORT).show();
                });
    }

    private void redirectToDashboard(String userType) {
        Intent intent;

        if ("School Head".equalsIgnoreCase(userType)) {
            Log.d("LoginDebug", "Redirecting to School Dashboard...");
            intent = new Intent(MainActivity.this, SchoolHeadDashboard.class);
        } else if ("pending".equalsIgnoreCase(userType)) {
            Log.d("LoginDebug", "Displaying not verified toast...");
            Toast.makeText(MainActivity.this, "Your Account hasn't been verified yet.", Toast.LENGTH_SHORT).show();
            intent = new Intent(MainActivity.this, MainActivity.class);
        } else if ("Admin".equalsIgnoreCase(userType)) {
            Log.d("LoginDebug", "Redirecting to Student Dashboard...");
            intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
        } else if ("Student".equalsIgnoreCase(userType)) {
            Log.d("LoginDebug", "Redirecting to Student Dashboard...");
            intent = new Intent(MainActivity.this, StudentDashboard.class);
        } else if ("Instructor".equalsIgnoreCase(userType)) {
            Log.d("LoginDebug", "Redirecting to Instructor Dashboard...");
            intent = new Intent(MainActivity.this, InstructorDashboard.class);
        } else {
            Toast.makeText(MainActivity.this, "Unknown user role!", Toast.LENGTH_SHORT).show();
            Log.e("LoginDebug", "Unknown user role: " + userType);
            return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void openSignUpPage() {
        startActivity(new Intent(MainActivity.this, SignupActivity.class));
    }
}
