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

public class InstructorDashboard extends AppCompatActivity {

    private static final String TAG = "InstructorDeptSchedule";
    private FirebaseFirestore db;
    private EditText inputDepartment;
    private Button viewDeptSchedulesButton;
    private ListView departmentScheduleList;
    private ArrayAdapter<String> adapter;
    private List<String> scheduleDisplayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_dashboard);

        db = FirebaseFirestore.getInstance();

        inputDepartment = findViewById(R.id.input_department);
        viewDeptSchedulesButton = findViewById(R.id.view_dept_schedules_button);
        departmentScheduleList = findViewById(R.id.schedule_list);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleDisplayList);
        departmentScheduleList.setAdapter(adapter);

        viewDeptSchedulesButton.setOnClickListener(v -> loadDepartmentSchedules());
    }

    private void loadDepartmentSchedules() {
        String department = inputDepartment.getText().toString().trim();

        if (department.isEmpty()) {
            Toast.makeText(this, "Please enter a department name", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Fetching schedules for department: '" + department + "'");

        // Query the "Schedule" collection filtering by department.
        db.collection("Schedule")
                .whereEqualTo("department", department)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    scheduleDisplayList.clear();
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(InstructorDashboard.this, "No schedules found for this department", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Query returned no documents.");
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Log.d(TAG, "Found document: " + doc.getId());
                        String dept = doc.getString("department");
                        String sec = doc.getString("section");
                        String yr = doc.getString("year");
                        String sem = doc.getString("semester");
                        String classYear = doc.getString("class_year");

                        if (dept == null) dept = "N/A";
                        if (sec == null) sec = "N/A";
                        if (yr == null) yr = "N/A";
                        if (sem == null) sem = "N/A";
                        if (classYear == null) classYear = "N/A";

                        // Retrieve the schedule array
                        List<Map<String, Object>> scheduleEntries = (List<Map<String, Object>>) doc.get("schedule");
                        StringBuilder scheduleInfo = new StringBuilder();
                        scheduleInfo.append("Department: ").append(dept)
                                .append(" | Section: ").append(sec)
                                .append("\nYear: ").append(yr)
                                .append(" Semester: ").append(sem)
                                .append(" | Class Year: ").append(classYear)
                                .append("\n");

                        if (scheduleEntries != null && !scheduleEntries.isEmpty()) {
                            for (Map<String, Object> entry : scheduleEntries) {
                                String day = entry.get("day") != null ? entry.get("day").toString() : "N/A";
                                String time = entry.get("time") != null ? entry.get("time").toString() : "N/A";
                                String course = entry.get("course") != null ? entry.get("course").toString() : "N/A";
                                String instructor = entry.get("instructor") != null ? entry.get("instructor").toString() : "N/A";

                                scheduleInfo.append(day)
                                        .append(" ").append(time).append(": ")
                                        .append(course).append(" (").append(instructor).append(")\n");
                            }
                        } else {
                            scheduleInfo.append("No schedule details available.\n");
                        }
                        scheduleDisplayList.add(scheduleInfo.toString());
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(InstructorDashboard.this, "Failed to fetch schedules", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching schedules", e);
                });
    }
}
