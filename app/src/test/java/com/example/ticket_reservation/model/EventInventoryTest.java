package com.example.ticket_reservation.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Event inventory mutations")
class EventInventoryTest {

    @Test
    @DisplayName("successful applyReservation increments reserved and lowers availability")
    void applyReservationSuccess() {
        Event e = new Event("id", "T", "2026-01-01", "", "L", "C", false, 10, 2);
        assertTrue(e.applyReservation(3));
        assertEquals(5, e.getTicketsReserved());
        assertEquals(5, e.getAvailableTickets());
    }

    @Test
    @DisplayName("canceled event rejects applyReservation")
    void applyReservationCanceledEvent() {
        Event e = new Event("id", "T", "2026-01-01", "", "L", "C", true, 100, 0);
        assertFalse(e.applyReservation(1));
        assertEquals(0, e.getTicketsReserved());
    }

    @Test
    @DisplayName("failed applyReservation leaves ticketsReserved unchanged")
    void applyReservationNoOpOnFailure() {
        Event e = new Event("id", "T", "2026-01-01", "", "L", "C", false, 10, 8);
        assertFalse(e.applyReservation(5));
        assertEquals(8, e.getTicketsReserved());
    }

    @Test
    @DisplayName("getAvailableTickets never negative")
    void availableNeverNegative() {
        Event e = new Event("id", "T", "2026-01-01", "", "L", "C", false, 5, 10);
        assertEquals(0, e.getAvailableTickets());
    }

    @Test
    @DisplayName("releaseTickets rejects invalid quantity")
    void releaseTicketsGuards() {
        Event e = new Event("id", "T", "2026-01-01", "", "L", "C", false, 10, 3);
        assertFalse(e.releaseTickets(0));
        assertFalse(e.releaseTickets(-1));
        assertFalse(e.releaseTickets(4));
        assertTrue(e.releaseTickets(3));
        assertEquals(0, e.getTicketsReserved());
    }
}
