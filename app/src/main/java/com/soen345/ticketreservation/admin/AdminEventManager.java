package com.soen345.ticketreservation.admin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory admin catalog with validation rules exercised by unit tests.
 */
public final class AdminEventManager {

    private final Map<String, AdminEvent> eventsById = new LinkedHashMap<>();

    public void addEvent(AdminEvent event) {
        Objects.requireNonNull(event, "event");
        validateIdForAdd(event.getId());
        validateMutableFields(event);
        if (eventsById.containsKey(event.getId())) {
            throw new IllegalStateException("Event with ID " + event.getId() + " already exists");
        }
        eventsById.put(event.getId(), event);
    }

    public void editEvent(AdminEvent event) {
        Objects.requireNonNull(event, "event");
        validateIdAlways(event.getId());
        validateMutableFields(event);
        if (!eventsById.containsKey(event.getId())) {
            throw new IllegalStateException("Event with ID " + event.getId() + " does not exist");
        }
        eventsById.put(event.getId(), event);
    }

    public void cancelEvent(String id) {
        validateIdAlways(id);
        AdminEvent existing = eventsById.get(id);
        if (existing == null) {
            throw new IllegalStateException("Event with ID " + id + " does not exist");
        }
        if (existing.isCancelled()) {
            return;
        }
        eventsById.put(
                id,
                existing.copy(null, null, null, null, null, null, null, null, true));
    }

    public AdminEvent getEventById(String id) {
        return eventsById.get(id);
    }

    public List<AdminEvent> getAllEvents() {
        return new ArrayList<>(eventsById.values());
    }

    public void clear() {
        eventsById.clear();
    }

    private static void validateIdForAdd(String id) {
        if (isBlank(id)) {
            throw new IllegalArgumentException("Event ID cannot be empty");
        }
    }

    private static void validateIdAlways(String id) {
        if (isBlank(id)) {
            throw new IllegalArgumentException("Event ID cannot be empty");
        }
    }

    private static void validateMutableFields(AdminEvent event) {
        if (isBlank(event.getTitle())) {
            throw new IllegalArgumentException("Event title cannot be empty");
        }
        if (isBlank(event.getDescription())) {
            throw new IllegalArgumentException("Event description cannot be empty");
        }
        if (isBlank(event.getCategoryId())) {
            throw new IllegalArgumentException("Event category cannot be empty");
        }
        if (isBlank(event.getLocation())) {
            throw new IllegalArgumentException("Event location cannot be empty");
        }
        if (isBlank(event.getDate())) {
            throw new IllegalArgumentException("Event date cannot be empty");
        }
        if (event.getPrice() < 0) {
            throw new IllegalArgumentException("Event price cannot be negative");
        }
        if (event.getAvailableTickets() < 0) {
            throw new IllegalArgumentException("Available tickets cannot be negative");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
