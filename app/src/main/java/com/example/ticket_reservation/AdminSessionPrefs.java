package com.example.ticket_reservation;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Session for the admin console only. Independent of {@link SessionPrefs} (customer bookings).
 */
public final class AdminSessionPrefs {

    public static final String PREFS_NAME = "ticket_admin_session";
    private static final String KEY_ADMIN_CONTACT = "admin_contact";
    private static final String KEY_ADMIN_DISPLAY = "admin_display";

    private AdminSessionPrefs() {
    }

    public static void setSession(Context context, String canonicalContact, String displayName) {
        String c = canonicalContact != null ? canonicalContact.trim() : "";
        String d = displayName != null ? displayName.trim() : "";
        prefs(context).edit()
                .putString(KEY_ADMIN_CONTACT, c)
                .putString(KEY_ADMIN_DISPLAY, d)
                .apply();
    }

    public static void clear(Context context) {
        prefs(context).edit().clear().apply();
    }

    /** Canonical email or phone (same as stored login id). */
    public static String getContact(Context context) {
        return prefs(context).getString(KEY_ADMIN_CONTACT, "");
    }

    public static String getDisplayName(Context context) {
        return prefs(context).getString(KEY_ADMIN_DISPLAY, "");
    }

    public static boolean hasAdminSession(Context context) {
        return !getContact(context).isEmpty();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
