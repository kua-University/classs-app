package com.example.classcsschedule;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewSchedulesActivity extends AppCompatActivity {

    private static final String TAG = "ViewSchedulesActivity";
    private FirebaseFirestore db;
    private ListView scheduleListView;
    private ArrayAdapter<String> adapter;
    private List<String> scheduleDisplayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedules);

        // Initialize Firestorm
        db = FirebaseFirestore.getInstance();
        // Reference to the ListView from XML
        scheduleListView = findViewById(R.id.schedule_list_view);
        // Initialize the list and adapter
        scheduleDisplayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleDisplayList);
        scheduleListView.setAdapter(adapter);

        // Fetch schedules from Firestorm

        fetchSchedules();
    }

    private void fetchSchedules() {
        db.collection("Schedule")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    scheduleDisplayList.clear();
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(ViewSchedulesActivity.this, "No schedules found", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Query returned no documents.");
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    // Loop through each schedule document
                    for (DocumentSnapshot doc : querySnapshot) {
                        Log.d(TAG, "Found document: " + doc.getId());
                        String department = doc.getString("department");
                        String section = doc.getString("section");
                        String year = doc.getString("year");
                        String semester = doc.getString("semester");
                        String classYear = doc.getString("class_year");

                        // If any field is null, substitute with "N/A"
                        if (department == null) department = "N/A";
                        if (section == null) section = "N/A";
                        if (year == null) year = "N/A";
                        if (semester == null) semester = "N/A";
                        if (classYear == null) classYear = "N/A";

                        // Get the schedule array (list of maps)
                        List<Map<String, Object>> schedule = (List<Map<String, Object>>) doc.get("schedule");
                        StringBuilder scheduleInfo = new StringBuilder();
                        scheduleInfo.append("Department: ").append(department)
                                .append(" | Section: ").append(section)
                                .append("\nYear: ").append(year)
                                .append(" Semester: ").append(semester)
                                .append(" | Class Year: ").append(classYear)
                                .append("\n");

                        if (schedule != null && !schedule.isEmpty()) {
                            // For each schedule entry, append day, time, course, and instructor
                            for (Map<String, Object> entry : schedule) {
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
                        // Add the formatted schedule to our list
                        scheduleDisplayList.add(scheduleInfo.toString());
                    }
                    // Notify the adapter that data has changed so it refreshes the ListView
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewSchedulesActivity.this, "Failed to fetch schedules", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching schedules", e);
                });
    }
}
