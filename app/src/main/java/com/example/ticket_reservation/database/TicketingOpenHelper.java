package com.example.ticket_reservation.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

/**
 * SQLite helper for the ticketing mock database. Creates schema on first open.
 * Call {@link TicketingDataSeeder#seedIfEmpty(Context, SQLiteDatabase)} after open to populate data.
 */
public class TicketingOpenHelper extends SQLiteOpenHelper {

    public TicketingOpenHelper(@NonNull Context context) {
        super(context.getApplicationContext(), TicketingContract.DB_NAME, null, TicketingContract.DB_VERSION);
    }

    @Override
    public void onConfigure(@NonNull SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TicketingContract.Users.TABLE + " ("
                + TicketingContract.Users.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TicketingContract.Users.FULL_NAME + " TEXT NOT NULL,"
                + TicketingContract.Users.EMAIL + " TEXT NOT NULL UNIQUE,"
                + TicketingContract.Users.PHONE + " TEXT NOT NULL"
                + ")");

        db.execSQL("CREATE TABLE " + TicketingContract.Events.TABLE + " ("
                + TicketingContract.Events.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TicketingContract.Events.TITLE + " TEXT NOT NULL,"
                + TicketingContract.Events.DESCRIPTION + " TEXT NOT NULL,"
                + TicketingContract.Events.CATEGORY + " TEXT NOT NULL,"
                + TicketingContract.Events.ORGANIZER + " TEXT NOT NULL,"
                + TicketingContract.Events.VENUE + " TEXT NOT NULL,"
                + TicketingContract.Events.CITY + " TEXT NOT NULL,"
                + TicketingContract.Events.DATE + " TEXT NOT NULL,"
                + TicketingContract.Events.START_TIME + " TEXT NOT NULL,"
                + TicketingContract.Events.END_TIME + " TEXT NOT NULL,"
                + TicketingContract.Events.CAPACITY + " INTEGER NOT NULL CHECK (" + TicketingContract.Events.CAPACITY + " >= 0),"
                + TicketingContract.Events.REGISTERED_COUNT + " INTEGER NOT NULL DEFAULT 0 CHECK ("
                + TicketingContract.Events.REGISTERED_COUNT + " >= 0),"
                + TicketingContract.Events.PRICE + " REAL NOT NULL CHECK (" + TicketingContract.Events.PRICE + " >= 0),"
                + TicketingContract.Events.IS_FREE + " INTEGER NOT NULL CHECK (" + TicketingContract.Events.IS_FREE + " IN (0,1)),"
                + TicketingContract.Events.STATUS + " TEXT NOT NULL,"
                + TicketingContract.Events.IMAGE_NAME + " TEXT NOT NULL,"
                + TicketingContract.Events.CONTACT_EMAIL + " TEXT NOT NULL,"
                + "CHECK (" + TicketingContract.Events.REGISTERED_COUNT + " <= " + TicketingContract.Events.CAPACITY + ")"
                + ")");

        db.execSQL("CREATE TABLE " + TicketingContract.Registrations.TABLE + " ("
                + TicketingContract.Registrations.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TicketingContract.Registrations.USER_ID + " INTEGER NOT NULL REFERENCES "
                + TicketingContract.Users.TABLE + "(" + TicketingContract.Users.ID + ") ON DELETE CASCADE,"
                + TicketingContract.Registrations.EVENT_ID + " INTEGER NOT NULL REFERENCES "
                + TicketingContract.Events.TABLE + "(" + TicketingContract.Events.ID + ") ON DELETE CASCADE,"
                + TicketingContract.Registrations.REGISTRATION_DATE + " TEXT NOT NULL,"
                + TicketingContract.Registrations.TICKET_TYPE + " TEXT NOT NULL,"
                + TicketingContract.Registrations.PAYMENT_STATUS + " TEXT NOT NULL"
                + ")");

        db.execSQL("CREATE INDEX idx_events_category ON " + TicketingContract.Events.TABLE
                + "(" + TicketingContract.Events.CATEGORY + ")");
        db.execSQL("CREATE INDEX idx_events_date ON " + TicketingContract.Events.TABLE
                + "(" + TicketingContract.Events.DATE + ")");
        db.execSQL("CREATE INDEX idx_events_city ON " + TicketingContract.Events.TABLE
                + "(" + TicketingContract.Events.CITY + ")");
        db.execSQL("CREATE INDEX idx_events_status ON " + TicketingContract.Events.TABLE
                + "(" + TicketingContract.Events.STATUS + ")");
        db.execSQL("CREATE INDEX idx_reg_user ON " + TicketingContract.Registrations.TABLE
                + "(" + TicketingContract.Registrations.USER_ID + ")");
        db.execSQL("CREATE INDEX idx_reg_event ON " + TicketingContract.Registrations.TABLE
                + "(" + TicketingContract.Registrations.EVENT_ID + ")");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TicketingContract.Registrations.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TicketingContract.Events.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TicketingContract.Users.TABLE);
        onCreate(db);
    }
}
