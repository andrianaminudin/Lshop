package com.example.lshop.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lshop.Adapter.InvoiceAdapter;
import com.example.lshop.Domain.ItemModel;
import com.example.lshop.Domain.OrderModel;
import com.example.lshop.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    private TextView nameTxt, phoneTxt, addressTxt, itemTotalTxt, taxTxt, deliveryTxt, totalTxt;
    private TextView orderDateTxt;
    private RecyclerView recyclerView;
    private ArrayList<ItemModel> cartItems;
    private double tax;
    private double delivery = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        initViews();
        loadProfileData();

        // Ambil data pesanan dari Intent
        OrderModel order = (OrderModel) getIntent().getSerializableExtra("orderData");
        if (order != null && order.getItems() != null) {
            loadCartData(order);
            setupCartList();
            calculateTotals();

            // Format dan tampilkan tanggal order
            long timestamp = order.getTimestamp();
            String formattedDate = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                    .format(new Date(timestamp));
            orderDateTxt.setText(formattedDate);
        } else {
            Toast.makeText(this, "Data pesanan tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        nameTxt = findViewById(R.id.nameTxt);
        phoneTxt = findViewById(R.id.phoneTxt);
        addressTxt = findViewById(R.id.addressTxt);
        itemTotalTxt = findViewById(R.id.itemTotalTxt);
        taxTxt = findViewById(R.id.taxTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        orderDateTxt = findViewById(R.id.orderDateTxt);
        recyclerView = findViewById(R.id.invoiceRecyclerView);
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("firstName", "") + " " + prefs.getString("lastName", "");
        String phone = prefs.getString("phoneNumber", "");
        String address = prefs.getString("address", "");

        nameTxt.setText(name.trim());
        phoneTxt.setText(phone);
        addressTxt.setText(address);
    }

    private void loadCartData(OrderModel order) {
        cartItems = order.getItems();
    }

    private void setupCartList() {
        InvoiceAdapter adapter = new InvoiceAdapter(cartItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void calculateTotals() {
        double itemTotal = 0;
        for (ItemModel item : cartItems) {
            itemTotal += item.getPrice() * item.getNumberinCart();
        }

        tax = itemTotal * 0.02;
        double total = itemTotal + tax + delivery;

        itemTotalTxt.setText(String.format("$%.2f", itemTotal));
        taxTxt.setText(String.format("$%.2f", tax));
        deliveryTxt.setText(String.format("$%.2f", delivery));
        totalTxt.setText(String.format("$%.2f", total));
    }
}
