package com.example.ticket_reservation.model;

import com.example.ticket_reservation.logic.ReservationRules;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Event shown to customers and managed by administrators.
 * Dates are stored as ISO-8601 date strings (yyyy-MM-dd). Start time is optional HH:mm (24h).
 */
public class Event {

    private final String id;
    private String title;
    private String isoDate;
    /** Local start time HH:mm, or empty if not set */
    private String startTime;
    private String location;
    private String category;
    private boolean canceled;
    private int capacity;
    private int ticketsReserved;

    public Event(String id, String title, String isoDate, String startTime, String location, String category,
                 boolean canceled, int capacity, int ticketsReserved) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.isoDate = Objects.requireNonNull(isoDate);
        this.startTime = normalizeStoredTime(startTime);
        this.location = Objects.requireNonNull(location);
        this.category = Objects.requireNonNull(category);
        this.canceled = canceled;
        this.capacity = capacity;
        this.ticketsReserved = ticketsReserved;
    }

    public static Event createNew(String title, String isoDate, String startTime, String location, String category,
                                  int capacity) {
        return new Event(UUID.randomUUID().toString(), title, isoDate, startTime, location, category,
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

    /** HH:mm (24h) or empty */
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = normalizeStoredTime(startTime);
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

    /** Time only, e.g. 7:30 PM; empty string if no start time. */
    public String getTimeDisplay(Locale locale) {
        return formatHhMmForDisplay(startTime, locale);
    }

    /** Date and optional time for list/detail/confirm, e.g. "Mar 15, 2026 · 7:30 PM". */
    public String getDateTimeDisplay(Locale locale) {
        String d = getDateDisplay();
        String t = getTimeDisplay(locale);
        if (t.isEmpty()) {
            return d;
        }
        return d + " · " + t;
    }

    /** Uses {@link Locale#getDefault()}. */
    public String getDateTimeDisplay() {
        return getDateTimeDisplay(Locale.getDefault());
    }

    public static String formatHhMmForDisplay(String hhmm, Locale locale) {
        if (hhmm == null || hhmm.isEmpty()) {
            return "";
        }
        String norm = normalizeStoredTime(hhmm);
        if (norm.isEmpty()) {
            return "";
        }
        try {
            SimpleDateFormat in = new SimpleDateFormat("HH:mm", Locale.US);
            in.setLenient(false);
            Date parsed = in.parse(norm);
            if (parsed == null) {
                return hhmm;
            }
            return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(parsed);
        } catch (ParseException e) {
            return hhmm;
        }
    }

    private static String normalizeStoredTime(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) {
            return "";
        }
        if (s.length() >= 5 && s.charAt(2) == ':') {
            return s.substring(0, 5);
        }
        return s;
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
