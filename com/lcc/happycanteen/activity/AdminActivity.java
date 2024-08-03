package com.lcc.happycanteen.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lcc.happycanteen.R;

import java.io.IOException;

public class AdminActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView adminImageView;
    private TextView adminNameTextView;
    private Button changeImageButton, menuLookupButton, orderLookupButton;
    private ImageButton menuImageButton;
    private ImageButton customerImageButton;
    private Button logoutButton;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        adminImageView = findViewById(R.id.imageButton);
        adminNameTextView = findViewById(R.id.nameEditText);
        changeImageButton = findViewById(R.id.changeImageButton);
        menuImageButton = findViewById(R.id.menuImageButton);
        customerImageButton = findViewById(R.id.customerImageButton);
        menuLookupButton = findViewById(R.id.menuLookupButton);
        orderLookupButton = findViewById(R.id.orderLookupButton);
        logoutButton = findViewById(R.id.logoutButton);

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        menuImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MenuEditingActivity.class);
                startActivity(intent);
            }
        });

        customerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, CustomerEditActivity.class);
                startActivity(intent);
            }
        });

        menuLookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        orderLookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        // Retrieve the admins name from the Intent and set it as the text of adminNameTextView
        String name = getIntent().getStringExtra("name");
        adminNameTextView.setText(name);

        // Get the document from the Firestore database
        mFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the field from the document
                        String fieldValue = document.getString("name");

                        // Set the field to the TextView
                        adminNameTextView.setText(fieldValue);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                adminImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}