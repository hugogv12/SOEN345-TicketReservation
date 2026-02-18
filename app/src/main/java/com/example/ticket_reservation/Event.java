package com.example.ticket_reservation;

/**
 * Simple model for an event (movies, concerts, travel, sports).
 */
public class Event {
    private final String title;
    private final String date;
    private final String location;

    public Event(String title, String date, String location) {
        this.title = title;
        this.date = date;
        this.location = location;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
}
