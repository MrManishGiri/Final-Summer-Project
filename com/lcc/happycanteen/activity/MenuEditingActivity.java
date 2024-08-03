package com.lcc.happycanteen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lcc.happycanteen.R;
import com.lcc.happycanteen.adapters.MenuItemAdapter;
import com.lcc.happycanteen.model.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuEditingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuItemAdapter menuItemAdapter;
    private static final int PICK_IMAGE = 1;
    private ImageButton imageButton;
    private EditText foodNameEditText, priceEditText, foodNameDelEditText;
    private Button addItemButton, removeItemButton;
    private FirebaseStorage storage;
    FirebaseFirestore mFirestore;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_editing);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imageButton = findViewById(R.id.imageButton);
        foodNameEditText = findViewById(R.id.foodNameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        foodNameDelEditText = findViewById(R.id.foodNameDelEditText);
        addItemButton = findViewById(R.id.addItemButton);
        removeItemButton = findViewById(R.id.removeItemButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = foodNameEditText.getText().toString();
                String price = priceEditText.getText().toString();

                if (foodName.isEmpty() || price.isEmpty() || imageUri == null) {
                    Toast.makeText(MenuEditingActivity.this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isNumeric(price) || Double.parseDouble(price) <= 0) {
                    Toast.makeText(MenuEditingActivity.this, "Price should be a positive numeric value", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImageAndAddItem(foodName, Double.parseDouble(price));
            }
        });

        removeItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = foodNameDelEditText.getText().toString();

                if (foodName.isEmpty()) {
                    Toast.makeText(MenuEditingActivity.this, "Please enter the food name", Toast.LENGTH_SHORT).show();
                    return;
                }

                removeItem(foodName);
            }
        });

        loadMenuItems();
    }

    private void uploadImageAndAddItem(String foodName, double price) {
        StorageReference ref = storage.getReference().child("images/" + foodName);

        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                addItem(foodName, price, uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuEditingActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addItem(String foodName, double price, String imageUrl) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", foodName);
        item.put("price", price);
        item.put("image", imageUrl);

        mFirestore.collection("menu")
                .document(foodName)
                .set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MenuEditingActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuEditingActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeItem(String foodName) {
        mFirestore.collection("menu")
                .document(foodName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MenuEditingActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuEditingActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageButton.setImageURI(imageUri);
        }
    }

    private void loadMenuItems() {
    mFirestore.collection("menu")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<MenuItem> menuItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MenuItem menuItem = document.toObject(MenuItem.class);
                            menuItems.add(menuItem);
                        }
                        menuItemAdapter = new MenuItemAdapter(MenuEditingActivity.this, menuItems);
                        recyclerView.setAdapter(menuItemAdapter);
                    } else {
                        Toast.makeText(MenuEditingActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}


}
