package com.example.ticket_reservation.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Row model for the ticketing mock {@code events} table (SQLite).
 * Not to be confused with {@link com.example.ticket_reservation.model.Event} (in-memory domain).
 */
public class Event {

    public static final String STATUS_UPCOMING = "upcoming";
    public static final String STATUS_FULL = "full";
    public static final String STATUS_CANCELLED = "cancelled";

    private final int id;
    @NonNull
    private final String title;
    @NonNull
    private final String description;
    @NonNull
    private final String category;
    @NonNull
    private final String organizer;
    @NonNull
    private final String venue;
    @NonNull
    private final String city;
    @NonNull
    private final String date;
    @NonNull
    private final String startTime;
    @NonNull
    private final String endTime;
    private final int capacity;
    private final int registeredCount;
    private final double price;
    private final boolean free;
    @NonNull
    private final String status;
    @NonNull
    private final String imageName;
    @NonNull
    private final String contactEmail;

    public Event(int id, @NonNull String title, @NonNull String description, @NonNull String category,
                 @NonNull String organizer, @NonNull String venue, @NonNull String city,
                 @NonNull String date, @NonNull String startTime, @NonNull String endTime,
                 int capacity, int registeredCount, double price, boolean free,
                 @NonNull String status, @NonNull String imageName, @NonNull String contactEmail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.organizer = organizer;
        this.venue = venue;
        this.city = city;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.registeredCount = registeredCount;
        this.price = price;
        this.free = free;
        this.status = status;
        this.imageName = imageName;
        this.contactEmail = contactEmail;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    @NonNull
    public String getOrganizer() {
        return organizer;
    }

    @NonNull
    public String getVenue() {
        return venue;
    }

    @NonNull
    public String getCity() {
        return city;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public String getStartTime() {
        return startTime;
    }

    @NonNull
    public String getEndTime() {
        return endTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public double getPrice() {
        return price;
    }

    public boolean isFree() {
        return free;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    @NonNull
    public String getImageName() {
        return imageName;
    }

    @NonNull
    public String getContactEmail() {
        return contactEmail;
    }

    public int getAvailableSeats() {
        return Math.max(0, capacity - registeredCount);
    }

    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return id == event.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
