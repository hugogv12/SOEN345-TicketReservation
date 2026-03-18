package com.soen345.ticketreservation.data;

import com.soen345.ticketreservation.admin.AdminEvent;

import java.util.Collections;
import java.util.List;

public final class AdminEventsFetchResult {

    private final List<AdminEvent> events;
    private final String error;

    private AdminEventsFetchResult(List<AdminEvent> events, String error) {
        this.events = events;
        this.error = error;
    }

    public static AdminEventsFetchResult ok(List<AdminEvent> events) {
        return new AdminEventsFetchResult(events == null ? Collections.emptyList() : events, null);
    }

    public static AdminEventsFetchResult error(String message) {
        return new AdminEventsFetchResult(null, message);
    }

    public List<AdminEvent> getEvents() {
        return events;
    }

    public String getError() {
        return error;
    }
}
