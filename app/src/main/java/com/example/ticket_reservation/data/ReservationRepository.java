package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Reservation;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory reservations for the current device session.
 */
public class ReservationRepository {

    private static ReservationRepository instance;

    private final List<Reservation> reservations = new ArrayList<>();

    public static synchronized ReservationRepository getInstance() {
        if (instance == null) {
            instance = new ReservationRepository();
        }
        return instance;
    }

    public static synchronized void resetSingletonForTests() {
        instance = null;
    }

    public synchronized void add(Reservation r) {
        reservations.add(r);
    }

    public synchronized List<Reservation> findByUser(String userKey) {
        List<Reservation> out = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getUserKey().equals(userKey)) {
                out.add(r);
            }
        }
        return out;
    }

    public synchronized Reservation findById(String id) {
        for (Reservation r : reservations) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public synchronized boolean remove(String id) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId().equals(id)) {
                reservations.remove(i);
                return true;
            }
        }
        return false;
    }

    public synchronized void clear() {
        reservations.clear();
    }
}
