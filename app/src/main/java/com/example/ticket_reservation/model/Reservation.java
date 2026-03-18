package com.example.ticket_reservation.model;

import java.util.Objects;
import java.util.UUID;

/**
 * A user's ticket reservation for an event: quantity held and snapshots of event metadata.
 */
public class Reservation {

    private final String id;
    private final String eventId;
    private final String userKey;
    private final int quantity;
    private final String eventTitleSnapshot;
    private final String eventIsoDateSnapshot;
    /** HH:mm at booking; empty if event had no start time */
    private final String eventStartTimeSnapshot;
    private final String eventLocationSnapshot;

    public Reservation(String id, String eventId, String userKey, int quantity,
                       String eventTitleSnapshot, String eventIsoDateSnapshot,
                       String eventStartTimeSnapshot,
                       String eventLocationSnapshot) {
        this.id = Objects.requireNonNull(id);
        this.eventId = Objects.requireNonNull(eventId);
        this.userKey = Objects.requireNonNull(userKey);
        this.quantity = quantity;
        this.eventTitleSnapshot = eventTitleSnapshot;
        this.eventIsoDateSnapshot = eventIsoDateSnapshot;
        this.eventStartTimeSnapshot = eventStartTimeSnapshot != null ? eventStartTimeSnapshot : "";
        this.eventLocationSnapshot = eventLocationSnapshot;
    }

    public static Reservation create(String eventId, String userKey, int quantity,
                                     String eventTitleSnapshot, String eventIsoDateSnapshot,
                                     String eventStartTimeSnapshot,
                                     String eventLocationSnapshot) {
        return new Reservation(UUID.randomUUID().toString(), eventId, userKey, quantity,
                eventTitleSnapshot, eventIsoDateSnapshot, eventStartTimeSnapshot, eventLocationSnapshot);
    }

    public static Reservation createWithExistingId(String id, String eventId, String userKey, int quantity,
                                                   String eventTitleSnapshot, String eventIsoDateSnapshot,
                                                   String eventStartTimeSnapshot,
                                                   String eventLocationSnapshot) {
        return new Reservation(id, eventId, userKey, quantity,
                eventTitleSnapshot, eventIsoDateSnapshot, eventStartTimeSnapshot, eventLocationSnapshot);
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

    public String getEventStartTimeSnapshot() {
        return eventStartTimeSnapshot;
    }

    public String getEventLocationSnapshot() {
        return eventLocationSnapshot;
    }
}
