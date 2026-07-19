package com.example.studentremainderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends BaseActivity {

    private SessionManager sessionManager;

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

        loadSmallProfilePic();
    }

    private void loadSmallProfilePic() {
        ImageView btnProfile = findViewById(R.id.btnProfile);
        String uriStr = sessionManager.getProfilePic();
        if (uriStr != null) {
            try {
                btnProfile.setImageURI(android.net.Uri.parse(uriStr));
                btnProfile.setPadding(0, 0, 0, 0);
                btnProfile.setColorFilter(null);
            } catch (Exception e) {
                btnProfile.setImageResource(R.drawable.ic_account);
            }
        }
    }

    private void setupSettingsItems() {
        // Account
        setupItem(R.id.settingProfile, getString(R.string.profile), R.drawable.ic_account);
        setupItem(R.id.settingNotifications, getString(R.string.notifications), android.R.drawable.ic_lock_idle_alarm);

        // General
        setupItem(R.id.settingLanguage, getString(R.string.language), android.R.drawable.ic_menu_sort_alphabetically);
        setupItem(R.id.settingDarkMode, getString(R.string.dark_mode), 
                sessionManager.isDarkMode() ? android.R.drawable.ic_menu_day : android.R.drawable.ic_menu_view);

        // Support
        setupItem(R.id.settingHelp, getString(R.string.help_support), android.R.drawable.ic_menu_help);
        setupItem(R.id.settingAbout, getString(R.string.about), android.R.drawable.ic_menu_info_details);
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
        } else if (layoutId == R.id.settingNotifications) {
            view.setOnClickListener(v -> {
                startActivity(new Intent(this, NotificationSettingsActivity.class));
            });
        } else if (layoutId == R.id.settingLanguage) {
            view.setOnClickListener(v -> showLanguageDialog());
        } else if (layoutId == R.id.settingDarkMode) {
            view.setOnClickListener(v -> toggleDarkMode());
        } else if (layoutId == R.id.settingHelp) {
            view.setOnClickListener(v -> {
                startActivity(new Intent(this, HelpSupportActivity.class));
            });
        } else if (layoutId == R.id.settingAbout) {
            view.setOnClickListener(v -> {
                startActivity(new Intent(this, AboutActivity.class));
            });
        } else {
            view.setOnClickListener(v -> Toast.makeText(this, title + " clicked", Toast.LENGTH_SHORT).show());
        }
    }

    private void toggleDarkMode() {
        boolean isDark = sessionManager.isDarkMode();
        sessionManager.setDarkMode(!isDark);
        
        if (!isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        
        recreate();
    }

    private void showLanguageDialog() {
        String[] languages = {getString(R.string.english), getString(R.string.kiswahili)};
        String[] langCodes = {"en", "sw"};
        
        int currentLangIndex = 0;
        String currentLangCode = sessionManager.getLanguage();
        for (int i = 0; i < langCodes.length; i++) {
            if (langCodes[i].equals(currentLangCode)) {
                currentLangIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_language)
                .setSingleChoiceItems(languages, currentLangIndex, (dialog, which) -> {
                    String selectedLang = langCodes[which];
                    sessionManager.setLanguage(selectedLang);
                    LocaleHelper.setLocale(this, selectedLang);
                    
                    dialog.dismiss();
                    
                    // Full app restart to apply changes everywhere
                    Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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