package com.example.classcsschedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Spinner roleSpinner;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firestore (ONLY ONCE)
        db = FirebaseFirestore.getInstance();

        // Firestore test connection
        testFirestoreConnection();

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        roleSpinner = findViewById(R.id.roleSpinner); // Role selection
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView loginLink = findViewById(R.id.loginLink);

        // Handle Sign-Up button click
        signUpButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            String selectedRole = roleSpinner.getSelectedItem().toString().toLowerCase();

            // Validate inputs
            if (!isValidInput(email, password, confirmPassword)) {
                return;
            }

            // Hash the password before storing
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Save user to Firestore
            saveUserToFirestore(email, hashedPassword, selectedRole);
        });

        // Navigate to Login Screen
        loginLink.setOnClickListener(v -> {
            Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }

    // Firestore test connection
    private void testFirestoreConnection() {
        db.collection("test").document("check")
                .set(new HashMap<String, Object>() {{
                    put("status", "connected");
                }})
                .addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Connected Successfully!"))
                .addOnFailureListener(e -> Log.e("FirestoreTest", "Failed to connect: " + e.getMessage()));
    }

    // Validate email, password, and confirm password inputs
    private boolean isValidInput(String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Save user to Firestore
    private void saveUserToFirestore(String email, String password, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);
        user.put("role", role);

        db.collection("users") // Collection name is "users"
                .add(user) // Firestore generates a unique document ID
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "User saved with ID: " + documentReference.getId());
                    Toast.makeText(SignUpActivity.this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show();
                    redirectToDashboard(role);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save user data: " + e.getMessage());
                    Toast.makeText(SignUpActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Redirect user to the appropriate dashboard based on their role
    private void redirectToDashboard(String role) {
        Intent intent;
        if ("student".equals(role)) {
            intent = new Intent(SignUpActivity.this, StudentDashboardActivity.class);
        } else if ("teacher".equals(role)) {
            intent = new Intent(SignUpActivity.this, TeacherDashboardActivity.class);
        } else {
            Toast.makeText(this, "Invalid role assigned!", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(intent);
        finish();
    }
}
