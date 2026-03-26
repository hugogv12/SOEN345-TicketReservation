package com.example.ticket_reservation;

import androidx.annotation.Nullable;

/**
 * Optional hooks so Espresso can wait for background auth work to finish on the UI thread.
 * No-op in production; instrumented tests register a {@link Gate} (e.g. CountingIdlingResource).
 */
public final class AuthAsyncIdling {

    public interface Gate {
        void enter();

        void exit();
    }

    @Nullable
    private static Gate gate;

    private AuthAsyncIdling() {
    }

    public static void setGate(@Nullable Gate g) {
        gate = g;
    }

    public static void enter() {
        if (gate != null) {
            gate.enter();
        }
    }

    public static void exit() {
        if (gate != null) {
            gate.exit();
        }
    }
}
