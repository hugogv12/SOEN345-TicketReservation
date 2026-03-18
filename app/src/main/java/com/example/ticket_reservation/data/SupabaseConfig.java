package com.example.ticket_reservation.data;

import com.example.ticket_reservation.BuildConfig;

public final class SupabaseConfig {

    /**
     * When true (set by the instrumented test runner), the app behaves as if Supabase keys were absent:
     * local auth and in-memory/seeded catalog only.
     */
    private static volatile boolean instrumentedLocalOnly;

    /**
     * When non-null, {@link #baseUrl()} and {@link #anonKey()} use these values (JVM MockWebServer
     * contract tests only). Always call {@link #clearNetworkingTestOverrides()} in test teardown.
     */
    private static volatile String testBaseUrlOverride;
    private static volatile String testAnonKeyOverride;

    private SupabaseConfig() {
    }

    public static void setInstrumentedLocalOnly(boolean localOnly) {
        instrumentedLocalOnly = localOnly;
    }

    /** Exposed for tests: whether the custom Android test runner forced offline / non-remote behaviour. */
    public static boolean isInstrumentedLocalOnly() {
        return instrumentedLocalOnly;
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
        String u = testBaseUrlOverride != null ? testBaseUrlOverride.trim() : BuildConfig.SUPABASE_URL.trim();
        while (u.endsWith("/")) {
            u = u.substring(0, u.length() - 1);
        }
        return u;
    }

    static String anonKey() {
        return testAnonKeyOverride != null ? testAnonKeyOverride.trim() : BuildConfig.SUPABASE_ANON_KEY.trim();
    }

    /** @see #testBaseUrlOverride */
    static void setNetworkingTestOverrides(String baseUrl, String anonKey) {
        testBaseUrlOverride = baseUrl;
        testAnonKeyOverride = anonKey;
    }

    /** @see #testBaseUrlOverride */
    static void clearNetworkingTestOverrides() {
        testBaseUrlOverride = null;
        testAnonKeyOverride = null;
    }
}
