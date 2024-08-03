package com.lcc.happycanteen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lcc.happycanteen.R;
import com.lcc.happycanteen.model.OrderItem;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItem> orderItems;
    private FirebaseFirestore mFirestore;
    private Context context;

    public OrderItemAdapter(Context context, List<OrderItem> orderItemList) {
        this.context = context;
        this.orderItems = orderItemList;
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {

        OrderItem orderItem = orderItems.get(position);
        holder.foodItemNameTextView.setText(orderItem.getFoodItemName());
        holder.orderNumberTextView.setText(String.valueOf(orderItem.getOrderNumber()));
        holder.phoneNumberTextView.setText(orderItem.getPhoneNumber());
        holder.totalPriceTextView.setText(String.valueOf(orderItem.getPrice()));

    holder.deleteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                OrderItem currentItem = orderItems.get(currentPosition);
                mFirestore.collection("orders").document(currentItem.getId()).delete();
                orderItems.remove(currentPosition);
                notifyItemRemoved(currentPosition);
            }
        }
    });
}

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumberTextView;
        TextView phoneNumberTextView;
        TextView foodItemNameTextView;
        TextView totalPriceTextView;
        ImageButton cancelButton;
        ImageButton deleteButton;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumberTextView = itemView.findViewById(R.id.orderNumberTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);
            foodItemNameTextView = itemView.findViewById(R.id.foodNameTextView);
            totalPriceTextView = itemView.findViewById(R.id.foodPriceTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}