package com.example.classcsschedule;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentDashboard extends AppCompatActivity {

    private static final String TAG = "StudentDashboard";
    private EditText inputDepartment, inputClassYear, inputSection, inputSemester, inputAcademicYear;
    private Button viewSchedulesButton;
    private ListView scheduleList;
    private FirebaseFirestore db;
    private ArrayAdapter<String> scheduleAdapter;
    private List<String> scheduleListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        inputDepartment = findViewById(R.id.input_department);
        inputClassYear = findViewById(R.id.input_class_year);
        inputSection = findViewById(R.id.input_section);
        inputSemester = findViewById(R.id.input_semester);
        inputAcademicYear = findViewById(R.id.input_academic_year);
        viewSchedulesButton = findViewById(R.id.view_schedules_button);
        scheduleList = findViewById(R.id.schedule_list);

        // Set up the button click listener
        viewSchedulesButton.setOnClickListener(v -> loadStudentSchedules());
    }

    private void loadStudentSchedules() {
        // Retrieve and trim input values
        String department = inputDepartment.getText().toString().trim();
        String classYear = inputClassYear.getText().toString().trim();
        String section = inputSection.getText().toString().trim();
        String semester = inputSemester.getText().toString().trim();
        String academicYear = inputAcademicYear.getText().toString().trim();

        // Validate that none of the fields are empty
        if (department.isEmpty() || classYear.isEmpty() || section.isEmpty() ||
                semester.isEmpty() || academicYear.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Querying schedule for dept='" + department + "', classYear='" + classYear +
                "', section='" + section + "', semester='" + semester + "', year='" + academicYear + "'");

        // Query Firestore "Schedule" collection with the provided filters
        db.collection("Schedule")
                .whereEqualTo("department", department)
                .whereEqualTo("class_year", classYear)
                .whereEqualTo("section", section)
                .whereEqualTo("semester", semester)
                .whereEqualTo("year", academicYear)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    scheduleListData.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(StudentDashboard.this, "No schedules found for your selection", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Query returned no documents.");
                        updateListView();
                        return;
                    }

                    // Process each matching document
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.d(TAG, "Found document: " + document.getId());

                        // Retrieve the "schedule" array (a list of maps)
                        List<?> scheduleEntries = (List<?>) document.get("schedule");
                        if (scheduleEntries != null && !scheduleEntries.isEmpty()) {
                            for (Object obj : scheduleEntries) {
                                if (obj instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, String> scheduleMap = (Map<String, String>) obj;
                                    // Retrieve values with defaults if null
                                    String course = scheduleMap.get("course") != null ? scheduleMap.get("course") : "N/A";
                                    String instructor = scheduleMap.get("instructor") != null ? scheduleMap.get("instructor") : "N/A";
                                    String day = scheduleMap.get("day") != null ? scheduleMap.get("day") : "N/A";
                                    String time = scheduleMap.get("time") != null ? scheduleMap.get("time") : "N/A";

                                    String entry = "Course: " + course +
                                            "\nInstructor: " + instructor +
                                            "\nDay: " + day +
                                            "\nTime: " + time +
                                            "\nSemester: " + semester +
                                            "\nYear: " + academicYear;
                                    scheduleListData.add(entry);
                                }
                            }
                        }
                    }

                    if (scheduleListData.isEmpty()) {
                        Toast.makeText(StudentDashboard.this, "No schedules found for your selection", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No matching schedule entries found in the documents.");
                    }

                    updateListView();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudentDashboard.this, "Failed to load schedules: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading schedules", e);
                });
    }

    private void updateListView() {
        scheduleAdapter = new ArrayAdapter<>(StudentDashboard.this,
                android.R.layout.simple_list_item_1, scheduleListData);
        scheduleList.setAdapter(scheduleAdapter);
    }
}
