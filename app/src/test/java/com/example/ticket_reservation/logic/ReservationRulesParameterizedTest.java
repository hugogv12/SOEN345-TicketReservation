package com.example.ticket_reservation.logic;

import com.example.ticket_reservation.model.Event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Table-driven checks for {@link ReservationRules#canReserve(Event, int)}.
 */
@DisplayName("ReservationRules (parameterized)")
class ReservationRulesParameterizedTest {

    private static Event ev(int capacity, int reserved, boolean canceled) {
        return new Event("id", "T", "2026-01-01", "", "L", "C", canceled, capacity, reserved);
    }

    @ParameterizedTest(name = "cap={0} res={1} qty={2} → {3}")
    @CsvSource({
            "10, 0, 1, true",
            "10, 0, 10, true",
            "10, 9, 1, true",
            "10, 0, 11, false",
            "10, 8, 3, false",
            "5, 0, 0, false",
            "5, 0, -2, false",
    })
    @DisplayName("capacity / reserved / quantity expectations")
    void canReserveMatrix(int capacity, int reserved, int qty, boolean expected) {
        assertEquals(expected, ReservationRules.canReserve(ev(capacity, reserved, false), qty));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 100})
    @DisplayName("canceled events never allow reserve")
    void canceledAlwaysFalse(int qty) {
        Event canceled = ev(50, 0, true);
        assertFalse(ReservationRules.canReserve(canceled, qty));
    }
}
