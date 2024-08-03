package com.lcc.happycanteen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lcc.happycanteen.R;

import java.util.HashMap;
import java.util.Map;

public class AdminLoginActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText;
    Button loginButton, signinButton;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
//        signinButton = findViewById(R.id.signinButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
       /* signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });*/
    }


    private void signin() {

        String name = nameEditText.getText().toString().trim();
        Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        String emailPattern = "^[a-zA-Z0-9]+@lcc\\.edu\\.np$";


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Email should be in the format: name.facultybatch@lcc.edu.np", Toast.LENGTH_SHORT).show();
            return;
        }

       /* //register the user in firebase
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminLoginActivity.this, "Admin registered successfully", Toast.LENGTH_SHORT).show();
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference documentReference = mFirestore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);
                user.put("role", "admin");
                user.put("password", password);
                documentReference.set(user).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: " +
                        "Admin profile is created for " + userID));

                startActivity(new Intent(AdminLoginActivity.this, AdminLoginActivity.class));
            } else {
                Toast.makeText(AdminLoginActivity.this, "Failed to register user" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    private void login() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        String emailPattern = "^[a-zA-Z0-9]+@lcc\\.edu\\.np$";

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Email should be in the format: emailprefix@lcc.edu.np", Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminLoginActivity.this, "Welcome to Admin Dashboard!", Toast.LENGTH_SHORT).show();
                // Navigate to respective activity based on user role
                DocumentReference documentReference = mFirestore.collection("users").document(mAuth.getCurrentUser().getUid());
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    String role = documentSnapshot.getString("role");
                    Intent intent;
                    if ("admin".equals(role)) {
                        intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                });
            } else {
                Toast.makeText(AdminLoginActivity.this, "Failed to log in: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}