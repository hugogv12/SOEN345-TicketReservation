package com.soen345.ticketreservation.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AdminEventManager")
class AdminEventManagerTest {

    private AdminEventManager manager;
    private AdminEvent validEvent;

    @BeforeEach
    void setUp() {
        manager = new AdminEventManager();
        validEvent = new AdminEvent(
                "event1",
                "Test Event",
                "A test event",
                "cat1",
                "Test Location",
                "2026-03-30",
                100,
                50.0);
    }

    @Test
    void testAddEventSuccess() {
        manager.addEvent(validEvent);
        AdminEvent retrieved = manager.getEventById("event1");
        assertEquals(validEvent, retrieved);
    }

    @Test
    void testAddEventWithBlankId() {
        AdminEvent invalid = validEvent.copy("", null, null, null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Event ID cannot be empty", ex.getMessage());
    }

    @Test
    void testAddEventWithBlankTitle() {
        AdminEvent invalid = validEvent.copy(null, "", null, null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Event title cannot be empty", ex.getMessage());
    }

    @Test
    void testAddEventWithWhitespaceTitle() {
        AdminEvent invalid = validEvent.copy(null, "   ", null, null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Event title cannot be empty", ex.getMessage());
    }

    @Test
    void testAddEventWithBlankDescription() {
        AdminEvent invalid = validEvent.copy(null, null, "", null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Event description cannot be empty", ex.getMessage());
    }

    @Test
    void testAddEventWithBlankCategory() {
        AdminEvent invalid = validEvent.copy(null, null, null, "", null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Event category cannot be empty", ex.getMessage());
    }

    @Test
    void testAddEventWithBlankLocation() {
        AdminEvent invalid = validEvent.copy(null, null, null, null, "", null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Event location cannot be empty", ex.getMessage());
    }

    @Test
    void testAddEventWithBlankDate() {
        AdminEvent invalid = validEvent.copy(null, null, null, null, null, "", null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Event date cannot be empty", ex.getMessage());
    }

    @Test
    void testAddEventWithNegativePrice() {
        AdminEvent invalid = validEvent.copy(null, null, null, null, null, null, null, -10.0, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Event price cannot be negative", ex.getMessage());
    }

    @Test
    void testAddEventWithNegativeTickets() {
        AdminEvent invalid = validEvent.copy(null, null, null, null, null, null, -5, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(invalid));
        assertEquals("Available tickets cannot be negative", ex.getMessage());
    }

    @Test
    void testAddEventWithDuplicateId() {
        manager.addEvent(validEvent);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> manager.addEvent(validEvent));
        assertEquals("Event with ID event1 already exists", ex.getMessage());
    }

    @Test
    void testEditEventSuccess() {
        manager.addEvent(validEvent);
        AdminEvent updated = validEvent.copy(null, "Updated Event", null, null, null, null, null, 75.0, null);
        manager.editEvent(updated);
        AdminEvent retrieved = manager.getEventById("event1");
        assertEquals(updated, retrieved);
        assertEquals("Updated Event", retrieved.getTitle());
        assertEquals(75.0, retrieved.getPrice(), 0.0);
    }

    @Test
    void testEditEventWithBlankTitle() {
        manager.addEvent(validEvent);
        AdminEvent invalid = validEvent.copy(null, "", null, null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(invalid));
        assertEquals("Event title cannot be empty", ex.getMessage());
    }

    @Test
    void testEditEventWithBlankDescription() {
        manager.addEvent(validEvent);
        AdminEvent invalid = validEvent.copy(null, null, "", null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(invalid));
        assertEquals("Event description cannot be empty", ex.getMessage());
    }

    @Test
    void testEditEventWithBlankCategory() {
        manager.addEvent(validEvent);
        AdminEvent invalid = validEvent.copy(null, null, null, "", null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(invalid));
        assertEquals("Event category cannot be empty", ex.getMessage());
    }

    @Test
    void testEditEventWithBlankLocation() {
        manager.addEvent(validEvent);
        AdminEvent invalid = validEvent.copy(null, null, null, null, "", null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(invalid));
        assertEquals("Event location cannot be empty", ex.getMessage());
    }

    @Test
    void testEditEventWithBlankDate() {
        manager.addEvent(validEvent);
        AdminEvent invalid = validEvent.copy(null, null, null, null, null, "", null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(invalid));
        assertEquals("Event date cannot be empty", ex.getMessage());
    }

    @Test
    void testEditEventWithNegativePrice() {
        manager.addEvent(validEvent);
        AdminEvent invalid = validEvent.copy(null, null, null, null, null, null, null, -5.0, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(invalid));
        assertEquals("Event price cannot be negative", ex.getMessage());
    }

    @Test
    void testEditEventWithNegativeTickets() {
        manager.addEvent(validEvent);
        AdminEvent invalid = validEvent.copy(null, null, null, null, null, null, -10, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(invalid));
        assertEquals("Available tickets cannot be negative", ex.getMessage());
    }

    @Test
    void testEditEventNotFound() {
        AdminEvent missing = validEvent.copy("nonexistent", null, null, null, null, null, null, null, null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> manager.editEvent(missing));
        assertEquals("Event with ID nonexistent does not exist", ex.getMessage());
    }

    @Test
    void testCancelEventSuccess() {
        manager.addEvent(validEvent);
        manager.cancelEvent("event1");
        AdminEvent retrieved = manager.getEventById("event1");
        assertTrue(retrieved.isCancelled());
    }

    @Test
    void testCancelEventPreservesOtherFields() {
        manager.addEvent(validEvent);
        manager.cancelEvent("event1");
        AdminEvent retrieved = manager.getEventById("event1");
        assertEquals("Test Event", retrieved.getTitle());
        assertEquals(50.0, retrieved.getPrice(), 0.0);
        assertEquals(100, retrieved.getAvailableTickets());
    }

    @Test
    void testCancelEventNotFound() {
        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> manager.cancelEvent("nonexistent"));
        assertEquals("Event with ID nonexistent does not exist", ex.getMessage());
    }

    @Test
    void testCancelAlreadyCancelledEvent() {
        manager.addEvent(validEvent);
        manager.cancelEvent("event1");
        manager.cancelEvent("event1");
        AdminEvent retrieved = manager.getEventById("event1");
        assertTrue(retrieved.isCancelled());
    }

    @Test
    void testGetEventByIdFound() {
        manager.addEvent(validEvent);
        assertEquals(validEvent, manager.getEventById("event1"));
    }

    @Test
    void testGetEventByIdNotFound() {
        assertNull(manager.getEventById("nonexistent"));
    }

    @Test
    void testGetAllEventsEmpty() {
        assertEquals(0, manager.getAllEvents().size());
    }

    @Test
    void testGetAllEventsSingle() {
        manager.addEvent(validEvent);
        var events = manager.getAllEvents();
        assertEquals(1, events.size());
        assertEquals(validEvent, events.get(0));
    }

    @Test
    void testGetAllEventsMultiple() {
        AdminEvent event2 = validEvent.copy("event2", "Second Event", null, null, null, null, null, null, null);
        AdminEvent event3 = validEvent.copy("event3", "Third Event", null, null, null, null, null, null, null);
        manager.addEvent(validEvent);
        manager.addEvent(event2);
        manager.addEvent(event3);
        assertEquals(3, manager.getAllEvents().size());
    }

    @Test
    void testAddEventWithZeroPrice() {
        AdminEvent free = validEvent.copy(null, null, null, null, null, null, null, 0.0, null);
        manager.addEvent(free);
        assertEquals(0.0, manager.getEventById("event1").getPrice(), 0.0);
    }

    @Test
    void testAddEventWithZeroTickets() {
        AdminEvent soldOut = validEvent.copy(null, null, null, null, null, null, 0, null, null);
        manager.addEvent(soldOut);
        assertEquals(0, manager.getEventById("event1").getAvailableTickets());
    }

    @Test
    void addEventThrowsWhenIdIsWhitespaceOnly() {
        AdminEvent bad = validEvent.copy("   ", null, null, null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(bad));
        assertEquals("Event ID cannot be empty", ex.getMessage());
    }

    @Test
    void addEventThrowsWhenDescriptionIsWhitespaceOnly() {
        AdminEvent bad = validEvent.copy(null, null, "\t", null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(bad));
        assertEquals("Event description cannot be empty", ex.getMessage());
    }

    @Test
    void addEventThrowsWhenCategoryIdIsWhitespaceOnly() {
        AdminEvent bad = validEvent.copy(null, null, null, " ", null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(bad));
        assertEquals("Event category cannot be empty", ex.getMessage());
    }

    @Test
    void addEventThrowsWhenLocationIsWhitespaceOnly() {
        AdminEvent bad = validEvent.copy(null, null, null, null, "  ", null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(bad));
        assertEquals("Event location cannot be empty", ex.getMessage());
    }

    @Test
    void addEventThrowsWhenDateIsWhitespaceOnly() {
        AdminEvent bad = validEvent.copy(null, null, null, null, null, " ", null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.addEvent(bad));
        assertEquals("Event date cannot be empty", ex.getMessage());
    }

    @Test
    void editEventThrowsIllegalArgumentExceptionForBlankIdBeforeCheckingExistence() {
        AdminEvent bad = validEvent.copy("", null, null, null, null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(bad));
        assertEquals("Event ID cannot be empty", ex.getMessage());
    }

    @Test
    void editEventThrowsForWhitespaceOnlyLocation() {
        manager.addEvent(validEvent);
        AdminEvent bad = validEvent.copy(null, null, null, null, "  ", null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(bad));
        assertEquals("Event location cannot be empty", ex.getMessage());
    }

    @Test
    void editEventThrowsForWhitespaceOnlyDate() {
        manager.addEvent(validEvent);
        AdminEvent bad = validEvent.copy(null, null, null, null, null, "\n", null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(bad));
        assertEquals("Event date cannot be empty", ex.getMessage());
    }

    @Test
    void editEventThrowsForWhitespaceOnlyCategoryId() {
        manager.addEvent(validEvent);
        AdminEvent bad = validEvent.copy(null, null, null, " ", null, null, null, null, null);
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> manager.editEvent(bad));
        assertEquals("Event category cannot be empty", ex.getMessage());
    }

    @Test
    void clearEmptiesAllEvents() {
        manager.addEvent(validEvent);
        manager.addEvent(validEvent.copy("event2", null, null, null, null, null, null, null, null));
        manager.clear();
        assertEquals(0, manager.getAllEvents().size());
    }

    @Test
    void afterClearSameIdCanBeAddedAgain() {
        manager.addEvent(validEvent);
        manager.clear();
        manager.addEvent(validEvent);
        assertEquals(1, manager.getAllEvents().size());
    }

    @Test
    void getAllEventsReturnsIndependentSnapshot() {
        manager.addEvent(validEvent);
        var snapshot = manager.getAllEvents();
        assertEquals(1, snapshot.size());
        assertEquals(1, manager.getAllEvents().size());
    }

    @Test
    void cancelEventOnAlreadyCancelledEventKeepsIsCancelledTrue() {
        AdminEvent cancelled = validEvent.copy(null, null, null, null, null, null, null, null, true);
        manager.addEvent(cancelled);
        manager.cancelEvent("event1");
        assertTrue(manager.getEventById("event1").isCancelled());
    }

    @Test
    void addEventAcceptsPriceOfExactlyZero() {
        manager.addEvent(validEvent.copy(null, null, null, null, null, null, null, 0.0, null));
        assertNotNull(manager.getEventById("event1"));
    }

    @Test
    void addEventAcceptsAvailableTicketsOfExactlyZero() {
        manager.addEvent(validEvent.copy(null, null, null, null, null, null, 0, null, null));
        assertEquals(0, manager.getEventById("event1").getAvailableTickets());
    }
}
