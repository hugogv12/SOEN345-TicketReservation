package com.example.ticket_reservation.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("FilterCriteria")
class FilterCriteriaTest {

    @Test
    @DisplayName("empty() has no constraints")
    void emptyHasNullDimensions() {
        FilterCriteria c = FilterCriteria.empty();
        assertEquals("", c.getSearchQuery());
        assertNull(c.getIsoDate());
        assertNull(c.getLocation());
        assertNull(c.getCategory());
    }

    @Test
    @DisplayName("trims search; blank date/location/category become null")
    void normalization() {
        FilterCriteria c = new FilterCriteria("  jazz  ", "  ", "   ", "\t");
        assertEquals("jazz", c.getSearchQuery());
        assertNull(c.getIsoDate());
        assertNull(c.getLocation());
        assertNull(c.getCategory());
    }

    @Test
    @DisplayName("ALL sentinel clears location and category")
    void allSentinel() {
        FilterCriteria c = new FilterCriteria(null, null, "ALL", "all");
        assertNull(c.getLocation());
        assertNull(c.getCategory());
    }

    @Test
    @DisplayName("equals and hashCode by value")
    void valueEquality() {
        FilterCriteria a = new FilterCriteria("x", "2026-01-01", "Montreal", "Sports");
        FilterCriteria b = new FilterCriteria("x", "2026-01-01", "Montreal", "Sports");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        FilterCriteria c = new FilterCriteria("y", "2026-01-01", "Montreal", "Sports");
        assertNotEquals(a, c);
    }
}
