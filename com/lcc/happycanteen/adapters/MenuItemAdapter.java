package com.lcc.happycanteen.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lcc.happycanteen.R;
import com.lcc.happycanteen.model.MenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class   MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

    private List<MenuItem> menuItems;
    private FirebaseFirestore mFirestore;
    private Context context;

    public MenuItemAdapter(Context context, List<MenuItem> menuItemList) {
        this.context = context;
        this.menuItems = menuItemList;
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.foodNameTextView.setText(menuItem.getName());
        holder.foodPriceTextView.setText(String.valueOf(menuItem.getPrice()));
        Glide.with(context).load(menuItem.getImage()).into(holder.foodImageView);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            mFirestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String role = documentSnapshot.getString("role");
                        if ("admin".equals(role)) {
                            holder.deleteButton.setVisibility(View.VISIBLE);
                            holder.cancelButton.setVisibility(View.GONE);
                        } else {
                            holder.deleteButton.setVisibility(View.GONE);
                            holder.cancelButton.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to get user role", Toast.LENGTH_SHORT).show();
                    }
                });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
        }

        holder.orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    mFirestore.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String customerName = documentSnapshot.getString("name");

                                Long orderNumberLong = System.currentTimeMillis();
                                String orderNumberString = Long.toString(orderNumberLong);

                                Map<String, Object> orderItem = new HashMap<>();
                                orderItem.put("orderNumber", System.currentTimeMillis());
                                orderItem.put("customerName", customerName);
                                orderItem.put("foodItemName", menuItem.getName());
                                orderItem.put("totalPrice", menuItem.getPrice());

                                mFirestore.collection("orders")
                                    .add(orderItem)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed to place order", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to get customer name", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    Toast.makeText(context, "Please sign in to place an order", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    MenuItem currentMenuItem = menuItems.get(currentPosition);
                    mFirestore.collection("orders").document(currentMenuItem.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                                menuItems.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to delete order", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    MenuItem currentMenuItem = menuItems.get(currentPosition);
                    mFirestore.collection("orders").document(currentMenuItem.getId())
                        .update("status", "cancelled")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                                currentMenuItem.setStatus("cancelled");
                                notifyItemChanged(currentPosition);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });

        if ("cancelled".equals(menuItem.getStatus())) {
            holder.statusTextView.setText("Cancelled");
            holder.statusTextView.setTextColor(Color.RED);
            holder.statusTextView.setVisibility(View.VISIBLE);
        } else {
            holder.statusTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

   static class MenuItemViewHolder extends RecyclerView.ViewHolder {
    ImageView foodImageView;
    TextView foodNameTextView;
    TextView foodPriceTextView;
    TextView statusTextView;
    ImageButton orderButton;
    ImageButton deleteButton;
    ImageButton cancelButton;

    public MenuItemViewHolder(@NonNull View itemView) {
        super(itemView);
        foodImageView = itemView.findViewById(R.id.foodImageView);
        foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
        foodPriceTextView = itemView.findViewById(R.id.foodPriceTextView);
        statusTextView = itemView.findViewById(R.id.statusTextView);
        orderButton = itemView.findViewById(R.id.orderButton);
        deleteButton = itemView.findViewById(R.id.deleteButton);
        cancelButton = itemView.findViewById(R.id.cancelButton);
    }
}
}