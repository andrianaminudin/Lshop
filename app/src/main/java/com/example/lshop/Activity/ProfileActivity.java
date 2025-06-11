package com.example.lshop.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    EditText firstName, lastName, phoneNumber, gender, dateOfBirth, address;
    TextView userName, textEmail;
    ImageView imageProfile;
    Button btnUpdateProfile;
    Button btnLogout;
    ImageButton btnEdit;
    DatabaseReference userRef;
    FirebaseUser currentUser;
    private ImageButton btnEditPassword;
    private LinearLayout passwordChangeForm;
    private boolean isFormVisible = false; // status form, default tidak tampil
    EditText currentPassword, newPassword, confirmPassword;
    private String email;
    private Button btnChangePassword;  // Declare the button for changing the password


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        email = sharedPreferences.getString("email", null);

        if (userId == null) {
            Toast.makeText(ProfileActivity.this, "User belum login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Ambil referensi database
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Inisialisasi view
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);
        gender = findViewById(R.id.gender);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        address = findViewById(R.id.address);
        userName = findViewById(R.id.userName);
        textEmail = findViewById(R.id.textEmail);
        imageProfile = findViewById(R.id.imageProfile);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnEdit = findViewById(R.id.btnEdit);
        btnEditPassword = findViewById(R.id.btnEditPassword);
        passwordChangeForm = findViewById(R.id.passwordChangeForm);
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);  // Initialize the change password button
        btnLogout = findViewById(R.id.btnLogout);  // Initialize the logout button


        // Aksi tombol edit password
        btnEditPassword.setOnClickListener(v -> togglePasswordForm());

        // Aksi tombol change password
        btnChangePassword.setOnClickListener(v -> {
            // Show a dialog instead of a toast
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Fitur dalam Pengembangan")
                    .setMessage("Fitur ini masih dalam pengembangan")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();

            // Clear the password fields
            currentPassword.setText("");
            newPassword.setText("");
            confirmPassword.setText("");

            // Close the password change form
            passwordChangeForm.setVisibility(View.GONE);

            // Optionally, change the edit button back to 'edit' icon
            btnEditPassword.setImageResource(android.R.drawable.ic_menu_edit);
            isFormVisible = false;  // Reset the form visibility status
        });

        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Clear shared preferences (reuse the existing sharedPreferences)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Clears all saved preferences
            editor.apply();

            // Redirect to login activity
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();  // Close ProfileActivity
        });

        // Atur input readonly saat awal
        View[] inputs = {firstName, lastName, phoneNumber, gender, dateOfBirth, address};
        setFieldsEnabled(inputs, false);
        btnUpdateProfile.setVisibility(View.GONE);

        // Ambil data user dari Firebase
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        loadUserProfile(currentUser);

        // Tombol edit diklik
        btnEdit.setOnClickListener(v -> {
            setFieldsEnabled(inputs, true);
            btnEdit.setVisibility(View.GONE);
            btnUpdateProfile.setVisibility(View.VISIBLE);
        });

        // Tombol update profil
        btnUpdateProfile.setOnClickListener(v -> {
            String fname = firstName.getText().toString().trim();
            String lname = lastName.getText().toString().trim();
            String phone = phoneNumber.getText().toString().trim();
            String genderStr = gender.getText().toString().trim();
            String dob = dateOfBirth.getText().toString().trim();
            String addr = address.getText().toString().trim();

            if (fname.isEmpty() || lname.isEmpty() || phone.isEmpty() || genderStr.isEmpty() || dob.isEmpty() || addr.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("firstName", fname);
            data.put("lastName", lname);
            data.put("phone", phone);
            data.put("gender", genderStr);
            data.put("dob", dob);
            data.put("address", addr);

            userRef.updateChildren(data)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Profil diperbarui", Toast.LENGTH_SHORT).show();
                        setFieldsEnabled(inputs, false);
                        btnEdit.setVisibility(View.VISIBLE);
                        btnUpdateProfile.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // Gender picker dialog
        gender.setOnClickListener(v -> {
            final String[] options = {"Pria", "Wanita"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Pilih gender");
            builder.setItems(options, (dialog, which) -> gender.setText(options[which]));
            builder.show();
        });

        // Date picker
        dateOfBirth.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateOfBirth.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Bottom navigation
        ChipNavigationBar bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setItemSelected(R.id.profile, true);
        bottomNavigation.setOnItemSelectedListener(i -> {
            if (i == R.id.home) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            } else if (i == R.id.cart) {
                startActivity(new Intent(ProfileActivity.this, OrderHistoryActivity.class));
            } else if (i ==R.id.profile) {

            } else if (i ==R.id.about) {
                startActivity(new Intent(ProfileActivity.this, AboutActivity.class));
            }
        });
    }

    private void togglePasswordForm() {
        if (isFormVisible) {
            passwordChangeForm.setVisibility(View.GONE);
            btnEditPassword.setImageResource(android.R.drawable.ic_menu_edit); // ikon edit
        } else {
            passwordChangeForm.setVisibility(View.VISIBLE);
            btnEditPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // ikon close

            // Kosongkan isian form password
            currentPassword.setText("");
            newPassword.setText("");
            confirmPassword.setText("");
        }
        isFormVisible = !isFormVisible;
    }

    private void setFieldsEnabled(View[] views, boolean enabled) {
        for (View v : views) {
            v.setEnabled(enabled);
            if (v instanceof EditText) {
                ((EditText) v).setFocusable(enabled);
                ((EditText) v).setFocusableInTouchMode(enabled);
            }
        }
    }

    private void loadUserProfile(FirebaseUser currentUser) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fname = snapshot.child("firstName").getValue(String.class);
                    String lname = snapshot.child("lastName").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String genderStr = snapshot.child("gender").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);
                    String addr = snapshot.child("address").getValue(String.class);
                    String uname = snapshot.child("username").getValue(String.class);

                    firstName.setText(fname);
                    lastName.setText(lname);
                    phoneNumber.setText(phone);
                    gender.setText(genderStr);
                    dateOfBirth.setText(dob);
                    address.setText(addr);
                    if (uname != null) userName.setText("@" + uname);
                    if (email != null) textEmail.setText(email);

                    // Simpan data ke SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("firstName", fname);
                    editor.putString("lastName", lname);
                    editor.putString("phoneNumber", phone);
                    editor.putString("gender", genderStr);
                    editor.putString("dateOfBirth", dob);
                    editor.putString("address", addr);
                    editor.apply(); // Simpan perubahan
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });

        if (currentUser != null) {
            textEmail.setText(email);
        }
    }

}
