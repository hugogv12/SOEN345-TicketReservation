package com.example.ticket_reservation.data;

import com.example.ticket_reservation.BuildConfig;

public final class SupabaseConfig {

    /**
     * When true (set by the instrumented test runner), the app behaves as if Supabase keys were absent:
     * local auth and in-memory/seeded catalog only.
     */
    private static volatile boolean instrumentedLocalOnly;

    private SupabaseConfig() {
    }

    public static void setInstrumentedLocalOnly(boolean localOnly) {
        instrumentedLocalOnly = localOnly;
    }

    /** True when {@code local.properties} defines both {@code supabase.url} and {@code supabase.anon.key}. */
    public static boolean isConfigured() {
        if (instrumentedLocalOnly) {
            return false;
        }
        String url = BuildConfig.SUPABASE_URL;
        String key = BuildConfig.SUPABASE_ANON_KEY;
        return url != null && !url.isEmpty() && key != null && !key.isEmpty();
    }

    static String baseUrl() {
        String u = BuildConfig.SUPABASE_URL.trim();
        while (u.endsWith("/")) {
            u = u.substring(0, u.length() - 1);
        }
        return u;
    }

    static String anonKey() {
        return BuildConfig.SUPABASE_ANON_KEY.trim();
    }
}
