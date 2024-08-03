package com.lcc.happycanteen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lcc.happycanteen.R;
import com.lcc.happycanteen.model.MenuItem;

import java.util.List;

public class CustomerActivity extends AppCompatActivity {

    private TextView nameEditText, phoneNumberEditText, emailEditText;
    private Button logoutButton, orderButton, orderListButton, menuButton;
    private SearchView searchView;
    private RecyclerView menuRecyclerView;
//    private MenuItemAdapter menuItemAdapter;
    private FirebaseFirestore mFirestore;
    private List<MenuItem> menuItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        mFirestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        logoutButton = findViewById(R.id.logoutButton);
//        orderButton = findViewById(R.id.orderButton);
        orderListButton = findViewById(R.id.orderListButton);
        menuButton = findViewById(R.id.menuButton);
//        searchBar = findViewById(R.id.searchBar);
//        menuRecyclerView = findViewById(R.id.menuRecyclerView);

//        menuItemList = new ArrayList<>();
//        menuItemAdapter = new MenuItemAdapter(menuItemList);
//        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        menuRecyclerView.setAdapter(menuItemAdapter);

        loadUserData();
//        loadMenuItems();

       /* searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                menuItemAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/


        //ORDER BUTTON IS COMMENTED OUT
      /*  orderButton.setOnClickListener(v -> {
            // Get the selected food item
            MenuItem selectedFoodItem = menuItemAdapter.getSelectedFoodItem();

            if (selectedFoodItem != null) {
                // Create a new order
                Map<String, Object> order = new HashMap<>();
                String userID = phoneNumberEditText.getText().toString();
                order.put("userID", userID); // Assuming the user's ID is stored in a variable called "userID"
                order.put("foodItemID", selectedFoodItem.getId());
                order.put("quantity", 1); // Replace with actual quantity
                order.put("timestamp", com.google.firebase.Timestamp.now());

                // Add the order to the Firestore database
                mFirestore.collection("orders").add(order)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(CustomerActivity.this, "OrderItem placed successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CustomerActivity.this, "Error placing order", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(CustomerActivity.this, "No food item selected", Toast.LENGTH_SHORT).show();
            }
        });
*/
        orderListButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, OrderActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, MenuActivity.class);
            startActivity(intent);
        });

    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            mFirestore.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            nameEditText.setText(document.getString("name"));
                            phoneNumberEditText.setText(document.getString("phoneNumber"));
                            emailEditText.setText(document.getString("email"));
                        } else {
                            Toast.makeText(CustomerActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CustomerActivity.this, "Error getting documents: ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(CustomerActivity.this, "No user signed in", Toast.LENGTH_SHORT).show();
        }
    }

    //LOAD MENU ITEMS IS COMMENTED OUT
    /*private void loadMenuItems() {
        mFirestore.collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        MenuItem foodItem = document.toObject(MenuItem.class);
                        menuItemList.add(foodItem);
                    }
                    menuItemAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CustomerActivity.this, "Error getting documents: ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
}