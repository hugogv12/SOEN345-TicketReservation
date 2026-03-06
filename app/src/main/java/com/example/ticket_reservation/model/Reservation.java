package com.example.ticket_reservation.model;

import java.util.Objects;
import java.util.UUID;

/**
 * A user's ticket hold for an event (in-memory / local only; no payment).
 */
public class Reservation {

    private final String id;
    private final String eventId;
    private final String userKey;
    private final int quantity;
    private final String eventTitleSnapshot;
    private final String eventIsoDateSnapshot;
    private final String eventLocationSnapshot;

    public Reservation(String id, String eventId, String userKey, int quantity,
                       String eventTitleSnapshot, String eventIsoDateSnapshot,
                       String eventLocationSnapshot) {
        this.id = Objects.requireNonNull(id);
        this.eventId = Objects.requireNonNull(eventId);
        this.userKey = Objects.requireNonNull(userKey);
        this.quantity = quantity;
        this.eventTitleSnapshot = eventTitleSnapshot;
        this.eventIsoDateSnapshot = eventIsoDateSnapshot;
        this.eventLocationSnapshot = eventLocationSnapshot;
    }

    public static Reservation create(String eventId, String userKey, int quantity,
                                     String eventTitleSnapshot, String eventIsoDateSnapshot,
                                     String eventLocationSnapshot) {
        return new Reservation(UUID.randomUUID().toString(), eventId, userKey, quantity,
                eventTitleSnapshot, eventIsoDateSnapshot, eventLocationSnapshot);
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserKey() {
        return userKey;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getEventTitleSnapshot() {
        return eventTitleSnapshot;
    }

    public String getEventIsoDateSnapshot() {
        return eventIsoDateSnapshot;
    }

    public String getEventLocationSnapshot() {
        return eventLocationSnapshot;
    }
}
