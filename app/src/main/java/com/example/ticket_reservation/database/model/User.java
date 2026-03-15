package com.example.ticket_reservation.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Row model for the ticketing mock {@code users} table.
 */
public class User {

    private final int id;
    @NonNull
    private final String fullName;
    @NonNull
    private final String email;
    @NonNull
    private final String phone;

    public User(int id, @NonNull String fullName, @NonNull String email, @NonNull String phone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getFullName() {
        return fullName;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
