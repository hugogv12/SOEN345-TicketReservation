package com.example.ticket_reservation.data;

import android.os.Looper;

import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Coordinates catalog and reservations: Supabase RPC when {@link SupabaseConfig#isConfigured()}
 * and this service is constructed with {@code remotePersistence}; otherwise in-memory only.
 */
public class BookingService {

    private final EventRepository events;
    private final ReservationRepository reservations;
    private final boolean remotePersistence;

    public BookingService(EventRepository events, ReservationRepository reservations) {
        this(events, reservations, false);
    }

    public BookingService(EventRepository events, ReservationRepository reservations,
                          boolean remotePersistence) {
        this.events = events;
        this.reservations = reservations;
        this.remotePersistence = remotePersistence;
    }

    public static BookingService getInstance() {
        return new BookingService(
                EventRepository.getInstance(),
                ReservationRepository.getInstance(),
                SupabaseConfig.isConfigured());
    }

    public enum BookResult {
        SUCCESS,
        EVENT_NOT_FOUND,
        NOT_AVAILABLE
    }

    public BookResult book(String userKey, String eventId, int quantity) {
        synchronized (events) {
            Event event = events.findById(eventId);
            if (event == null) {
                return BookResult.EVENT_NOT_FOUND;
            }
            if (remotePersistence) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    throw new IllegalStateException("book() with Supabase must run off the main thread");
                }
                try {
                    JSONObject rpc = SupabaseRest.bookEvent(
                            eventId,
                            userKey,
                            quantity,
                            event.getTitle(),
                            event.getIsoDate(),
                            event.getStartTime(),
                            event.getLocation()
                    );
                    if (!rpc.optBoolean("ok")) {
                        return "not_available".equals(rpc.optString("reason"))
                                ? BookResult.NOT_AVAILABLE
                                : BookResult.EVENT_NOT_FOUND;
                    }
                    if (!event.applyReservation(quantity)) {
                        return BookResult.NOT_AVAILABLE;
                    }
                    String rid = rpc.optString("reservation_id");
                    if (rid.isEmpty()) {
                        return BookResult.NOT_AVAILABLE;
                    }
                    Reservation r = Reservation.createWithExistingId(
                            rid,
                            eventId,
                            userKey,
                            quantity,
                            event.getTitle(),
                            event.getIsoDate(),
                            event.getStartTime(),
                            event.getLocation()
                    );
                    synchronized (reservations) {
                        reservations.add(r);
                    }
                    return BookResult.SUCCESS;
                } catch (IOException | JSONException e) {
                    return BookResult.NOT_AVAILABLE;
                }
            }
            if (!event.applyReservation(quantity)) {
                return BookResult.NOT_AVAILABLE;
            }
            Reservation r = Reservation.create(
                    eventId,
                    userKey,
                    quantity,
                    event.getTitle(),
                    event.getIsoDate(),
                    event.getStartTime(),
                    event.getLocation()
            );
            synchronized (reservations) {
                reservations.add(r);
            }
            return BookResult.SUCCESS;
        }
    }

    public boolean cancelReservation(String userKey, String reservationId) {
        synchronized (events) {
            Reservation r;
            synchronized (reservations) {
                r = reservations.findById(reservationId);
                if (r == null || !r.getUserKey().equals(userKey)) {
                    return false;
                }
            }
            if (remotePersistence) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    throw new IllegalStateException(
                            "cancelReservation() with Supabase must run off the main thread");
                }
                try {
                    JSONObject rpc = SupabaseRest.cancelReservation(reservationId, userKey);
                    if (!rpc.optBoolean("ok")) {
                        return false;
                    }
                } catch (IOException | JSONException e) {
                    return false;
                }
            }
            Event event = events.findById(r.getEventId());
            if (event != null) {
                event.releaseTickets(r.getQuantity());
            }
            synchronized (reservations) {
                return reservations.remove(reservationId);
            }
        }
    }
}
