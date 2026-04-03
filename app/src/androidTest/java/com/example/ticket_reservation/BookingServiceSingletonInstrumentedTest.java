package com.example.ticket_reservation;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.ticket_reservation.data.BookingService;
import com.example.ticket_reservation.data.EventRepository;
import com.example.ticket_reservation.data.ReservationRepository;
import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class BookingServiceSingletonInstrumentedTest {

    @Before
    public void resetStores() {
        EventRepository.resetSingletonForTests();
        ReservationRepository.resetSingletonForTests();
    }

    @Test
    public void singletonBooking_roundTripOnDevice() {
        BookingService service = BookingService.getInstance();
        EventRepository events = EventRepository.getInstance();
        ReservationRepository reservations = ReservationRepository.getInstance();

        Event first = events.getAllEvents().get(0);
        assertEquals(BookingService.BookResult.SUCCESS, service.book("device-user", first.getId(), 1));

        List<Reservation> list = reservations.findByUser("device-user");
        assertEquals(1, list.size());

        assertTrue(service.cancelReservation("device-user", list.get(0).getId()));
        assertEquals(0, events.findById(first.getId()).getTicketsReserved());
    }
}
