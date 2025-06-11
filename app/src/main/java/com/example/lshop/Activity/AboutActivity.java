package com.example.lshop.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lshop.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Bottom navigation
        ChipNavigationBar bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setItemSelected(R.id.about, true);
        bottomNavigation.setOnItemSelectedListener(i -> {
            if (i == R.id.home) {
                startActivity(new Intent(AboutActivity.this, MainActivity.class));
            } else if (i == R.id.cart) {
                startActivity(new Intent(AboutActivity.this, OrderHistoryActivity.class));
            } else if (i == R.id.profile) {
                startActivity(new Intent(AboutActivity.this, ProfileActivity.class));
            } else if (i == R.id.about) {
            }
        });
    }
}