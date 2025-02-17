package com.example.classcsschedule;

import static android.os.Build.VERSION_CODES.R;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Test Firestore by adding data
        Map<String, Object> testData = new HashMap<>();
        testData.put("testKey", "testValue");

        db.collection("testCollection")
                .add(testData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreTest", "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreTest", "Error adding document", e);
                });
    }
}
