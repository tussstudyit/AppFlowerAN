package com.example.appshopbanhang.core.session;

import android.content.Context;
import android.content.SharedPreferences;

public class QuanLyPhien {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "tendn";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ROLE = "quyen";

    private final SharedPreferences preferences;

    public QuanLyPhien(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(String username, String role) {
        preferences.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROLE, role)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public void logout() {
        preferences.edit()
                .remove(KEY_USERNAME)
                .remove(KEY_ROLE)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, null);
    }

    public String getRole() {
        return preferences.getString(KEY_ROLE, null);
    }
}

