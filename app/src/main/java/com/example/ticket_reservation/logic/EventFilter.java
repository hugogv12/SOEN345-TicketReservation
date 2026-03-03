package com.example.ticket_reservation.logic;

import com.example.ticket_reservation.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pure in-memory filtering for events (used by the UI and covered by unit tests).
 */
public final class EventFilter {

    private EventFilter() {
    }

    /**
     * @param includeCanceled when false, canceled events are removed before other criteria apply
     */
    public static List<Event> apply(Iterable<Event> source, FilterCriteria criteria, boolean includeCanceled) {
        FilterCriteria c = criteria == null ? FilterCriteria.empty() : criteria;
        List<Event> out = new ArrayList<>();
        String qLower = c.getSearchQuery().toLowerCase(Locale.US);
        for (Event e : source) {
            if (!includeCanceled && e.isCanceled()) {
                continue;
            }
            if (!qLower.isEmpty() && !e.getTitle().toLowerCase(Locale.US).contains(qLower)) {
                continue;
            }
            if (c.getIsoDate() != null && !c.getIsoDate().equals(e.getIsoDate())) {
                continue;
            }
            if (c.getLocation() != null && !c.getLocation().equalsIgnoreCase(e.getLocation())) {
                continue;
            }
            if (c.getCategory() != null && !c.getCategory().equalsIgnoreCase(e.getCategory())) {
                continue;
            }
            out.add(e);
        }
        return out;
    }
}
