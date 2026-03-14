package com.example.ticket_reservation.logic;

import com.example.ticket_reservation.model.Event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for reservation validation rules ({@link ReservationRules}).
 *
 * <p><b>Documented test cases</b></p>
 * <ul>
 *   <li>TC-R-01: Valid event and positive quantity within availability returns true.</li>
 *   <li>TC-R-02: Canceled event cannot be reserved.</li>
 *   <li>TC-R-03: Zero or negative quantity rejected.</li>
 *   <li>TC-R-04: Request exceeding available tickets rejected.</li>
 *   <li>TC-R-05: Null event rejected.</li>
 *   <li>TC-R-06: Exact remaining capacity allowed.</li>
 * </ul>
 */
@DisplayName("ReservationRules")
class ReservationRulesTest {

    private static Event active(int capacity, int reserved) {
        return new Event("id", "T", "2026-01-01", "L", "C", false, capacity, reserved);
    }

    @Test
    @DisplayName("TC-R-01: normal reservation allowed")
    void allowsWhenAvailable() {
        assertTrue(ReservationRules.canReserve(active(10, 3), 5));
    }

    @Test
    @DisplayName("TC-R-02: canceled event blocked")
    void canceledBlocked() {
        Event e = new Event("id", "T", "2026-01-01", "L", "C", true, 10, 0);
        assertFalse(ReservationRules.canReserve(e, 1));
    }

    @Test
    @DisplayName("TC-R-03: non-positive quantity")
    void nonPositiveQuantity() {
        assertFalse(ReservationRules.canReserve(active(10, 0), 0));
        assertFalse(ReservationRules.canReserve(active(10, 0), -1));
    }

    @Test
    @DisplayName("TC-R-04: over capacity")
    void overCapacity() {
        assertFalse(ReservationRules.canReserve(active(10, 8), 3));
    }

    @Test
    @DisplayName("TC-R-05: null event")
    void nullEvent() {
        assertFalse(ReservationRules.canReserve(null, 1));
    }

    @Test
    @DisplayName("TC-R-06: exact availability")
    void exactAvailability() {
        assertTrue(ReservationRules.canReserve(active(10, 7), 3));
    }
}
