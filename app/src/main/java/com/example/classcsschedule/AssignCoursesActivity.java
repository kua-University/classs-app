package com.example.classcsschedule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignCoursesActivity extends AppCompatActivity {

    private EditText inputFormType, inputSemester, inputYear, inputNumberOfSection, inputClassYear;
    private LinearLayout courseContainer;
    private Button btnAddCourse, btnAssign;
    private FirebaseFirestore db;
    // Not used directly but kept for consistency with your original code
    private List<Map<String, Object>> courseList = new ArrayList<>();
    private int courseCount = 1;
    private String adminId = "";

    // Lists to hold Firestore data for courses and instructors
    private List<String> courseOptions = new ArrayList<>();
    private List<String> instructorOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_courses);

        inputFormType = findViewById(R.id.input_form_type);
        inputSemester = findViewById(R.id.input_semester);
        inputClassYear = findViewById(R.id.class_year);
        inputYear = findViewById(R.id.input_year);
        inputNumberOfSection = findViewById(R.id.input_number_of_section);
        courseContainer = findViewById(R.id.course_container);
        btnAddCourse = findViewById(R.id.btn_add_course);
        btnAssign = findViewById(R.id.btn_assign);
        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        adminId = sharedPreferences.getString("adminId", "");

        // Load data for the spinners from Firestore
        loadCourses();
        loadInstructors();

        btnAddCourse.setOnClickListener(v -> addCourseField());
        btnAssign.setOnClickListener(v -> assignCourse());
    }

    // Query the "courses" collection and load course titles into courseOptions.
    private void loadCourses() {
        db.collection("courses").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    courseOptions.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        // Adjust the field name if needed
                        String courseTitle = doc.getString("courseTitle");
                        if (courseTitle != null) {
                            courseOptions.add(courseTitle);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(AssignCoursesActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show());
    }

    // Query the "Users" collection filtering for Instructors and load their names.
    private void loadInstructors() {
        db.collection("Users").whereEqualTo("user_type", "Instructor").get()
                .addOnSuccessListener(querySnapshot -> {
                    instructorOptions.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        // Adjust the field name if needed
                        String instructorName = doc.getString("name");
                        if (instructorName != null) {
                            instructorOptions.add(instructorName);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(AssignCoursesActivity.this, "Failed to load instructors", Toast.LENGTH_SHORT).show());
    }

    // Replace the EditTexts with Spinners populated by Firestore data.
    private void addCourseField() {
        LinearLayout courseLayout = new LinearLayout(this);
        courseLayout.setOrientation(LinearLayout.VERTICAL);
        courseLayout.setPadding(0, 16, 0, 16);

        // Spinner for courses
        Spinner courseSpinner = new Spinner(this);
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, courseOptions);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);
        courseLayout.addView(courseSpinner);

        // Spinner for instructors
        Spinner instructorSpinner = new Spinner(this);
        ArrayAdapter<String> instructorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, instructorOptions);
        instructorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorSpinner.setAdapter(instructorAdapter);
        courseLayout.addView(instructorSpinner);

        courseContainer.addView(courseLayout);
        courseList.add(new HashMap<>());
        courseCount++;
    }

    // Collect data from spinners and save the department assignment to Firestore.
    private void assignCourse() {
        String department = inputFormType.getText().toString().trim();
        String classYear = inputClassYear.getText().toString().trim();
        String year = inputYear.getText().toString().trim();
        String semester = inputSemester.getText().toString().trim();
        String numberOfSections = inputNumberOfSection.getText().toString().trim();

        if (department.isEmpty() || year.isEmpty() || semester.isEmpty() || numberOfSections.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!numberOfSections.matches("\\d+")) {
            Toast.makeText(this, "Enter a valid number of sections!", Toast.LENGTH_SHORT).show();
            return;
        }
        int numSections = Integer.parseInt(numberOfSections);

        // Validate and collect selected values from each spinner.
        List<Map<String, String>> courses = new ArrayList<>();
        for (int i = 0; i < courseContainer.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) courseContainer.getChildAt(i);
            Spinner courseSpinner = (Spinner) layout.getChildAt(0);
            Spinner instructorSpinner = (Spinner) layout.getChildAt(1);

            String courseTitle = (String) courseSpinner.getSelectedItem();
            String instructorName = (String) instructorSpinner.getSelectedItem();

            if (courseTitle == null || instructorName == null || courseTitle.isEmpty() || instructorName.isEmpty()) {
                Toast.makeText(this, "Please select a course and instructor for each entry!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> course = new HashMap<>();
            course.put("course", courseTitle);
            course.put("instructor", instructorName);
            courses.add(course);
        }

        if (courses.isEmpty()) {
            Toast.makeText(this, "At least one course is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data to be saved
        Map<String, Object> departmentData = new HashMap<>();
        departmentData.put("year", year);
        departmentData.put("class_year", classYear);
        departmentData.put("semester", semester);
        departmentData.put("number_of_sections", numSections);
        departmentData.put("assigned_by", adminId);
        departmentData.put("courses", courses);

        db.collection("DepartmentCourses").document(department)
                .set(departmentData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Courses assigned successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to assign courses", Toast.LENGTH_SHORT).show());
    }
}
