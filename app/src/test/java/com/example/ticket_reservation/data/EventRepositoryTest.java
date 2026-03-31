package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EventRepository")
class EventRepositoryTest {

    private EventRepository repo;

    @BeforeEach
    void setUp() {
        repo = new EventRepository();
    }

    @Test
    @DisplayName("fresh repository is empty until seed or add")
    void startsEmpty() {
        assertTrue(repo.getAllEvents().isEmpty());
        repo.seedSampleData();
        assertEquals(15, repo.getAllEvents().size());
    }

    @Test
    @DisplayName("seed is idempotent")
    void seedIdempotent() {
        repo.seedSampleData();
        int n = repo.getAllEvents().size();
        repo.seedSampleData();
        assertEquals(n, repo.getAllEvents().size());
    }

    @Test
    @DisplayName("findById returns null for unknown id")
    void findMissing() {
        repo.add(Event.createNew("Only", "2026-06-01", "", "Here", "Concert", 10));
        String id = repo.getAllEvents().get(0).getId();
        assertNotNull(repo.findById(id));
        assertNull(repo.findById("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    @DisplayName("replace swaps instance by id")
    void replace() {
        Event e = Event.createNew("Old", "2026-01-01", "", "L", "Movie", 20);
        repo.add(e);
        Event updated = new Event(e.getId(), "New title", "2026-01-02", "", "L2", "Sports", false, 30, 5);
        repo.replace(updated);
        Event live = repo.findById(e.getId());
        assertEquals("New title", live.getTitle());
        assertEquals(30, live.getCapacity());
        assertEquals(5, live.getTicketsReserved());
    }

    @Test
    @DisplayName("distinctLocations sorted case-insensitively")
    void distinctLocationsSorted() {
        repo.add(Event.createNew("A", "2026-01-01", "", "zebra", "Concert", 1));
        repo.add(Event.createNew("B", "2026-01-02", "", "Alpha", "Concert", 1));
        repo.add(Event.createNew("C", "2026-01-03", "", "mId", "Concert", 1));
        List<String> locs = repo.distinctLocations();
        assertEquals(3, locs.size());
        assertEquals("Alpha", locs.get(0));
        assertEquals("mId", locs.get(1));
        assertEquals("zebra", locs.get(2));
    }

    @Test
    @DisplayName("defaultCategoryLabels has five fixed labels")
    void defaultCategories() {
        List<String> c = EventRepository.defaultCategoryLabels();
        assertEquals(5, c.size());
        assertTrue(c.contains("Concert"));
        assertTrue(c.contains("Movie"));
    }
}
