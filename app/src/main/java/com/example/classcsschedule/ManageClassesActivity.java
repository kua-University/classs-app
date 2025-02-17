package com.example.classcsschedule;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ManageClassesActivity extends AppCompatActivity {

    private EditText classNameInput, classTimeInput, classDayInput;
    private ArrayList<String> classList;
    private ArrayAdapter<String> classAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_classes);

        // Initialize UI components
        classNameInput = findViewById(R.id.classNameInput);
        classTimeInput = findViewById(R.id.classTimeInput);
        classDayInput = findViewById(R.id.classDayInput);
        Button addClassButton = findViewById(R.id.addClassButton);
        ListView classListView = findViewById(R.id.classListView);

        // Initialize list and adapter
        classList = new ArrayList<>();
        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classList);
        classListView.setAdapter(classAdapter);

        // Add class button functionality
        addClassButton.setOnClickListener(v -> {
            try {
                addNewClass();
            } catch (Exception e) {
                Toast.makeText(this, "An error occurred while adding the class.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewClass() {
        // Retrieve user input
        String className = classNameInput.getText().toString().trim();
        String classTime = classTimeInput.getText().toString().trim();
        String classDay = classDayInput.getText().toString().trim();

        // Validate input fields
        if (className.isEmpty() || classTime.isEmpty() || classDay.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construct new class entry
        String newClass = classDay + " - " + className + " at " + classTime;

        // Add class to the list and notify adapter
        classList.add(newClass);
        classAdapter.notifyDataSetChanged();

        // Clear input fields for new entry
        classNameInput.setText("");
        classTimeInput.setText("");
        classDayInput.setText("");

        // Success message
        Toast.makeText(this, "Class added successfully!", Toast.LENGTH_SHORT).show();
    }
}
