package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ReservationRepository} (in-memory list, no singleton).
 */
@DisplayName("ReservationRepository")
class ReservationRepositoryTest {

    private ReservationRepository repo;

    @BeforeEach
    void setUp() {
        repo = new ReservationRepository();
    }

    @Nested
    @DisplayName("findByUser")
    class FindByUser {

        @Test
        void given_reservationsForSeveralUsers_whenFindByUser_thenOnlyMatchingRows() {
            repo.add(Reservation.create("e1", "alice", 1, "T", "2026-01-01", "", "L"));
            repo.add(Reservation.create("e1", "bob", 2, "T", "2026-01-01", "", "L"));
            repo.add(Reservation.create("e2", "alice", 3, "T2", "2026-02-01", "", "L2"));
            assertEquals(2, repo.findByUser("alice").size());
            assertEquals(1, repo.findByUser("bob").size());
        }

        @Test
        void given_noReservationsForUser_whenFindByUser_thenEmptyList() {
            repo.add(Reservation.create("e1", "alice", 1, "T", "2026-01-01", "", "L"));
            assertTrue(repo.findByUser("nobody").isEmpty());
        }

        @Test
        void given_userKeyDiffersByCase_whenFindByUser_thenExactMatchOnly() {
            repo.add(Reservation.create("e1", "Alice", 1, "T", "2026-01-01", "", "L"));
            assertEquals(1, repo.findByUser("Alice").size());
            assertTrue(repo.findByUser("alice").isEmpty());
        }

        @Test
        void given_findByUserResult_whenCallerClearsReturnedList_thenRepositoryUnchanged() {
            repo.add(Reservation.create("e1", "u", 1, "T", "2026-01-01", "", "L"));
            List<Reservation> copy = repo.findByUser("u");
            copy.clear();
            assertEquals(1, repo.findByUser("u").size());
        }
    }

    @Nested
    @DisplayName("findById / remove")
    class FindByIdAndRemove {

        @Test
        void given_storedReservation_whenFindById_thenSameId() {
            Reservation r = Reservation.create("e", "u", 4, "T", "2026-01-01", "", "L");
            repo.add(r);
            assertNotNull(repo.findById(r.getId()));
            assertEquals(r.getId(), repo.findById(r.getId()).getId());
        }

        @Test
        void given_unknownId_whenFindById_thenNull() {
            assertNull(repo.findById("00000000-0000-0000-0000-000000000000"));
        }

        @Test
        void given_storedReservation_whenRemove_thenGoneAndSecondRemoveFalse() {
            Reservation r = Reservation.create("e", "u", 4, "T", "2026-01-01", "", "L");
            repo.add(r);
            assertTrue(repo.remove(r.getId()));
            assertNull(repo.findById(r.getId()));
            assertFalse(repo.remove(r.getId()));
        }

        @Test
        void given_unknownId_whenRemove_thenFalse() {
            assertFalse(repo.remove("00000000-0000-0000-0000-000000000000"));
        }

        @Test
        void given_twoReservations_whenRemoveFirst_thenSecondStillFindable() {
            Reservation a = Reservation.createWithExistingId(
                    "id-a", "e", "u", 1, "T", "2026-01-01", "", "L");
            Reservation b = Reservation.createWithExistingId(
                    "id-b", "e", "u", 1, "T", "2026-01-01", "", "L");
            repo.add(a);
            repo.add(b);
            assertTrue(repo.remove("id-a"));
            assertNotNull(repo.findById("id-b"));
            assertEquals(1, repo.findByUser("u").size());
        }
    }

    @Nested
    @DisplayName("clear")
    class Clear {

        @Test
        void given_rows_whenClear_thenFindByUserEmpty() {
            repo.add(Reservation.create("e", "u", 1, "T", "2026-01-01", "", "L"));
            repo.clear();
            assertTrue(repo.findByUser("u").isEmpty());
        }
    }

    @Nested
    @DisplayName("replaceAllForUser")
    class ReplaceAllForUser {

        @Test
        void given_mixedUsers_whenReplaceAllForOne_thenOthersUnchanged() {
            repo.add(Reservation.create("e1", "alice", 1, "T1", "2026-01-01", "", "L"));
            repo.add(Reservation.create("e2", "bob", 1, "T2", "2026-01-02", "", "L"));
            List<Reservation> nextAlice = new ArrayList<>();
            nextAlice.add(Reservation.createWithExistingId(
                    "sync-1", "e9", "alice", 2, "New", "2026-06-01", "10:00", "Venue"));
            repo.replaceAllForUser("alice", nextAlice);
            assertEquals(1, repo.findByUser("alice").size());
            assertEquals("sync-1", repo.findByUser("alice").get(0).getId());
            assertEquals(2, repo.findByUser("alice").get(0).getQuantity());
            assertEquals(1, repo.findByUser("bob").size());
        }

        @Test
        void given_userHasRows_whenReplaceWithEmpty_thenUserClearedOthersRemain() {
            repo.add(Reservation.create("e1", "alice", 1, "T", "2026-01-01", "", "L"));
            repo.add(Reservation.create("e2", "bob", 1, "T", "2026-01-02", "", "L"));
            repo.replaceAllForUser("alice", Collections.emptyList());
            assertTrue(repo.findByUser("alice").isEmpty());
            assertEquals(1, repo.findByUser("bob").size());
        }

        @Test
        void given_unknownUser_whenReplaceAll_thenOnlyInsertsNewRows() {
            List<Reservation> rows = new ArrayList<>();
            rows.add(Reservation.createWithExistingId(
                    "ghost-1", "e1", "ghost", 1, "T", "2026-01-01", "", "L"));
            repo.replaceAllForUser("ghost", rows);
            assertEquals(1, repo.findByUser("ghost").size());
            assertEquals("ghost-1", repo.findByUser("ghost").get(0).getId());
        }
    }
}
