package com.soen345.ticketreservation.admin;

import java.util.Objects;

/**
 * Admin-side event record used by {@link AdminEventManager} (plain Java, unit-testable).
 */
public final class AdminEvent {

    private final String id;
    private final String title;
    private final String description;
    private final String categoryId;
    private final String location;
    private final String date;
    private final int availableTickets;
    private final double price;
    private final boolean cancelled;

    public AdminEvent(
            String id,
            String title,
            String description,
            String categoryId,
            String location,
            String date,
            int availableTickets,
            double price,
            boolean cancelled) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.categoryId = Objects.requireNonNull(categoryId);
        this.location = Objects.requireNonNull(location);
        this.date = Objects.requireNonNull(date);
        this.availableTickets = availableTickets;
        this.price = price;
        this.cancelled = cancelled;
    }

    public AdminEvent(
            String id,
            String title,
            String description,
            String categoryId,
            String location,
            String date,
            int availableTickets,
            double price) {
        this(id, title, description, categoryId, location, date, availableTickets, price, false);
    }

    /**
     * Kotlin-style copy: pass {@code null} for any argument to keep the existing value.
     */
    public AdminEvent copy(
            String id,
            String title,
            String description,
            String categoryId,
            String location,
            String date,
            Integer availableTickets,
            Double price,
            Boolean cancelled) {
        return new AdminEvent(
                id != null ? id : this.id,
                title != null ? title : this.title,
                description != null ? description : this.description,
                categoryId != null ? categoryId : this.categoryId,
                location != null ? location : this.location,
                date != null ? date : this.date,
                availableTickets != null ? availableTickets : this.availableTickets,
                price != null ? price : this.price,
                cancelled != null ? cancelled : this.cancelled);
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

    public String getCategoryId() {
        return categoryId;
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

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminEvent that = (AdminEvent) o;
        return availableTickets == that.availableTickets
                && Double.compare(that.price, price) == 0
                && cancelled == that.cancelled
                && id.equals(that.id)
                && title.equals(that.title)
                && description.equals(that.description)
                && categoryId.equals(that.categoryId)
                && location.equals(that.location)
                && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, categoryId, location, date, availableTickets, price, cancelled);
    }
}
