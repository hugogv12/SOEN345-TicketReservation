package com.soen345.ticketreservation.ui.events_page;

/**
 * Lightweight event DTO for {@link com.soen345.ticketreservation.data.SupabaseClient} list APIs.
 */
public final class Event {

    private final String id;
    private final String title;
    private final String description;
    private final String category;
    private final String location;
    private final String date;
    private final int availableTickets;
    private final double price;
    private boolean reservedByCurrentUser;

    public Event(
            String id,
            String title,
            String description,
            String category,
            String location,
            String date,
            int availableTickets,
            double price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.date = date;
        this.availableTickets = availableTickets;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public double getPrice() {
        return price;
    }

    public boolean isReservedByCurrentUser() {
        return reservedByCurrentUser;
    }

    public void setReservedByCurrentUser(boolean reservedByCurrentUser) {
        this.reservedByCurrentUser = reservedByCurrentUser;
    }
}
