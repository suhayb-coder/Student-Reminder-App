package com.example.studentremainderapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "StudentTaskPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled";
    private static final String KEY_NOTIFICATION_SOUND = "notificationSound";
    private static final String KEY_PROFILE_PIC = "profilePic";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String name, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public void updateSession(String name, String email) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getName() {
        return pref.getString(KEY_NAME, "User");
    }

    public void setLanguage(String lang) {
        editor.putString(KEY_LANGUAGE, lang);
        editor.commit();
    }

    public String getLanguage() {
        return pref.getString(KEY_LANGUAGE, "en"); // Default to English
    }

    public void setDarkMode(boolean isDark) {
        editor.putBoolean(KEY_DARK_MODE, isDark);
        editor.commit();
    }

    public boolean isDarkMode() {
        return pref.getBoolean(KEY_DARK_MODE, false);
    }

    public void setNotificationsEnabled(boolean enabled) {
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled);
        editor.commit();
    }

    public boolean isNotificationsEnabled() {
        return pref.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationSound(String uri) {
        editor.putString(KEY_NOTIFICATION_SOUND, uri);
        editor.commit();
    }

    public String getNotificationSound() {
        return pref.getString(KEY_NOTIFICATION_SOUND, null);
    }

    public void setProfilePic(String uri) {
        editor.putString(KEY_PROFILE_PIC, uri);
        editor.commit();
    }

    public String getProfilePic() {
        return pref.getString(KEY_PROFILE_PIC, null);
    }

    public void logoutUser() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_USER_ID);
        editor.commit();
    }
}