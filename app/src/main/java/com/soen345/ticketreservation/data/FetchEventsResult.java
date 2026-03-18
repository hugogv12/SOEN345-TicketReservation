package com.soen345.ticketreservation.data;

import com.soen345.ticketreservation.ui.events_page.Event;

import java.util.Collections;
import java.util.List;

public final class FetchEventsResult {

    private final String errorMessage;
    private final List<Event> events;

    public FetchEventsResult(String errorMessage, List<Event> events) {
        this.errorMessage = errorMessage;
        this.events = events;
    }

    public static FetchEventsResult error(String message) {
        return new FetchEventsResult(message, null);
    }

    public static FetchEventsResult ok(List<Event> events) {
        return new FetchEventsResult(null, events == null ? Collections.emptyList() : events);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<Event> getEvents() {
        return events;
    }
}
