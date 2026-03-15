package com.example.ticket_reservation.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Row model for the ticketing mock {@code registrations} table.
 * Not to be confused with {@link com.example.ticket_reservation.model.Reservation}.
 */
public class Registration {

    public static final String TICKET_STANDARD = "standard";
    public static final String TICKET_VIP = "vip";
    public static final String TICKET_STUDENT = "student";

    public static final String PAYMENT_PAID = "paid";
    public static final String PAYMENT_PENDING = "pending";
    public static final String PAYMENT_REFUNDED = "refunded";

    private final int id;
    private final int userId;
    private final int eventId;
    @NonNull
    private final String registrationDate;
    @NonNull
    private final String ticketType;
    @NonNull
    private final String paymentStatus;

    public Registration(int id, int userId, int eventId, @NonNull String registrationDate,
                        @NonNull String ticketType, @NonNull String paymentStatus) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
        this.ticketType = ticketType;
        this.paymentStatus = paymentStatus;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    @NonNull
    public String getRegistrationDate() {
        return registrationDate;
    }

    @NonNull
    public String getTicketType() {
        return ticketType;
    }

    @NonNull
    public String getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Registration that = (Registration) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
