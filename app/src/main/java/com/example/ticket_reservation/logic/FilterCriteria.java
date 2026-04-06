package com.example.ticket_reservation.logic;

import java.util.Objects;

/**
 * In-memory filter state for the event list (search + date + location + category).
 * Null or blank fields mean "no constraint" for that dimension.
 */
public final class FilterCriteria {

    private final String searchQuery;
    private final String isoDate;
    private final String location;
    private final String category;

    public FilterCriteria(String searchQuery, String isoDate, String location, String category) {
        this.searchQuery = searchQuery == null ? "" : searchQuery.trim();
        this.isoDate = blankToNull(isoDate);
        this.location = blankToNull(location);
        this.category = blankToNull(category);
    }

    private static String blankToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() || "ALL".equalsIgnoreCase(t) ? null : t;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getIsoDate() {
        return isoDate;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public static FilterCriteria empty() {
        return new FilterCriteria(null, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilterCriteria that = (FilterCriteria) o;
        return Objects.equals(searchQuery, that.searchQuery)
                && Objects.equals(isoDate, that.isoDate)
                && Objects.equals(location, that.location)
                && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchQuery, isoDate, location, category);
    }
}
