package com.example.classcsschedule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScheduleActivity extends AppCompatActivity {

    private TextView scheduleItem; // Reference to the TextView displaying schedule items

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable the Action Bar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_schedule);

        // Reference the schedule item TextView
        scheduleItem = findViewById(R.id.scheduleItem);

        // Set functionality for the "Add New Schedule" button
        Button addScheduleButton = findViewById(R.id.addScheduleButton);
        addScheduleButton.setOnClickListener(v -> showAddScheduleDialog());

        // Set functionality for the "Go Back" button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Close this activity and go back
    }

    // Method to display the "Add New Schedule" dialog
    private void showAddScheduleDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);

        // Reference the input fields in the dialog
        EditText classNameInput = dialogView.findViewById(R.id.classNameInput);
        EditText classTimeInput = dialogView.findViewById(R.id.classTimeInput);
        EditText classDayInput = dialogView.findViewById(R.id.classDayInput);

        // Create and display the dialog
        new AlertDialog.Builder(this)
                .setTitle("Add New Schedule")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    // Get user input
                    String className = classNameInput.getText().toString();
                    String classTime = classTimeInput.getText().toString();
                    String classDay = classDayInput.getText().toString();

                    // Validate inputs
                    if (className.isEmpty() || classTime.isEmpty() || classDay.isEmpty()) {
                        Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update the schedule TextView
                    String newSchedule = classDay + " - " + className + " at " + classTime;
                    updateSchedule(newSchedule);

                    // Display success message
                    Toast.makeText(this, "Schedule added successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // Method to update the schedule TextView with new schedule
    @SuppressLint("SetTextI18n")
    private void updateSchedule(String newSchedule) {
        // Append the new schedule to the existing text
        String existingText = scheduleItem.getText().toString();
        scheduleItem.setText(existingText + "\n" + newSchedule);
    }
}
