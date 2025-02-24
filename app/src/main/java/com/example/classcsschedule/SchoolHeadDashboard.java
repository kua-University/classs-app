package com.example.classcsschedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SchoolHeadDashboard extends AppCompatActivity {
    private Button assignCoursesButton, generateschedulebutton, viewSchedulesButton;
    private ListView scheduleList;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_school_head);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        checkIfLoggedIn();

        db = FirebaseFirestore.getInstance();

        assignCoursesButton = findViewById(R.id.assign_courses_button);
        generateschedulebutton = findViewById(R.id.create_schedule_button);
        viewSchedulesButton = findViewById(R.id.view_schedules_button);
        scheduleList = findViewById(R.id.schedule_list);

        // Assign Courses Button
        assignCoursesButton.setOnClickListener(v -> {
            Intent intent = new Intent(SchoolHeadDashboard.this, AssignCoursesActivity.class);
            startActivity(intent);
        });

        // Create Schedule Button
        generateschedulebutton.setOnClickListener(v -> {
            Intent intent = new Intent(SchoolHeadDashboard.this, GeneratescheduleActivity.class);
            startActivity(intent);
        });

        // View Schedules Button
        viewSchedulesButton.setOnClickListener(v -> loadSchedules());



        Button viewSchedulesButton = findViewById(R.id.view_schedules_button);
        viewSchedulesButton.setOnClickListener(v -> {
            // Launch the activity to view generated schedules.
            Intent intent = new Intent(SchoolHeadDashboard.this, ViewSchedulesActivity.class);
            startActivity(intent);
        }); // <-- Fixed: added missing bracket here

    }

    private void checkIfLoggedIn() {
        String userType = sharedPreferences.getString("userType", "");
        if (!userType.equals("School Head")) {
            Toast.makeText(this, "Unauthorized Access. Redirecting...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadSchedules() {
        scheduleList.setVisibility(View.VISIBLE);
        db.collection("Schedule")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> scheduleListData = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String department = document.getString("department");
                        String section = document.getString("section");
                        String classYear = document.getString("class_year");
                        String semester = document.getString("semester");
                        String year = document.getString("year");

                        String scheduleEntry = department + " - Section " + section + " (" + classYear + " Year, " + semester + " Semester, " + year + ")";
                        scheduleListData.add(scheduleEntry);
                    }
                    scheduleList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleListData));
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load schedules", Toast.LENGTH_SHORT).show());
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SchoolHeadDashboard.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
