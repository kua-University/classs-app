package com.example.classcsschedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherDashboardActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // Reference to UI elements
        TextView welcomeText = findViewById(R.id.teacherWelcomeText);
        Button manageClassesButton = findViewById(R.id.manageClassesButton);
        Button viewStudentSchedulesButton = findViewById(R.id.viewStudentSchedulesButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Firebase instances
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Fetch and display teacher's full name
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            if (fullName != null) {
                                welcomeText.setText("Welcome, " + fullName + "!");
                            } else {
                                welcomeText.setText("Welcome, Teacher!");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load user data!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Button to navigate to Manage Classes activity
        manageClassesButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, ManageClassesActivity.class);
            startActivity(intent);
        });

        // Button to navigate to View Student Schedules activity
        viewStudentSchedulesButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, ViewStudentSchedulesActivity.class);
            startActivity(intent);
        });

        // Logout button
        logoutButton.setOnClickListener(v -> {
            auth.signOut(); // Sign out from Firebase
            Toast.makeText(TeacherDashboardActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TeacherDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the dashboard activity
        });
    }
}
