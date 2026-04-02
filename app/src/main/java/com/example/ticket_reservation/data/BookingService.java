package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;

/**
 * Coordinates inventory and reservation records (in-memory).
 */
public class BookingService {

    private final EventRepository events;
    private final ReservationRepository reservations;

    public BookingService(EventRepository events, ReservationRepository reservations) {
        this.events = events;
        this.reservations = reservations;
    }

    public static BookingService getInstance() {
        return new BookingService(EventRepository.getInstance(), ReservationRepository.getInstance());
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
            if (!event.applyReservation(quantity)) {
                return BookResult.NOT_AVAILABLE;
            }
            Reservation r = Reservation.create(
                    eventId,
                    userKey,
                    quantity,
                    event.getTitle(),
                    event.getIsoDate(),
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
