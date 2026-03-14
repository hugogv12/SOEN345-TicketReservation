package com.example.ticket_reservation;

import android.content.Context;
import android.content.SharedPreferences;


public final class SessionPrefs {

    public static final String PREFS_NAME = "ticket_reservation_session";
    private static final String KEY_USER = "user_key";

    private SessionPrefs() {
    }

    public static void setUserKey(Context context, String userKey) {
        prefs(context).edit().putString(KEY_USER, userKey).apply();
    }

    public static String getUserKey(Context context) {
        return prefs(context).getString(KEY_USER, "");
    }

    public static boolean hasUser(Context context) {
        return !getUserKey(context).isEmpty();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
