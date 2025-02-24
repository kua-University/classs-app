package com.example.classcsschedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Navigate to Schedule Activity
        Button scheduleButton = findViewById(R.id.scheduleButton);
        scheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        // Navigate to Quick Actions Activity
        Button quickActionsButton = findViewById(R.id.quickActionsButton);
        quickActionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, QuickActionsActivity.class);
            startActivity(intent);
        });
    }
}
