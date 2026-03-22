package com.example.ticket_reservation.data;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;

import java.util.List;

public final class SupabaseDataSync {

    private SupabaseDataSync() {
    }

    public static void refreshEventsAsync(AppCompatActivity activity, EventRepository repo, Runnable onUi) {
        if (!SupabaseConfig.isConfigured()) {
            onUi.run();
            return;
        }
        new Thread(() -> {
            try {
                List<Event> remote = SupabaseRest.fetchEvents();
                repo.replaceAllFromRemote(remote);
            } catch (Exception ignored) {
            }
            activity.runOnUiThread(onUi);
        }).start();
    }

    public static void refreshReservationsAsync(AppCompatActivity activity, String userKey,
                                                ReservationRepository reservations, Runnable onUi) {
        if (!SupabaseConfig.isConfigured()) {
            onUi.run();
            return;
        }
        new Thread(() -> {
            try {
                List<Reservation> remote = SupabaseRest.fetchReservationsForUser(userKey);
                reservations.replaceAllForUser(userKey, remote);
            } catch (Exception ignored) {
            }
            activity.runOnUiThread(onUi);
        }).start();
    }
}
