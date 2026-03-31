package com.example.ticket_reservation.logic;

import com.example.ticket_reservation.model.Event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("EventFilter (parameterized search)")
class EventFilterParameterizedTest {

    private List<Event> events;

    @BeforeEach
    void setUp() {
        events = new ArrayList<>();
        events.add(new Event("1", "Summer Jazz Night", "2026-04-01", "", "Montreal", "Concert", false, 50, 0));
        events.add(new Event("2", "Hockey Finals", "2026-04-02", "", "Montreal", "Sports", false, 200, 0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jazz", "JAZZ", "JaZz", " jazz "})
    @DisplayName("title search is case-insensitive and trimmed via criteria")
    void searchCaseInsensitiveVariants(String rawQuery) {
        FilterCriteria c = new FilterCriteria(rawQuery, null, null, null);
        List<Event> out = EventFilter.apply(events, c, false);
        assertEquals(1, out.size());
        assertEquals("Summer Jazz Night", out.get(0).getTitle());
    }
}
