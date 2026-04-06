package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class EventRepository {

    private static EventRepository instance;
    private static boolean seedAfterResetForTests;

    private final List<Event> events = new ArrayList<>();

    public EventRepository() {
    }

    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
            if (!SupabaseConfig.isConfigured() || seedAfterResetForTests) {
                instance.seedSampleData();
            }
            seedAfterResetForTests = false;
        }
        return instance;
    }

    /**
     * Testing only: replace singleton with a fresh repository.
     */
    public static synchronized void resetSingletonForTests() {
        instance = null;
        seedAfterResetForTests = true;
    }

    public synchronized void seedSampleData() {
        if (!events.isEmpty()) {
            return;
        }
        events.add(Event.createNew("Summer Concert 2026", "2026-03-15", "20:00",
                "Bell Centre, Montreal", "Concert", 500));
        events.add(Event.createNew("Tech Conference", "2026-03-20", "09:00",
                "Palais des congrès, Montreal", "Conference", 200));
        events.add(Event.createNew("Hockey Game", "2026-03-25", "19:00",
                "Bell Centre, Montreal", "Sports", 800));
        events.add(Event.createNew("Indie Film Night", "2026-04-02", "21:30",
                "Cinéma du Parc, Montreal", "Movie", 120));
        events.add(Event.createNew("Charlevoix Flavour Train Weekend", "2026-05-10", "08:15",
                "Gare du Palais, Quebec City", "Travel", 45));
        events.add(Event.createNew("Laurentian Shuttle & Spa Day", "2026-05-18", "07:45",
                "Jean-Talon Metro, Montreal", "Travel", 60));
        events.add(Event.createNew("Alouettes Preseason Scrimmage", "2026-04-12", "13:00",
                "Percival Molson Stadium, Montreal", "Sports", 350));
        events.add(Event.createNew("Montreal Jazz Evenings: Brass Session", "2026-04-28", "20:30",
                "MTelus, Montreal", "Concert", 280));
        events.add(Event.createNew("FinTech Canada Forum", "2026-05-05", "08:30",
                "Fairmont Queen Elizabeth, Montreal", "Conference", 150));
        events.add(Event.createNew("Dune Marathon IMAX", "2026-04-22", "18:45",
                "Cinéma Banque Scotia, Montreal", "Movie", 300));
        events.add(Event.createNew("Eastern Townships Winery Circuit", "2026-06-02", "09:00",
                "Central Station, Montreal", "Travel", 55));
        events.add(Event.createNew("PWHL Montreal Home Stand", "2026-03-30", "19:00",
                "Place Bell, Laval", "Sports", 420));
        events.add(Event.createNew("Osheaga Afterdark: Analog Dreams", "2026-07-14", "22:00",
                "Parc Jean-Drapeau, Montreal", "Concert", 600));
        events.add(Event.createNew("Design Systems at Scale", "2026-05-22", "10:00",
                "Phi Centre, Montreal", "Conference", 180));
        events.add(Event.createNew("National Canadian Film Day Encore", "2026-04-17", "18:30",
                "Cinémathèque québécoise, Montreal", "Movie", 90));
    }

    public synchronized void replaceAllFromRemote(List<Event> remote) {
        events.clear();
        events.addAll(remote);
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


    public synchronized List<String> distinctCategories() {
        Set<String> set = new LinkedHashSet<>();
        for (Event e : events) {
            set.add(e.getCategory());
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
