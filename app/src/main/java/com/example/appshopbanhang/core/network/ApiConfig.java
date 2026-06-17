package com.example.appshopbanhang.core.network;

import android.content.Context;
import android.content.SharedPreferences;

public final class ApiConfig {
    public static final String DEFAULT_BASE_URL = "http://10.0.2.2:8080";
    private static final String PREFS_NAME = "ApiConfig";
    private static final String KEY_BASE_URL = "baseUrl";
    private static volatile String baseUrl = DEFAULT_BASE_URL;

    private ApiConfig() {
    }

    public static void load(Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        baseUrl = normalizeBaseUrl(preferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL));
    }

    public static void save(Context context, String value) {
        String normalized = normalizeBaseUrl(value);
        baseUrl = normalized;
        if (context != null) {
            context.getApplicationContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_BASE_URL, normalized)
                    .apply();
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String normalizeBaseUrl(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            return DEFAULT_BASE_URL;
        }
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://" + normalized;
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static String imageUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        String trimmed = path.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        if (trimmed.startsWith("/")) {
            return getBaseUrl() + trimmed;
        }
        return getBaseUrl() + "/" + trimmed;
    }
}
