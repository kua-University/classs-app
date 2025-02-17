package com.example.classcsschedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);

        // Set click listener for Login button
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both email and password!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate manually using Firestore
            authenticateUser(email, password);
        });

        // Set click listener for Register link
        registerLink.setOnClickListener(v -> {
            Intent signupIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signupIntent);
        });
    }

    // Authenticate user by checking hashed password
    private void authenticateUser(String email, String password) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String hashedPassword = document.getString("password");
                            String userId = document.getId(); // Firestore document ID
                            if (hashedPassword != null && BCrypt.checkpw(password, hashedPassword)) {
                                // Password is correct, fetch user role
                                fetchUserRole(userId);
                                return;
                            }
                        }
                        Toast.makeText(LoginActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fetch user role from Firestore and redirect accordingly
    private void fetchUserRole(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if (role != null) {
                            redirectToDashboard(role); // Redirect user to appropriate dashboard
                        } else {
                            Toast.makeText(LoginActivity.this, "User role not found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User data not found in Firestore!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Error fetching user role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Redirect user to the appropriate dashboard
    private void redirectToDashboard(String role) {
        Intent intent;
        if ("student".equals(role)) {
            intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
        } else if ("teacher".equals(role)) {
            intent = new Intent(LoginActivity.this, TeacherDashboardActivity.class);
        } else {
            Toast.makeText(this, "Invalid role!", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(intent);
        finish(); // Close the LoginActivity
    }
}
