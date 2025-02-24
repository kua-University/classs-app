package com.example.classcsschedule;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminManageUsersActivity extends AppCompatActivity {
    // UI elements for "Create Department Head" section
    private EditText etUsername, etPassword, etEmail, etDepartment;
    private Button btnCreateDepartmentHead, btnGoBack;

    // UI elements for "Verify Users" section
    private ListView lvUsers;
    private ArrayList<User> userList;
    private UserAdapter userAdapter;

    // Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_users);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views for the create department head section
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etDepartment = findViewById(R.id.etDepartment);
        btnCreateDepartmentHead = findViewById(R.id.btnCreateDepartmentHead);

        // Set click listener to create a new Department Head user
        btnCreateDepartmentHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDepartmentHead();
            }
        });

        // Initialize "Go Back" button and set click listener
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Finish current activity and go back
            }
        });

        // Initialize ListView and adapter for the verify users section
        lvUsers = findViewById(R.id.lvUsers);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        lvUsers.setAdapter(userAdapter);

        // Fetch users from Firestore
        retrieveUsers();
    }

    // Creates a Department Head user in Firestore using input from EditTexts
    private void createDepartmentHead() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || department.isEmpty()) {
            Toast.makeText(AdminManageUsersActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }




        // Prepare user data to store in Firestore
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("password", password);
        userMap.put("email", email);
        userMap.put("department", department);
        userMap.put("user_type", "School Head");

        db.collection("Users")
                .add(userMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AdminManageUsersActivity.this, "Department Head created", Toast.LENGTH_SHORT).show();
                        // Optionally, clear input fields after creation
                        etUsername.setText("");
                        etPassword.setText("");
                        etEmail.setText("");
                        etDepartment.setText("");
                        // Refresh the user list to include the new user
                        retrieveUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminManageUsersActivity.this, "Failed to create department head", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Retrieves the list of users from Firestore and updates the ListView
    private void retrieveUsers() {
        db.collection("Users")
                .whereEqualTo("role", "pending")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert the document into a User object
                                User user = document.toObject(User.class);
                                // Store the Firestore document ID for updating purposes
                                user.setDocId(document.getId());
                                // Also retrieve the "userId" field if needed for display
                                user.setUserId(document.getString("userId"));
                                userList.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(AdminManageUsersActivity.this, "Error getting users", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    // Updates the user's role in Firestore (either to "Student" or "Instructor")
    public void assignRole(String docId, final String role) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("user_type", role);

        db.collection("Users").document(docId)
                .update(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminManageUsersActivity.this, "User role updated to " + role, Toast.LENGTH_SHORT).show();
                        // Refresh the user list to reflect changes
                        retrieveUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminManageUsersActivity.this, "Error updating role", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}