-- Ticketing mock database (SQLite)
-- Categories: Concert | Sports | Conference | Travel | Movie (strict)
-- Status: upcoming | full | cancelled
-- Run full seed from Java: TicketingOpenHelper + TicketingDataSeeder.seed()

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL CHECK (category IN ('Concert', 'Sports', 'Conference', 'Travel', 'Movie')),
    organizer TEXT NOT NULL,
    venue TEXT NOT NULL,
    city TEXT NOT NULL,
    date TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity >= 0),
    registered_count INTEGER NOT NULL DEFAULT 0 CHECK (registered_count >= 0),
    price REAL NOT NULL CHECK (price >= 0),
    is_free INTEGER NOT NULL CHECK (is_free IN (0, 1)),
    status TEXT NOT NULL CHECK (status IN ('upcoming', 'full', 'cancelled')),
    image_name TEXT NOT NULL,
    contact_email TEXT NOT NULL,
    CHECK (registered_count <= capacity),
    CHECK ((is_free = 1 AND price = 0) OR (is_free = 0))
);

CREATE TABLE IF NOT EXISTS registrations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    event_id INTEGER NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    registration_date TEXT NOT NULL,
    ticket_type TEXT NOT NULL CHECK (ticket_type IN ('standard', 'vip', 'student')),
    payment_status TEXT NOT NULL CHECK (payment_status IN ('paid', 'pending', 'refunded'))
);

CREATE INDEX IF NOT EXISTS idx_events_category ON events (category);
CREATE INDEX IF NOT EXISTS idx_events_date ON events (date);
CREATE INDEX IF NOT EXISTS idx_events_city ON events (city);
CREATE INDEX IF NOT EXISTS idx_events_status ON events (status);
CREATE INDEX IF NOT EXISTS idx_registrations_user ON registrations (user_id);
CREATE INDEX IF NOT EXISTS idx_registrations_event ON registrations (event_id);
