package com.lcc.happycanteen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lcc.happycanteen.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText nameEditText, batchEditText, facultyEditText, phoneNumberEditText, emailEditText, passwordEditText;
    Button signUpButton;
    TextView loginText;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        batchEditText = findViewById(R.id.batchEditText);
        facultyEditText = findViewById(R.id.facultyEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginText = findViewById(R.id.loginText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signUp() {
        String name = nameEditText.getText().toString().trim();
        String batch = batchEditText.getText().toString().trim();
        String faculty = facultyEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        String formattedName = name.replace(" ", "").toLowerCase();
        String emailPrefix = formattedName + "." + faculty.toLowerCase() + batch;
        String emailPattern = "^" + emailPrefix + "@lcc\\.edu\\.np$";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(batch) || TextUtils.isEmpty(faculty) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isDigitsOnly(batch) || batch.length() != 4) {
            Toast.makeText(this, "Batch should be numeric and 4 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!faculty.equals("BIM") && !faculty.equals("BCA") && !faculty.equals("BCSIT")) {
            Toast.makeText(this, "Faculty should be either BIM, BCA, or BCSIT", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phoneNumber.startsWith("+977")) {
            Toast.makeText(this, "Phone number should start with +977", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!faculty.matches("[a-zA-Z]+")) {
            Toast.makeText(this, "Faculty should contain only alphabets", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Email should be in the format: name.facultybatch@lcc.edu.np", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phoneNumber.length() != 14) {
            Toast.makeText(this, "Phone number should be 10 digits long", Toast.LENGTH_SHORT).show();
            return;
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int batchYear = Integer.parseInt(batch);
        if (batchYear > currentYear) {
            Toast.makeText(this, "Batch year should be either equal to the current year or less than the current year", Toast.LENGTH_SHORT).show();
            return;
        }

        mFirestore.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(SignUpActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            mFirestore.collection("users")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            if (!task1.getResult().isEmpty()) {
                                                Toast.makeText(SignUpActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                registerUser(email, password, name, batch, faculty, phoneNumber);
                                            }
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task1.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void registerUser(String email, String password, String name, String batch, String faculty, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference documentReference = mFirestore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("batch", batch);
                user.put("faculty", faculty);
                user.put("phoneNumber", phoneNumber);
                user.put("role", "customer");
                user.put("email", email);
                user.put("password", password);
                documentReference.set(user).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: user profile is created for " + userID));

                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to register user" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}