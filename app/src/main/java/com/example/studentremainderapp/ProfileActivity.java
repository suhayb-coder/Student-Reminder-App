package com.example.studentremainderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupProfileOptions();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutDialog());
    }

    private void setupProfileOptions() {
        // Edit Profile option
        View editProfile = findViewById(R.id.optionEditProfile);
        ((TextView) editProfile.findViewById(R.id.tvSettingTitle)).setText("Edit Personal Info");
        ((ImageView) editProfile.findViewById(R.id.ivSettingIcon)).setImageResource(android.R.drawable.ic_menu_edit);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of your account?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    // In a real app, clear session and go to login
                    finish(); 
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}