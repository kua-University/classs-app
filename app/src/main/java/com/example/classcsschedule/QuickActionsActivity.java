package com.example.classcsschedule;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuickActionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_actions);

        // Notification Button functionality
        Button notificationButton = findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(v ->
                Toast.makeText(QuickActionsActivity.this, "Notifications Coming Soon!", Toast.LENGTH_SHORT).show()
        );

        // Set Reminder Button functionality
        Button reminderButton = findViewById(R.id.reminderButton);
        reminderButton.setOnClickListener(v ->
                Toast.makeText(QuickActionsActivity.this, "Reminder feature under development!", Toast.LENGTH_SHORT).show()
        );

        // Update Profile Button functionality
        Button updateProfileButton = findViewById(R.id.profileButton);
        updateProfileButton.setOnClickListener(v ->
                Toast.makeText(QuickActionsActivity.this, "Profile update feature is coming soon!", Toast.LENGTH_SHORT).show()
        );

        // Go Back Button functionality
        Button goBackButton = findViewById(R.id.backButton);
        goBackButton.setOnClickListener(v -> {
            // Navigate back to the previous activity
            finish(); // Close the current activity
        });


    }
}
