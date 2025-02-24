package com.example.classcsschedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Reference to UI buttons
        Button viewScheduleButton = findViewById(R.id.viewScheduleButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        // Reference to UI buttons
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button submitFeedbackButton = findViewById(R.id.submitFeedbackButton);


        // Button to navigate to the student schedule screen
        viewScheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });


        // Button to handle logout
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(StudentDashboardActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
