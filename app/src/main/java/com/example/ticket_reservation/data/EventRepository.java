package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * In-memory event store shared by customer, admin, and booking flows.
 */
public class EventRepository {

    private static EventRepository instance;

    private final List<Event> events = new ArrayList<>();

    public EventRepository() {
    }

    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
            instance.seedSampleData();
        }
        return instance;
    }

    /**
     * Testing only: replace singleton with a fresh repository.
     */
    public static synchronized void resetSingletonForTests() {
        instance = null;
    }

    public synchronized void seedSampleData() {
        if (!events.isEmpty()) {
            return;
        }
        events.add(Event.createNew("Summer Concert 2026", "2026-03-15", "Bell Centre, Montreal",
                "Concert", 500));
        events.add(Event.createNew("Tech Conference", "2026-03-20", "Palais des congrès, Montreal",
                "Conference", 200));
        events.add(Event.createNew("Hockey Game", "2026-03-25", "Bell Centre, Montreal",
                "Sports", 800));
        events.add(Event.createNew("Indie Film Night", "2026-04-02", "Cinéma du Parc, Montreal",
                "Movie", 120));
    }

    public synchronized List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }

    public synchronized Event findById(String id) {
        for (Event e : events) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    public synchronized void add(Event event) {
        events.add(event);
    }

    public synchronized void replace(Event updated) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(updated.getId())) {
                events.set(i, updated);
                return;
            }
        }
    }

    /** Sorted distinct locations for filter dropdown */
    public synchronized List<String> distinctLocations() {
        Set<String> set = new LinkedHashSet<>();
        for (Event e : events) {
            set.add(e.getLocation());
        }
        List<String> list = new ArrayList<>(set);
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    public static List<String> defaultCategoryLabels() {
        List<String> c = new ArrayList<>();
        c.add("Concert");
        c.add("Sports");
        c.add("Conference");
        c.add("Travel");
        c.add("Movie");
        return c;
    }
}
