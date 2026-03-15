package com.example.ticket_reservation.database;

/**
 * Table and column names for the ticketing mock SQLite schema.
 */
public final class TicketingContract {

    private TicketingContract() {
    }

    public static final String DB_NAME = "ticketing_mock.db";
    public static final int DB_VERSION = 1;

    public static final class Users {
        public static final String TABLE = "users";
        public static final String ID = "id";
        public static final String FULL_NAME = "full_name";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
    }

    public static final class Events {
        public static final String TABLE = "events";
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String CATEGORY = "category";
        public static final String ORGANIZER = "organizer";
        public static final String VENUE = "venue";
        public static final String CITY = "city";
        public static final String DATE = "date";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String CAPACITY = "capacity";
        public static final String REGISTERED_COUNT = "registered_count";
        public static final String PRICE = "price";
        public static final String IS_FREE = "is_free";
        public static final String STATUS = "status";
        public static final String IMAGE_NAME = "image_name";
        public static final String CONTACT_EMAIL = "contact_email";
    }

    public static final class Registrations {
        public static final String TABLE = "registrations";
        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String EVENT_ID = "event_id";
        public static final String REGISTRATION_DATE = "registration_date";
        public static final String TICKET_TYPE = "ticket_type";
        public static final String PAYMENT_STATUS = "payment_status";
    }
}
