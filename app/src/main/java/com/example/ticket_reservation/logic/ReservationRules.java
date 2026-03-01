package com.example.ticket_reservation.logic;

import com.example.ticket_reservation.model.Event;

/**
 * Validation rules for reservations (unit-tested independently of Android).
 */
public final class ReservationRules {

    private ReservationRules() {
    }

    public static boolean canReserve(Event event, int requestedQuantity) {
        if (event == null || requestedQuantity <= 0) {
            return false;
        }
        if (event.isCanceled()) {
            return false;
        }
        return event.getAvailableTickets() >= requestedQuantity;
    }
}
