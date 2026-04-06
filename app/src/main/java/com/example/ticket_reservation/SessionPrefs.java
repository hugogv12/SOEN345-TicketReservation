package com.example.ticket_reservation;

import android.content.Context;
import android.content.SharedPreferences;


public final class SessionPrefs {

    public static final String PREFS_NAME = "ticket_reservation_session";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    /** Same as email; used as {@code user_key} for reservations API. */
    private static final String KEY_USER_KEY = "user_key";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    /** Legacy single field (pre–email/username/password). */
    private static final String KEY_LEGACY_USER = "user_key_legacy";

    private SessionPrefs() {
    }

    /** Legacy helper for tests: treat {@code userKey} as email and derive a simple display name. */
    public static void setUserKey(Context context, String userKey) {
        if (userKey == null || userKey.trim().isEmpty()) {
            clear(context);
            return;
        }
        String k = userKey.trim();
        String name = k.contains("@") ? k.substring(0, k.indexOf('@')) : k;
        setSession(context, k, name, null);
    }

    public static void setSession(Context context, String email, String username, String accessToken) {
        String em = email != null ? email.trim() : "";
        String un = username != null ? username.trim() : "";
        prefs(context).edit()
                .putString(KEY_EMAIL, em)
                .putString(KEY_USERNAME, un)
                .putString(KEY_USER_KEY, em)
                .putString(KEY_ACCESS_TOKEN, accessToken != null ? accessToken : "")
                .remove(KEY_LEGACY_USER)
                .apply();
    }

    public static void clear(Context context) {
        prefs(context).edit().clear().apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences p = prefs(context);
        String email = p.getString(KEY_EMAIL, "");
        if (!email.isEmpty()) {
            return email;
        }
        return p.getString(KEY_LEGACY_USER, "");
    }

    public static String getUsername(Context context) {
        return prefs(context).getString(KEY_USERNAME, "");
    }

    /**
     * Stable id for reservations (email). Kept for backward compatibility with existing DB rows.
     */
    public static String getUserKey(Context context) {
        SharedPreferences p = prefs(context);
        String key = p.getString(KEY_USER_KEY, "");
        if (!key.isEmpty()) {
            return key;
        }
        String email = p.getString(KEY_EMAIL, "");
        if (!email.isEmpty()) {
            return email;
        }
        return p.getString(KEY_LEGACY_USER, "");
    }

    public static String getAccessToken(Context context) {
        return prefs(context).getString(KEY_ACCESS_TOKEN, "");
    }

    public static boolean hasUser(Context context) {
        return !getUserKey(context).isEmpty();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
