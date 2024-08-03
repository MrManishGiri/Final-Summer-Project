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
import com.lcc.happycanteen.adapters.OrderItemAdapter;
import com.lcc.happycanteen.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderItemAdapter orderItemAdapter;
    private FirebaseFirestore mFirestore;

    private List<OrderItem> orderItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        orderItemAdapter = new OrderItemAdapter(this, orderItemList);
        recyclerView.setAdapter(orderItemAdapter);

        loadOrderItems();
    }

   private void loadOrderItems() {
    mFirestore.collection("orders")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<OrderItem> orderItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderItem orderItem = document.toObject(OrderItem.class);
                            orderItem.setFoodItemName(document.getString("foodItemName"));

                            orderItems.add(orderItem);
                        }
                        orderItemAdapter = new OrderItemAdapter(OrderActivity.this, orderItems);
                        recyclerView.setAdapter(orderItemAdapter);
                    } else {
                        Toast.makeText(OrderActivity.this, "Failed to load order items", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
}