package com.example.classcsschedule;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratescheduleActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView finishText;
    private String adminId = "";

    // Remove the hardcoded year & semester; use EditText fields instead.
    private EditText inputYear, inputSemester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_schedule);

        db = FirebaseFirestore.getInstance();
        finishText = findViewById(R.id.finish_text);

        // Get references to the EditText fields for year and semester
        inputYear = findViewById(R.id.input_year);
        inputSemester = findViewById(R.id.input_semester);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        adminId = sharedPreferences.getString("adminId", "");

        // Buttons for different class years
        Button firstYearButton = findViewById(R.id.first_button);
        Button secondYearButton = findViewById(R.id.second_button);
        Button thirdYearButton = findViewById(R.id.third_button);
        Button fourthYearButton = findViewById(R.id.fourth_button);

        firstYearButton.setOnClickListener(v -> generateScheduleForClassYear("First"));
        secondYearButton.setOnClickListener(v -> generateScheduleForClassYear("Second"));
        thirdYearButton.setOnClickListener(v -> generateScheduleForClassYear("Third"));
        fourthYearButton.setOnClickListener(v -> generateScheduleForClassYear("Fourth"));
    }

    @SuppressLint("SetTextI18n")
    private void generateScheduleForClassYear(String classYear) {
        // Get the user-entered year and semester
        String year = inputYear.getText().toString().trim();
        String semester = inputSemester.getText().toString().trim();

        // Validate user input
        if (year.isEmpty() || semester.isEmpty()) {
            finishText.setText("Please enter both year and semester first!");
            return;
        }

        db.collection("DepartmentCourses")
                .whereEqualTo("assigned_by", adminId)
                .whereEqualTo("year", year)
                .whereEqualTo("semester", semester)
                .whereEqualTo("class_year", classYear)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        finishText.setText("No courses assigned for " + classYear + " Year");
                        return;
                    }

                    boolean hasCourses = false;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String departmentName = document.getId();

                        // Retrieve number_of_sections as a Long instead of a String
                        Long numSectionsLong = document.getLong("number_of_sections");
                        List<Map<String, String>> courses = (List<Map<String, String>>) document.get("courses");

                        if (numSectionsLong == null || courses == null || courses.isEmpty()) {
                            continue;
                        }

                        int numSections = numSectionsLong.intValue();
                        generateDepartmentSchedule(departmentName, numSections, courses, classYear, semester, year);
                        hasCourses = true;
                    }

                    if (!hasCourses) {
                        finishText.setText("No courses assigned for " + classYear + " Year");
                    } else {
                        finishText.setText("Schedule Generation Completed for " + classYear + " Year");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Schedule", "Error fetching department courses", e);
                    Toast.makeText(this, "Error fetching department courses", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("SetTextI18n")
    private void generateDepartmentSchedule(String department, int numSections, List<Map<String, String>> courses,
                                            String classYear, String semester, String year) {
        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        List<String> timeSlots = Arrays.asList("3:00 - 4:30", "4:30 - 6:00", "8:00 - 10:00");

        Map<String, List<String>> instructorSchedule = new HashMap<>();

        for (int section = 1; section <= numSections; section++) {
            List<Map<String, String>> sectionSchedule = new ArrayList<>();
            Map<String, Integer> courseFrequency = new HashMap<>();

            for (String day : days) {
                for (String time : timeSlots) {
                    Map<String, String> selectedCourse = null;

                    for (Map<String, String> course : courses) {
                        String courseName = course.get("course");
                        String instructor = course.get("instructor");

                        if (courseFrequency.getOrDefault(courseName, 0) >= 2) continue;
                        if (hasInstructorConflict(instructorSchedule, instructor, day, time)) continue;
                        if (alreadyScheduledToday(sectionSchedule, day, courseName)) continue;

                        selectedCourse = course;
                        courseFrequency.put(courseName, courseFrequency.getOrDefault(courseName, 0) + 1);
                        break;
                    }

                    if (selectedCourse != null) {
                        String instructor = selectedCourse.get("instructor");
                        instructorSchedule.putIfAbsent(instructor, new ArrayList<>());
                        instructorSchedule.get(instructor).add(day + " " + time);

                        sectionSchedule.add(Map.of(
                                "day", day,
                                "time", time,
                                "course", selectedCourse.get("course"),
                                "instructor", instructor
                        ));
                    }
                }
            }

            saveScheduleToFirestore(department, section, sectionSchedule, classYear, semester, year);
            finishText.setText("Schedule Generation Completed for " + classYear + " Year");
        }
    }

    private boolean hasInstructorConflict(Map<String, List<String>> instructorSchedule, String instructor, String day, String time) {
        return instructorSchedule.containsKey(instructor) && instructorSchedule.get(instructor).contains(day + " " + time);
    }

    private boolean alreadyScheduledToday(List<Map<String, String>> sectionSchedule, String day, String course) {
        for (Map<String, String> entry : sectionSchedule) {
            if (entry.get("day").equals(day) && entry.get("course").equals(course)) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    private void saveScheduleToFirestore(String department, int section, List<Map<String, String>> schedule,
                                         String classYear, String semester, String year) {
        db.collection("Schedule")
                .whereEqualTo("department", department)
                .whereEqualTo("section", String.valueOf(section))
                .whereEqualTo("semester", semester)
                .whereEqualTo("year", year)
                .whereEqualTo("class_year", classYear)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        finishText.setText("Schedule already generated for " + classYear + " Year");
                        Log.d("Schedule", "Schedule already exists for " + classYear + " Year, "
                                + department + " Section " + section);
                        return;
                    }

                    Map<String, Object> scheduleData = new HashMap<>();
                    scheduleData.put("department", department);
                    scheduleData.put("section", String.valueOf(section));
                    scheduleData.put("semester", semester);
                    scheduleData.put("year", year);
                    scheduleData.put("class_year", classYear);
                    scheduleData.put("schedule", schedule);
                    // Optional: Add a status field for backend clarity
                    scheduleData.put("status", "generated successfully");

                    db.collection("Schedule")
                            .add(scheduleData)
                            .addOnSuccessListener(docRef -> {
                                Log.d("Schedule", "Schedule added for " + classYear + " Year - "
                                        + department + " Section " + section);
                                finishText.setText("Schedule generated successfully for " + classYear + " Year!");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Schedule", "Failed to save schedule", e);
                                finishText.setText("Failed to generate schedule for " + classYear + " Year");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Schedule", "Error checking existing schedules", e);
                    finishText.setText("Error checking existing schedules");
                });
    }
}
