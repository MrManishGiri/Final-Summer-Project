package com.lcc.happycanteen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lcc.happycanteen.R;
import com.lcc.happycanteen.adapters.MenuItemAdapter;
import com.lcc.happycanteen.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuItemAdapter menuItemAdapter;
    private FirebaseFirestore mFirestore;

    private List<MenuItem> menuItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuItemList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        menuItemAdapter = new MenuItemAdapter(this, menuItemList);
        recyclerView.setAdapter(menuItemAdapter);

        loadMenuItems();
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
                            menuItemAdapter = new MenuItemAdapter(MenuActivity.this, menuItems);
                            recyclerView.setAdapter(menuItemAdapter);
                        } else {
                            Toast.makeText(MenuActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}