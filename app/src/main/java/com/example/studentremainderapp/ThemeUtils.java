package com.example.studentremainderapp;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {
    public static void applyTheme(SessionManager sessionManager) {
        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
