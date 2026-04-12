package com.example.ticket_reservation;

import com.example.ticket_reservation.data.SupabaseConfig;

/**
 * Applies the same Supabase policy as {@code TicketTestRunner} (androidTest) before instrumentation
 * starts. Lives in {@code main} so the behaviour can be covered by fast JVM unit tests.
 */
public final class TicketTestRunnerBootstrap {

    private TicketTestRunnerBootstrap() {
    }

    /**
     * Force instrumented builds to treat Supabase as disabled for app logic ({@link SupabaseConfig#isConfigured()}),
     * avoiding flaky network during Espresso runs.
     */
    public static void applyDefaultSupabaseTestPolicy() {
        SupabaseConfig.setInstrumentedLocalOnly(true);
    }
}
