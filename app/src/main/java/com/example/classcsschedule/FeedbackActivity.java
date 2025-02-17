package com.example.classcsschedule;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackInput;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize Firebase Firestore and Authentication
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Reference UI elements
        feedbackInput = findViewById(R.id.feedbackInput);
        Button submitButton = findViewById(R.id.submitFeedbackButton);

        // Submit button action
        submitButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedback = feedbackInput.getText().toString().trim();

        if (feedback.isEmpty()) {
            Toast.makeText(FeedbackActivity.this, "Please enter your feedback!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the logged-in user
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(FeedbackActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a feedback object
        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("userId", user.getUid());  // Store user ID
        feedbackData.put("email", user.getEmail()); // Store user email
        feedbackData.put("feedbackText", feedback); // Store the feedback message
        feedbackData.put("timestamp", System.currentTimeMillis()); // Timestamp

        // Save feedback in Firestore under "feedback" collection
        db.collection("feedback")
                .add(feedbackData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FeedbackActivity.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    feedbackInput.setText(""); // Clear input field after submission
                })
                .addOnFailureListener(e ->
                        Toast.makeText(FeedbackActivity.this, "Failed to submit feedback!", Toast.LENGTH_SHORT).show()
                );
    }
}
