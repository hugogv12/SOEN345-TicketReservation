package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
 *   <li>TC-B-06: Cancel unknown reservation id.</li>
 *   <li>TC-B-07–TC-B-08: Non-positive quantity rejected.</li>
 *   <li>TC-B-09: Two users cannot oversell remaining inventory.</li>
 *   <li>TC-B-10: Reservation snapshots event title, date, location.</li>
 *   <li>TC-B-11: Cancel after catalog cleared still removes reservation row.</li>
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
        events.add(Event.createNew("Gig", "2026-05-01", "", "Venue", "Concert", 5));
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

    @Test
    @DisplayName("TC-B-06: cancel unknown reservation id")
    void cancelUnknownId() {
        assertFalse(service.cancelReservation("alice", "nonexistent-reservation-id"));
    }

    @Test
    @DisplayName("TC-B-07: zero quantity booking rejected")
    void zeroQuantityBook() {
        Event e = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.NOT_AVAILABLE, service.book("u", e.getId(), 0));
    }

    @Test
    @DisplayName("TC-B-08: negative quantity booking rejected")
    void negativeQuantityBook() {
        Event e = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.NOT_AVAILABLE, service.book("u", e.getId(), -2));
    }

    @Test
    @DisplayName("TC-B-09: two users exhaust capacity without oversell")
    void twoUsersExhaustCapacity() {
        Event e = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.SUCCESS, service.book("alice", e.getId(), 3));
        assertEquals(BookingService.BookResult.SUCCESS, service.book("bob", e.getId(), 2));
        assertEquals(BookingService.BookResult.NOT_AVAILABLE, service.book("carol", e.getId(), 1));
        assertEquals(5, events.findById(e.getId()).getTicketsReserved());
        assertEquals(1, reservations.findByUser("alice").size());
        assertEquals(1, reservations.findByUser("bob").size());
    }

    @Test
    @DisplayName("TC-B-10: reservation snapshots catalog metadata")
    void reservationSnapshotsEventFields() {
        Event e = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.SUCCESS, service.book("pat", e.getId(), 1));
        Reservation r = reservations.findByUser("pat").get(0);
        assertEquals("Gig", r.getEventTitleSnapshot());
        assertEquals("2026-05-01", r.getEventIsoDateSnapshot());
        assertEquals("Venue", r.getEventLocationSnapshot());
        assertEquals(e.getId(), r.getEventId());
    }

    @Test
    @DisplayName("TC-B-11: cancel after event removed from catalog still drops reservation")
    void cancelAfterCatalogCleared() {
        Event e = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.SUCCESS, service.book("u", e.getId(), 2));
        Reservation r = reservations.findByUser("u").get(0);
        events.replaceAllFromRemote(Collections.emptyList());
        assertNull(events.findById(e.getId()));
        assertTrue(service.cancelReservation("u", r.getId()));
        assertNull(reservations.findById(r.getId()));
    }
}
