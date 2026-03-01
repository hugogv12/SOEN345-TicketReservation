package com.example.ticket_reservation.model;

import com.example.ticket_reservation.logic.ReservationRules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Event shown to customers and managed by administrators.
 * Dates are stored as ISO-8601 date strings (yyyy-MM-dd) for simple filtering and API 24 compatibility.
 */
public class Event {

    private final String id;
    private String title;
    private String isoDate;
    private String location;
    private String category;
    private boolean canceled;
    private int capacity;
    private int ticketsReserved;

    public Event(String id, String title, String isoDate, String location, String category,
                 boolean canceled, int capacity, int ticketsReserved) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.isoDate = Objects.requireNonNull(isoDate);
        this.location = Objects.requireNonNull(location);
        this.category = Objects.requireNonNull(category);
        this.canceled = canceled;
        this.capacity = capacity;
        this.ticketsReserved = ticketsReserved;
    }

    public static Event createNew(String title, String isoDate, String location, String category,
                                  int capacity) {
        return new Event(UUID.randomUUID().toString(), title, isoDate, location, category,
                false, capacity, 0);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /** ISO local date yyyy-MM-dd */
    public String getIsoDate() {
        return isoDate;
    }

    public void setIsoDate(String isoDate) {
        this.isoDate = isoDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getTicketsReserved() {
        return ticketsReserved;
    }

    public int getAvailableTickets() {
        return Math.max(0, capacity - ticketsReserved);
    }

    /** Atomically increase reserved count if {@link ReservationRules#canReserve} passes. */
    public boolean applyReservation(int quantity) {
        if (!ReservationRules.canReserve(this, quantity)) {
            return false;
        }
        ticketsReserved += quantity;
        return true;
    }

    /** Release tickets back to inventory (e.g. cancellation). */
    public boolean releaseTickets(int quantity) {
        if (quantity <= 0 || ticketsReserved < quantity) {
            return false;
        }
        ticketsReserved -= quantity;
        return true;
    }

    public String getDateDisplay() {
        try {
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            iso.setLenient(false);
            Date d = iso.parse(isoDate);
            if (d == null) {
                return isoDate;
            }
            SimpleDateFormat out = new SimpleDateFormat("MMM d, yyyy", Locale.US);
            return out.format(d);
        } catch (ParseException e) {
            return isoDate;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
