package com.example.ticket_reservation.integration;

import com.example.ticket_reservation.data.BookingService;
import com.example.ticket_reservation.data.EventRepository;
import com.example.ticket_reservation.data.ReservationRepository;
import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration-style tests: {@link BookingService} with the same <strong>singleton</strong>
 * {@link EventRepository} and {@link ReservationRepository} the Android app uses at runtime.
 *
 * <p>This complements {@link com.example.ticket_reservation.data.BookingServiceTest}, which injects
 * fresh repository instances per test (isolated unit-style). Here we verify the coordinated
 * behaviour when all layers share application-wide in-memory stores.</p>
 */
@Tag("integration")
@DisplayName("Booking pipeline (singleton integration)")
class BookingSingletonIntegrationTest {

    @BeforeEach
    void resetStores() {
        EventRepository.resetSingletonForTests();
        ReservationRepository.resetSingletonForTests();
    }

    @Test
    @DisplayName("TC-I-01: book via singletons updates catalog and reservation store")
    void bookUpdatesBothStores() {
        BookingService service = BookingService.getInstance();
        EventRepository events = EventRepository.getInstance();
        ReservationRepository reservations = ReservationRepository.getInstance();

        Event first = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.SUCCESS, service.book("integration-user", first.getId(), 2));

        Event updated = events.findById(first.getId());
        assertEquals(2, updated.getTicketsReserved());

        List<Reservation> list = reservations.findByUser("integration-user");
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).getQuantity());
    }

    @Test
    @DisplayName("TC-I-02: cancel via singletons restores inventory and removes reservation")
    void cancelRestoresInventory() {
        BookingService service = BookingService.getInstance();
        EventRepository events = EventRepository.getInstance();
        ReservationRepository reservations = ReservationRepository.getInstance();

        Event first = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.SUCCESS, service.book("u", first.getId(), 3));
        Reservation r = reservations.findByUser("u").get(0);

        assertTrue(service.cancelReservation("u", r.getId()));
        assertEquals(0, events.findById(first.getId()).getTicketsReserved());
        assertTrue(reservations.findByUser("u").isEmpty());
    }

    @Test
    @DisplayName("TC-I-03: double booking respects remaining capacity across shared catalog")
    void capacitySharedAcrossCalls() {
        BookingService service = BookingService.getInstance();
        EventRepository events = EventRepository.getInstance();

        Event small = null;
        for (Event e : events.getAllEvents()) {
            if (e.getCapacity() == 120) {
                small = e;
                break;
            }
        }
        assertTrue(small != null);

        assertEquals(BookingService.BookResult.SUCCESS, service.book("a", small.getId(), 100));
        assertEquals(BookingService.BookResult.NOT_AVAILABLE, service.book("b", small.getId(), 30));
        assertEquals(BookingService.BookResult.SUCCESS, service.book("b", small.getId(), 20));
    }
}
