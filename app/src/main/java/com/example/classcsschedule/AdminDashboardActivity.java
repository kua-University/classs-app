package com.example.classcsschedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button btnManageUsers, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_dashboard);

        btnManageUsers = findViewById(R.id.manage_users_button);
        btnLogout = findViewById(R.id.logout_button);

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminManageUsersActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


    }


}
