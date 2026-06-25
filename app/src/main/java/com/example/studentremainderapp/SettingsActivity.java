package com.example.studentremainderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupSettingsItems();
        setupNavigation();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void setupSettingsItems() {
        // Account
        setupItem(R.id.settingProfile, "Profile", R.drawable.ic_account);
        setupItem(R.id.settingNotifications, "Notifications", android.R.drawable.ic_lock_idle_alarm);

        // General
        setupItem(R.id.settingLanguage, "Language", android.R.drawable.ic_menu_sort_alphabetically);
        setupItem(R.id.settingDarkMode, "Dark Mode", android.R.drawable.ic_menu_day);

        // Support
        setupItem(R.id.settingHelp, "Help & Support", android.R.drawable.ic_menu_help);
        setupItem(R.id.settingAbout, "About", android.R.drawable.ic_menu_info_details);
    }

    private void setupItem(int layoutId, String title, int iconRes) {
        View view = findViewById(layoutId);
        TextView tvTitle = view.findViewById(R.id.tvSettingTitle);
        ImageView ivIcon = view.findViewById(R.id.ivSettingIcon);

        tvTitle.setText(title);
        ivIcon.setImageResource(iconRes);

        if (layoutId == R.id.settingProfile) {
            view.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
            });
        } else {
            view.setOnClickListener(v -> Toast.makeText(this, title + " clicked", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupNavigation() {
        findViewById(R.id.navTasks).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navCalendar).setOnClickListener(v -> {
            startActivity(new Intent(this, CalendarActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        // Current page is Settings
    }
}