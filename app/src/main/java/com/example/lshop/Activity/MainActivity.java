package com.example.lshop.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import com.example.lshop.Adapter.CategoryAdapter;
import com.example.lshop.Adapter.PopularAdapter;
import com.example.lshop.Adapter.SliderAdapter;
import com.example.lshop.Domain.BannerModel;
import com.example.lshop.R;
import com.example.lshop.ViewModel.MainViewModel;
import com.example.lshop.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private TextView textViewUsername; // TextView untuk menampilkan username


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new MainViewModel();

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");

        TextView textViewUsername = findViewById(R.id.userTxt);

        if (username != null && !username.isEmpty()) {
            textViewUsername.setText(username);
        }

        initSlider();
        initPopular();

        EditText searchEditText = findViewById(R.id.searchTxt);
        // Modify the searchEditText OnEditorActionListener
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                // Show a dialog instead of a toast
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Fitur Belum Tersedia")
                        .setMessage("Fitur ini masih dalam pengembangan")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .setCancelable(false)
                        .show();

                // Clear the text in the search box
                searchEditText.setText("");

                return true;
            }
            return false;
        });

        // Bottom navigation
        ChipNavigationBar bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setItemSelected(R.id.home, true);
        bottomNavigation.setOnItemSelectedListener(i -> {
            if (i == R.id.home) {
            } else if (i == R.id.cart) {
                startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
            } else if (i == R.id.profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            } else if (i == R.id.about) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));

            }
        });

        // Mengarahkan ke CartActivity saat cart icon di klik
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));


    }


    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        viewModel.loadPopular().observeForever(itemModels -> {
            if (!itemModels.isEmpty()) {
                binding.popularView.setLayoutManager(
                        new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.popularView.setAdapter(new PopularAdapter(itemModels));
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
        viewModel.loadPopular();
    }


    private void initSlider() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        viewModel.loadBanner().observeForever(bannerModels -> {
            if (bannerModels!=null && !bannerModels.isEmpty()){
                banners(bannerModels);
                binding.progressBarSlider.setVisibility(View.GONE);
            }
        });
        viewModel.loadBanner();
    }


    private void banners(ArrayList<BannerModel> bannerModels) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(bannerModels,binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
    }
}