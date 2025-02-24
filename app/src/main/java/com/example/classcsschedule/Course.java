package com.example.classcsschedule;

public class Course {
    private String title;
    private String instructor;
    private String department; // Optional field if you want to filter courses by department

    // Empty constructor required for Firestore deserialization
    public Course() { }

    public Course(String title, String instructor, String department) {
        this.title = title;
        this.instructor = instructor;
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getDepartment() {
        return department;
    }
}
