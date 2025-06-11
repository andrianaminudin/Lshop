package com.example.lshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lshop.Activity.InvoiceActivity;
import com.example.lshop.Domain.OrderModel;
import com.example.lshop.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<OrderModel> orderList;
    private Context context;

    public OrderHistoryAdapter(List<OrderModel> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;

        // Sort the orders by timestamp, newest first (descending order)
        Collections.sort(this.orderList, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        holder.bind(order);

        holder.itemView.setOnClickListener(v -> {
            // Pass the entire OrderModel to InvoiceActivity
            Intent intent = new Intent(context, InvoiceActivity.class);
            intent.putExtra("orderData", order);  // Send the complete order object
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderDateTextView;
        private TextView orderTotalTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            orderTotalTextView = itemView.findViewById(R.id.orderTotalTextView);
        }

        public void bind(OrderModel order) {
            // Format timestamp to date
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            String date = sdf.format(new Date(order.getTimestamp()));
            orderDateTextView.setText(date);

            // Set total price
            orderTotalTextView.setText(String.format("$%.2f", order.getTotal()));
        }
    }
}
