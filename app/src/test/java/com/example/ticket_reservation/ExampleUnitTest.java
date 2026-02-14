package com.example.ticket_reservation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Example unit tests (JUnit 5) for the Ticket Reservation app.
 * Expand with domain logic tests as features are added.
 */
class ExampleUnitTest {

    @Test
    void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    void appNameIsNotNull() {
        String appName = "Ticket Reservation";
        assertNotNull(appName);
        assertEquals("Ticket Reservation", appName);
    }
}
