package com.example.studentremainderapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvUserName, tvUserEmail;
    private ImageView ivProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        LocaleHelper.setLocale(this, sessionManager.getLanguage());
        ThemeUtils.applyTheme(sessionManager);
        
        setContentView(R.layout.activity_profile);
        
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        
        updateUI();

        setupProfileOptions();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        tvUserName.setText(sessionManager.getName());
        tvUserEmail.setText(sessionManager.getEmail());
        
        String uriStr = sessionManager.getProfilePic();
        if (uriStr != null) {
            try {
                ivProfilePic.setImageURI(Uri.parse(uriStr));
                ivProfilePic.setPadding(0, 0, 0, 0);
                ivProfilePic.setColorFilter(null);
            } catch (Exception e) {
                ivProfilePic.setImageResource(R.drawable.ic_account);
            }
        } else {
            ivProfilePic.setImageResource(R.drawable.ic_account);
            ivProfilePic.setPadding(20 * (int)getResources().getDisplayMetrics().density, 20 * (int)getResources().getDisplayMetrics().density, 20 * (int)getResources().getDisplayMetrics().density, 20 * (int)getResources().getDisplayMetrics().density);
            ivProfilePic.setColorFilter(getColor(R.color.primary_color));
        }
    }

    private void setupProfileOptions() {
        // Edit Profile option
        View editProfile = findViewById(R.id.optionEditProfile);
        ((TextView) editProfile.findViewById(R.id.tvSettingTitle)).setText(R.string.edit_personal_info);
        ((ImageView) editProfile.findViewById(R.id.ivSettingIcon)).setImageResource(android.R.drawable.ic_menu_edit);
        
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.logout, (dialog, which) -> {
                    sessionManager.logoutUser();
                    Toast.makeText(this, R.string.logged_out_msg, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); 
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}