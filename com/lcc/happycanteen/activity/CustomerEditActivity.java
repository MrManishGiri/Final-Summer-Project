package com.lcc.happycanteen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lcc.happycanteen.R;

public class CustomerEditActivity extends AppCompatActivity {

    private EditText numberEditText;
    private EditText emailDeleteEditText;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_edit);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get references to the views
        numberEditText = findViewById(R.id.numberEditText);
        emailDeleteEditText = findViewById(R.id.emailDeleteEditText);
        Button removeCustomerButton = findViewById(R.id.removeCustomerButton);

        // Set click listener on the button
        removeCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCustomer();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void removeCustomer() {
        String phoneNumber = numberEditText.getText().toString();
        String email = emailDeleteEditText.getText().toString();

        // Retrieve the document first to check the role
        mFirestore.collection("users").document(phoneNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String role = documentSnapshot.getString("role");
                        if ("admin".equals(role)) {
                            Toast.makeText(CustomerEditActivity.this, "It is not possible to delete this account", Toast.LENGTH_SHORT).show();
                        } else {
                            // If role is not admin, delete the document
                            mFirestore.collection("users").document(phoneNumber)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CustomerEditActivity.this, "Customer removed successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CustomerEditActivity.this, "Error removing customer", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            mFirestore.collection("users").document(email)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CustomerEditActivity.this, "Customer removed successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CustomerEditActivity.this, "Error removing customer", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CustomerEditActivity.this, "Error retrieving customer", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}