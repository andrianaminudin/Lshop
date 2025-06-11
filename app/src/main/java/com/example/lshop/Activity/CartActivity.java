package com.example.lshop.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lshop.Adapter.CartAdapter;
import com.example.lshop.Helper.ManagmentCart;
import com.example.lshop.R;
import com.example.lshop.databinding.ActivityCartBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.HashMap;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        calculatorCart();
        setVariable();
        initCartList();
    }

    private void initCartList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView2.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView2.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), this, this::calculatorCart));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.checkoutBtn.setOnClickListener(v -> {
            if (isProfileComplete()) {
                // Show order success dialog
                showOrderSuccessDialog();
            } else {
                showIncompleteProfileDialog();
            }
        });
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round((managmentCart.getTotalFee() * percentTax * 100.0)) / 100.0;

        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round((managmentCart.getTotalFee() * 100.0)) / 100.0;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private boolean isProfileComplete() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return !sharedPreferences.getString("firstName", "").isEmpty()
                && !sharedPreferences.getString("lastName", "").isEmpty()
                && !sharedPreferences.getString("phoneNumber", "").isEmpty()
                && !sharedPreferences.getString("gender", "").isEmpty()
                && !sharedPreferences.getString("dateOfBirth", "").isEmpty()
                && !sharedPreferences.getString("address", "").isEmpty();
    }

    private void showIncompleteProfileDialog() {
        new AlertDialog.Builder(CartActivity.this)
                .setTitle("Profil Tidak Lengkap")
                .setMessage("Harap lengkapi data diri Anda terlebih dahulu sebelum melanjutkan.")
                .setPositiveButton("OK", (dialog, which) -> {
                    startActivity(new Intent(CartActivity.this, ProfileActivity.class));
                })
                .setCancelable(false)
                .show();
    }

    private void showOrderSuccessDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_order_success, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setView(dialogView);

        Button okButton = dialogView.findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> {
            // Ambil username dari SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String username = prefs.getString("username", "guest");

            // Buat data order
            String orderId = UUID.randomUUID().toString(); // generate order id unik
            DatabaseReference userOrderRef = FirebaseDatabase.getInstance()
                    .getReference("Orders")
                    .child(username)
                    .child(orderId);

            double percentTax = 0.02;
            double delivery = 10;
            double subtotal = managmentCart.getTotalFee();
            double tax = Math.round(subtotal * percentTax * 100.0) / 100.0;
            double total = Math.round((subtotal + tax + delivery) * 100.0) / 100.0;

            // Buat object order
            HashMap<String, Object> orderData = new HashMap<>();
            orderData.put("timestamp", System.currentTimeMillis());
            orderData.put("subtotal", subtotal);
            orderData.put("tax", tax);
            orderData.put("delivery", delivery);
            orderData.put("total", total);
            orderData.put("items", managmentCart.getListCart());

            // Simpan ke database
            userOrderRef.setValue(orderData)
                    .addOnSuccessListener(aVoid -> {
                        // Lanjut ke InvoiceActivity
                        Intent intent = new Intent(CartActivity.this, OrderHistoryActivity.class);
                        intent.putExtra("cartList", new Gson().toJson(managmentCart.getListCart()));
                        startActivity(intent);
                        managmentCart.clearCart();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal menyimpan pesanan", Toast.LENGTH_SHORT).show();
                    });
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

}
