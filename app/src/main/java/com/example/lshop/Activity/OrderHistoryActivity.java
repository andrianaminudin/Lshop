package com.example.lshop.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lshop.Adapter.OrderHistoryAdapter;
import com.example.lshop.Domain.OrderModel;
import com.example.lshop.R;
import com.google.gson.Gson;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<OrderModel> orderList;
    private FirebaseDatabase database;
    private String username;
    private TextView noOrdersTextView; // Declare the TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        bottomNavigation();

        recyclerView = findViewById(R.id.historyRecyclerView);
        orderList = new ArrayList<>();
        noOrdersTextView = findViewById(R.id.noOrdersTextView); // Initialize the TextView

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = prefs.getString("username", "");

        database = FirebaseDatabase.getInstance();
        loadOrderHistory();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(orderList, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadOrderHistory() {
        DatabaseReference userOrderRef = database.getReference("Orders").child(username);
        userOrderRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    OrderModel order = snapshot.getValue(OrderModel.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                // Notify adapter and update UI
                adapter.notifyDataSetChanged();
                updateUI();
            } else {
                Toast.makeText(this, "Failed to load order history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (orderList.isEmpty()) {
            // If no orders, show the "No orders yet" message
            noOrdersTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // If orders exist, hide the "No orders yet" message and show the RecyclerView
            noOrdersTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void bottomNavigation() {
        ChipNavigationBar bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setItemSelected(R.id.cart, true);

        bottomNavigation.setOnItemSelectedListener(i -> {
            if (i == R.id.home) {
                startActivity(new Intent(OrderHistoryActivity.this, MainActivity.class));
            } else if (i == R.id.profile) {
                startActivity(new Intent(OrderHistoryActivity.this, ProfileActivity.class));
            } else if (i == R.id.about) {
                startActivity(new Intent(OrderHistoryActivity.this, AboutActivity.class));
            } else if (i == R.id.cart) {

            }
        });
    }
}
