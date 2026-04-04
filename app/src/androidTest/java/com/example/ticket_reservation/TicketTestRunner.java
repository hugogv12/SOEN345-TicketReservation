package com.example.ticket_reservation;

import android.os.Bundle;

import androidx.test.runner.AndroidJUnitRunner;

import com.example.ticket_reservation.data.SupabaseConfig;

/**
 * Forces {@link SupabaseConfig#isConfigured()} to false so instrumented tests use local accounts and
 * seeded events instead of flaky network calls to Supabase.
 */
public class TicketTestRunner extends AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle arguments) {
        SupabaseConfig.setInstrumentedLocalOnly(true);
        super.onCreate(arguments);
    }
}
