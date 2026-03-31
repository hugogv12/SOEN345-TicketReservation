package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ReservationRepository")
class ReservationRepositoryTest {

    private ReservationRepository repo;

    @BeforeEach
    void setUp() {
        repo = new ReservationRepository();
    }

    @Test
    @DisplayName("findByUser filters by key")
    void findByUser() {
        repo.add(Reservation.create("e1", "alice", 1, "T", "2026-01-01", "", "L"));
        repo.add(Reservation.create("e1", "bob", 2, "T", "2026-01-01", "", "L"));
        repo.add(Reservation.create("e2", "alice", 3, "T2", "2026-02-01", "", "L2"));
        List<Reservation> a = repo.findByUser("alice");
        assertEquals(2, a.size());
        assertEquals(1, repo.findByUser("bob").size());
    }

    @Test
    @DisplayName("findById and remove")
    void findAndRemove() {
        Reservation r = Reservation.create("e", "u", 4, "T", "2026-01-01", "", "L");
        repo.add(r);
        assertNotNull(repo.findById(r.getId()));
        assertTrue(repo.remove(r.getId()));
        assertNull(repo.findById(r.getId()));
        assertFalse(repo.remove(r.getId()));
    }

    @Test
    @DisplayName("clear drops all reservations")
    void clear() {
        repo.add(Reservation.create("e", "u", 1, "T", "2026-01-01", "", "L"));
        repo.clear();
        assertTrue(repo.findByUser("u").isEmpty());
    }
}
