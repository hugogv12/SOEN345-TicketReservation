package com.example.ticket_reservation.logic;

import com.example.ticket_reservation.model.Event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for in-memory event filtering ({@link EventFilter}).
 *
 * <p><b>Documented test cases</b></p>
 * <ul>
 *   <li>TC-F-01: Empty criteria returns all non-canceled events when includeCanceled is false.</li>
 *   <li>TC-F-02: Category filter keeps only matching events.</li>
 *   <li>TC-F-03: Location filter is case-insensitive on the criteria side (stored location must match).</li>
 *   <li>TC-F-04: ISO date filter matches exactly one calendar day.</li>
 *   <li>TC-F-05: Search query matches title substring (case-insensitive).</li>
 *   <li>TC-F-06: Combined criteria apply as AND; canceled events excluded for customers.</li>
 *   <li>TC-F-07: Admin view can include canceled events when includeCanceled is true.</li>
 * </ul>
 */
@DisplayName("EventFilter")
class EventFilterTest {

    private List<Event> events;

    @BeforeEach
    void setUp() {
        events = new ArrayList<>();
        events.add(new Event("a", "Concert A", "2026-03-15", "", "Montreal", "Concert", false, 100, 0));
        events.add(new Event("b", "Sports B", "2026-03-20", "", "Montreal", "Sports", false, 200, 50));
        events.add(new Event("c", "Canceled Gig", "2026-03-15", "", "Quebec City", "Concert", true, 50, 0));
    }

    @Nested
    @DisplayName("TC-F-01 / TC-F-07 canceled handling")
    class CanceledHandling {

        @Test
        @DisplayName("TC-F-01: no criteria excludes canceled for customer list")
        void emptyCriteriaExcludesCanceled() {
            List<Event> out = EventFilter.apply(events, FilterCriteria.empty(), false);
            assertEquals(2, out.size());
            assertTrue(out.stream().noneMatch(Event::isCanceled));
        }

        @Test
        @DisplayName("TC-F-07: includeCanceled shows canceled events")
        void includeCanceled() {
            List<Event> out = EventFilter.apply(events, FilterCriteria.empty(), true);
            assertEquals(3, out.size());
        }
    }

    @Test
    @DisplayName("TC-F-02: filter by category")
    void filterByCategory() {
        FilterCriteria c = new FilterCriteria(null, null, null, "Sports");
        List<Event> out = EventFilter.apply(events, c, false);
        assertEquals(1, out.size());
        assertEquals("Sports B", out.get(0).getTitle());
    }

    @Test
    @DisplayName("TC-F-03: filter by location")
    void filterByLocation() {
        FilterCriteria c = new FilterCriteria(null, null, "Quebec City", null);
        List<Event> out = EventFilter.apply(events, c, true);
        assertEquals(1, out.size());
        assertEquals("Canceled Gig", out.get(0).getTitle());
    }

    @Test
    @DisplayName("TC-F-04: filter by ISO date")
    void filterByIsoDate() {
        FilterCriteria c = new FilterCriteria(null, "2026-03-15", null, null);
        List<Event> out = EventFilter.apply(events, c, false);
        assertEquals(1, out.size());
        assertEquals("Concert A", out.get(0).getTitle());
    }

    @Test
    @DisplayName("TC-F-05: search title substring")
    void searchTitle() {
        FilterCriteria c = new FilterCriteria("sport", null, null, null);
        List<Event> out = EventFilter.apply(events, c, false);
        assertEquals(1, out.size());
        assertEquals("Sports B", out.get(0).getTitle());
    }

    @Test
    @DisplayName("TC-F-05b: search is case-insensitive on title")
    void searchTitleCaseInsensitive() {
        FilterCriteria c = new FilterCriteria("SPORT", null, null, null);
        List<Event> out = EventFilter.apply(events, c, false);
        assertEquals(1, out.size());
        assertEquals("Sports B", out.get(0).getTitle());
    }

    @Test
    @DisplayName("TC-F-06: combined AND filters")
    void combinedFilters() {
        FilterCriteria c = new FilterCriteria("con", "2026-03-15", "Montreal", "Concert");
        List<Event> out = EventFilter.apply(events, c, false);
        assertEquals(1, out.size());
        assertEquals("Concert A", out.get(0).getTitle());
    }
}
