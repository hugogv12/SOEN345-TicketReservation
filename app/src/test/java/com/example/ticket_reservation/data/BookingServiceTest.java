package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link BookingService} (booking and cancellation against in-memory stores).
 *
 * <p><b>Documented test cases</b></p>
 * <ul>
 *   <li>TC-B-01: Successful booking increments reserved tickets and stores a reservation.</li>
 *   <li>TC-B-02: Second booking fails when inventory insufficient.</li>
 *   <li>TC-B-03: Unknown event id returns EVENT_NOT_FOUND.</li>
 *   <li>TC-B-04: Cancel restores ticket availability and removes the reservation.</li>
 *   <li>TC-B-05: Cancel with wrong user key is rejected.</li>
 * </ul>
 */
@DisplayName("BookingService")
class BookingServiceTest {

    private EventRepository events;
    private ReservationRepository reservations;
    private BookingService service;

    @BeforeEach
    void setUp() {
        events = new EventRepository();
        reservations = new ReservationRepository();
        service = new BookingService(events, reservations);
        events.add(Event.createNew("Gig", "2026-05-01", "Venue", "Concert", 5));
    }

    @Test
    @DisplayName("TC-B-01: booking succeeds and updates inventory")
    void bookingSuccess() {
        Event e = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.SUCCESS, service.book("user1", e.getId(), 2));
        Event updated = events.findById(e.getId());
        assertEquals(2, updated.getTicketsReserved());
        assertEquals(3, updated.getAvailableTickets());
        List<Reservation> list = reservations.findByUser("user1");
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).getQuantity());
    }

    @Test
    @DisplayName("TC-B-02: not enough tickets")
    void notEnoughTickets() {
        Event e = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.SUCCESS, service.book("u", e.getId(), 4));
        assertEquals(BookingService.BookResult.NOT_AVAILABLE, service.book("u2", e.getId(), 2));
    }

    @Test
    @DisplayName("TC-B-03: unknown event")
    void unknownEvent() {
        assertEquals(BookingService.BookResult.EVENT_NOT_FOUND, service.book("u", "missing-id", 1));
    }

    @Test
    @DisplayName("TC-B-04: cancel restores inventory")
    void cancelRestores() {
        Event e = events.getAllEvents().get(0);
        service.book("alice", e.getId(), 2);
        Reservation r = reservations.findByUser("alice").get(0);
        assertTrue(service.cancelReservation("alice", r.getId()));
        assertEquals(0, events.findById(e.getId()).getTicketsReserved());
        assertTrue(reservations.findByUser("alice").isEmpty());
    }

    @Test
    @DisplayName("TC-B-05: wrong user cannot cancel")
    void wrongUserCancel() {
        Event e = events.getAllEvents().get(0);
        service.book("alice", e.getId(), 1);
        Reservation r = reservations.findByUser("alice").get(0);
        assertFalse(service.cancelReservation("bob", r.getId()));
    }
}
